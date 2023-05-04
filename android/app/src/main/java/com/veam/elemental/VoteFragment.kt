package com.veam.elemental

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import kotlinx.android.synthetic.main.fragment_vote.*
import kotlinx.android.synthetic.main.fragment_vote.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [vote.newInstance] factory method to
 * create an instance of this fragment.
 */
class VoteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var user:String=""




    lateinit var localSave: SharedPreferences

    var data:JSONObject=JSONObject("{}")

    var allVotes:ArrayList<DataVote> = ArrayList()

    var searchVotes:ArrayList<DataVote> = ArrayList()

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
        val view = inflater.inflate(R.layout.fragment_vote, container, false)
        localSave=activity!!.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)


        view.searchButton.visibility=View.GONE
        user= FirebaseAuth.getInstance().currentUser!!.uid




        var lastSearch=""
        view.searchButton.setOnClickListener {
            if(searchText.text.toString()!=lastSearch&&!view!!.votesRefreshLayout.isRefreshing) {
                view!!.votesRefreshLayout.isRefreshing = true
                setRV(view)
                lastSearch=searchText.text.toString()
            }
        }
        view.searchText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.isEmpty()) {
                    view!!.votesRefreshLayout.isRefreshing = true
                    setRV(view)
                    view.searchButton.visibility=View.GONE
                }
                else{
                    view.searchButton.visibility=View.VISIBLE
                }
            }
        })
        updateVote(view)
        view!!.votesRefreshLayout.setOnRefreshListener {
            updateVote(view)
        }
        openSettings(view)
        return view
    }

    fun openSettings(pview:View){
        pview.openVoteSettings.setOnClickListener {
            val inflater:LayoutInflater = LayoutInflater.from(context)

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.vote_settings,null)

            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )



            popupWindow.isFocusable = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 25.0F
            }

            view.setOnTouchListener { v, event -> //Close the window when clicked
                popupWindow.dismiss()
                true
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val fade = android.transition.Fade()

                popupWindow.enterTransition = fade

                // Slide animation for popup window exit transition
                popupWindow.exitTransition = fade

            }

            val sortModeLatest = view.findViewById<TextView>(R.id.voteSortModeLatest)
            val sortModeVotes = view.findViewById<TextView>(R.id.voteSortModeVotes)
            val sortModeComments = view.findViewById<TextView>(R.id.voteSortModeComments)
            val sortModeUserName = view.findViewById<TextView>(R.id.voteSortModeUserName)
            val sortModeElementName = view.findViewById<TextView>(R.id.voteSortModeElementName)
            val sortModeRequiredVotes = view.findViewById<TextView>(R.id.voteSortModeRequiredVotes)
            val sortModeHot = view.findViewById<TextView>(R.id.voteSortHot)

            val buttons= listOf<TextView>(sortModeLatest,sortModeVotes,sortModeComments,sortModeUserName,sortModeElementName,sortModeRequiredVotes,sortModeHot)

            val sortType = view.findViewById<TextView>(R.id.voteSortType)


            lowLightButton(buttons)
            setSortTypeText(sortType)
            sortModeLatest.setOnClickListener {
                localSave.edit().putInt("voteSortMode",0).apply()
                lowLightButton(buttons)
                setRV(pview)
            }
            sortModeVotes.setOnClickListener {
                localSave.edit().putInt("voteSortMode",1).apply()
                lowLightButton(buttons)
                setRV(pview)
            }
            sortModeComments.setOnClickListener {
                localSave.edit().putInt("voteSortMode",2).apply()
                lowLightButton(buttons)
                setRV(pview)
            }
            sortModeUserName.setOnClickListener {
                localSave.edit().putInt("voteSortMode",3).apply()
                lowLightButton(buttons)
                setRV(pview)
            }
            sortModeElementName.setOnClickListener {
                localSave.edit().putInt("voteSortMode",4).apply()
                lowLightButton(buttons)
                setRV(pview)
            }
            sortModeRequiredVotes.setOnClickListener {
                localSave.edit().putInt("voteSortMode",5).apply()
                lowLightButton(buttons)
                setRV(pview)
            }
            sortModeHot.setOnClickListener {
                localSave.edit().putInt("voteSortMode",6).apply()
                lowLightButton(buttons)
                setRV(pview)
            }

            sortType.setOnClickListener {
                var current = localSave.getInt("voteSortType",0)
                current++
                current%=2
                localSave.edit().putInt("voteSortType",current).apply()
                setSortTypeText(sortType)
                setRV(pview)
            }

            TransitionManager.beginDelayedTransition(voteFragment)
            popupWindow.showAtLocation(
                voteFragment, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )

        }
    }
    fun lowLightButton(buttons:List<TextView>){
        val handler = Handler()
        var alpha=1F
        val refreshRate = localSave.getFloat("refreshRate", 60F)
        val animSpeed=2F
        val selected=localSave.getInt("voteSortMode",0)
        var i=0
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (alpha>0.5) {
                    i=0
                    buttons.forEach {
                        if(i==selected)
                            it.alpha=0.5F+(1-alpha)
                        else
                            it.alpha=alpha
                        i++
                    }

                    alpha-=animSpeed/refreshRate
                    handler.postDelayed(this, (1000 / refreshRate).toLong())
                }
                else{
                    i=0
                    buttons.forEach {
                        if(i==selected)
                            it.alpha=1F
                        else
                            it.alpha=0.5F
                        i++
                    }

                }
            }
        }, (1000 / refreshRate).toLong())
    }
    
    fun setSortTypeText(sortType:TextView){
        if(localSave.getInt("voteSortType",0)==0){
            sortType.text = "↓"
        }
        if(localSave.getInt("voteSortType",0)==1){
            sortType.text = "↑"
        }
    }

    fun updateVote(view: View) {
        if(view!=null&&context!=null) {
            view!!.votesRefreshLayout.isRefreshing = true
            CustomServer.GetActiveVotes(object : ServerListener {
                override fun runWithValue(value: String) {
                    try {
                        data = JSONObject(value)
                        setRV(view)
                    }
                    catch(e:Exception){
                        Log.e("errorOnvotes",e.stackTrace.joinToString("\n"))
                    }
                }
            }, context!!).execute()
        }
    }

    fun setRV(view:View){
        try {
            allVotes.clear()
            searchVotes.clear()
            var userIds=""
            var oldScroll = view.vote_rv.layoutManager?.onSaveInstanceState()

            data.keys().forEach { it ->

                var element = DataVote(data.getJSONObject(it).getString("name"),data.getJSONObject(it).getString("color"),data.getJSONObject(it).getString("from"),data.getJSONObject(it).getString("vote"),it,data.getJSONObject(it).getString("time"),data.getJSONObject(it).getString("user"),data.getJSONObject(it).getString("username"),data.getJSONObject(it).getString("upvoted"),data.getJSONObject(it).getString("downvoted"),data.getJSONObject(it).getString("needed"),"0")

                try {
                    element.elementNoComments=(
                        data.getJSONObject(it).getJSONObject("comments").length()
                            .toString()
                    )
                } catch (e: JSONException) { }

                if (view.searchText.text.toString().isEmpty()) {
                    searchVotes.add(element)
                }
                else{
                    val searchText=view.searchText.text.toString().trim()
                    if(searchText.startsWith("by:",true)){
                        if (element.elementUsername.contains(searchText.substring(3).trim(), true)) {
                            searchVotes.add(element)
                        }
                    }
                    else if(searchText.startsWith("using:",true)){
                        val elements=ArrayList(searchText.substring(6).trim().split(",").map { it.toLowerCase() })
                        val elementName:ArrayList<String> = ArrayList()
                        element.elementFrom.split(",").forEach {it0->
                            elementName.add(localSave.getString("elements/$it0/name","").toString().toLowerCase())
                        }
                        if(elementName.containsAll(elements))
                            searchVotes.add(element)
                    }
                    else {
                        if (element.elementName.contains(searchText, true)) {
                            searchVotes.add(element)
                        }
                    }
                }
                if(userIds=="")
                    userIds=element.elementUser
                else
                    userIds+=",${element.elementUser}"

            }



            //Sort
            if(localSave.getInt("voteSortMode",0)==0){
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.reverse()
            }
            if(localSave.getInt("voteSortMode",0)==1){
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.sortBy{it.elementVote.toLong()}
                else
                    searchVotes.sortByDescending{it.elementVote.toLong()}
            }
            if(localSave.getInt("voteSortMode",0)==2){
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.sortBy{it.elementNoComments.toLong()}
                else
                    searchVotes.sortByDescending{it.elementNoComments.toLong()}
            }
            if(localSave.getInt("voteSortMode",0)==3){
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.sortBy{it.elementUsername}
                else
                    searchVotes.sortByDescending{it.elementUsername}
            }
            if(localSave.getInt("voteSortMode",0)==4){
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.sortBy{it.elementName}
                else
                    searchVotes.sortByDescending{it.elementName}
            }
            if(localSave.getInt("voteSortMode",0)==5){
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.sortBy{it.elementRequiredVote.toInt()-it.elementVote.toInt()}
                else
                    searchVotes.sortByDescending{it.elementRequiredVote.toInt()-it.elementVote.toInt()}
            }
            if(localSave.getInt("voteSortMode",0)==6){
                val currtime=System.currentTimeMillis().toDouble()
                if(localSave.getInt("voteSortType",0)==0)
                    searchVotes.sortBy{it.elementVote.toDouble()/(currtime-it.elementTime.toDouble())}
                else
                    searchVotes.sortByDescending{it.elementVote.toDouble()/(currtime-it.elementTime.toDouble())}
            }


            if (vote_rv != null) {
                if (activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    vote_rv.layoutManager =
                        LinearLayoutManager(activity!!)
                else
                    vote_rv.layoutManager = StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL
                    )

                vote_rv.adapter = VoteAdapter(
                    searchVotes,
                    activity!!
                )
                view.vote_rv.layoutManager!!.onRestoreInstanceState(
                    oldScroll
                )
                Log.d("checkapp","here")
                view!!.votesRefreshLayout.isRefreshing = false
            }


        } catch (e: Exception) {
            Log.e("errorOnLoadingVotes",e.stackTrace.joinToString("\n"))
        }
    }










    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment vote.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VoteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}



data class DataVote(var elementName: String="",
                var elementColor: String="",
                var elementFrom: String="",
                var elementVote: String="",
                var elementID: String="",
                var elementTime: String="",
                var elementUser: String="",
                var elementUsername: String="",
                var elementUpvoted:String="",
                var elementDownvoted:String="",
                var elementRequiredVote: String="",
                var elementNoComments: String=""){}


