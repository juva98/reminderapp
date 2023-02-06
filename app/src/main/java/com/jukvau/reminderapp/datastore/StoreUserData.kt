package com.jukvau.reminderapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class StoreUserData(private val context: Context) {

    companion object {
        private val Context.dataStoreUN: DataStore<Preferences> by preferencesDataStore("UserName")
        private val Context.dataStorePW: DataStore<Preferences> by preferencesDataStore("Password")
        val USER_DATA = stringPreferencesKey("user_name")
        val USER_PASS = stringPreferencesKey("user_pass")
    }

    val getUserName: Flow<String?> = context.dataStoreUN.data
        .map { preferences ->
            preferences[USER_DATA] ?: "seppo"
        }

    val getPassWord: Flow<String?> = context.dataStorePW.data
        .map { preferences ->
            preferences[USER_PASS] ?: "hymysuu"
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