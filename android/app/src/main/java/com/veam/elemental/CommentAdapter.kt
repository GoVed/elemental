package com.veam.elemental

import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.comment_rv.view.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class CommentAdapter(val name: ArrayList<String>,val msg: ArrayList<String>,val id: ArrayList<String>,val parentHolderVote:VoteViewHolder?,val parentHolderCombine:FrameLayout?,val pID:String,val commentID:ArrayList<String>, val context: android.content.Context) : RecyclerView.Adapter<CommentHolder>() {

    var menuOpen:Boolean=false
    var isMod=false
    var modCheck=false
    var isReporting=false
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):CommentHolder {
        return CommentHolder(LayoutInflater.from(context).inflate(R.layout.comment_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return name.size
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.userTV.text = name[position]
        holder.msgTV.text = msg[position]
        holder.userTV.setOnClickListener {
            if(id[position]!="0") {
                parentHolderVote?.let { it1 -> openProfile(it1, position) }
                parentHolderCombine?.let { it1 -> openProfile(it1, position) }
            }
        }
        holder.userTV.setOnLongClickListener {
            toggleMenu(holder,position)
            true
        }
        holder.mainLayout.setOnLongClickListener{
            toggleMenu(holder,position)
            true
        }
        setReportAndRemoveChat(holder,position)
    }

    fun setReportAndRemoveChat(holder: CommentHolder, position: Int) {
        holder.reportChat.setOnClickListener {
            if(!isReporting) {
                isReporting = true
                if (parentHolderVote != null) {
                    CustomServer.ReportVoteComment(
                        FirebaseAuth.getInstance().uid.toString(),
                        pID,
                        commentID[position],
                        object : ServerListener {
                            override fun runWithValue(value: String) {
                                if (value == "1") {
                                    isReporting=false
                                    toggleMenu(holder, position)
                                    Toast.makeText(context,"Reported", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        context!!
                    ).execute()
                }
                if (parentHolderCombine != null) {
                    CustomServer.ReportElementComment(
                        FirebaseAuth.getInstance().uid.toString(),
                        pID,
                        commentID[position],
                        object : ServerListener {
                            override fun runWithValue(value: String) {
                                if (value == "1") {
                                    isReporting=false
                                    toggleMenu(holder, position)
                                    Toast.makeText(context,"Reported", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        context!!
                    ).execute()
                }
            }
        }

        holder.deleteChat.setOnClickListener {
            if(!isReporting) {
                isReporting = true
                if (parentHolderVote != null) {
                    CustomServer.RemoveVoteComment(
                        FirebaseAuth.getInstance().uid.toString(),
                        pID,
                        commentID[position],
                        object : ServerListener {
                            override fun runWithValue(value: String) {
                                if (value == "1") {
                                    isReporting=false
                                    toggleMenu(holder, position)
                                    Toast.makeText(context,"Removed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        context!!
                    ).execute()
                }
                if (parentHolderCombine != null) {
                    CustomServer.RemoveElementComment(
                        FirebaseAuth.getInstance().uid.toString(),
                        pID,
                        commentID[position],
                        object : ServerListener {
                            override fun runWithValue(value: String) {
                                if (value == "1") {
                                    isReporting=false
                                    toggleMenu(holder, position)
                                    Toast.makeText(context,"Removed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        context!!
                    ).execute()
                }
            }
        }
    }

    fun toggleMenu(holder: CommentHolder,position: Int){
        if(!isReporting) {
            if (menuOpen) {
                menuOpen = false
                holder.reportChat.visibility = View.GONE
                holder.deleteChat.visibility = View.GONE
            } else {
                menuOpen = true
                if (id[position] == FirebaseAuth.getInstance().uid)
                    holder.deleteChat.visibility = View.VISIBLE
                else {
                    holder.reportChat.visibility = View.VISIBLE
                    if (modCheck) {
                        if (isMod) {
                            holder.reportChat.visibility = View.GONE
                            holder.deleteChat.visibility = View.VISIBLE
                        }
                    } else {
                        CustomServer.IsMod(
                            FirebaseAuth.getInstance().uid.toString(),
                            "0",
                            object : ServerListener {
                                override fun runWithValue(value: String) {
                                    if (value == "1") {
                                        isMod = true
                                        holder.reportChat.visibility = View.GONE
                                        holder.deleteChat.visibility = View.VISIBLE
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
    }

    fun openProfile(holder:FrameLayout,position:Int){
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
                    id[position],
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
                    id[position],
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

        CustomServer.GetProfile(id[position],object :ServerListener{
            override fun runWithValue(value: String) {
                try{
                    val data= JSONObject(value)
                    nameTV.text = data.getString("name")
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


        androidx.transition.TransitionManager.beginDelayedTransition(holder)
        popupWindow.showAtLocation(
            holder, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            -100 // Y offset
        )
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
                    id[position],
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
                    id[position],
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

        CustomServer.GetProfile(id[position],object :ServerListener{
            override fun runWithValue(value: String) {
                try{
                    val data= JSONObject(value)
                    nameTV.text = data.getString("name")
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


}
class CommentHolder (view: View) : RecyclerView.ViewHolder(view) {
    val mainLayout = view.commentRVMainLayout
    val userTV = view.senderName
    val msgTV = view.msg
    val reportChat = view.reportChat
    val deleteChat = view.deleteChat
}
