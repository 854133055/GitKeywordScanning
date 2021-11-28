package com.mml.plugin.constants

object Constants {

    public final const val SCANNINGLOGCAT_ID = "scanning"
    /**
     * 当前所有git group信息
     */
    public final const val GROUPS = "api/v4/groups/"
    public final const val PROJECT = "/projects?per_page=100"


    public final const val ALLID = -1
    public final const val SELECTED = 1
    public final const val UNSELECTED = 0

    public final const val MMMMMM = 42

    public final const val GitURlKEY = "gitUrl"
    public final const val GITTOKENKEY = "gitToken"
}

enum class TaskType(value : Int) {
    runTask(1),
    selectedSpecialModule(2),
    selectedSpecialGroup(3),
    selectedSpecialPrpject(4)
}