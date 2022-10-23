package com.brianbett.vlc

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class HandlePlaylists {
    companion object{
        fun savePlaylist(playList: PlayList,context: Context,songToAdd: AudioFile){
            val gson= Gson()
            val savedPlaylistsString: String = MyPreferences.getItemFromSP(context, "playlists")
            val type: Type = object : TypeToken<ArrayList<PlayList?>?>() {}.type
            val allPlaylists: ArrayList<PlayList> = gson.fromJson(savedPlaylistsString, type)
            if(allPlaylists.isEmpty()){
                addSongToPlaylist(context,songToAdd,playList)
                allPlaylists.add(playList)
            }else{
                addSongToPlaylist(context,songToAdd,playList)
                allPlaylists.forEach { savedPlaylist ->
                    if (savedPlaylist.playlistName!=playList.playlistName){

                        allPlaylists.add(playList)
                    }
                }

            }




        }

        fun addSongToPlaylist(context: Context,songToAdd:AudioFile,playList: PlayList){
            if (playList.songs.contains(songToAdd)){
                val songName=songToAdd.songTitle
                Toast.makeText(context,"$songName already exists in this playlist",Toast.LENGTH_SHORT).show()
            }else{
                playList.songs.add(songToAdd)
            }

        }

    }
}