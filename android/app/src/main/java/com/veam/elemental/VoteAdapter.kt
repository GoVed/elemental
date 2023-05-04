package com.veam.elemental

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.vote_rv.view.*
import org.json.JSONObject

import java.util.concurrent.TimeUnit
import kotlin.Exception

// val name: ArrayList<String>,val color: ArrayList<String>,val from: ArrayList<String>,val vote: ArrayList<String>,val id: ArrayList<String>,val postTime: ArrayList<String>,val userName: ArrayList<String>,val userID : ArrayList<String>,val noComments: ArrayList<String>,val needed: ArrayList<String>,val upvoted: ArrayList<String>,val downvoted: ArrayList<String>
class VoteAdapter(val voteData: ArrayList<DataVote>, val context: android.content.Context) : RecyclerView.Adapter<VoteViewHolder>() {

    val user = FirebaseAuth.getInstance().currentUser!!.uid

    var username= mutableMapOf<Int,String>()


    lateinit var localSave: SharedPreferences


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteViewHolder {
        localSave=context!!.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
        return VoteViewHolder(LayoutInflater.from(context).inflate(R.layout.vote_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return voteData.size
    }

    override fun onBindViewHolder(holder: VoteViewHolder, position: Int) {
        Log.d("checkapp","here $position")
        if(position<itemCount){ //I know that position will always be less than itemCount but when firebase acts up, the latency causes issues
            holder?.elementName?.text = voteData[position].elementName
            holder?.commentCount?.text = voteData[position].elementNoComments + " \uD83D\uDCAC"

            val drawable = holder.mainLayout.background as GradientDrawable
            drawable.setColor(Color.parseColor(voteData[position].elementColor))

            var r=Color.red(Color.parseColor(voteData[position].elementColor))
            var g=Color.green(Color.parseColor(voteData[position].elementColor))
            var b=Color.blue(Color.parseColor(voteData[position].elementColor))
            var a=Color.alpha(Color.parseColor(voteData[position].elementColor))
            var tot = r+g+b

            if(tot<381) {
                holder?.elementName?.setTextColor(Color.WHITE)
                holder?.voteCount?.setTextColor(Color.WHITE)
                holder?.commentCount?.setTextColor(Color.WHITE)

                holder?.timeTV?.setTextColor(Color.WHITE)
            }
            else{
                holder?.elementName?.setTextColor(Color.BLACK)
                holder?.voteCount?.setTextColor(Color.BLACK)
                holder?.commentCount?.setTextColor(Color.BLACK)

                holder?.timeTV?.setTextColor(Color.BLACK)

            }
            if(a<200) {
                holder?.elementName?.setTextColor(Color.BLACK)
                holder?.voteCount?.setTextColor(Color.BLACK)
                holder?.commentCount?.setTextColor(Color.BLACK)

                holder?.timeTV?.setTextColor(Color.BLACK)
            }

            if (position < itemCount)
                holder?.voteCount?.text = voteData[position].elementVote + "/${voteData[position].elementRequiredVote} ⬆⬇"



            setcombination(holder,position)
            setClickVotes(holder,position)
            setUsernameAndTime(holder,position)
            setCommentsView(holder,position)
            setVoteView(holder,position)
            setReportAndRemove(holder,position)
        }

    }

    private fun setReportAndRemove(holder: VoteViewHolder, position: Int) {
        holder.reportVote.visibility=View.GONE
        holder.removeVote.visibility=View.GONE
        holder.mainLayout.setOnLongClickListener {
            if(holder.reportVote.visibility==View.VISIBLE||holder.removeVote.visibility==View.VISIBLE){
                holder.reportVote.visibility=View.GONE
                holder.removeVote.visibility=View.GONE
            }
            else{
                if(voteData[position].elementUser==user||localSave.getString("mods/0","").toString().split(",").contains(user)) {
                    holder.removeVote.visibility=View.VISIBLE
                }
                else{
                    holder.reportVote.visibility=View.VISIBLE
                }
            }
            true
        }
        holder.reportVote.setOnClickListener {
            CustomServer.ReportVote(user,voteData[position].elementID,object :ServerListener{
                override fun runWithValue(value: String) {
                    if(value=="1"){
                        holder.reportVote.visibility=View.GONE
                        holder.removeVote.visibility=View.GONE
                        Toast.makeText(context,"Suggestion reported", Toast.LENGTH_SHORT).show()
                    }
                }
            },context!!).execute()
        }

        holder.removeVote.setOnClickListener {
            if(holder.removeVote.text.toString()=="Remove Suggestion"){
                holder.removeVote.text="Confirm ?"
            }
            else {
                CustomServer.RemoveVote(
                    user,
                    voteData[position].elementID,
                    object : ServerListener {
                        override fun runWithValue(value: String) {
                            holder.reportVote.visibility = View.GONE
                            holder.removeVote.visibility = View.GONE
                            holder.removeVote.text="Remove Suggestion"
                            if (value == "removed") {
                                holder.element1.visibility=View.GONE
                                holder.element2.visibility=View.GONE
                                holder.element3.visibility=View.GONE
                                holder.element4.visibility=View.GONE

                                holder.uvButton.visibility=View.GONE
                                holder.dvButton.visibility=View.GONE
                                holder.commentCount.visibility=View.GONE
                                holder.voteCount.text="The suggestion has been removed!"
                                Toast.makeText(
                                    context,
                                    "Suggestion removed/deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (value == "mod") {
                                Toast.makeText(context, "Mod required", Toast.LENGTH_SHORT).show()
                            }
                            if (value == "time") {
                                Toast.makeText(
                                    context,
                                    "Time limit exceeded, cannot remove your vote",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (value == "negative") {
                                Toast.makeText(
                                    context,
                                    "Cannot remove down-voted suggestions",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    context!!
                ).execute()
            }
        }
    }

    fun popupAnim(tv:TextView){
        val handler = Handler()
        var changeBy=-1
        val refreshRate = localSave.getFloat("refreshRate", 60F)
        val animSpeed=2F
        handler.postDelayed(object : Runnable {
            override fun run() {
                if(tv.alpha<=1){
                    tv.alpha+=(changeBy*animSpeed/refreshRate)
                    if(tv.alpha<0.7)
                        changeBy=1
                    handler.postDelayed(this, (1000 / refreshRate).toLong())
                }
                else{
                    tv.alpha=1F
                }
            }
        }, (1000 / refreshRate).toLong())
    }

    fun setCommentsView(holder: VoteViewHolder,position: Int){
        holder.mainLayout.setOnClickListener {
            openComments(holder,voteData[position].elementID,position)
        }
    }

    fun openComments(holder: VoteViewHolder,id:String,position: Int){

        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.comments,null)

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


        val chatRV = view.findViewById<RecyclerView>(R.id.chatRVLayout)
        val sendMsgBtn = view.findViewById<TextView>(R.id.sendChat)
        val msgET = view.findViewById<EditText>(R.id.chatMsg)

        sendMsgBtn.setOnClickListener {
            val msgByUser = msgET.text.toString().trim()
            if (msgByUser.isNotEmpty()) {
                voteData[position].elementNoComments = (voteData[position].elementNoComments.toLong() + 1).toString()
                CustomServer.AddComment(user, id, msgByUser,context!!).execute()
                holder.commentCount.text = voteData[position].elementNoComments + " \uD83D\uDCAC"
                setCommentsChats(id, chatRV,holder)
                msgET.setText("")
            }
        }

        setCommentsChats(id,chatRV,holder)


        androidx.transition.TransitionManager.beginDelayedTransition(holder.mainLayout)
        popupWindow.showAtLocation(
            holder.mainLayout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            -100 // Y offset
        )
    }

    fun setCommentsChats(id:String,chatRV:RecyclerView,holder: VoteViewHolder){
        CustomServer.GetValue("votes/$id/comments",object :ServerListener{
            override fun runWithValue(value: String) {
                val username:ArrayList<String> = ArrayList()
                val msg:ArrayList<String> = ArrayList()
                val userID:ArrayList<String> = ArrayList()
                val commentID:ArrayList<String> = ArrayList()
                if(value!="undefined") {
                    try {
                        val data = JSONObject(value)
                        val requiredUsernames: ArrayList<String> = ArrayList()
                        data.keys().forEach {
                            commentID.add(it)
                            userID.add(data.getJSONObject(it).getString("uid"))
                            if (!requiredUsernames.contains(
                                    data.getJSONObject(it).getString("uid")
                                )
                            )
                                requiredUsernames.add(data.getJSONObject(it).getString("uid"))
                        }
                        CustomServer.GetUsername(
                            requiredUsernames.joinToString(","),
                            object : ServerListener {
                                override fun runWithValue(value: String) {
                                    val usernames = JSONObject(value)
                                    data.keys().forEach {
                                        username.add(
                                            usernames.getString(
                                                data.getJSONObject(it).getString("uid")
                                            )
                                        )
                                        msg.add(data.getJSONObject(it).getString("msg"))
                                    }
                                    if (chatRV != null) {
                                        chatRV.layoutManager = LinearLayoutManager(context!!)
                                        chatRV.adapter = CommentAdapter(
                                            username,
                                            msg,
                                            userID,
                                            holder,
                                            null,
                                            id,
                                            commentID,
                                            context!!
                                        )
                                        chatRV.scrollToPosition(username.size - 1)
                                    }
                                }
                            },
                            context!!
                        ).execute()
                    }
                    catch (e:java.lang.Exception){
                        username.add("No comments yet!")
                        msg.add(" ")
                        userID.add("0")
                        commentID.add("0")
                        if(chatRV!=null){
                            chatRV.layoutManager=LinearLayoutManager(context!!)
                            chatRV.adapter=CommentAdapter(username,msg,userID,holder,null,id,commentID,context!!)
                            chatRV.scrollToPosition(username.size-1)
                        }
                        Log.e("errorLoadingComment",e.stackTrace.joinToString("\n"))
                    }
                }
                else{

                    username.add("No comments yet!")
                    msg.add(" ")
                    userID.add("0")
                    commentID.add("0")
                    if(chatRV!=null){
                        chatRV.layoutManager=LinearLayoutManager(context!!)
                        chatRV.adapter=CommentAdapter(username,msg,userID,holder,null,id,commentID,context!!)
                        chatRV.scrollToPosition(username.size-1)
                    }
                }
            }
        },context!!).execute()
    }

    private fun setUsernameAndTime(holder: VoteViewHolder, position: Int) {
        holder.timeTV.text=""
        try {
            holder.timeTV.text = "${voteData[position].elementUsername}"
        }
        catch(e:Exception) {
            holder.timeTV.text = ""
        }
        holder.timeTV.setOnClickListener {
            openProfile(holder,position)
        }
        setTime(holder, position)
    }

    fun openProfile(holder:VoteViewHolder,position:Int){
        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.profile,null)

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 25.0F
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val fade = android.transition.Fade()

            popupWindow.enterTransition = fade

            // Slide animation for popup window exit transition
            popupWindow.exitTransition = fade

        }


        val nameTV = view.findViewById<TextView>(R.id.profileName)
        val unlockedTV = view.findViewById<TextView>(R.id.profileElementsUnlocked)
        val createdTV = view.findViewById<TextView>(R.id.profileElementsCreated)
        val balanceTV = view.findViewById<TextView>(R.id.profileBalance)
        val rankTV = view.findViewById<TextView>(R.id.profileRank)
        val onlineTV = view.findViewById<TextView>(R.id.profileOnline)
        val statusTV = view.findViewById<TextView>(R.id.profileStatus)
        val reportUsername = view.findViewById<TextView>(R.id.reportUsername)
        val removeUsername = view.findViewById<TextView>(R.id.removeUsername)
        val mainLayout = view.findViewById<LinearLayout>(R.id.mainProfileLayout)

        var menuOpen=false
        var isReporting=false
        var isMod=false
        var modCheck=false

        fun toggleMenu(){
            if(!isReporting){
                if(menuOpen){
                    menuOpen=false
                    reportUsername.visibility=View.GONE
                    removeUsername.visibility=View.GONE
                }
                else{
                    menuOpen = true
                    reportUsername.visibility=View.VISIBLE
                    if (modCheck) {
                        if (isMod) {
                            reportUsername.visibility = View.GONE
                            removeUsername.visibility = View.VISIBLE
                        }
                    } else {
                        CustomServer.IsMod(
                            FirebaseAuth.getInstance().uid.toString(),
                            "0",
                            object : ServerListener {
                                override fun runWithValue(value: String) {
                                    if (value == "1") {
                                        isMod = true
                                        reportUsername.visibility = View.GONE
                                        removeUsername.visibility = View.VISIBLE
                                    }
                                    modCheck = true
                                }
                            },
                            context!!
                        ).execute()
                    }

                }
            }
        }

        reportUsername.setOnClickListener {
            if (!isReporting) {
                isReporting = true
                CustomServer.ReportUsername(
                    FirebaseAuth.getInstance().uid.toString(),
                    voteData[position].elementUser,
                    object : ServerListener {
                        override fun runWithValue(value: String) {
                            if (value == "1") {
                                isReporting = false
                                toggleMenu()
                                Toast.makeText(context,"Reported", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    context!!
                ).execute()
            }
        }

        removeUsername.setOnClickListener {
            if (!isReporting) {
                isReporting = true
                CustomServer.ResetUsername(
                    FirebaseAuth.getInstance().uid.toString(),
                    voteData[position].elementUser,
                    object : ServerListener {
                        override fun runWithValue(value: String) {
                            if (value == "1") {
                                isReporting = false
                                toggleMenu()
                                Toast.makeText(context,"Removed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    context!!
                ).execute()
            }
        }

        mainLayout.setOnLongClickListener {
            toggleMenu()
            true
        }
        nameTV.text = voteData[position].elementUsername
        CustomServer.GetProfile(voteData[position].elementUser,object :ServerListener{
            override fun runWithValue(value: String) {
                try{
                    val data=JSONObject(value)
                    statusTV.text = data.getString("status")
                    unlockedTV.text = "${data.getString("unlocked")}"
                    rankTV.text = "${data.getString("rank")}"
                    createdTV.text = "${data.getString("elementsCreated")}"
                    balanceTV.text = "${data.getString("bal")}"
                    if(data.getString("online")=="1")
                        onlineTV.text = "Online"
                    else{
                        onlineTV.text=onlineTV.text.toString()+"\n\n"
                        if(data.getString("lastOnline")!="0") {
                            val diff =
                                System.currentTimeMillis() - data.getString("lastOnline").toLong()
                            if (diff < 5000) {
                                onlineTV.text = " A few seconds ago"
                            } else if (diff < 60000) {
                                onlineTV.text =
                                    TimeUnit.MILLISECONDS.toSeconds(
                                        diff
                                    ).toString() + " seconds ago"
                            } else if (diff < 3600000) {
                                onlineTV.text =
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        diff
                                    ).toString() + " minutes ago"
                            } else if (diff < 86400000) {
                                onlineTV.text =
                                    TimeUnit.MILLISECONDS.toHours(
                                        diff
                                    ).toString() + " hours ago"
                            } else {
                                var daysAgo = TimeUnit.MILLISECONDS.toDays(diff)
                                if (daysAgo < 30) {
                                    onlineTV.text =
                                        daysAgo.toString() + " days ago"
                                } else if (daysAgo < 360) {
                                    onlineTV.text =
                                        (daysAgo / 30).toString() + " months and " + (daysAgo % 30).toString() + " days ago"
                                } else {
                                    onlineTV.text =
                                        (daysAgo / 360).toString() + " years, " + ((daysAgo % 360) / 30).toString() + " months" + ((daysAgo % 360) % 30).toString() + " ago"
                                }
                            }
                        }
                        else
                            onlineTV.text = "Offline"
                    }
                }catch (e:Exception){
                    Log.e("errorDisplayingProfile","${e.message}")
                }

            }
        },context!!).execute()


        androidx.transition.TransitionManager.beginDelayedTransition(holder.mainLayout)
        popupWindow.showAtLocation(
            holder.mainLayout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            -100 // Y offset
        )
    }

    fun setTime(holder: VoteViewHolder, position: Int) {

        val diff= System.currentTimeMillis()-voteData[position].elementTime.toLong()
        if (diff<5000){
            holder.timeTV.text = holder.timeTV.text.toString()+" (A few seconds ago)"
        }
        else if (diff<60000){
            holder.timeTV.text = holder.timeTV.text.toString()+" ("+TimeUnit.MILLISECONDS.toSeconds(diff).toString() + " seconds ago)"
        }

        else if (diff<3600000){
            holder.timeTV.text = holder.timeTV.text.toString()+" ("+TimeUnit.MILLISECONDS.toMinutes(diff).toString() + " minutes ago)"
        }

        else if (diff<86400000){
            holder.timeTV.text = holder.timeTV.text.toString()+" ("+TimeUnit.MILLISECONDS.toHours(diff).toString() + " hours ago)"
        }
        else{
            var daysAgo=TimeUnit.MILLISECONDS.toDays(diff)
            if(daysAgo<30){
                holder.timeTV.text = holder.timeTV.text.toString()+" ("+daysAgo.toString() + " days ago)"
            }
            else if(daysAgo<360){
                holder.timeTV.text = holder.timeTV.text.toString()+" ("+(daysAgo/30).toString() + " months and " + (daysAgo%30).toString() + " days ago)"
            }
            else{
                holder.timeTV.text = holder.timeTV.text.toString()+" ("+(daysAgo/360).toString() + " years, " + ((daysAgo%360)/30).toString() + " months"+((daysAgo%360)%30).toString()+" ago)"
            }
        }
    }

    fun setVoteView(holder: VoteViewHolder,position: Int){

        var upvotedUser=voteData[position].elementUpvoted.split(",")
        var downvotedUser=voteData[position].elementDownvoted.split(",")
        if (upvotedUser.contains(user)) {
            holder.uvButton.setTextColor(Color.GREEN)

        } else if (downvotedUser.contains(user)) {
            holder.dvButton.setTextColor(Color.RED)

        } else {
            holder.uvButton.setTextColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.text,
                    context.theme
                )
            )
            holder.dvButton.setTextColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.text,
                    context.theme
                )
            )

        }


    }




    fun setClickVotes(holder: VoteViewHolder, position: Int) {

        setVoteView(holder,position)
        var upvotedUser=ArrayList(voteData[position].elementUpvoted.split(","))
        var downvotedUser=ArrayList(voteData[position].elementDownvoted.split(","))


        holder.uvButton.setOnClickListener {
            popupAnim(holder.uvButton)
            if(upvotedUser.contains(user)==false){
                holder.uvButton.setTextColor(Color.CYAN)
                holder.dvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
            }
            else{
                holder.uvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
                holder.dvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
            }
            CustomServer.Upvote(user,voteData[position].elementID,object :ServerListener{
                override fun runWithValue(value: String) {
                    if(value=="0"){
                        holder.uvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
                        holder.dvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))

                        upvotedUser.remove(user)
                        downvotedUser.remove(user)



                        if (position < itemCount){
                            holder?.voteCount?.text = "${voteData[position].elementVote.toLong()}/${voteData[position].elementRequiredVote} ⬆⬇"
                            }
                    }
                    if(value=="1"){
                        holder.uvButton.setTextColor(Color.GREEN)
                        holder.dvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))

                        upvotedUser.add(user)

                        if (position < itemCount)
                            holder?.voteCount?.text = "${voteData[position].elementVote.toLong()+1}/${voteData[position].elementRequiredVote} ⬆⬇"
                    }
                    if(value=="2"){
                        holder.element1.visibility=View.GONE
                        holder.element2.visibility=View.GONE
                        holder.element3.visibility=View.GONE
                        holder.element4.visibility=View.GONE

                        holder.uvButton.visibility=View.GONE
                        holder.dvButton.visibility=View.GONE
                        holder.commentCount.visibility=View.GONE
                        holder.voteCount.text="The Element has been added"
                    }

                    voteData[position].elementUpvoted=upvotedUser.joinToString(",")
                    voteData[position].elementDownvoted=downvotedUser.joinToString(",")
                }
            },context!!).execute()
        }

        holder.dvButton.setOnClickListener {
            popupAnim(holder.dvButton)
            if(downvotedUser.contains(user)==false){
                holder.dvButton.setTextColor(Color.MAGENTA)
                holder.uvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
            }
            else{
                holder.uvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
                holder.dvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
            }
            CustomServer.Downvote(user,voteData[position].elementID,object :ServerListener{
                override fun runWithValue(value: String) {
                    if(value=="0"){
                        holder.uvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
                        holder.dvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
                        upvotedUser.remove(user)
                        downvotedUser.remove(user)

                        if (position < itemCount)
                            holder?.voteCount?.text = "${voteData[position].elementVote.toLong()}/${voteData[position].elementRequiredVote} ⬆⬇"
                    }
                    if(value=="-1"){
                        holder.dvButton.setTextColor(Color.RED)
                        holder.uvButton.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
                        downvotedUser.add(user)

                        if (position < itemCount)
                            holder?.voteCount?.text = "${voteData[position].elementVote.toLong()-1}/${(voteData[position].elementRequiredVote.toLong()/-2).toLong()} ⬆⬇"
                    }
                    if(value=="-2"){
                        holder.element1.visibility=View.GONE
                        holder.element2.visibility=View.GONE
                        holder.element3.visibility=View.GONE
                        holder.element4.visibility=View.GONE

                        holder.uvButton.visibility=View.GONE
                        holder.dvButton.visibility=View.GONE
                        holder.commentCount.visibility=View.GONE
                        holder.voteCount.text="The Element has been removed"
                    }

                    voteData[position].elementUpvoted=upvotedUser.joinToString(",")
                    voteData[position].elementDownvoted=downvotedUser.joinToString(",")
                }
            },context!!).execute()
        }

    }

    fun setcombination(holder: VoteViewHolder,position: Int) {
        var combineElements: ArrayList<String> = ArrayList()
        combineElements=ArrayList(voteData[position].elementFrom.split(","))
        setTV(combineElements[0],holder.element1)
        setTV(combineElements[1],holder.element2)

        if(combineElements.size>2) {
            setTV(combineElements[2],holder.element3)
        }
        else{
            holder.element3.visibility=View.GONE

        }
        if(combineElements.size>3) {
            setTV(combineElements[3],holder.element4)
        }
        else{
            holder.element4.visibility=View.GONE

        }


    }



    fun setTV(name:String,tv: TextView) {
        var color="#FFFFFFFF"

        color=localSave.getString("elements/$name/color","#55555555").toString()
        tv?.setText(localSave.getString("elements/$name/name","Failed to load").toString())

        var shape: GradientDrawable = GradientDrawable()
        shape.cornerRadius=32F
        shape.setColor(Color.parseColor(color))
        var r=Color.red(Color.parseColor(color))
        var g=Color.green(Color.parseColor(color))
        var b=Color.blue(Color.parseColor(color))
        var a=Color.alpha(Color.parseColor(color))
        var tot = r+g+b
        tv?.background=shape
        if(tot<381)
            tv?.setTextColor(Color.WHITE)
        else
            tv?.setTextColor(Color.BLACK)
        if(a<200)
            tv?.setTextColor(Color.BLACK)
    }


}
class VoteViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val elementName = view.elementNameTV
    val element1 = view.element1
    val element2 = view.element2
    val element3 = view.element3
    val element4 = view.element4

    val voteCount = view.votes
    val commentCount = view.comments
    val mainLayout = view.mainVoteLayout
    val uvButton=view.upvote
    val dvButton=view.downvote
    val timeTV = view.timeTV
    val reportVote = view.reportVote
    val removeVote = view.removeVote
}


