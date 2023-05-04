package com.veam.elemental

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8


class CustomServer{
    
    class GetValue(val path: String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}r/$path")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class UpdateElements(val user: String,var elements: String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}updateElements/$user/$elements")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class CombineElements(val user: String,var elementID: String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}combineElements/$user/$elementID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class UpdateBal(val user: String,var newBal: String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}updateBal/$user/$newBal")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }


    class AddLog0(val user: String,var add: String,var msg:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addLog0/$user/$add/${URLEncoder.encode(msg,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class AddLog1(val user: String,var add: String,var unlocked:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addLog1/$user/$add/${URLEncoder.encode(unlocked,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(result.length - 1, result.length) == "\"")
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class AddLog2(val user: String,var add: String,var name:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addLog2/$user/$add/${URLEncoder.encode(name,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class AddLog3(val user: String,var add: String,var name:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addLog3/$user/$add/${URLEncoder.encode(name,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class AddLog4(val user: String,var add: String,var from:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addLog4/$user/$add/${URLEncoder.encode(from,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetActiveVotes(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getActiveVotes")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetUserCount(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getUserCount")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class Upvote(var userID:String,var voteID:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}upvote/$userID/$voteID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class Downvote(var userID:String,var voteID:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}downvote/$userID/$voteID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class GetUsername(var userids:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getUsername/$userids")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>1) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class AddComment(val userID: String,var voteID: String,var msg:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addComment/$userID/$voteID/${URLEncoder.encode(msg,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class AddVote(val name: String,var color: String,val from:String,val userID: String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                color=color.replace("#","")
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addVote/${URLEncoder.encode(name,"utf-8")}/$color/$from/$userID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetTotalElements(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}totalElements")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class GetTotalCombinations(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}totalCombinations")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class UpdateUnlocked(val user: String,var noOfElements: String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}updateUnlocked/$user/$noOfElements")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetLeaderboard(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}leaderboard")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class UpdateUsername(val user: String,var newName: String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}updateUsername/$user/${URLEncoder.encode(newName,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class CheckAndCreateNewUser(val user: String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}checkAndCreateNewUser/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class GetPing(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}ping")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetCategorizedElements(val user: String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getSettedCategory/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(result.length - 1, result.length) == "\"")
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class GetAllCategory(val user: String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getAllCategory/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(result.length - 1, result.length) == "\"")
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetCategory(val user: String,val elementID:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                if(elementID.toLongOrNull()!=null) {
                    url = URL(
                        "${localSave.getString(
                            "serverloc",
                            "http://<server_ip>/elementalPK/"
                        )}getCategory/$user/$elementID"
                    )
                    connection = url.openConnection() as HttpURLConnection
                    connection!!.connect()
                    result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                    if (result.length > 0) {
                        if (result.substring(0, 1) == "\"" && result.substring(
                                result.length - 1,
                                result.length
                            ) == "\""
                        )
                            result = result.substring(1, result.length - 1)
                    }
                }
                else{
                    result=""
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }
    class GetRecommendedCategory(val elementID:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getRecommendedCategory/$elementID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(result.length - 1, result.length) == "\"")
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class SetCategory(val userID:String,val elementID:String,val newCat:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}setCategory/$userID/$elementID/${URLEncoder.encode(newCat,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(result.length - 1, result.length) == "\"")
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetRefer(val user: String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getRefer/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class SetRefer(val user: String,val referID:String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}refer/$user/$referID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetReferStatus(val user: String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getReferStatus/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class Redeem(val user: String,val code:String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}redeem/$user/$code")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetLeaderboardWithID(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}leaderboardWithID")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetProfile(val user: String,var listener: ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getPlayerInfo/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.length>0) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class SetOnline(val user: String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}setOnline/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()

            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class SetOffline(val user: String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}setOffline/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetLog(var user:String,var amount:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getLog/$user/$amount")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetPrivacy(var user:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getPrivacy/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class SetPrivacy(val user: String,val privacy:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}setPrivacy/$user/$privacy")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetStatus(var user:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getStatus/$user")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class SetStatus(val user: String,val status:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}setStatus/$user/${URLEncoder.encode(status,"utf-8")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class SetFCMToken(val user: String,val token:String,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}setFCMToken/$user/$token")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()

            }

            return result!!
        }
    }

    class GetElements(var from:Long,var to:Long,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                if(to!=0.toLong())
                    url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getElements/$from/$to")
                else
                    url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getElements/$from")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetCombinations(var from:Long,var to:Long,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                if(to!=0.toLong())
                    url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getCombinations/$from/$to")
                else
                    url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getCombinations/$from")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class AddElementComment(var uid:String,var eid:String,var comment:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}addElementComment/$uid/$eid/$comment")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetElementComment(var eid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}getElementComment/$eid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class IsMod(var uid:String,var modClass:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}isMod/$uid/$modClass")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class ReportVoteComment(var uid:String,var vid:String,var cid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}reportVoteComment/$uid/$vid/$cid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class ReportVote(var uid:String,var vid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}reportVote/$uid/$vid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class ReportElementComment(var uid:String,var eid:String,var cid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}reportElementComment/$uid/$eid/$cid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class RemoveVoteComment(var uid:String,var vid:String,var cid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}removeVoteComment/$uid/$vid/$cid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class RemoveVote(var uid:String,var vid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}removeVote/$uid/$vid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class RemoveElementComment(var uid:String,var eid:String,var cid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}removeElementComment/$uid/$eid/$cid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class ReportUsername(var uid:String,var reportid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}reportUsername/$uid/$reportid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class ResetUsername(var uid:String,var reportid:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}resetUsername/$uid/$reportid")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class GetServerLoc(var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)

            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {
                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}")
                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                result = connection.url.toString()

                if(result.substring(0,1)=="\""&&result.substring(result.length-1,result.length)=="\"")
                    result=result.substring(1,result.length-1)
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

    class InitSync(var uid:String,var elementStartID:String,var elementEndID:String,var combinationStartID:String,var combinationEndID:String,var listener:ServerListener,val context:Context) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            val localSave=context.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
            var result="connection failed"
            val url: URL
            var connection: HttpURLConnection? = null
            try {

                url = URL("${localSave.getString("serverloc","http://<server_ip>/elementalPK/")}initSync/$uid/$elementStartID/$elementEndID/$combinationStartID/$combinationEndID")

                connection = url.openConnection() as HttpURLConnection
                connection!!.connect()
                result = InputStreamReader(connection!!.inputStream, UTF_8).readText()
                if(result.isNotEmpty()) {
                    if (result.substring(0, 1) == "\"" && result.substring(
                            result.length - 1,
                            result.length
                        ) == "\""
                    )
                        result = result.substring(1, result.length - 1)
                }
            } catch (e: IOException) {
                Log.d("VersionTask", Log.getStackTraceString(e))
            } finally {
                connection?.disconnect()
            }
            return result!!
        }

        override fun onPostExecute(result: String) {
            listener.runWithValue(result)
            super.onPostExecute(result)
        }
    }

}

interface ServerListener{
    fun runWithValue(value:String){}
}





