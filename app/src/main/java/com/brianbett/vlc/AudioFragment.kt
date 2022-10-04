package com.brianbett.vlc

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.util.*


class AudioFragment : Fragment() {
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
                }else{
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,onBackPressedCallback)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_audio, container, false)
        val tabLayout=rootView.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2=rootView.findViewById<ViewPager2>(R.id.view_pager)


        val shuffleBtn=rootView.findViewById<FloatingActionButton>(R.id.shuffle)
        val mediaPlayer=rootView.findViewById<LinearLayout>(R.id.media_player)
        val pauseSong=rootView.findViewById<TextView>(R.id.pause_song)
        val resumeSong=rootView.findViewById<TextView>(R.id.resume_playback)
        val songDuration=rootView.findViewById<TextView>(R.id.remaining_time)
        val progressView=rootView.findViewById<ProgressBar>(R.id.song_progress_view)
        val songName=rootView.findViewById<TextView>(R.id.song_name)

        //        accessing view model
        model = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val toolbar=rootView.findViewById<MaterialToolbar>(R.id.tool_bar)
//        accessing toolbar menu
//        toolbar.setOnMenuItemClickListener{menuItem->
//            when (menuItem.itemId){
//                R.id.sort_by_date_ascending->{
//                    sortList(model,"DATE_ASC")
//                    toolbar.menu.findItem(R.id.sort_by_date_descending).isVisible=true
//                    toolbar.menu.findItem(R.id.sort_by_date_ascending).isVisible=false
//                }
//                R.id.sort_by_date_descending->{
//                    sortList(model,"DATE_DESC")
//                    toolbar.menu.findItem(R.id.sort_by_date_descending).isVisible=false
//                    toolbar.menu.findItem(R.id.sort_by_date_ascending).isVisible=true
//                }
//                R.id.sort_by_length->sortList(model,"LENGTH")
//                else->sortList(model,"NAME_ASC")
//            }
//            return@setOnMenuItemClickListener false
//        }


//        observing changes in current song playing
        val songObserver=Observer<AudioFile>{currentSong->
            mediaPlayer.visibility=View.VISIBLE
            shuffleBtn.visibility=View.VISIBLE
            songName.text=currentSong.songTitle
            val songDurationString=convertMilliseconds(currentSong.songDuration.toInt())
            songDuration.text=songDurationString

            //        observing progress
            val progressObserver=Observer<Int>{currentProgress->
                progressView.max= currentSong.songDuration.toInt()
                progressView.progress = currentProgress
            }
            model.currentSongProgress.observe(viewLifecycleOwner,progressObserver)
        }
        model.currentSong.observe(viewLifecycleOwner,songObserver)

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



        popupView=inflater.inflate(R.layout.audio_player_popup,container,false)
        popupWindow=PopupWindow(popupView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        mediaPlayer.setOnClickListener{
            popupView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_fast))
            popupWindow.showAtLocation(it, Gravity.BOTTOM, 0, 0)
            handlePopupWindow()

        }
        val viewPagerAdapter=ViewPagerAdapter(this,tabLayout)
        viewPager2.adapter=viewPagerAdapter
//        viewPager2.currentItem=2
//        tabLayout.getTabAt(2)!!.select()


        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem=tab!!.position


            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
                tabLayout.getTabAt(position)!!.select()
                super.onPageSelected(position)
            }
        })
        return rootView
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

        songProgress.setOnSeekBarChangeListener( object :SeekBar.OnSeekBarChangeListener{
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
            val songDurationString=convertMilliseconds(currentSong.songDuration.toInt())
            songDuration.text=songDurationString
            //        observing progress
            val progressObserver=Observer<Int>{currentProgress->
                songProgress.max= currentSong.songDuration.toInt()
                songProgress.progress = currentProgress
                val remainingTimeMs=currentSong.songDuration.toInt()-currentProgress
                val remainingTime=convertMilliseconds(remainingTimeMs)
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
    private fun convertMilliseconds(timeInMillis:Int):String{
        val audioDurationMinutes =timeInMillis / 60000
        val audioDurationInSeconds=timeInMillis % 60000 / 1000
        val formattedSeconds=if(audioDurationInSeconds<10) "0$audioDurationInSeconds" else audioDurationInSeconds
        val songDurationString:String = if(audioDurationMinutes>60){
            val hours=audioDurationMinutes/60
            val minutes= audioDurationMinutes%60
            val formattedMinutes=if(minutes<10) "0$minutes" else minutes

            "$hours:$formattedMinutes:$formattedSeconds"
        }else{
            "$audioDurationMinutes:$formattedSeconds"
        }

        return songDurationString.format("%. 2f")
    }
    private fun sortList(model:MyViewModel,sortOrder:String){
        val listToSort=model.songsToPlay.value
        when (sortOrder) {
            "LENGTH"->Collections.sort(
                listToSort!!,
                kotlin.Comparator { audioFile1, audioFile2 -> return@Comparator audioFile1.songDuration.toInt() - audioFile2.songDuration.toInt() })
            "NAME_ASC"->Collections.sort(
                listToSort!!,
                kotlin.Comparator { audioFile1, audioFile2 -> return@Comparator audioFile1.songTitle.compareTo( audioFile2.songTitle) })
            "NAME_DESC"->Collections.sort(
                listToSort!!,
                kotlin.Comparator { audioFile1, audioFile2 -> return@Comparator audioFile1.songTitle.compareTo( audioFile2.songTitle) })
            "DATE_DESC"->Collections.sort(
                listToSort!!,
                kotlin.Comparator { audioFile1, audioFile2 -> return@Comparator audioFile2.dateAdded.toInt()-audioFile1.dateAdded.toInt() })
            "DATE_ASC"->Collections.sort(
                listToSort!!,
                kotlin.Comparator { audioFile1, audioFile2 -> return@Comparator audioFile1.dateAdded.toInt()-audioFile2.dateAdded.toInt() })
//            else ->Collections.sort(
//                listToSort!!,
//                kotlin.Comparator { audioFile1, audioFile2 -> return@Comparator audioFile1.songArtist.compareTo( audioFile2.songArtist) })

        }
        model.songsToPlay.value=listToSort
    }
}