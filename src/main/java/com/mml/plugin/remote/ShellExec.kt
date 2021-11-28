package com.mml.plugin.remote

import com.mml.plugin.shell.Shell
import com.mml.plugin.utils.FileUtil

object ShellExec {

    fun getCloneAndScan(gitUrls: List<String?>?, key: String, branch: String? = "develop",
                        fileType: String = "*.*"): String {
        val commands = StringBuilder()
        commands.append("cd ${FileUtil.getCleanSaveFile().absolutePath} && rm -rf *").append(" && ")
        gitUrls?.forEach {
            commands.append("git clone -b $branch $it").append(" && ")
        }
        commands.append("grep -R -C 1 --color=auto --include \"$fileType\" -E \"${key}\" ./")

        val shell = Shell("sh")
        var result = shell.run(commands.toString())

        if (result.isSuccess) {
            return result.stdout()
        }
        return ""
    }


    fun gitCloneAndScan(gitUrl: String?, key: String, branch: String? = "develop", fileType: String = "*.*") : String {
        return getCloneAndScan(arrayListOf(gitUrl), key, branch, fileType)
    }

}