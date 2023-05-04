package com.dicoding.storyapp.utils.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPreference(context : Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
    private val preferences : SharedPreferences.Editor = sharedPreferences.edit()

    fun getName() : String? {
        return sharedPreferences.getString("NAME",null)
    }
    fun saveUser(name : String, token :String, id:String){
        preferences.putBoolean("LOGIN",true)
        preferences.putString("NAME", name)
        preferences.putString("TOKEN", token)
        preferences.putString("ID", id)
        preferences.apply()
    }

    fun logout(){
        preferences.clear().apply()
    }
    fun getToken(): String? {
        return sharedPreferences.getString("TOKEN",null)
    }
    fun isLogin() : Boolean{
        return sharedPreferences.getBoolean("LOGIN",false)
    }
}