package com.jukvau.reminderapp

import android.app.Application

class reminderappApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}