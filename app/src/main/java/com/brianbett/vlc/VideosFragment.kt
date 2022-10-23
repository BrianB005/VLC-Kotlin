package com.brianbett.vlc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brianbett.vlc.databinding.FragmentVideoBinding
import java.util.*


class VideosFragment : Fragment() {


    private lateinit var model: MyViewModel

    private lateinit var loadingSongsProgress: ProgressBar
    private lateinit var loadingSongsText: TextView
    private lateinit var  videosRecyclerView:RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding=FragmentVideoBinding.inflate(inflater,container, false)

        loadingSongsProgress=binding.loadingSongsProgress
        loadingSongsText=binding.loadingText
        videosRecyclerView=binding.videosRecyclerView
        val videosList= LinkedList<VideoFile>()


        val videosAdapter=VideosRecyclerViewAdapter(videosList)
        videosRecyclerView.adapter=videosAdapter
        videosRecyclerView.layoutManager=GridLayoutManager(context,2)

        loadingSongsProgress.visibility=View.VISIBLE
        loadingSongsText.visibility=View.VISIBLE
        videosRecyclerView.visibility=View.GONE

        model=ViewModelProvider(requireActivity())[MyViewModel::class.java]

        when {
            ActivityCompat.checkSelfPermission(
                context?.applicationContext!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED -> {
                val permissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        fetchVideos(context?.applicationContext!!)

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
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                Toast.makeText(
                    context,
                    "The app won't serve you well if you don't grant the requested permissions",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {

                fetchVideos(context?.applicationContext!!)
            }

        }

        val videosObserver= Observer<LinkedList<VideoFile>>{ updatedVideosList->
            videosList.addAll(updatedVideosList)
            videosAdapter.notifyDataSetChanged()
            loadingSongsProgress.visibility=View.GONE
            loadingSongsText.visibility=View.GONE
            videosRecyclerView.visibility=View.VISIBLE
        }
        model.allVideos.observe(viewLifecycleOwner,videosObserver)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun fetchVideos(context: Context){
        val videosList=LinkedList<VideoFile>()

        val projection= arrayOf(MediaStore.Video.Media.DATA,MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.TITLE,MediaStore.Video.Media.SIZE,

            )
        val sortOrder=MediaStore.Video.Media.DATE_ADDED+" DESC"
        val videosUri=MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.query(videosUri,projection,null,null,sortOrder).use { cursor ->
            if(cursor!=null){
                while (cursor.moveToNext()){
                    val videoDuration=cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                    val videoTitle=cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                    val videoSize=cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
                    val videoData=cursor.getColumnIndex(MediaStore.Video.Media.DATA)
                    val videoBucketName=cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                    val dateAdded=cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                    val videoFile=VideoFile(cursor.getString(videoTitle),
                        cursor.getLong(videoDuration),cursor.getLong(videoSize),
                        cursor.getString(videoData),cursor.getLong(dateAdded),cursor.getString(videoBucketName))
                    videosList.add(videoFile)

                }
                model.allVideos.value=videosList

                cursor.close()
            }
        }

    }

}