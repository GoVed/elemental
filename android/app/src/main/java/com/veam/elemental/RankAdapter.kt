package com.veam.elemental

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.rank_rv.view.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RankAdapter(val rank: ArrayList<Long>,val name: ArrayList<String>, val unlocked: ArrayList<Long>,val id:ArrayList<String>,val online:ArrayList<Boolean>, val context: android.content.Context) : RecyclerView.Adapter<RankHolder>() {
    val user = FirebaseAuth.getInstance().currentUser!!.uid
    var localSave: SharedPreferences =context!!.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RankHolder {
        return RankHolder(LayoutInflater.from(context).inflate(R.layout.rank_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return name.size
    }

    override fun onBindViewHolder(holder: RankHolder, position: Int) {
        if(System.currentTimeMillis()-localSave.getLong("rv/leaderboard/setScroll",0)<200) {
            val handler = Handler()
            holder?.mainLayout?.alpha = 0F
            val refreshRate = localSave.getFloat("refreshRate", 60F)
            val animSpeed = 2F
            handler.postDelayed(object : Runnable {
                override fun run() {
                    holder?.mainLayout?.alpha =
                        holder?.mainLayout?.alpha?.plus(animSpeed / refreshRate)!!
                    if (holder?.mainLayout?.alpha < 1)
                        handler.postDelayed(this, (1000 / refreshRate).toLong())
                }
            }, (1000 / refreshRate).toLong())
        }
        holder.rankTV.text = rank[position].toString()
        holder.nameTV.text = name[position]
        holder.unlockedTV.text = unlocked[position].toString()

        if(online[position])
            holder.onlineStatus.visibility = View.VISIBLE
        else
            holder.onlineStatus.visibility = View.INVISIBLE

        if(rank[position]==1.toLong()) {

            holder.mainLayout.setBackgroundColor(Color.rgb(255, 215, 0))
            holder.rankTV.setTextColor(Color.BLACK)
            holder.nameTV.setTextColor(Color.BLACK)
            holder.unlockedTV.setTextColor(Color.BLACK)
        }
        else if(rank[position]==2.toLong()) {


            holder.mainLayout.setBackgroundColor(Color.rgb(192, 192, 192))
            holder.rankTV.setTextColor(Color.BLACK)
            holder.nameTV.setTextColor(Color.BLACK)
            holder.unlockedTV.setTextColor(Color.BLACK)
        }
        else if(rank[position]==3.toLong()) {

            holder.mainLayout.setBackgroundColor(Color.rgb(205, 127, 50))
        }
        else{
            holder.mainLayout.setBackgroundColor(Color.alpha(0))
            holder.rankTV.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
            holder.nameTV.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
            holder.unlockedTV.setTextColor(ResourcesCompat.getColor(context.resources, R.color.text, context.theme))
        }

        holder.mainLayout.setOnClickListener {
            openProfile(holder,position)
        }
    }

    fun openProfile(holder:RankHolder,position:Int){
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


        nameTV.text = name[position]
        unlockedTV.text = "${unlocked[position]}"
        CustomServer.GetProfile(id[position],object :ServerListener{
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
}
class RankHolder (view: View) : RecyclerView.ViewHolder(view) {
    val rankTV=view.rank
    val nameTV=view.profileName
    val unlockedTV=view.unlocked
    val mainLayout=view.mainRankLayout
    val onlineStatus=view.onlineStatus
}
