package com.brianbett.vlc

import kotlin.math.roundToInt

class ConvertFileSize {
    companion object{
        fun convertBytes(numberOfBytes:Int):String{
            val numberOfKB=numberOfBytes/1024
            val convertedSize = if(numberOfKB>1024){

                val size = if(numberOfKB/1024>1024){
                    val numberOfGB=(numberOfKB.toFloat()/(1024*1024))
                    val roundedToTwoDP=(numberOfGB*100.0).roundToInt()/100.0
                    "$roundedToTwoDP GB "
                }else{
                    val numberOfMB=(numberOfKB.toFloat()/1024)
                    val roundedToTwoDP=(numberOfMB*100.0).roundToInt()/100.0
                    "$roundedToTwoDP MB"
                }


                "$size "
            }else{
                "$numberOfKB KB"
            }
            return convertedSize
        }
    }
}