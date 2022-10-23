package com.brianbett.vlc

class ConvertMilliseconds {
    companion object{
        fun convertMilliseconds(timeInMillis:Int):String{
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

            return songDurationString
        }
    }
}