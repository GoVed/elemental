package com.veam.elemental


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.log_rv.view.*
import java.util.concurrent.TimeUnit
import kotlin.math.log


class LogsAdapter(val type: ArrayList<String>,val add: ArrayList<String>,val logId: ArrayList<String>,val info: ArrayList<String>, val context: android.content.Context) : RecyclerView.Adapter<VoteLogHolder>() {
    val user = FirebaseAuth.getInstance().currentUser!!.uid
    var localSave: SharedPreferences=context!!.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
    var loadingUserID: ArrayList<String> = ArrayList()
    var userNames: MutableMap<String,String> = mutableMapOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteLogHolder {
        return VoteLogHolder(LayoutInflater.from(context).inflate(R.layout.log_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return type.size
    }

    override fun onBindViewHolder(holder: VoteLogHolder, position: Int) {

        if(System.currentTimeMillis()-localSave.getLong("rv/logs/setScroll",0)<200) {
            val handler = Handler()
            holder?.layout?.alpha = 0F
            val refreshRate = localSave.getFloat("refreshRate", 60F)
            val animSpeed = 2F
            handler.postDelayed(object : Runnable {
                override fun run() {
                    holder?.layout?.alpha =
                        holder?.layout?.alpha?.plus(animSpeed / refreshRate)!!
                    if (holder?.layout?.alpha < 1)
                        handler.postDelayed(this, (1000 / refreshRate).toLong())
                }
            }, (1000 / refreshRate).toLong())
        }
        if(add[position]!="+0"&&add[position]!="0"&&add[position]!="-0") {
            holder.coinTV.text = add[position] + " \uD83D\uDCB5"
            holder.coinTV.visibility=View.VISIBLE
            holder.title.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_topleft_dark_gray, null)
        }
        else{
            holder.title.background = ResourcesCompat.getDrawable(context.resources, R.drawable.rounded_top_dark_gray, null)
            holder.coinTV.visibility=View.GONE
        }
        var timeDiff=""
        if(logId[position].toLong()>100000){
            val diff= System.currentTimeMillis()-logId[position].toLong()
            if (diff<5000){
                timeDiff = " (A few seconds ago)"
            }
            else if (diff<60000){
                timeDiff = " ("+ TimeUnit.MILLISECONDS.toSeconds(diff).toString() + " seconds ago)"
            }

            else if (diff<3600000){
                timeDiff = " ("+ TimeUnit.MILLISECONDS.toMinutes(diff).toString() + " minutes ago)"
            }

            else if (diff<86400000){
                timeDiff = " ("+ TimeUnit.MILLISECONDS.toHours(diff).toString() + " hours ago)"
            }
            else{
                var daysAgo= TimeUnit.MILLISECONDS.toDays(diff)
                if(daysAgo<30){
                    timeDiff = " ("+daysAgo.toString() + " days ago)"
                }
                else if(daysAgo<360){
                    timeDiff = " ("+(daysAgo/30).toString() + " months and " + (daysAgo%30).toString() + " days ago)"
                }
                else{
                    timeDiff = " ("+(daysAgo/360).toString() + " years, " + ((daysAgo%360)/30).toString() + " months"+((daysAgo%360)%30).toString()+" ago)"
                }
            }
        }


        if(type[position]=="0"){
            if(info[position]=="Welcome to the game!"){
                holder.title.text = "Welcome User!"

                holder.msgTV.text = "Welcome, to Elemental\n" +
                        "Here is a starting gift for you!\n\n" +
                        "This game is all about unlocking new elements by combining the elements you already have.\n" +
                        "At the start, you are given 4 basic elements Fire, Water, Air and Earth.\n" +
                        "Start your journey in elemental by combining this element.\n" +
                        "Suppose when you are combining but no such combination exists. " +
                        "You could create your suggestion and let other people review it.\n" +
                        "If your suggestion get enough vote by other player then it would be added to the game.\n" +
                        "You could vote other's suggestion by Upvoting(˄) or Downvoting(˅) the vote \n\n" +
                        "How to play?\n" +
                        "In COMBINE(⌂) section, you could combine different elements to make new ones.\n" +
                        "In VOTE(˄˅) section, you could create a new element or vote others.\n\n" +
                        "Tips:\n" +
                        "Its better to set your username set in SETTINGS section.\n" +
                        "Also, please don't give inappropriate names of element or in your username.\n\n" +
                        "Coin System:\n" +
                        "Every element you discover, you get "+ localSave.getInt("price/elementUnlocked",3)+" \uD83D\uDCB5 \n" +
                        "To create a new Vote, you need "+ localSave.getInt("price/newVote",15)+" \uD83D\uDCB5 \n" +
                        "If your new Element gets enough vote it will be added to the game and you will be rewarded with 5 \uD83D\uDCB5 more than that\n\n" +
                        "That's all, enjoy playing"
            }
            else{

                if(info[position].indexOf("u/")!=-1){
                    var userID = info[position].substring(info[position].indexOf("u/")+2)
                    val after = userID.substring(userID.indexOf("/")+1)
                    userID = userID.substring(0,userID.indexOf("/"))
                    if(!loadingUserID.contains(userID)) {
                        if(userNames[userID]==null) {
                            loadingUserID.add(userID)
                            CustomServer.GetValue("users/$userID/name", object : ServerListener {
                                override fun runWithValue(value: String) {
                                    Log.d(
                                        "checkvals",
                                        "hmm ${info[position].substring(
                                            0,
                                            info[position].indexOf("u/")
                                        ) + " then " + value + " then " + after}"
                                    )
                                    val newInfo = info[position].substring(
                                        0,
                                        info[position].indexOf("u/")
                                    ) + value + after
                                    Log.d("checkvalnew", "hmm $newInfo")
                                    if (newInfo.indexOf(";") == -1) {
                                        holder.title.text = "Message"
                                        holder.msgTV.text = newInfo + " " + timeDiff
                                    } else {
                                        holder.title.text =
                                            newInfo.substring(0, newInfo.indexOf(";"))
                                        holder.msgTV.text =
                                            newInfo.substring(newInfo.indexOf(";") + 1) + " " + timeDiff
                                    }
                                    userNames[userID] = value
                                    loadingUserID.remove(userID)
                                }
                            }, context!!).execute()
                        }
                        else{
                            val newInfo = info[position].substring(
                                0,
                                info[position].indexOf("u/")
                            ) + userNames[userID] + after
                            Log.d("checkvalnew", "hmm $newInfo")
                            if (newInfo.indexOf(";") == -1) {
                                holder.title.text = "Message"
                                holder.msgTV.text = newInfo + " " + timeDiff
                            } else {
                                holder.title.text =
                                    newInfo.substring(0, newInfo.indexOf(";"))
                                holder.msgTV.text =
                                    newInfo.substring(newInfo.indexOf(";") + 1) + " " + timeDiff
                            }
                        }
                    }
                }
                else {
                    if (info[position].indexOf(";") == -1) {
                        holder.title.text = "Message"
                        holder.msgTV.text = info[position] + " " + timeDiff
                    } else {
                        holder.title.text = info[position].substring(0, info[position].indexOf(";"))
                        holder.msgTV.text =
                            info[position].substring(info[position].indexOf(";") + 1) + " " + timeDiff
                    }
                }
            }



        }

        if(type[position]=="1"){
            holder.title.text = "Element Unlocked!"
            holder.msgTV.text = "Congratulations, you discovered "+localSave.getString("elements/"+info[position]+"/name","") + " " + timeDiff
        }
        if(type[position]=="2"){
            holder.title.text = "New Element vote created"
            holder.msgTV.text = "Your vote of element \""+info[position]+"\" is live! " + timeDiff
        }

        if(type[position]=="3"){
            holder.title.text = "Your new element got added!"
            holder.msgTV.text = "Your new element \""+info[position]+"\" got enough votes, it is added to game. "+timeDiff
        }

        if(type[position]=="4"){
            holder.title.text = "Used Hint!"
            holder.msgTV.text = "Hint : ${info[position]}"+timeDiff
        }

    }


}
class VoteLogHolder (view: View) : RecyclerView.ViewHolder(view) {
    val title=view.title
    val msgTV=view.msg
    val coinTV=view.coin
    val layout=view.mainLogLayout
}


