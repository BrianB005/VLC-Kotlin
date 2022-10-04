package com.brianbett.vlc

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.brianbett.vlc.databinding.FragmentTracksBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis


class TracksFragment : Fragment(){


    private lateinit var  tracksRecyclerViewAdapter:TracksRecyclerViewAdapter
    private lateinit var loadingSongsProgress:ProgressBar
    private lateinit var loadingSongsText:TextView
    private lateinit var tracksRecyclerView:RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding:FragmentTracksBinding=FragmentTracksBinding.inflate(inflater,container,false)
       tracksRecyclerView=binding.tracksRecyclerView
        loadingSongsProgress=binding.loadingSongsProgress
        loadingSongsText=binding.loadingText


        val model: MyViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]


        val audiosList=LinkedList<AudioFile>()

        tracksRecyclerViewAdapter = TracksRecyclerViewAdapter(audiosList,model)
        tracksRecyclerView.adapter=tracksRecyclerViewAdapter
        when {
            ActivityCompat.checkSelfPermission(context?.applicationContext!!,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED-> {
                val permissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        CoroutineScope(Dispatchers.IO).launch {
                            getAudios(context?.applicationContext!!, model)
                            CoroutineScope(Main).launch {
                                tracksRecyclerView.visibility = View.VISIBLE
                                loadingSongsText.visibility = View.GONE
                                loadingSongsProgress.visibility = View.GONE
                            }
                        }

                    } else {
                        Toast.makeText(
                            context,
                            "The app won't serve you well if you don't grant the requested permissions",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)-> {
                Toast.makeText(
                    context,
                    "The app won't serve you well if you don't grant the requested permissions",
                    Toast.LENGTH_LONG
                ).show()
            }
            else->{


                CoroutineScope(Dispatchers.IO).launch {
                    getAudios(context?.applicationContext!!, model)
                   CoroutineScope(Main).launch {
                       tracksRecyclerView.visibility = View.VISIBLE
                       loadingSongsText.visibility = View.GONE
                       loadingSongsProgress.visibility = View.GONE
                   }
                }










                }
            }
        val songsObserver=Observer<LinkedList<AudioFile>>{updatedAudiosList->
            audiosList.addAll(updatedAudiosList)
            tracksRecyclerView.visibility=View.VISIBLE
            loadingSongsText.visibility=View.GONE
            loadingSongsProgress.visibility=View.GONE
            tracksRecyclerViewAdapter.notifyDataSetChanged()
        }
        model.songsToPlay.observe(viewLifecycleOwner,songsObserver)




        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
   fun getAudios(applicationContext: Context,model: MyViewModel){


        val tracksList=LinkedList<AudioFile>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID)
        val sortOrder = MediaStore.Audio.Media.DATE_ADDED


       applicationContext.contentResolver.query(
           MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
           projection,
           null,
           null,
           sortOrder
       )?.use { cursor ->


           while (cursor.moveToNext()) {


               val audioTitle: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
               val audioArtist: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
               val audioDuration: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
               val audioData: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
               val audioAlbum: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

               val audioAlbumId: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
               val dateAdded: Int =
                   cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

               val songId: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

               val albumArtUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI





               if (File(cursor.getString(audioData)).exists()) {
                   val albumArt: String = (ContentUris.withAppendedId(
                       albumArtUri,
                       cursor.getLong(audioAlbumId)
                   )).toString()
                   val audioFile = AudioFile(
                       cursor.getString(audioTitle),
                       cursor.getLong(audioDuration),
                       cursor.getString(audioArtist),
                       cursor.getLong(songId),
                       cursor.getString(audioData),
                       cursor.getLong(dateAdded)
                   )
                   tracksList.add(audioFile)
               }
           }




           requireActivity().runOnUiThread {
               model.songsToPlay.value = tracksList
           }


           cursor.close()
       }









    }


}