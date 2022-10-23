package com.brianbett.vlc

class AudioFile(
    val songTitle:String,
    val songDuration:Long,
    val songArtist:String,
    val songId:Long,
    val songURI:String,
    val dateAdded: Long
) {

    override fun toString(): String {
        return "AudioFile(songTitle='$songTitle', songDuration=$songDuration, songArtist='$songArtist', songId=$songId, songURI='$songURI', dateAdded=$dateAdded)"
    }
}