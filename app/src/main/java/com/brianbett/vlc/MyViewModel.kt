package com.brianbett.vlc


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class MyViewModel : ViewModel() {
    val  songsToPlay:MutableLiveData<LinkedList<AudioFile>> by lazy {
        MutableLiveData<LinkedList<AudioFile>> ()
    }

    val songsIterator:MutableLiveData<MutableListIterator<AudioFile>> by lazy{
        MutableLiveData<MutableListIterator<AudioFile>>()
    }
    val  currentSong:MutableLiveData<AudioFile> by lazy {
        MutableLiveData<AudioFile> ()
    }
    val currentSongProgress:MutableLiveData<Int> by lazy {
        MutableLiveData<Int> ()
    }
    val isPlayPaused:MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean> ()
    }
    val playListToInsert:MutableLiveData<LinkedList<AudioFile>> by lazy{
        MutableLiveData<LinkedList<AudioFile>>()
    }
    val playlistToAppend:MutableLiveData<LinkedList<AudioFile>> by lazy{
        MutableLiveData<LinkedList<AudioFile>>()
    }
    val repeatAllSongs:MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean> ()
    }
    val currentPlayList:MutableLiveData<String> by lazy{
        MutableLiveData<String> ()
    }



    //    videos
    val allVideos:MutableLiveData<LinkedList<VideoFile>> by lazy {
        MutableLiveData<LinkedList<VideoFile>>()
    }

    val currentVideo:MutableLiveData<VideoFile> by lazy{
        MutableLiveData<VideoFile>()
    }
    val currentVideoProgress:MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }
    val isVideoPaused:MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

//    playlists
    val playlists:MutableLiveData<ArrayList<PlayList>> by lazy{
        MutableLiveData<ArrayList<PlayList>>()
    }
}





