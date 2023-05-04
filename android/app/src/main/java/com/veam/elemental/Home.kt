package com.veam.elemental

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.view.ViewGroup
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_combine.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_vote.*


class Home : AppCompatActivity(),CombineFragment.OnLoaded,SettingsFragment.VolumeChangedListener{
    public var selected=0
    var loading=true
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var localSave: SharedPreferences
    var user:String=""

    lateinit var bgMusic:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        localSave=this.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        user=firebaseAuth.currentUser!!.uid
        val context=this

        pagerView.adapter=TabAdapter(context)
        tabLayout.visibility=View.INVISIBLE
        pagerView.visibility=View.INVISIBLE
        val display=getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val refreshRate = display.defaultDisplay.refreshRate
        localSave.edit().putFloat("refreshRate",refreshRate).apply()
        startLoadingAnim()
        TabLayoutMediator(tabLayout,pagerView){tab, position ->
            when(position){
                0 -> tab.text="₂1₃"
                1 -> tab.text="☰"
                2 -> tab.text="⌂"
                3 -> tab.text="˄˅"
                4 -> tab.text="⚙"
            }
        }.attach()
        pagerView.currentItem = 2


        bgMusic= MediaPlayer.create(this,R.raw.bgmusic)
        bgMusic.isLooping=true
        val volume=localSave.getFloat("bgMusicVolume",0.5F)

        bgMusic.setVolume(volume,volume)
        if(!bgMusic.isPlaying) {
            bgMusic.start()
            if((System.currentTimeMillis()-localSave.getLong("bgMusic/stoppedTime",0))<1000){
                bgMusic.seekTo(localSave.getInt("bgMusic/seek",0))
            }
        }

    }

    override fun volumeChangedTo(volume: Float) {
        val volume=localSave.getFloat("bgMusicVolume",0.5F)
        bgMusic.setVolume(volume,volume)
    }

    override fun onPause() {
        CustomServer.SetOffline(user,this).execute()

        super.onPause()
        bgMusic.pause()
    }

    override fun onResume() {
        CustomServer.SetOnline(user,this).execute()
        super.onResume()
        bgMusic.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        localSave.edit().putInt("bgMusic/seek",bgMusic.currentPosition).apply()
        localSave.edit().putLong("bgMusic/stoppedTime",System.currentTimeMillis()).apply()
        bgMusic.stop()
        bgMusic.release()
    }





    fun startLoadingAnim(){
        val handler= Handler()
        val display=getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val refreshRate = display.defaultDisplay.refreshRate
        handler.postDelayed(object:Runnable{
            override fun run() {
                loadingIV.rotation+=360/refreshRate
                if(loading)
                    handler.postDelayed(this,(1000/refreshRate).toLong())
            }
        },(1000/refreshRate).toLong())
    }

    fun loadedAnim(){
        if(tabLayout!=null) {
            tabLayout.visibility = View.VISIBLE
            pagerView.visibility = View.VISIBLE
            tabLayout.alpha = 0F
            pagerView.alpha = 0F
            val handler = Handler()

            val refreshRate = localSave.getFloat("refreshRate", 60F)
            handler.postDelayed(object : Runnable {
                override fun run() {
                    loadingIV.alpha -= 1 / refreshRate
                    loadingTV.alpha -= 1 / refreshRate
                    tabLayout.alpha += 1 / refreshRate
                    pagerView.alpha += 1 / refreshRate
                    if (pagerView.alpha < 1)
                        handler.postDelayed(this, (1000 / refreshRate).toLong())
                    else {
                        loadingIV.visibility = View.GONE
                        loadingTV.visibility = View.GONE
                        loading = false
                    }
                }
            }, (1000 / refreshRate).toLong())
        }
    }

    override fun onLoadStart() {

        if(tabLayout!=null) {
            tabLayout.visibility = View.GONE
            pagerView.visibility = View.GONE
        }
    }

    override fun onLoaded() {
        loadedAnim()
    }

}


