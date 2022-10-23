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
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.brianbett.vlc.databinding.AudioPopupMenuBinding
import com.brianbett.vlc.databinding.PlaylistsPopupBinding
import com.brianbett.vlc.databinding.SingleAudioFileBinding
import java.util.*
import kotlin.collections.ArrayList

class TracksRecyclerViewAdapter(
    private val audiosList:LinkedList<AudioFile>,
    private val myViewModel: MyViewModel


) :
    RecyclerView.Adapter<TracksRecyclerViewAdapter.MyViewHolder>() {

    lateinit var  context: Context

    lateinit var layoutInflater:LayoutInflater
    lateinit var viewGroup: ViewGroup


    class MyViewHolder(binding:SingleAudioFileBinding): RecyclerView.ViewHolder(binding.root){
        var  audioName:TextView = binding.audioName
        var  showAudioMenu:TextView = binding.showAudioMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        layoutInflater=LayoutInflater.from(parent.context)
        val binding:SingleAudioFileBinding=SingleAudioFileBinding.inflate(layoutInflater,parent,false)
        context=parent.context
        viewGroup=parent

        return MyViewHolder(binding)
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.audioName.text= audiosList[position].songTitle

        holder.showAudioMenu.setOnClickListener{

            val popupViewBinding=AudioPopupMenuBinding.inflate(layoutInflater,viewGroup,false)
            popupViewBinding.root.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_fast))
            val popupWindow=PopupWindow(popupViewBinding.root,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true)
            popupWindow.showAtLocation(it,Gravity.BOTTOM,0,0)

            popupViewBinding.title.text=audiosList[position].songTitle
            popupViewBinding.playAll.setOnClickListener {

                myViewModel.songsToPlay.value=audiosList
                myViewModel.songsIterator.value= audiosList.listIterator()
                myViewModel.isPlayPaused.value=false
                AudioPlayer.playAllSongsUnShuffled(context, model = myViewModel)
                popupWindow.dismiss()
            }

            popupViewBinding.insertNext.setOnClickListener {
                val songToAdd=audiosList[position]
                val playlist=myViewModel.playListToInsert.value?:LinkedList<AudioFile>()
                playlist.add(0,songToAdd)
                myViewModel.playListToInsert.value=playlist
                Toast.makeText(context,"${songToAdd.songTitle} added to the current playlist",Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }
            popupViewBinding.append.setOnClickListener {
                val songToAdd=audiosList[position]
                val playlist=myViewModel.playListToInsert.value?:LinkedList<AudioFile>()
                playlist.add(songToAdd)
                myViewModel.playlistToAppend.value=playlist
                Toast.makeText(context,"${songToAdd.songTitle} added to the current playlist",Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }
            popupViewBinding.addToPlaylist.setOnClickListener {playlistView->
                popupWindow.dismiss()
                val playlistBinding=PlaylistsPopupBinding.inflate(layoutInflater,viewGroup,false)
                playlistBinding.root.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_fast))
                val popupWindow2=PopupWindow(playlistBinding.root,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true)
                popupWindow2.showAtLocation(playlistView,Gravity.BOTTOM,0,0)
                val playlists=myViewModel.playlists.value?: ArrayList()
                val playlistsRecyclerView=playlistBinding.playlistsRecyclerView
                val adapter=PlaylistsRecyclerViewAdapter(myViewModel,playlists,popupWindow2,audiosList[position])
                playlistsRecyclerView.adapter=adapter
                playlistBinding.savePlaylist.setOnClickListener {
                    val playlistName = (playlistBinding.playlistNameInput).text.toString()
                    if (playlistName.isEmpty()) {
                        playlistBinding.textInputLayout.error = "You must provide a playlist name"
                    } else {
                        val songs = LinkedList<AudioFile>()
                        songs.add(audiosList[position])
                        val newPlaylist = PlayList(playlistName, songs)
                        myViewModel.playlists.value!!.add(newPlaylist)
                        val songName=audiosList[position].songTitle
                        Toast.makeText(context,"$songName Added to $playlistName playlist",Toast.LENGTH_SHORT).show()
                        (playlistBinding.playlistNameInput).setText("")
                        popupWindow2.dismiss()
                    }
                }



            }



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