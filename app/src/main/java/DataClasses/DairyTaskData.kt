package DataClasses

data class DairyTaskData(
    var dairyTaskName: String="",
    val dairyTaskDescription: String="",
    val dairyTaskId:String ="",
    val notificationTime:String="",
    val date:String="",
    var isDone:Boolean=true,
    val category: String= "",
    var containsSub: Boolean = false)

