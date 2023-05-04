package com.veam.elemental

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.ClipboardManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var user=FirebaseAuth.getInstance().currentUser!!.uid
    val version=BuildConfig.VERSION_NAME
    var freeNameChange=false
    var currentAudioSelected=0
    lateinit var localSave: SharedPreferences
    lateinit var volumeChangedListener: VolumeChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        volumeChangedListener=activity as VolumeChangedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)
        localSave=activity!!.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
        setSettings(view)
        setOnSetUsername(view)
        setThemeSpinner(view)
        setSignout(view)
        setSaveDisplayName(view)
        setOpenDiscord(view)
        setOpenReddit(view)
        setNewVersionAvailable(view)
        setPing(view)
        setUID(view)
        setAudio(view)
        setRefer(view)
        setRedeem(view)
        setBalancePrivacy(view)
        setStatus(view)

        MobileAds.initialize(context) {}
        val mAdView = view.findViewById<AdView>(R.id.adView)

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        return view
    }

    fun setStatus(view: View){
        view.status.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                view.setStatus.text="SAVE >"
            }
        })
        view.setStatus.setOnClickListener {
            CustomServer.SetStatus(user,view.status.text.toString(),context!!).execute()
            Toast.makeText(activity,"Status updated",Toast.LENGTH_SHORT).show()
            view.setStatus.text = ">"
        }
    }

    fun setBalancePrivacy(view: View){
        if(localSave.getString("balPrivacy","private")=="private")
            animateBalanceSelected(0)
        else
            animateBalanceSelected(1)
        CustomServer.GetPrivacy(user,object :ServerListener{
            override fun runWithValue(value: String) {
                if(value=="0"){
                    if(localSave.getString("balPrivacy","private")=="public")
                        animateBalanceSelected(1,0)
                    localSave.edit().putString("balPrivacy","private").apply()
                }
                if(value=="1"){
                    if(localSave.getString("balPrivacy","private")=="private")
                        animateBalanceSelected(0,1)
                    localSave.edit().putString("balPrivacy","public").apply()
                }
            }
        },context!!).execute()
        view.privateBal.setOnClickListener {
            if(localSave.getString("balPrivacy","private")!="private"){
                localSave.edit().putString("balPrivacy","private").apply()
                CustomServer.SetPrivacy(user,"0",context!!).execute()
                animateBalanceSelected(0,1)
            }
        }
        view.publicBal.setOnClickListener {
            if(localSave.getString("balPrivacy","private")!="public"){
                localSave.edit().putString("balPrivacy","public").apply()
                CustomServer.SetPrivacy(user,"1",context!!).execute()
                animateBalanceSelected(1,0)
            }
        }
    }
    fun animateBalanceSelected(id:Int,prevSelected:Int=-1){
        val handler = Handler()
        val refreshRate = localSave.getFloat("refreshRate", 60F)
        val animSpeed=2F
        var alpha=0F
        handler.postDelayed(object : Runnable {
            override fun run() {

                if(view!=null) {
                    if (alpha < 255) {
                        var alphaInHex = Integer.toHexString(alpha.toInt())
                        if (alphaInHex.length == 1) {
                            alphaInHex = "0$alphaInHex"
                        }
                        if (id == 0) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            Log.d("checkcol", "hmm with $alpha #${alphaInHex}5050AA")
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.privateBal.background = shape
                        } else if (id == 1) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.publicBal.background = shape
                        }
                        alphaInHex = Integer.toHexString((255 - alpha).toInt())
                        if (alphaInHex.length == 1) {
                            alphaInHex = "0$alphaInHex"
                        }
                        if (prevSelected == 0) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.privateBal.background = shape
                        } else if (prevSelected == 1) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.publicBal.background = shape
                        }
                        alpha += 255 / refreshRate * animSpeed
                        handler.postDelayed(this, (1000 / refreshRate).toLong())
                    } else {
                        if (alpha != 255F) {
                            alpha = 255F
                            var alphaInHex = Integer.toHexString(alpha.toInt())
                            if (alphaInHex.length == 1) {
                                alphaInHex = "0$alphaInHex"
                            }
                            if (id == 0) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                Log.d("checkcol", "hmm with $alpha #${alphaInHex}5050AA")
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.privateBal.background = shape
                            } else if (id == 1) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.publicBal.background = shape
                            }
                            alphaInHex = Integer.toHexString((255 - alpha).toInt())
                            if (alphaInHex.length == 1) {
                                alphaInHex = "0$alphaInHex"
                            }
                            if (prevSelected == 0) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.privateBal.background = shape
                            } else if (prevSelected == 1) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.publicBal.background = shape
                            }
                        }
                    }
                }
                else{
                    handler.postDelayed(this, 500)
                }
            }
        }, (1000 / refreshRate).toLong())
    }


    fun setRedeem(view: View){
        view.redeemButton.setOnClickListener {
            if(redeemID.text.toString().length>0){
                if(context!=null) {
                    CustomServer.Redeem(user, redeemID.text.toString(), object : ServerListener {
                        override fun runWithValue(value: String) {
                            if (value == "invalid")
                                Toast.makeText(activity, "Invalid code!", Toast.LENGTH_SHORT).show()
                            else if (value == "already")
                                Toast.makeText(
                                    activity,
                                    "Code already redeemed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else if (value == "claimed")
                                Toast.makeText(
                                    activity,
                                    "Code successfully redeemed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                    }, context!!).execute()
                }
            }
            else
                Toast.makeText(activity,"Please enter the redeem code.",Toast.LENGTH_SHORT).show()
        }
    }

    fun setRefer(view: View){

        view.referOther.visibility=View.GONE
        view.copyCode.setOnClickListener {
            var clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Refer ID",user))
            Toast.makeText(activity,"Code Copied",Toast.LENGTH_SHORT).show()
        }
        view.shareCode.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Elemental is a great alchemy puzzle game with endless elements!\n\nTry it now for free: https://play.google.com/store/apps/details?id=com.veam.elemental\n\nAfter installing use my referral code $user in settings to get 150 coins instantly!.");
            startActivity(shareIntent)
        }
        view.referUser.setOnClickListener {
            if(referID.text.toString().length>0){
                if(context!=null) {
                    CustomServer.SetRefer(
                        user,
                        view.referID.text.toString(),
                        object : ServerListener {
                            override fun runWithValue(value: String) {
                                if (value == "already")
                                    Toast.makeText(
                                        activity,
                                        "Already referred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else if (value == "invalid")
                                    Toast.makeText(
                                        activity,
                                        "Invalid referral code, check again!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else if (value == "referred") {
                                    Toast.makeText(
                                        activity,
                                        "Successfully referred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    view.referredLayout.background = ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.rounded_bottom_light_gray,
                                        null
                                    )
                                    view.referOther.visibility = View.GONE
                                }
                            }
                        },
                        context!!
                    ).execute()
                }
            }
            else
                Toast.makeText(activity,"Please enter the referral code.",Toast.LENGTH_SHORT).show()
        }
        if(context!=null) {
            if (localSave.getString("refer","").toString() != "") {
                if (context != null) {
                    CustomServer.GetUsername(localSave.getString("refer","").toString(), object : ServerListener {
                        override fun runWithValue(value: String) {
                            val usernames: ArrayList<String> = ArrayList()
                            if (value.length > 0 && value != "connection failed") {
                                val data = JSONObject(value)
                                data.keys().forEach {
                                    usernames.add(data.getString(it))
                                }
                            }
                            if (usernames.isEmpty())
                                usernames.add("No refer yet!")
                            if (view.referredUsers != null) {
                                view.referredUsers.layoutManager =
                                    LinearLayoutManager(
                                        context,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                if(context!=null)
                                    view.referredUsers.adapter = ReferredUserAdapter(usernames, context!!)
                            }
                        }
                    }, context!!).execute()
                }
            }
//            CustomServer.GetRefer(user, object : ServerListener {
//                override fun runWithValue(value: String) {
//
//                }
//            }, context!!).execute()
        }
        if(context!=null) {
            CustomServer.GetReferStatus(user, object : ServerListener {
                override fun runWithValue(value: String) {
                    if (value == "0") {
                        view.referOther.visibility = View.VISIBLE
                    } else {
                        view.referredLayout.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.rounded_bottom_light_gray,
                            null
                        )
                    }
                }
            }, context!!).execute()
        }
    }

    interface VolumeChangedListener{
        fun volumeChangedTo(volume:Float){}
    }

    fun setAudio(view: View){
        animateAudioSelected(currentAudioSelected)
        view!!.musicSlider.position=localSave.getFloat("bgMusicVolume",0.5F)*2
        view!!.titleBgMusic.setOnClickListener {
            if(currentAudioSelected!=0) {
                animateAudioSelected(0, currentAudioSelected)
                currentAudioSelected = 0
                view!!.musicSlider.position=localSave.getFloat("bgMusicVolume",0.5F)*2
            }
        }
        view!!.titleOtherSounds.setOnClickListener {
            if(currentAudioSelected!=1) {
                animateAudioSelected(1, currentAudioSelected)
                currentAudioSelected = 1
                view.musicSlider.position=localSave.getFloat("SFXVolume",1F)
            }
        }
        view.musicSlider!!.positionListener={p->

            if(currentAudioSelected==0) {
                localSave.edit().putFloat("bgMusicVolume", p /2).apply()
                volumeChangedListener.volumeChangedTo(p/2)
            }
            else if(currentAudioSelected==1)
                localSave.edit().putFloat("SFXVolume",p).apply()
        }
    }

    fun animateAudioSelected(id:Int,prevSelected:Int=-1){
        val handler = Handler()
        val refreshRate = localSave.getFloat("refreshRate", 60F)
        val animSpeed=2F
        var alpha=0F
        handler.postDelayed(object : Runnable {
            override fun run() {

                if(view!=null) {
                    if (alpha < 255) {
                        var alphaInHex = Integer.toHexString(alpha.toInt())
                        if (alphaInHex.length == 1) {
                            alphaInHex = "0$alphaInHex"
                        }
                        if (id == 0) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            Log.d("checkcol", "hmm with $alpha #${alphaInHex}5050AA")
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.titleBgMusic.background = shape
                        } else if (id == 1) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.titleOtherSounds.background = shape
                        }
                        alphaInHex = Integer.toHexString((255 - alpha).toInt())
                        if (alphaInHex.length == 1) {
                            alphaInHex = "0$alphaInHex"
                        }
                        if (prevSelected == 0) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.titleBgMusic.background = shape
                        } else if (prevSelected == 1) {
                            var shape: GradientDrawable = GradientDrawable()
                            shape.cornerRadius = 32F
                            shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                            view!!.titleOtherSounds.background = shape
                        }
                        alpha += 255 / refreshRate * animSpeed
                        handler.postDelayed(this, (1000 / refreshRate).toLong())
                    } else {
                        if (alpha != 255F) {
                            alpha = 255F
                            var alphaInHex = Integer.toHexString(alpha.toInt())
                            if (alphaInHex.length == 1) {
                                alphaInHex = "0$alphaInHex"
                            }
                            if (id == 0) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                Log.d("checkcol", "hmm with $alpha #${alphaInHex}5050AA")
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.titleBgMusic.background = shape
                            } else if (id == 1) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.titleOtherSounds.background = shape
                            }
                            alphaInHex = Integer.toHexString((255 - alpha).toInt())
                            if (alphaInHex.length == 1) {
                                alphaInHex = "0$alphaInHex"
                            }
                            if (prevSelected == 0) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.titleBgMusic.background = shape
                            } else if (prevSelected == 1) {
                                var shape: GradientDrawable = GradientDrawable()
                                shape.cornerRadius = 32F
                                shape.setColor(Color.parseColor("#${alphaInHex}5050AA"))
                                view!!.titleOtherSounds.background = shape
                            }
                        }
                    }
                }
                else{
                    handler.postDelayed(this, 500)
                }
            }
        }, (1000 / refreshRate).toLong())
    }

    fun setUID(view: View){
        view.uid.text="UID: $user\nVersion: $version"
        view.uid.setOnClickListener {
            var clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Elemental Debug Info",view.uid.text.toString()))
            Toast.makeText(activity,"UID Copied",Toast.LENGTH_SHORT).show()
        }
    }

    fun setPing(view: View){
        val start=System.currentTimeMillis()
        CustomServer.GetPing(object :ServerListener{
            override fun runWithValue(value: String) {
                view.checkPing.text="PING: ${System.currentTimeMillis()-start} ms"
            }
        },context!!).execute()
        view.checkPing.setOnClickListener {
            val start=System.currentTimeMillis()
            CustomServer.GetPing(object :ServerListener{
                override fun runWithValue(value: String) {
                    view.checkPing.text="PING: ${System.currentTimeMillis()-start} ms"
                }
            },context!!).execute()
        }
    }

    fun setNewVersionAvailable(view: View){
        view.newVersionAvailableTV.visibility=View.GONE
        CustomServer.GetValue("version",object :ServerListener{
            override fun runWithValue(value: String) {
                if(value!=version)
                    view.newVersionAvailableTV.visibility=View.VISIBLE
            }
        },context!!).execute()
    }

    fun setOpenDiscord(view: View){
        view.openDiscord.setOnClickListener {
            startActivity(Intent(android.content.Intent.ACTION_VIEW).setData(Uri.parse("https://discord.gg/rbBw8vk")))
        }
    }

    fun setOpenReddit(view: View){
        view.openReddit.setOnClickListener {
            startActivity(Intent(android.content.Intent.ACTION_VIEW).setData(Uri.parse("https://www.reddit.com/r/elementalgame/")))
        }
    }

    fun setSaveDisplayName(view: View){
        view.username.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(freeNameChange)
                    view.setUsername.text="SAVE >"
                else
                    view.setUsername.text="SAVE [25\uD83D\uDCB5] >"
            }

        })
    }

    fun setSignout(view: View){
        view.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity,MainActivity::class.java))
            activity!!.finish()
        }

    }

    fun setThemeSpinner(view: View) {
        var themes: ArrayList<String> = ArrayList()
        themes.add("System")
        themes.add("Dark")
        themes.add("Light")
        view.themeSpinner.adapter = ArrayAdapter(activity!!.applicationContext, R.layout.spinner_text, themes)
        view.themeSpinner.setSelection(localSave.getInt("theme",1))
        view.themeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                localSave.edit().putInt("theme",position).apply()
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
        }
    }

    fun setSettings(view: View) {
        if(localSave.getString("username","")!="") {
            if(localSave.getString("username","")==user) {
                freeNameChange = true
                view.username.setText("No name")
            }
            else
                view.username.setText(localSave.getString("username",""))
            view.setUsername.text=">"
        }

//        CustomServer.GetValue("users/$user/name",object :ServerListener{
//            override fun runWithValue(value: String) {
//
//            }
//        },context!!).execute()

        view.status.setText(localSave.getString("status",""))
        view.setStatus.text=">"
//        CustomServer.GetStatus(user,object :ServerListener{
//            override fun runWithValue(value: String) {
//
//            }
//        },context!!).execute()

    }
    fun setOnSetUsername(view: View) {
        view.setUsername.setOnClickListener {
            CustomServer.UpdateUsername(user,username.text.toString(),object :ServerListener{
                override fun runWithValue(value: String) {
                    if(value=="updated"){
                        Toast.makeText(activity,"Username updated",Toast.LENGTH_SHORT).show()
                        view.setUsername.text = ">"
                    }
                    else if(value=="balance"){
                        Toast.makeText(activity,"Insufficient balance",Toast.LENGTH_SHORT).show()
                    }
                    else if(value=="taken"){
                        Toast.makeText(activity,"Username already taken",Toast.LENGTH_SHORT).show()
                    }
                    else if(value=="length"){
                        Toast.makeText(activity,"Username must be between 5 to 20 characters",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(activity,"Some error occured, Try again!",Toast.LENGTH_SHORT).show()
                    }
                }
            },context!!).execute()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
