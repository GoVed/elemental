package com.veam.elemental

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    var called=false
    var loggingIn=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkMode()
        setContentView(R.layout.activity_main)

        val localSave = this@MainActivity.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
        localSave.edit().putFloat("displayDensity", Resources.getSystem().displayMetrics.density).apply()
        val display=(getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        localSave.edit().putFloat("refreshRate",display.refreshRate).apply()

        firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser!=null){
            google_button.visibility= View.GONE
            signingIn.visibility=View.VISIBLE
            localSave.edit().putString("serverloc","http://veamserver.ddns.net/elementalPK/").apply()

            CustomServer.GetServerLoc(object :ServerListener{
                override fun runWithValue(value: String) {
                    if(!loggingIn&&value.contains(".")){
                        loggingIn=true
                        localSave.edit().putString("serverloc",value).apply()

                        CustomServer.CheckAndCreateNewUser(firebaseAuth.currentUser!!.uid,object :ServerListener{
                            override fun runWithValue(value: String) {
                                if(!called) {

                                    called=true
                                    startActivity(Intent(this@MainActivity, Home::class.java))
                                    finish()
                                }
                            }
                        },this@MainActivity).execute()
                    }
                }
            },this).execute()

            Firebase.database.getReference("serverloc").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {

                    if(!loggingIn){
                        loggingIn=true
                        localSave.edit().putString("serverloc", p0.value.toString()).apply()
                        CustomServer.CheckAndCreateNewUser(firebaseAuth.currentUser!!.uid,object :ServerListener{
                            override fun runWithValue(value: String) {
                                if(!called) {
                                    called=true
                                    startActivity(Intent(this@MainActivity, Home::class.java))
                                    finish()
                                }
                            }
                        },this@MainActivity).execute()
                    }
                }
            })
        }

        configureGoogleSignIn()
        setupUI()
        setNotificationChannels()
    }

    fun setNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val mChannel1 = NotificationChannel("upvoted_added", "Upvoted element is added", NotificationManager.IMPORTANCE_MIN)
            mChannel1.description = "The element that you upvoted being added to the game"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel1)

            val mChannel2 = NotificationChannel("suggestion_added", "Your suggestion is added", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel2.description = "The element that you suggested being added to the game"
            notificationManager.createNotificationChannel(mChannel2)

            val mChannel3 = NotificationChannel("suggestion_upvoted", "Your suggestion is upvoted", NotificationManager.IMPORTANCE_MIN)
            mChannel3.description = "The element that you suggested being upvoted by some other player"
            notificationManager.createNotificationChannel(mChannel3)

            val mChannel4 = NotificationChannel("suggestion_comment", "Your suggestion gets a new comment", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel4.description = "The element that you suggested gets a new comment"
            notificationManager.createNotificationChannel(mChannel4)

            val mChannel5 = NotificationChannel("new_refer", "New refer", NotificationManager.IMPORTANCE_HIGH)
            mChannel5.description = "Someone uses your referral code"
            notificationManager.createNotificationChannel(mChannel5)
        }
    }


    fun setDarkMode(){
        var localSave = this@MainActivity.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
        if(localSave.getInt("theme",1)==0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        if(localSave.getInt("theme",1)==1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        if(localSave.getInt("theme",1)==2){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }
    private fun setupUI() {
        google_button.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                google_button.visibility= View.GONE
                signingIn.visibility=View.VISIBLE
                var localSave = this@MainActivity.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)


                localSave.edit().putString("serverloc","http://veamserver.ddns.net/elementalPK/").apply()

                CustomServer.GetServerLoc(object :ServerListener{
                    override fun runWithValue(value: String) {
                        if(!loggingIn&&value.contains(".")){
                            loggingIn=true
                            localSave.edit().putString("serverloc",value).apply()
                            CustomServer.CheckAndCreateNewUser(firebaseAuth.currentUser!!.uid,object :ServerListener{
                                override fun runWithValue(value: String) {
                                    if(!called) {
                                        called=true
                                        startActivity(Intent(this@MainActivity, Home::class.java))
                                        finish()
                                    }
                                }
                            },this@MainActivity).execute()
                        }
                    }
                },this).execute()


                Firebase.database.getReference("serverloc").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        if(!loggingIn) {
                            loggingIn = true
                            localSave.edit().putString("serverloc", p0.value.toString()).apply()
                            CustomServer.CheckAndCreateNewUser(firebaseAuth.currentUser!!.uid,object :ServerListener{
                                override fun runWithValue(value: String) {
                                    if(!called) {
                                        called=true
                                        startActivity(Intent(this@MainActivity, Home::class.java))
                                        finish()
                                    }
                                }
                            },this@MainActivity).execute()
                        }
                    }
                })


            } else {
                Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
