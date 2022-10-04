package com.brianbett.vlc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.brianbett.vlc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.slide_in_btp,R.anim.slide_out_ttb)
        setContentView(binding.root)

        val bottomNavigationView=binding.bottomNavigationView

        val videoFragment=VideoFragment()

        supportFragmentManager.beginTransaction().replace(R.id.container,videoFragment).commit()
        bottomNavigationView.setOnItemSelectedListener {
            val selectedFragment:Fragment=when (it.itemId){
                R.id.audio->AudioFragment()
                R.id.play_lists->PlaylistsFragment()
                R.id.browse->BrowseFragment()
                R.id.more->MoreFragment()
                else ->VideoFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.container,selectedFragment).commit()
            return@setOnItemSelectedListener true
        }


    }

}