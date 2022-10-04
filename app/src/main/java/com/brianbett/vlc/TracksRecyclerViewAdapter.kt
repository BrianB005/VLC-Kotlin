package com.brianbett.vlc

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.brianbett.vlc.databinding.SingleAudioFileBinding
import java.util.*

class TracksRecyclerViewAdapter(
    private val audiosList:LinkedList<AudioFile>,
    private val myViewModel: MyViewModel

) :
    RecyclerView.Adapter<TracksRecyclerViewAdapter.MyViewHolder>() {

    lateinit var  context: Context
    lateinit var layoutInflater:LayoutInflater


    class MyViewHolder(binding:SingleAudioFileBinding): RecyclerView.ViewHolder(binding.root){
        var  audioName:TextView = binding.audioName
        var  showAudioMenu:TextView = binding.showAudioMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        layoutInflater=LayoutInflater.from(parent.context)
        val binding:SingleAudioFileBinding=SingleAudioFileBinding.inflate(layoutInflater,parent,false)
        context=parent.context


        return MyViewHolder(binding)
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.audioName.text= audiosList[position].songTitle

        holder.showAudioMenu.setOnClickListener{
            val popupMenu=PopupMenu(context,it)
            popupMenu.inflate(R.menu.audio_menu)
            popupMenu.menu.getItem(0).title=audiosList[position].songTitle
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId){
                    R.id.play_all->{

                        myViewModel.songsToPlay.value=audiosList
                        myViewModel.songsIterator.value= audiosList.listIterator()
                        myViewModel.isPlayPaused.value=false
                        AudioPlayer.playAllSongsShuffled(context, model = myViewModel)
                    }
                    R.id.insert_next->{
                        val songToAdd=audiosList[position]
                        val currentPlaylist= myViewModel.playListToAppend.value?:LinkedList<AudioFile>()
                        currentPlaylist.add(songToAdd)
//                        Log.d("Size",.toString())
                        Toast.makeText(context,"${songToAdd.songTitle} added to the current playlist",Toast.LENGTH_SHORT).show()
                    }
                    R.id.append->{

                    }
                }
                return@setOnMenuItemClickListener false
            }
            popupMenu.show()
        }

        holder.itemView.setOnClickListener{
            AudioPlayer.playSingleSong(context, model = myViewModel,audiosList[position].songURI)
            myViewModel.currentSong.value=audiosList[position]
            myViewModel.isPlayPaused.value=false

        }
    }

    override fun getItemCount(): Int {
        return audiosList.size
    }
}