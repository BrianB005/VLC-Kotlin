package com.brianbett.vlc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.brianbett.vlc.databinding.SingleSavedPlaylistBinding
import com.brianbett.vlc.databinding.VideoPlayerPopupBinding
import java.io.File
import java.util.*


class SavedPlaylistsRecyclerViewAdapter(private val playlists:ArrayList<PlayList>,val myViewModel: MyViewModel) :RecyclerView.Adapter<SavedPlaylistsRecyclerViewAdapter.MyViewHolder>(){

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context=parent.context
        inflater=LayoutInflater.from(parent.context)
        val binding=SingleSavedPlaylistBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
          holder.numberOfTracks.text="${playlists[position].songs.size} Tracks"
        holder.playListName.text=playlists[position].playlistName
        var playlistDurationMs:Long=0

        playlists[position].songs.forEach { song->playlistDurationMs+=song.songDuration }
        holder.playListDuration.text=ConvertMilliseconds.convertMilliseconds(playlistDurationMs.toInt())
        holder.itemView.setOnClickListener {
            myViewModel.songsToPlay.value = playlists[position].songs
            myViewModel.songsIterator.value = playlists[position].songs.listIterator()
            myViewModel.isPlayPaused.value = false
            myViewModel.currentPlayList.value=playlists[position].playlistName
            AudioPlayer.playAllSongsUnShuffled(context, model = myViewModel)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }


    class MyViewHolder(binding:SingleSavedPlaylistBinding):RecyclerView.ViewHolder(binding.root){

        val playListName=binding.playlistName
        val playListDuration=binding.playlistDuration
        val numberOfTracks=binding.numberOfTracks

    }
}