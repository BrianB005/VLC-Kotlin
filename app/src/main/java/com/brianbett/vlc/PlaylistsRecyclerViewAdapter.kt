package com.brianbett.vlc

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.brianbett.vlc.databinding.SinglePlayListBinding

class PlaylistsRecyclerViewAdapter(private val model: MyViewModel, private val playLists:ArrayList<PlayList>,private val popupWindow: PopupWindow,private val songToAdd:AudioFile) :
    RecyclerView.Adapter<PlaylistsRecyclerViewAdapter.MyViewHolder>() {
    class MyViewHolder(private val binding:SinglePlayListBinding):RecyclerView.ViewHolder(binding.root){
        val playlistName=binding.playlistName
    }
    private lateinit var context:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding=SinglePlayListBinding.inflate(LayoutInflater.from(parent.context))
        context=parent.context
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.playlistName.text=playLists[position].playlistName
        holder.itemView.setOnClickListener{
            val index=model.playlists.value!!.indexOf((playLists[position]))
            val playlistToAddTo= model.playlists.value!![index]
            model.playlists.value!!.remove(playlistToAddTo)
            playlistToAddTo.songs.add(songToAdd)
            model.playlists.value!!.add(playlistToAddTo)
            Toast.makeText(context,"${songToAdd.songTitle} Added to ${playLists[position].playlistName} playlist", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()

        }
    }

    override fun getItemCount(): Int {
        return playLists.size
    }
}