package com.brianbett.vlc

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brianbett.vlc.databinding.FragmentArtistsBinding


class ArtistsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding=FragmentArtistsBinding.inflate(inflater,container,false)
        val mProjection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        )
        val contentResolver=context?.contentResolver

        val artistCursor: Cursor = contentResolver?.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            mProjection,
            null,
            null,
            MediaStore.Audio.Artists.ARTIST + " ASC"
        )!!

        val artistId=artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
        val artistName=artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
        val numberOfTracks=artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
        val numberOfAlbums=artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)

        while (artistCursor.moveToNext()){
            val artist=Artist(artistCursor.getString(artistName),artistCursor.getInt(numberOfAlbums)
                ,artistCursor.getInt(numberOfTracks),artistCursor.getString(artistId))

        }
        artistCursor.close()
        return binding.root
    }


}