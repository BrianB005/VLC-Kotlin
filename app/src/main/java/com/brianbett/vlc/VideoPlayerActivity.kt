package com.brianbett.vlc

import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.VideoView
import androidx.lifecycle.ViewModelProvider
import com.brianbett.vlc.databinding.ActivityVideoPlayerBinding

import java.util.*
import kotlin.properties.Delegates

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var videoView:VideoView
    private var isPaused by Delegates.notNull<Boolean>()
    private lateinit var model: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isPaused=false

        model=ViewModelProvider(this)[MyViewModel::class.java]
        val binding= ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent=intent

        val video= intent.getSerializableExtra("video") as VideoFile
        videoView=binding.videoView
        val pauseVideo=binding.pauseVideo
        val resumeVideo=binding.resumeVideo
        val elapsedTime=binding.timeElapsed
        val videoDuration=binding.videoDuration
        val videoTitle=binding.videoTitle
        val videoProgress=binding.seekBar


        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                elapsedTime.text=ConvertMilliseconds.convertMilliseconds(videoView.currentPosition)
                videoProgress.progress=videoView.currentPosition
                handler.postDelayed(this, 1000)
            }
        }, 0)


        videoProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if(fromUser){
                    videoView.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })


//            switching audioTracks
        videoView.setOnInfoListener { mp, _, _ ->
            val mediaTracks = mp!!.trackInfo
            for (track in mediaTracks.indices) {
                if (mediaTracks[track].trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO
                    && mediaTracks[track].language.equals(Locale.getDefault().isO3Language)){
                    mp.selectTrack(track)


                }
            }
            true
        }

        var stoppedTime=0

        videoProgress.max=video.duration.toInt()

        videoDuration.text=ConvertMilliseconds.convertMilliseconds(video.duration.toInt())
        videoTitle.text=video.videoName
        videoView.setVideoURI(Uri.parse(video.videoUri))


        pauseVideo.setOnClickListener {
            videoView.pause()
            stoppedTime=videoView.currentPosition
            resumeVideo.visibility= View.VISIBLE
            pauseVideo.visibility= View.GONE
            isPaused=true

        }

        videoView.setOnCompletionListener {
            isPaused=true
        }
        resumeVideo.setOnClickListener {

            isPaused=false
            videoView.seekTo(stoppedTime)
            videoView.start()
            pauseVideo.visibility= View.VISIBLE
            resumeVideo.visibility= View.GONE
        }
        videoView.start()
    }

    override fun onPostResume() {
        super.onPostResume()
        if(model.currentVideoProgress.value!=null) {
            videoView.seekTo(model.currentVideoProgress.value!!)
            if (model.isVideoPaused.value==false) {
                videoView.pause()
            } else {
                videoView.start()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        model.isVideoPaused.value=videoView.isPlaying
        model.currentVideoProgress.value=videoView.currentPosition
    }



    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }


}