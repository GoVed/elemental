package com.veam.elemental

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_log.*
import kotlinx.android.synthetic.main.fragment_log.view.*
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var settedRV = false
    private var loadAmount=10
    private var loadingMore=false

    var type: ArrayList<String> = ArrayList()
    var add: ArrayList<String> = ArrayList()
    var id:ArrayList<String> = ArrayList()
    var info:ArrayList<String> = ArrayList()

    var user=FirebaseAuth.getInstance().currentUser!!.uid
//    var user= "4p7pgiJuHbU4LKo4HMj5XTV5YAS2"
    lateinit var rewardedAd:RewardedAd
    lateinit var globalView:View
    lateinit var localSave: SharedPreferences
    lateinit var adapter:LogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        adapter=LogsAdapter(type, add, id,info, activity!!)
        localSave=context!!.getSharedPreferences("elementalSave", android.content.Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_log, container, false)
        globalView=view

        setRV(view)

        view!!.refreshLayoutLog.setOnRefreshListener {
            setRV(view)
        }
        loadRewardedAd(view)
        setWatchAdBtn(view)
        return view
    }


    override fun onResume() {
        super.onResume()
        CustomServer.SetOnline(user,context!!).execute()
    }

    private fun loadRewardedAd(view: View){
        MobileAds.initialize(context!!)
        val adRequest = AdRequest.Builder().build()
        //My ID
        rewardedAd = RewardedAd(context, "ca-app-pub-5197879139590015/8447837326")
        //Test ID
//        rewardedAd = RewardedAd(context, "ca-app-pub-3940256099942544/5224354917")
//        val testDeviceIds = Arrays.asList("127AF145B25E37690D219CC49947CA86")
//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
//        MobileAds.setRequestConfiguration(configuration)
//        adRequest.isTestDevice(context!!)

        if(!rewardedAd.isLoaded) {

            val adLoadCallback = object : RewardedAdLoadCallback() {
                override fun onRewardedAdLoaded() {
                    view.watchAdBtn.visibility = View.VISIBLE
                }

                override fun onRewardedAdFailedToLoad(errorCode: Int) {
                    view.watchAdBtn.visibility = View.GONE
                }
            }
            rewardedAd.loadAd(adRequest, adLoadCallback)
        }
        else{
            view.watchAdBtn.visibility = View.VISIBLE
        }
    }

    private fun setWatchAdBtn(view: View){
        view.watchAdBtn.setOnClickListener {
            if(localSave.getInt("availableAd",0)>0) {
                if (rewardedAd.isLoaded) {
                    val activityContext = activity
                    val adCallback = object : RewardedAdCallback() {
                        override fun onRewardedAdOpened() {

                        }

                        override fun onRewardedAdClosed() {
                            loadRewardedAd(view)
                        }

                        override fun onUserEarnedReward(p0: RewardItem) {
                            localSave.edit().putInt("availableAd",localSave.getInt("availableAd",0)-1).apply()
                            CustomServer.GetValue("users/$user/bal", object : ServerListener {
                                override fun runWithValue(value: String) {
                                    CustomServer.UpdateBal(user, "${value.toLong() + 5}", context!!)
                                        .execute()
                                }
                            }, context!!).execute()
                            CustomServer.AddLog0(user, "+5", "Reward for watching ad", context!!)
                                .execute()
                        }

                        override fun onRewardedAdFailedToShow(errorCode: Int) {
                            Toast.makeText(activity, "Ad failed to show", Toast.LENGTH_SHORT).show()
                            loadRewardedAd(view)
                        }
                    }
                    rewardedAd.show(activityContext, adCallback)
                } else {
                    Toast.makeText(
                        activity,
                        "No ads available, try again later!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadRewardedAd(view)
                }
            }
            else {
                var timeDiff="Ads will be available in a while."
                val diff=localSave.getLong("lastAdAdded",0)+10800000-System.currentTimeMillis()
                if(System.currentTimeMillis()-localSave.getLong("lastAdAdded",0)>10800000){
                    localSave.edit().putLong("lastAdAdded",System.currentTimeMillis()).apply()
                    localSave.edit().putInt("availableAd",3).apply()
                }
                if (diff<5000){
                    timeDiff = "Please wait few seconds to get new ad"
                }
                else if (diff<60000){
                    timeDiff = "Please wait for "+ TimeUnit.MILLISECONDS.toSeconds(diff).toString() + " second/s."
                }

                else if (diff<3600000){
                    timeDiff = "Please wait for "+ TimeUnit.MILLISECONDS.toMinutes(diff).toString() + " minute/s."
                }
                else{
                    timeDiff = "Please wait for "+ TimeUnit.MILLISECONDS.toHours(diff).toString() + " hour/s."
                }
                Toast.makeText(
                    activity,
                    timeDiff,
                    Toast.LENGTH_SHORT
                ).show()
                loadRewardedAd(view)

            }
        }

    }

    private fun setRV(view: View) {
        if(!loadingMore)
            view.refreshLayoutLog.isRefreshing=true
        CustomServer.GetValue("users/$user/log",object :ServerListener{
            override fun runWithValue(value: String) {
                try {
                    val data=JSONObject(value)
                    type.clear()
                    add.clear()
                    info.clear()
                    id.clear()
                    data.keys().forEach {

                        type.add(data.getJSONObject(it).getString("type"))
                        add.add(data.getJSONObject(it).getString("add"))
                        if(data.getJSONObject(it).getString("type")=="0")
                            info.add(data.getJSONObject(it).getString("msg"))
                        else if(data.getJSONObject(it).getString("type")=="1")
                            info.add(data.getJSONObject(it).getString("unlocked"))
                        else if(data.getJSONObject(it).getString("type")=="2")
                            info.add(data.getJSONObject(it).getString("name"))
                        else if(data.getJSONObject(it).getString("type")=="3")
                            info.add(data.getJSONObject(it).getString("name"))
                        else if(data.getJSONObject(it).getString("type")=="4")
                            info.add(data.getJSONObject(it).getString("from"))
                        else
                            info.add("")
                        id.add(it)

                    }

                    type.reverse()
                    id.reverse()
                    info.reverse()
                    add.reverse()

                    if(logs_rv!=null) {
                        if(!settedRV){
                            logs_rv.adapter=adapter
                            if(activity!!.resources.configuration.orientation== Configuration.ORIENTATION_PORTRAIT)
                                logs_rv.layoutManager = LinearLayoutManager(activity!!)
                            else
                                logs_rv.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
                            logs_rv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)
                                    if(dy!=0) {
                                        if (System.currentTimeMillis() - localSave.getLong("rv/logs/lastScroll", 0) < 200)
                                            localSave.edit().putLong("rv/logs/setScroll", System.currentTimeMillis()).apply()
                                        localSave.edit().putLong("rv/logs/lastScroll", System.currentTimeMillis()).apply()
                                    }

                                }
                            })
                            settedRV=true
                        }

                        adapter.notifyDataSetChanged()

                    }

                }
                catch(e:Exception){
                    Log.d("Error occured on logs","${e.message}")
                }

                if(!loadingMore)
                    view.refreshLayoutLog.isRefreshing=false
                else
                    loadingMore=false
            }
        },context!!).execute()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



