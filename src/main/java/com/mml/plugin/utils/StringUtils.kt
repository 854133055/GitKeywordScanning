package com.mml.plugin.utils

object StringUtils {
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    private val logcatContent = StringBuilder()


}