package com.brianbett.vlc

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import android.media.AudioManager as AudioManager1


class AudioPlayer {


    companion object {
        private val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

        }
        var goingForward=true
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("ServiceCast")
        private fun requestAudioFocus(context: Context):Boolean {

//            val audioFocusChangeListener=AudioManager1.OnAudioFocusChangeListener { focusChange->
//                when (focusChange){
//                    AudioManager1.AUDIOFOCUS_LOSS_TRANSIENT-> mediaPlayer.pause()
//                    AudioManager1.AUDIOFOCUS_LOSS-> mediaPlayer.stop()
//                    AudioManager1.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK-> mediaPlayer.pause()
//                    AudioManager1.AUDIOFOCUS_GAIN->mediaPlayer.start()
//                }
//
//            }
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager1
            val focusRequest = AudioFocusRequest.Builder(AudioManager1.AUDIOFOCUS_GAIN).setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                    ).build()
            return when (audioManager.requestAudioFocus(focusRequest)) {
                AudioManager1.AUDIOFOCUS_REQUEST_GRANTED -> {
                    true
                }
                else -> false

            }
        }



        @RequiresApi(Build.VERSION_CODES.O)
        fun playAllSongsUnShuffled(context: Context,model: MyViewModel) {
            if (requestAudioFocus(context)) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }

//                val iterator=model.songsToPlay.value!!.iterator()
                val iterator = model.songsIterator.value

                if (iterator != null) {
                    val firstSong = iterator.next()
                    val firstSongUri = firstSong.songURI
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(context, Uri.parse(firstSongUri))
                    mediaPlayer.prepare()
                    model.currentSong.value = firstSong
                    mediaPlayer.start()
                    val handler = Handler()
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            updateCurrentProgress(model)
                            handler.postDelayed(this, 500)
                        }
                    }, 0)

                    mediaPlayer.setOnCompletionListener {
                        model.isPlayPaused.value = true
                        try {
                            mediaPlayer.stop()
                            mediaPlayer.reset()
                                if (model.repeatAllSongs.value!=null) {
                                    if (goingForward) {
                                        if (iterator.hasPrevious()) {
                                            model.isPlayPaused.value = false
                                            val songToRepeat = iterator.previous()
                                            val songToRepeatUri = songToRepeat.songURI
                                            model.currentSong.value = songToRepeat
                                            playSong(songToRepeatUri, context)

                                        } else {
                                            Log.d("Start", "No record to replay")
                                        }

                                        goingForward = false
                                    } else {
                                        if (iterator.hasNext()) {
                                            model.isPlayPaused.value = false
                                            val songToRepeat = iterator.next()
                                            val songToRepeatUri = songToRepeat.songURI
                                            model.currentSong.value = songToRepeat
                                            playSong(songToRepeatUri, context)
                                        }

                                    }
                                } else {
                                    if (iterator.hasNext()) {
                                        model.isPlayPaused.value = false
                                        val nextSong = iterator.next()
                                        val nextSongURI = nextSong.songURI
                                        model.currentSong.value = nextSong
                                        playSong(nextSongURI, context)
                                    } else {
                                        Log.d("End", "End of list")
                                    }
                                    goingForward = true
                                }



                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }


            @RequiresApi(Build.VERSION_CODES.O)
            fun playSingleSong(context: Context, model: MyViewModel,songURI:String){
                if (requestAudioFocus(context)) {
                    mediaPlayer.reset()
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                    val handler = Handler()
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            updateCurrentProgress(model)
                            handler.postDelayed(this, 500)
                        }
                    }, 0)
                    mediaPlayer.setDataSource(context, Uri.parse(songURI))
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    mediaPlayer.setOnCompletionListener {
                        model.isPlayPaused.value=true
                    }

                }
            }
        private fun updateCurrentProgress(model: MyViewModel){
            val currentProgress= mediaPlayer.currentPosition
            model.currentSongProgress.value=currentProgress

        }
        fun pausePlay(){

            mediaPlayer.pause()

        }
        fun resumePlay(){
            mediaPlayer.start()

        }

        fun playNext(model: MyViewModel,context:Context){
            val iterator=model.songsIterator.value
            if(iterator!=null) {
                if(!goingForward){
                    if (iterator.hasNext()) {
                        iterator.next()
                    }
                    goingForward=true
                }

                if (iterator.hasNext()) {
                    val nextSong = iterator.next()
                    model.currentSong.value = nextSong
                    model.isPlayPaused.value = false
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(context, Uri.parse(nextSong.songURI))
                    mediaPlayer.prepare()
                    mediaPlayer.start()

                }else{
                    goingForward=false
                }
            }

        }
        fun playPrevious(model: MyViewModel,context:Context){
            val iterator=model.songsIterator.value
            if(iterator!=null) {
                if(goingForward){
                    goingForward = if (iterator.hasPrevious()) {
                        iterator.previous()
                        false
                    }else{
                        true
                    }

                }
                if (iterator.hasPrevious()) {
                    val previousSong = iterator.previous()
                    model.currentSong.value = previousSong
                    model.isPlayPaused.value = false
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(context, Uri.parse(previousSong.songURI))
                    mediaPlayer.prepare()
                    mediaPlayer.start()

                }else{

                    goingForward=true
                }
            }

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun playFromPlaylist(context: Context, model: MyViewModel){
            if(mediaPlayer.isPlaying){
                mediaPlayer.setOnCompletionListener {
                    playAllSongsUnShuffled(context,model)
                }
            }else{
                playAllSongsUnShuffled(context,model)
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun playFromAppendedPlaylist(context: Context, model: MyViewModel){
            if(mediaPlayer.isPlaying){
                mediaPlayer.setOnCompletionListener {
                    playAllSongsUnShuffled(context,model)
                }
            }else{
                playAllSongsUnShuffled(context,model)
            }
        }
        fun replayAllSongs(model: MyViewModel){
            val iterator=model.songsIterator.value
            if(iterator!=null) {
                if (goingForward) {
                    if (iterator.hasPrevious()) {
                        iterator.previous()


                    } else {
                        Log.d("Start","No record to replay")
                    }

                }else{
                    if(iterator.hasNext()){
                        iterator.next()
                    }

                }
            }
        }

        fun forward10(){
            mediaPlayer.seekTo(mediaPlayer.currentPosition+10000)

        }
        fun reverse10(){
            mediaPlayer.seekTo(mediaPlayer.currentPosition-10000)

        }
        fun seekToTime(seekTo:Int){
            mediaPlayer.seekTo(seekTo)

        }

        private fun playSong(songURI:String,context: Context){

            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, Uri.parse(songURI))
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

    }





}