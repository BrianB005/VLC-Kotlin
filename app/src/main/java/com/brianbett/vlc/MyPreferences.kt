package com.brianbett.vlc

import android.content.Context


class MyPreferences {
    fun saveItemToSP(context: Context,key:String,value:String){
        val sharedPreferences=context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE)
        val  editor=sharedPreferences.edit()

        editor.putString(key,value)
        editor.apply()
    }

    fun getItemFromSP(context: Context,key:String):String {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")!!
    }

}