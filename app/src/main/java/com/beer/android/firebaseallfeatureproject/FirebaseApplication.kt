package com.beer.android.firebaseallfeatureproject

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Created by pattadon on 12/27/17.
 */
class FirebaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}