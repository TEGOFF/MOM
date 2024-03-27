package com.example.tm.utilities

import java.text.SimpleDateFormat
import java.util.Date

data class DairyTaskData(
    val dairyTaskName: String="",
    val dairyTaskDescription: String="",
    val dairyTaskId:String ="",
    val notificationTime:String="",
    val date:String="",
    val isDone:Boolean=true)

