package com.jukvau.reminderapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random


class StoreUserData(private val context: Context) {

    companion object {
        private val Context.dataStoreUN: DataStore<Preferences> by preferencesDataStore("UserName")
        private val Context.dataStorePW: DataStore<Preferences> by preferencesDataStore("Password")
        private val Context.dataStoreID: DataStore<Preferences> by preferencesDataStore("ID")
        val USER_DATA = stringPreferencesKey("user_name")
        val USER_PASS = stringPreferencesKey("user_pass")
        val USER_ID = Random.nextInt(1, 1001)
    }

    // default username and password, leave empty string for testing

    val getUserName: Flow<String?> = context.dataStoreUN.data
        .map { preferences ->
            preferences[USER_DATA] ?: ""
        }

    val getPassWord: Flow<String?> = context.dataStorePW.data
        .map { preferences ->
            preferences[USER_PASS] ?: ""
        }

    suspend fun saveUsername(name:String) {
        context.dataStoreUN.edit { preferences ->
            preferences[USER_DATA] = name
        }
    }

    suspend fun savePassword(word:String) {
        context.dataStorePW.edit { preferences ->
            preferences[USER_PASS] = word
        }
    }

}