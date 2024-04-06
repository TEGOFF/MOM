package DataClasses

data class DairyTaskData(
    var dairyTaskName: String="",
    val dairyTaskDescription: String="",
    val dairyTaskId:String ="",
    val notificationTime:String="",
    val date:String="",
    val isDone:Boolean=false,
    val category: String= "",
    var containsSub: Boolean = false)

