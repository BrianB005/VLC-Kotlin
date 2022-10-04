package com.brianbett.vlc


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

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
    val playListToAppend:MutableLiveData<LinkedList<AudioFile>> by lazy{
        MutableLiveData<LinkedList<AudioFile>>()
    }
    val repeatAllSongs:MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean> ()
    }





}