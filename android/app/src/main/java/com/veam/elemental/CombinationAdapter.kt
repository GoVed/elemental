package com.veam.elemental

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.combination_rv.view.*


class CombinationAdapter(val combinationList:ArrayList<String>, val context: android.content.Context) : RecyclerView.Adapter<CombinationHolder>() {

    lateinit var localSave: SharedPreferences

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):CombinationHolder {
        localSave=context!!.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
        return CombinationHolder(LayoutInflater.from(context).inflate(R.layout.combination_rv, parent, false))
    }

    override fun getItemCount(): Int {
        return combinationList.size
    }

    override fun onBindViewHolder(holder: CombinationHolder, position: Int) {
        holder.titleTV.text = localSave.getString("elements/${combinationList[position]}/name","Failed to load")
        var combinations=localSave.getString("elements/${combinationList[position]}/combinationID","").toString().split(",")

        var shape: GradientDrawable = GradientDrawable()
        shape.cornerRadius=24F
        shape.setColor(Color.parseColor(localSave.getString("elements/${combinationList[position]}/color","#AAAAAAAA")))
        var r=Color.red(Color.parseColor(localSave.getString("elements/${combinationList[position]}/color","#AAAAAAAA")))
        var g=Color.green(Color.parseColor(localSave.getString("elements/${combinationList[position]}/color","#AAAAAAAA")))
        var b=Color.blue(Color.parseColor(localSave.getString("elements/${combinationList[position]}/color","#AAAAAAAA")))
        var a=Color.alpha(Color.parseColor(localSave.getString("elements/${combinationList[position]}/color","#AAAAAAAA")))
        var tot = r+g+b
        holder.rootLayout?.background=shape
        if(tot<381) {
            holder.titleTV?.setTextColor(Color.WHITE)
        }
        else{
            holder.titleTV?.setTextColor(Color.BLACK)

        }
        if(a<200) {
            holder.titleTV?.setTextColor(Color.BLACK)
        }
        holder.element2TV.visibility=View.GONE
        holder.element3TV.visibility=View.GONE
        holder.element4TV.visibility=View.GONE
        if(combinations.size>0){
            if(combinations[0]!="") {
                holder.element1TV.text = localSave.getString("elements/${combinations[0]}/name", "Failed to load")
                setColor(holder.element1TV, localSave.getString("elements/${combinations[0]}/color", "#AAAAAAAA").toString())
            }
            else{
                holder.element1TV.text = "Basic Element"
                setColor(holder.element1TV,"#AAAAAAAA")
            }

        }
        if(combinations.size>1){
            holder.element2TV.visibility=View.VISIBLE
            holder.element2TV.text=localSave.getString("elements/${combinations[1]}/name","Failed to load")
            setColor(holder.element2TV,localSave.getString("elements/${combinations[1]}/color","#AAAAAAAA").toString())
        }
        if(combinations.size>2){
            holder.element3TV.visibility=View.VISIBLE
            holder.element3TV.text=localSave.getString("elements/${combinations[2]}/name","Failed to load")
            setColor(holder.element3TV,localSave.getString("elements/${combinations[2]}/color","#AAAAAAAA").toString())
        }
        if(combinations.size>3){
            holder.element4TV.visibility=View.VISIBLE
            holder.element4TV.text=localSave.getString("elements/${combinations[3]}/name","Failed to load")
            setColor(holder.element4TV,localSave.getString("elements/${combinations[3]}/color","#AAAAAAAA").toString())
        }
    }

    fun setColor(tv:TextView,color:String){
        var shape: GradientDrawable = GradientDrawable()
        shape.cornerRadius=24F

        shape.setColor(Color.parseColor(color))
        var r=Color.red(Color.parseColor(color))
        var g=Color.green(Color.parseColor(color))
        var b=Color.blue(Color.parseColor(color))
        var a=Color.alpha(Color.parseColor(color))
        var tot = r+g+b
        tv.background=shape
        if(tot<381) {
            tv.setTextColor(Color.WHITE)
        }
        else{
            tv.setTextColor(Color.BLACK)
        }
        if(a<200) {
            tv.setTextColor(Color.BLACK)
        }
    }


}
class CombinationHolder (view: View) : RecyclerView.ViewHolder(view) {
    val titleTV = view.makes
    val element1TV=view.element1
    val element2TV=view.element2
    val element3TV=view.element3
    val element4TV=view.element4
    val rootLayout = view.mainCombinationView
}
