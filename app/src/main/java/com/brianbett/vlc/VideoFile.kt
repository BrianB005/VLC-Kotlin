package com.brianbett.vlc

import java.io.Serializable

data class VideoFile(val videoName:String,val duration:Long,val videoSize:Long,val videoUri:String,val dateAdded:Long,val folderName:String):Serializable{
    override fun toString(): String {
        return "VideoFile(videoName='$videoName', duration=$duration, videoSize=$videoSize, videoUri='$videoUri', dateAdded=$dateAdded, folderName='$folderName')"
    }
}