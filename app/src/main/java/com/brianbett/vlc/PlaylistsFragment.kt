package com.brianbett.vlc

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.brianbett.vlc.databinding.FragmentPlaylistsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import androidx.lifecycle.Observer
import java.util.*
import androidx.appcompat.widget.Toolbar
import kotlin.collections.ArrayList


class PlaylistsFragment : Fragment() {
    private lateinit var popupWindow:PopupWindow
    private lateinit var popupView:View
    private lateinit var model: MyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback=object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(popupWindow.isShowing){
                    popupView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_ttb_fast))
                    popupWindow.dismiss()
                }
                //                else{
//                    requireActivity().onBackPressedDispatcher.onBackPressed()
//                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,onBackPressedCallback)



    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        val binding:FragmentPlaylistsBinding=FragmentPlaylistsBinding.inflate(inflater,container,false)
        val playlistsRecyclerView=binding.playListsRecyclerView
        playlistsRecyclerView.layoutManager= GridLayoutManager(context,2)
        model=ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val shuffleBtn=binding.shuffle
        val mediaPlayer=binding.mediaPlayer
        val pauseSong=binding.pauseSong
        val resumeSong=binding.resumePlayback
        val songDuration=binding.remainingTime
        val progressView=binding.songProgressView
        val songName=binding.songName


        //    updating view model playlists to reflect the current playlists saved

        val savedPlaylistsString: String = MyPreferences.getItemFromSP(requireContext(), "playlists")
        val type: Type = object : TypeToken<ArrayList<PlayList?>?>() {}.type
        val gson= Gson()
        if(savedPlaylistsString.isNotEmpty()){
            val allPlaylists: ArrayList<PlayList> = gson.fromJson(savedPlaylistsString, type)?: ArrayList()
            model.playlists.value=allPlaylists
            Log.d("playlists",allPlaylists.toString())
        }
//        val playlists:ArrayList<PlayList> =ArrayList()
        val playlists=model.playlists.value!!
        val playlistsAdapter=SavedPlaylistsRecyclerViewAdapter(playlists,model)
        playlistsRecyclerView.adapter=playlistsAdapter
        val songObserver=Observer<AudioFile>{ currentSong->
            mediaPlayer.visibility=View.VISIBLE
            shuffleBtn.visibility=View.VISIBLE
            songName.text=currentSong.songTitle
            val songDurationString=ConvertMilliseconds.convertMilliseconds(currentSong.songDuration.toInt())
            songDuration.text=songDurationString

            //        observing progress
            val progressObserver=Observer<Int>{currentProgress->
                progressView.max= currentSong.songDuration.toInt()
                progressView.progress = currentProgress
                val remainingTimeMs=currentSong.songDuration-currentProgress
                val remainingTime=ConvertMilliseconds.convertMilliseconds(remainingTimeMs.toInt())
                binding.timeElapsed.text="-$remainingTime /"
            }
            model.currentSongProgress.observe(viewLifecycleOwner,progressObserver)
        }
        model.currentSong.observe(viewLifecycleOwner,songObserver)




//        val playlistsObserver=Observer<ArrayList<PlayList>>{ currentPlaylists->
//           playlists.addAll(currentPlaylists)
//            playlistsAdapter.notifyDataSetChanged()
//        }
//        model.playlists.observe(viewLifecycleOwner,playlistsObserver)
        val isPausedObserver=Observer<Boolean>{isPaused->
            if(isPaused){
                pauseSong.visibility=View.GONE
                resumeSong.visibility=View.VISIBLE
            }else{
                pauseSong.visibility=View.VISIBLE
                resumeSong.visibility=View.GONE
            }
        }
        model.isPlayPaused.observe(viewLifecycleOwner,isPausedObserver)


        val playListObserver=Observer<LinkedList<AudioFile>>{ playlist->
            model.songsIterator.value=playlist.listIterator()
            AudioPlayer.playFromPlaylist(requireContext(),model)
        }
        model.playListToInsert.observe(viewLifecycleOwner,playListObserver)

        val appendedPlayListObserver=Observer<LinkedList<AudioFile>>{ playlist->
            model.songsIterator.value=playlist.listIterator()
            AudioPlayer.playFromAppendedPlaylist(requireContext(),model)
        }
        model.playlistToAppend.observe(viewLifecycleOwner,appendedPlayListObserver)

        pauseSong.setOnClickListener{
            model.isPlayPaused.value=true
            AudioPlayer.pausePlay()
            pauseSong.visibility=View.GONE

            resumeSong.visibility=View.VISIBLE
        }
        resumeSong.setOnClickListener{
            model.isPlayPaused.value=false
            AudioPlayer.resumePlay()
            pauseSong.visibility=View.VISIBLE
            resumeSong.visibility=View.GONE

        }



        popupView=inflater.inflate(R.layout.audio_player_popup,container,false)
        popupWindow= PopupWindow(popupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        mediaPlayer.setOnClickListener{
            popupView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_fast))
            popupWindow.showAtLocation(it, Gravity.BOTTOM, 0, 0)
            handlePopupWindow()

        }
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    private  fun handlePopupWindow(){
        val rewindTen=popupView.findViewById<TextView>(R.id.rewind_10)
        val forwardTen=popupView.findViewById<TextView>(R.id.forward_10)
        val songName=popupView.findViewById<TextView>(R.id.song_name)
        val songDuration=popupView.findViewById<TextView>(R.id.song_duration)
        val timeRemaining=popupView.findViewById<TextView>(R.id.remaining_time)
        val shuffleSongs=popupView.findViewById<TextView>(R.id.shuffle)
        val repeatSongs=popupView.findViewById<TextView>(R.id.repeat_song)
        val undoRepeatSongs=popupView.findViewById<TextView>(R.id.undo_repeat_song)
        val playPrevious=popupView.findViewById<TextView>(R.id.play_previous)
        val playNext=popupView.findViewById<TextView>(R.id.play_next)
        val albumArt=popupView.findViewById<ImageView>(R.id.song_image)
        val pauseSong=popupView.findViewById<TextView>(R.id.pause_song)
        val resumeSong=popupView.findViewById<TextView>(R.id.resume_playback)
        val songProgress=popupView.findViewById<SeekBar>(R.id.song_progress_view)
        val toolbar=popupView.findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title="Currently playing"
        toolbar.subtitle=model.currentPlayList.value
        forwardTen.setOnClickListener {
            AudioPlayer.forward10()
        }
        rewindTen.setOnClickListener {
            AudioPlayer.reverse10()
        }
        repeatSongs.setOnClickListener {
            model.repeatAllSongs.value=true
            repeatSongs.visibility=View.GONE
            undoRepeatSongs.visibility=View.VISIBLE
        }
        undoRepeatSongs.setOnClickListener {
            model.repeatAllSongs.value=false
            repeatSongs.visibility=View.VISIBLE
            undoRepeatSongs.visibility=View.GONE
        }

        songProgress.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    AudioPlayer.seekToTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        playNext.setOnClickListener{

            AudioPlayer.playNext(model,requireContext())
        }
        playPrevious.setOnClickListener{
            AudioPlayer.playPrevious(model,requireContext())
        }

        val songObserver=Observer<AudioFile>{currentSong->

//            albumArt.setImageURI(Uri.parse(currentSong.albumArt))

            songName.text=currentSong.songTitle
            val songDurationString=ConvertMilliseconds.convertMilliseconds(currentSong.songDuration.toInt())
            songDuration.text=songDurationString
            //        observing progress
            val progressObserver=Observer<Int>{currentProgress->
                songProgress.max= currentSong.songDuration.toInt()
                songProgress.progress = currentProgress
                val remainingTimeMs=currentSong.songDuration.toInt()-currentProgress
                val remainingTime=ConvertMilliseconds.convertMilliseconds(remainingTimeMs)
                timeRemaining.text="-$remainingTime"

            }
            model.currentSongProgress.observe(requireActivity(),progressObserver)
        }
        model.currentSong.observe(requireActivity(),songObserver)

        val isPausedObserver=Observer<Boolean>{isPaused->
            if(isPaused){
                pauseSong.visibility=View.GONE
                resumeSong.visibility=View.VISIBLE
            }else{
                pauseSong.visibility=View.VISIBLE
                resumeSong.visibility=View.GONE
            }
        }
        model.isPlayPaused.observe(viewLifecycleOwner,isPausedObserver)
        pauseSong.setOnClickListener{
            model.isPlayPaused.value=true
            AudioPlayer.pausePlay()
            pauseSong.visibility=View.GONE

            resumeSong.visibility=View.VISIBLE
        }
        resumeSong.setOnClickListener{
            model.isPlayPaused.value=false
            AudioPlayer.resumePlay()
            pauseSong.visibility=View.VISIBLE
            resumeSong.visibility=View.GONE

        }


    }




}