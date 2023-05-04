package com.veam.elemental

import android.R
import android.app.NotificationManager
import android.media.RingtoneManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationService :FirebaseMessagingService(){
//    override fun onNewToken(p0: String) {
//        super.onNewToken(p0)
//        Firebase.database.getReference("serverloc").addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {}
//            override fun onDataChange(p1: DataSnapshot) {
//                CustomServer.SetFCMToken(FirebaseAuth.getInstance().currentUser!!.uid,p0,p1.value.toString()).execute()
//            }
//        })
//    }

}