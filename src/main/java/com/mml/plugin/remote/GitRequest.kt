package com.mml.plugin.remote

import com.mml.plugin.constants.Constants

object GitRequest {

    fun getOwnedGroups(callback: MCallback) {
        val url = BaseRequest.gitHost + Constants.GROUPS
        BaseRequest.commonGetRequest(null, url, callback)
    }

    fun getProjectInGroup(groupId: String, callback: MCallback) {
        val url = BaseRequest.gitHost + Constants.GROUPS + groupId + Constants.PROJECT
        BaseRequest.commonGetRequest(null, url, callback)
    }

    fun getGitCode(branch: String, url: String) {
//        ShellExec.run("git clone -b ${branch} ${url}")
    }
}