package com.example.tm.utilities

data class DairyTaskData(
    var dairyTaskName: String="",
    val dairyTaskDescription: String="",
    val dairyTaskId:String ="",
    val notificationTime:String="",
    val date:String="",
    val isDone:Boolean=true,
    val category: String= "",
    var containsSub: Boolean = false)

