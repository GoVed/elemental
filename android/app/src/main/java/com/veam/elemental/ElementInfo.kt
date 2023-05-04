package com.veam.elemental

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.fragment_combine.view.*
import java.util.*
import kotlin.collections.ArrayList

class ElementInfo(context: Context):View(context){

    var elementID=-1
    var drawElementID=-1

    var h:Int=300
    var w:Int=300

    lateinit var gCanvas:Canvas

    var sthap=false
    
    lateinit var localSave: SharedPreferences

    var elementsUnlockedID:ArrayList<Int> = ArrayList()
    var childs:ArrayList<Int> = ArrayList()
    

    var bounds=arrayOf(arrayOf(0f,0f),arrayOf(0f,0f),arrayOf(0f,0f),arrayOf(0f,0f),arrayOf(0f,0f))

    var stackID:Stack<Int> = Stack()
    var stackPos:Stack<Array<Float>> = Stack()
    var stackFocusID:Stack<Int> = Stack()

    var isAnim=false
    var animTitleOffset = arrayOf(0f,0f)
    var animOpacity=1f
    var animElementOffset=arrayOf(0f,0f)
    var animFocusElement=0

    fun setElementInfo(elementID:Int){
        this.elementID=elementID
        drawElementID=elementID
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event!=null){
            if(event.action==MotionEvent.ACTION_UP&&!isAnim) {
                if (event.y > bounds[0][0] && event.y < bounds[0][1]) {
                    goBack()
                    invalidate()
                }
                var i = 1
                for (each in childs) {
                    if (event.y > bounds[i][0] && event.y < bounds[i][1]) {
                        stackID.push(drawElementID)
                        stackPos.push(bounds[i])
                        stackFocusID.push(i)
                        selectElement(i)

                        invalidate()
                        break
                    }
                    i++
                }
            }
        }


        return true
    }


    override fun onDraw(canvas: Canvas?) {

        if(canvas != null) {
            localSave = context.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
            h=width
            w=height
            setTitle(localSave.getString("elements/$drawElementID/name","Loading...").toString(),localSave.getString("elements/$drawElementID/color","Loading...").toString(),canvas)
            if(localSave.getString("combinationMakesToFrom/$drawElementID","")==""){
                elementsUnlockedID.clear()
                elementsUnlockedID.addAll(localSave.getString("unlockedElements","0,1,2,3")!!.split(",").map { it.toInt() })
                var i=0
                while(i<localSave.getLong("totalCombinations",0)&&drawElementID>3) {
                    if (localSave.getString("combinations/$i/makes", "0").toString().toInt() == drawElementID) {
                        var flag=true
                        for(each in localSave.getString("combinations/$i/from", "0").toString().split(",").map { it.toInt() }){
                            if(!elementsUnlockedID.contains(each))
                                flag=false
                        }
                        if(flag)
                            break
                    }
                    i++
                }
                childs.clear()
                localSave.edit().putString("combinationMakesToFrom/$drawElementID",localSave.getString("combinations/$i/from", "0").toString()).apply()
                childs.addAll(localSave.getString("combinations/$i/from", "0").toString().split(",").map { it.toInt() })
            }
            else{
                childs.clear()
                childs.addAll(localSave.getString("combinationMakesToFrom/$drawElementID", "0").toString().split(",").map { it.toInt() })
            }
            var i=1
            if(drawElementID>3) {
                for (each in childs) {
                    setElement(i, localSave.getString("elements/$each/name", "Loading...").toString(), localSave.getString("elements/$each/color", "Loading...").toString(), canvas)
                    i++
                }
                drawLines(canvas)
            }
            else {
                childs.clear()
                childs.add(-1)
                setElement(1, "Basic Element", "#555555", canvas)
                drawLines(canvas)
                childs.clear()
            }


            if(!sthap) {

                sthap=true
            }


        }
    }

    fun selectElement(id:Int){

        animOpacity=1f
        var mul=-1
        val diff=16-stackPos.peek()[0]
        animFocusElement=id
        isAnim=true

        handler.post(object :Runnable{
            override fun run() {
                Log.d("checkAlpha","$animOpacity")
                animOpacity+=4f/60f*mul
                if(mul==-1) {
                    animElementOffset[0] -= 32 * (4f / 60f)
                    animElementOffset[1] += diff * (4f / 60f)
                }
                invalidate()
                if(animOpacity<=0f) {
                    animOpacity = 0f
                    animElementOffset= arrayOf(0f,0f)
                    mul=1
                    animFocusElement=0
                    drawElementID=childs[id - 1]

                }
                if(animOpacity<=1) {
                    handler.postDelayed(this, (1000 / localSave.getFloat("refreshRate", 60F)).toLong())
                }
                else{
                    isAnim=false
                    animOpacity = 1f
                }
            }
        })



    }

    fun goBack(){
        if(elementID!=drawElementID){
            animOpacity=1f
            var mul=-1
            val diff=stackPos.pop()[0]-16
            animFocusElement=0
            isAnim=true
            handler.post(object :Runnable{
                override fun run() {
                    Log.d("checkAlpha","$animOpacity")
                    animOpacity+=4f/60f*mul
                    if(mul==-1) {
                        animElementOffset[0] += 32 * (4f / 60f)
                        animElementOffset[1] += diff * (4f / 60f)
                    }
                    invalidate()
                    if(animOpacity<=0f) {
                        animOpacity = 0f
                        animElementOffset= arrayOf(0f,0f)
                        mul=1
                        animFocusElement=stackFocusID.pop()
                        drawElementID=stackID.pop()

                    }
                    if(animOpacity<=1) {
                        handler.postDelayed(this, (1000 / localSave.getFloat("refreshRate", 60F)).toLong())
                    }
                    else{
                        isAnim=false
                        animOpacity = 1f
                    }
                }
            })

        }
    }

    fun setTitle(name:String,colorBG:String,canvas: Canvas){
        val textPaint = Paint(ANTI_ALIAS_FLAG).apply { textSize = 30F }
        bounds[0]=setElement(16f,16f,name,colorBG,canvas,0)
    }

    fun setElement(id:Int,name:String,colorBG:String,canvas: Canvas){
        val textPaint = Paint(ANTI_ALIAS_FLAG).apply { textSize = 30F }
        bounds[id]=setElement(48f,bounds[id-1][1]+8,name,colorBG,canvas,id)
    }


    fun setElement(left:Float,top:Float,name:String,colorBG:String,canvas: Canvas,id: Int): Array<Float> {


        val path = Path()
        val paint = Paint().apply {
            color = Color.parseColor(colorBG)
            style = Paint.Style.FILL
        }



        val r=Color.red(Color.parseColor(colorBG))
        val g=Color.green(Color.parseColor(colorBG))
        val b=Color.blue(Color.parseColor(colorBG))
        val a=Color.alpha(Color.parseColor(colorBG))

        val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = if(r<150&&g<150&&b<150)
                Color.WHITE
            else
                Color.BLACK
            if(a<150)
                color = Color.BLACK
            textSize = 30F
        }

        if(animFocusElement!=id)
            paint.alpha=(paint.alpha*animOpacity).toInt()
        if(animFocusElement!=id)
            textPaint.alpha=(textPaint.alpha*animOpacity).toInt()
        val radii = floatArrayOf(
            10f, 10f, //Top left corner
            10f, 10f, //Top right corner
            10f, 10f,   //Bottom right corner
            10f, 10f   //Bottom left corner
        )

        path.reset() //Clears the previously set path
        var leftB=left
        var topB=top
        if(animFocusElement==id){
            leftB+=animElementOffset[0]
            topB+=animElementOffset[1]
        }
        var rightB=leftB+textPaint.measureText(name)+20
        if(rightB>(w-16).toFloat())
            rightB= (w-16).toFloat()
        var bottomB=topB+50
        val sepName=name.split(" ")
        val lines:ArrayList<String> = ArrayList()
        var curLine=""
        for(each in sepName){
            if(leftB+20 +textPaint.measureText("$curLine $each")>w.toFloat()){
                lines.add(curLine)
                curLine="$each "
                bottomB+=50
            }
            else
                curLine+= "$each "
        }
        lines.add(curLine)
        path.addRoundRect(leftB, topB, rightB, bottomB, radii, Path.Direction.CW)
        canvas.drawPath(path, paint)

        var i=0
        while (i<lines.size){
            canvas.drawText(lines[i], leftB+10, topB + 30+(50*i), textPaint)
            i++
        }
        return if(animFocusElement==id)
            arrayOf(topB-animElementOffset[1],bottomB-animElementOffset[1])
        else
            arrayOf(topB,bottomB)
    }

    fun drawLines(canvas: Canvas){
        //Main vertical line
        canvas.drawLine(24f,bounds[0][1].toFloat(),24f,(bounds[childs.size][0]+bounds[childs.size][1])/2,Paint(ANTI_ALIAS_FLAG).apply { color=Color.WHITE })

        //Horizontal line to indicate parent exists
        if(elementID!=drawElementID)
            canvas.drawLine(0f,(bounds[0][0]+bounds[0][1])/2,16f,(bounds[0][0]+bounds[0][1])/2,Paint(ANTI_ALIAS_FLAG).apply { color=Color.WHITE })

        //Horizontal lines for each child
        var i=1
        while(i<=childs.size){
            canvas.drawLine(24f,(bounds[i][0]+bounds[i][1])/2,48f,(bounds[i][0]+bounds[i][1])/2,Paint(ANTI_ALIAS_FLAG).apply { color=Color.WHITE })
            i++
        }
    }



}





//class ElementInfo(context: Context):View(context){
//
//    var elementID=-1
//    var tree:ArrayList<PCRelation> = ArrayList()
//    lateinit var sortedTree: List<PCRelation>
//    var grid= mutableMapOf<String,PCRelation>()
//    var bounds= mutableMapOf<Long,ArrayList<Float>>()
//    var levels=-1.toLong()
//    var currentId:Long=0
//
//    var maxX=0F
//    var maxY=0F
//
//    var focusX=0.5F
//    var focusY=0F
//
//
//
//    var h:Int=300
//    var w:Int=300
//
//    lateinit var gCanvas:Canvas
//
//    var sthap=false
//
//
//    lateinit var localSave: SharedPreferences
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        this.w=w-50
//        this.h=h-50
//        super.onSizeChanged(w, h, oldw, oldh)
//    }
//
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        focusX = (event!!.x)/w
//        focusY = event!!.y/h
//        if(localSave.getBoolean("invX",false))
//            focusX=1-focusX
//        if(localSave.getBoolean("invY",false))
//            focusY=1-focusY
//
//        if(focusX>1)
//            focusX=1F
//        if(focusX<0)
//            focusX=0F
//        if(focusY>1)
//            focusY=1F
//        if(focusY<0)
//            focusY=0F
//        invalidate()
//
//        return true
//    }
//    override fun onDraw(canvas: Canvas?) {
//        if(canvas != null) {
//            if(!sthap) {
//                localSave = context.getSharedPreferences("elementalSave", Context.MODE_PRIVATE)
//                setPCrelation(elementID.toLong(), -1, 0)
//                sortedTree = tree.sortedWith(compareBy({ it.etId }))
//                gCanvas = canvas
//                setGrid(0, 0F, 1F)
//
//                var min=1000F
//                grid.forEach { (s, pcRelation) ->
//                    val pos = s.split(",")
//                    val xPos:Float=pos[1].toFloat()*150*Math.pow(2.toDouble(),levels.toDouble()).toFloat()
//                    val yPos:Float=pos[0].toFloat()*50
//                    if(maxX<xPos)
//                        maxX=xPos
//                    if(maxY<yPos)
//                        maxY=yPos
//                    if(xPos<min)
//                        min=xPos
//                }
//                maxX+=min
//                maxY+=(h/5)*levels
//                sthap=true
//            }
//
//            drawFromGrid(canvas)
//            drawLines(canvas)
//        }
//    }
//
//
//
//
//    fun drawFromGrid(canvas: Canvas){
//
//
//        grid.forEach { (s, pcRelation) ->
//            val pos = s.split(",")
//            setElement(pos[1].toFloat()*150*Math.pow(2.toDouble(),levels.toDouble()).toFloat(),pos[0].toFloat()*(h/5),pcRelation.name,pcRelation.color,pcRelation.etId,canvas)
//        }
//    }
//
//    fun setGrid(id:Long,start:Float,end:Float){
//        grid["${sortedTree[id.toInt()].level},${(start+end)/2}"]=sortedTree[id.toInt()]
//        var t=(end-start)/sortedTree[id.toInt()].c.size
//        var j=0
//        sortedTree[id.toInt()].c.forEach {
//            setGrid(it,start+(t*j),start+(t*(j+1)))
//            j++
//        }
//    }
//
//    fun setPCrelation(elementID: Long,parentID:Long,level: Long): Long {
//        if(levels<level)
//            levels=level
//        val thisID=currentId
//        currentId++
//        val childeIDs:ArrayList<Long> = ArrayList()
//        val childIDs:ArrayList<Long> = ArrayList()
//        var i=0
//        while(i<localSave.getLong("totalCombinations",0)&&elementID>3){
//
//            if(localSave.getString("combinations/$i/makes","0").toString().toLongOrNull()==elementID){
//                val childIDString = localSave.getString("combinations/$i/from","0").toString().split(",")
//                childIDString.forEach {
//                    if(it!=elementID.toString()) {
//
//                        childeIDs.add(it.toLong())
//                    }
//                }
//                break
//            }
//            i++
//        }
//        childeIDs.forEach {
//            childIDs.add(setPCrelation(it,thisID,level+1))
//        }
//        tree.add(PCRelation(parentID,childIDs,thisID,localSave.getString("elements/$elementID/name","Cache failed").toString(),localSave.getString("elements/$elementID/color","#55555555").toString(),level))
//        return thisID
//    }
//
//
//
//    fun setElement(left:Float,top:Float,name:String,colorBG:String,etId: Long,canvas: Canvas){
//
//        val sfx=maxX/w
//        var sfy=maxY/h
//
//        if(sfy<1)
//            sfy=1F
//
//
//        val size=Math.pow((1-(Math.pow((Math.pow((focusX*w)-(left/sfx).toDouble(),2.0)+(Math.pow((focusY*h)-(top/sfy).toDouble(),2.0))),0.5)/w)),2.0).toFloat()*(w/300)
//
//        var focusOffsetX=0.0
//        var focusOffsetY=0.0
//        val fx=focusX*w
//        val fy=focusY*h
//        val x=left/sfx
//        val y=top/sfy
//
//        if(fx>x){
//            focusOffsetX= (-1*(fx*((x/fx)-Math.pow(((x/fx).toDouble()), Math.pow(((y*6)/h).toDouble(),(((300/w)+2.5).toDouble()))+Math.pow(((y*6)/h).toDouble(),(((300/w)+1).toDouble()))))))
//        }
//        if(fx<x){
//            focusOffsetX=-1*(w-fx)*(((x-w)/(w-fx))+(Math.pow(Math.abs((x-w)/(w-fx)).toDouble(), Math.pow(((y*6)/h).toDouble(),(((300/w)+2.5).toDouble()))+Math.pow(((y*6)/h).toDouble(),(((300/w)+1).toDouble())))))
//        }
//
//        if(fy>y){
//            focusOffsetY= (-1*(fy*((y/fy)-Math.pow(((y/fy).toDouble()),2.0))))
//        }
//        if(fy<y){
//            focusOffsetY=-1*(w-fy)*(((y-w)/(w-fy))+(Math.pow(Math.abs((y-w)/(w-fy)).toDouble(), 2.0)))
//        }
//
//        val path = Path()
//        val paint = Paint().apply {
//            color = Color.parseColor(colorBG)
//            style = Paint.Style.FILL
//        }
//
//        val r=Color.red(Color.parseColor(colorBG))
//        val g=Color.green(Color.parseColor(colorBG))
//        val b=Color.blue(Color.parseColor(colorBG))
//        val a=Color.alpha(Color.parseColor(colorBG))
//
//        val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
//            color = if(r<150&&g<150&&b<150)
//                Color.WHITE
//            else
//                Color.BLACK
//            if(a<150)
//                color = Color.BLACK
//            textSize = 30F*size
//        }
//        val radii = floatArrayOf(
//            10f*size, 10f*size, //Top left corner
//            10f*size, 10f*size, //Top right corner
//            10f*size, 10f*size,   //Bottom right corner
//            10f*size, 10f*size   //Bottom left corner
//        )
//
//        path.reset() //Clears the previously set path
//        val leftB=(((left-10)/sfx)-(textPaint.measureText(name)/2)+focusOffsetX).toFloat()
//        val topB=((top/sfy)+focusOffsetY).toFloat()
//        val rightB=(((left+10)/sfx)+(textPaint.measureText(name)/2)+focusOffsetX).toFloat()
//        val bottomB=((top/sfy)+(50*size)+focusOffsetY).toFloat()
//        path.addRoundRect(leftB, topB, rightB, bottomB, radii, Path.Direction.CW)
//        canvas.drawPath(path, paint)
//
//        canvas.drawText(name,
//            ((left/sfx)-(textPaint.measureText(name)/2)+focusOffsetX).toFloat(),
//            ((top/sfy)+(size*30)+focusOffsetY).toFloat(),textPaint)
//
//        val boundVals:ArrayList<Float> = ArrayList()
//        boundVals.add((leftB+rightB)/2)
//        boundVals.add(topB)
//        boundVals.add(bottomB)
//        boundVals.add(size)
//        bounds[etId]=boundVals
//    }
//
//    fun drawLines(canvas: Canvas){
//        var curId=0.toLong()
//        sortedTree.forEach {
//            if(it.p!= (-1).toLong()){
//                canvas.drawLine(bounds[curId]?.get(0)!!.toFloat(),bounds[curId]?.get(1)!!.toFloat(),bounds[it.p]?.get(0)!!.toFloat(),bounds[it.p]?.get(2)!!.toFloat(),Paint(ANTI_ALIAS_FLAG).apply { color=Color.WHITE })
//            }
//            curId++
//        }
//    }
//
//
//    fun setElementInfo(elementID:Int){
//        this.elementID=elementID
//    }
//}
//
//
//
//class PCRelation(var p:Long,var c:ArrayList<Long>,var etId:Long,var name:String,var color:String,var level:Long){
//    override fun toString(): String {
//        return "$etId: Parent=$p, Childs=$c, name=$name, color=$color, level=$level\n"
//    }
//}



