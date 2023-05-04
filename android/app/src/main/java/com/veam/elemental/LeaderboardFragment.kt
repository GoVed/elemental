package com.veam.elemental

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import kotlinx.android.synthetic.main.fragment_leaderboard.view.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LeaderboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LeaderboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var settedRV=false
    lateinit var localSave:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_leaderboard, container, false)
        localSave=context!!.getSharedPreferences("elementalSave", android.content.Context.MODE_PRIVATE)
        setRV(view)
        view!!.refreshLayout.setOnRefreshListener {
            setRV(view)
        }
        return view
    }


    override fun onResume() {
        super.onResume()
        CustomServer.SetOnline(FirebaseAuth.getInstance().currentUser!!.uid,context!!).execute()
    }

    private fun setRV(view: View?) {
        view!!.refreshLayout.isRefreshing=true
        var name: ArrayList<String> = ArrayList()
        var unlocked: ArrayList<Long> = ArrayList()
        var id: ArrayList<String> = ArrayList()
        var online: ArrayList<Boolean> = ArrayList()
        CustomServer.GetLeaderboardWithID(object :ServerListener{
            override fun runWithValue(value: String) {
                var rank: ArrayList<Long> = ArrayList()
                try{
                    val data=JSONObject(value)
                    data.keys().forEach {
                        id.add(it)
                        name.add(data.getJSONObject(it).getString("name"))
                        unlocked.add(data.getJSONObject(it).getString("unlocked").toLong())
                        if(data.getJSONObject(it).getString("online")=="1")
                            online.add(true)
                        else
                            online.add(false)
                    }

                    var prev:Long=0
                    var i:Long=0
                    unlocked.forEach {
                        if(prev!=it){
                            i++
                        }
                        rank.add(i)
                        prev=it
                    }
                }
                catch(e:Exception){
                    Log.e("errorOnLoadingLogs","${e.stackTrace}")
                }

                if(leaderboard_rv!=null){
                    if(!settedRV) {
                        leaderboard_rv.addOnScrollListener(object :
                            RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                                if (System.currentTimeMillis() - localSave.getLong(
                                        "rv/leaderboard/lastScroll",
                                        0
                                    ) < 200
                                )
                                    localSave.edit().putLong(
                                        "rv/leaderboard/setScroll",
                                        System.currentTimeMillis()
                                    ).apply()
                                localSave.edit().putLong(
                                    "rv/leaderboard/lastScroll",
                                    System.currentTimeMillis()
                                ).apply()
                            }
                        })
                        settedRV=true
                    }
                    leaderboard_rv.layoutManager=LinearLayoutManager(activity!!)
                    leaderboard_rv.adapter=RankAdapter(rank,name,unlocked,id,online,activity!!)
                }
                view!!.refreshLayout.isRefreshing=false
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
         * @return A new instance of fragment LeaderboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
