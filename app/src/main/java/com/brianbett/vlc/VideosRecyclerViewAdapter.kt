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
import com.brianbett.vlc.databinding.SingleVideoFileBinding
import com.brianbett.vlc.databinding.VideoPlayerPopupBinding
import java.io.File
import java.util.*


class VideosRecyclerViewAdapter(private val videosList:LinkedList<VideoFile>) :RecyclerView.Adapter<VideosRecyclerViewAdapter.MyViewHolder>(){

    private lateinit var context: Context
    private lateinit var inflater: LayoutInflater





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       context=parent.context
        inflater=LayoutInflater.from(parent.context)
        val binding=SingleVideoFileBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.videoTitle.text=videosList[position].videoName
        holder.videoSize.text=ConvertFileSize.convertBytes(videosList[position].videoSize.toInt())
        holder.videoDuration.text=ConvertMilliseconds.convertMilliseconds(videosList[position].duration.toInt())


        val videoThumbnail=ThumbnailUtils.createVideoThumbnail(videosList[position].videoUri,
            MediaStore.Video.Thumbnails.MINI_KIND)
        holder.imageView.setImageBitmap(videoThumbnail)


        holder.itemView.setOnClickListener{
            val intent=Intent(context,VideoPlayerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra("video",videosList[position])
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return videosList.size
    }


    class MyViewHolder(binding:SingleVideoFileBinding):RecyclerView.ViewHolder(binding.root){

        val imageView=binding.imageView
        val videoDuration=binding.videoDuration
        val videoSize=binding.videoSize
        val videoTitle=binding.videoTitle

    }
}