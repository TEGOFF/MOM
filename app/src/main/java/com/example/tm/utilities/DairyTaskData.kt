package com.example.tm.utilities

data class DairyTaskData(
    val dairyTaskName: String="",
    val dairyTaskDescription: String="",
    val dairyTaskId:String ="",
    val notificationTime:String="",
    val date:String="",
    val isDone:Boolean=true,
    val category: String= "")

