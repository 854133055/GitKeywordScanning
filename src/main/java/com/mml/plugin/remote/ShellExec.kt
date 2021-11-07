package com.mml.plugin.remote

object ShellExec {

    fun run(command : String) {
        Runtime.getRuntime().exec(command)
    }
}