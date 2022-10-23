package com.brianbett.vlc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.brianbett.vlc.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model:MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.slide_in_btp,R.anim.slide_out_ttb)
        setContentView(binding.root)
        model=ViewModelProvider(this)[MyViewModel::class.java]

        val bottomNavigationView=binding.bottomNavigationView

        val videoFragment=VideosFragment()

        supportFragmentManager.beginTransaction().replace(R.id.container,videoFragment).commit()
        bottomNavigationView.setOnItemSelectedListener {
            val selectedFragment:Fragment=when (it.itemId){
                R.id.audio->AudioFragment()
                R.id.play_lists->PlaylistsFragment()
                R.id.browse->BrowseFragment()
                R.id.more->MoreFragment()
                else ->VideosFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.container,selectedFragment).commit()
            return@setOnItemSelectedListener true
        }




    }

    override fun onStop() {
        super.onStop()
        val gson= Gson()
        val playlistsString=gson.toJson(model.playlists.value)
        MyPreferences.saveItemToSP(applicationContext,"playlists",playlistsString
        )
    }
}