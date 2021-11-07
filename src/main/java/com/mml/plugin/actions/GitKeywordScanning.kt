package com.mml.plugin.actions

import com.alibaba.fastjson.JSONArray
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.io.FileUtilRt
import com.mml.plugin.SimpleDialog
import com.mml.plugin.constants.Constants
import com.mml.plugin.constants.Constants.ALLID
import com.mml.plugin.constants.TaskType
import com.mml.plugin.remote.BaseRequest
import com.mml.plugin.remote.GitRequest
import com.mml.plugin.remote.MCallback
import com.mml.plugin.remote.resp.GitInfo
import okhttp3.Call
import okhttp3.Response
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException

//3XbMAw12WfJw3z4p7o-V
class GitKeywordScanning : AnAction(), ActionListener {

    private var dialog: SimpleDialog? = null
    private var groupsList : MutableList<GitInfo>? = null
    private var projectsList : MutableList<GitInfo>? = null

    private var isSpecialModule = false //是否指定单个项目
    private var groupsId : Int? = ALLID //选中的group。默认为全部
    private var projectId : Int? = ALLID //选中的group。默认为全部

    override fun actionPerformed(e: AnActionEvent) {
        dialog = SimpleDialog(this)
        dialog?.setFileType(getFileTypeList())
        dialog?.setSize(700, 180)
        dialog?.setCenter(e.project)
        dialog?.show()
    }

    private fun getFileTypeList(): ArrayList<String> {
        return arrayListOf("*.java", "*.kt", "*.gradle", "*.xml", "*.properties")
    }

    /**
     * 执行扫描动作
     */
    override fun actionPerformed(p0: ActionEvent?) {
        when (p0?.id) {
            TaskType.runTask.ordinal -> {
                beginScan()
            }
            TaskType.selectedSpecialModule.ordinal -> {
                selectedSpecialModule(p0)
            }
            TaskType.selectedSpecialGroup.ordinal -> {
                selectedSpecialGroup(p0)
            }
            TaskType.selectedSpecialPrpject.ordinal -> {
                selectedSpecialProject(p0)
            }
        }
    }

    private fun beginScan() {
        if (!isSpecialModule || (isSpecialModule && groupsId == ALLID)) {
            //扫描所有组
            val file = FileUtilRt.createTempDirectory("gitCodeScan", "")
            PluginManager.getLogger().info("MyPluginLog tmp path: ${file.absolutePath}")
        } else if (isSpecialModule && groupsId != ALLID && projectId == ALLID) {
            //扫描指定组下所有project
        } else if (isSpecialModule && groupsId != ALLID && projectId != ALLID) {
            //扫描指定project
//            GitRequest.getGitCode("develop", )
        }
    }

    /**
     * 选中指定某个项目
     */
    private fun selectedSpecialModule(p0: ActionEvent?) {
        if (p0?.source == Constants.UNSELECTED) {
            isSpecialModule = false
            return
        }
        isSpecialModule = true
        val gitlabHost = dialog?.gitlabHost
        val gitlabToken = dialog?.gitlabToken
        BaseRequest.gitHost = gitlabHost!!
        BaseRequest.token = gitlabToken!!
        GitRequest.getOwnedGroups(object : MCallback() {
            override fun onSuccess(response: Response) {
                val moduleList: MutableList<GitInfo> = JSONArray.parseArray(response.body()?.string(), GitInfo::class.java)
                moduleList.add(0, GitInfo(ALLID, "all"))
                groupsList = moduleList
                dialog?.setGitGroupList(moduleList as java.util.ArrayList<GitInfo>?)
            }
        })
    }

    /**
     * 指定某个git group组件
     */
    private fun selectedSpecialGroup(p0: ActionEvent?) {
        val index = p0?.source as Int
        if (index < 0) return
        val gitInfo = groupsList?.get(index)
        if (gitInfo != null && gitInfo.id != ALLID) {
            groupsId = gitInfo.id
            GitRequest.getProjectInGroup(gitInfo.id?.toString()?:"", object : MCallback() {
                override fun onSuccess(response: Response) {
                    val projectList: MutableList<GitInfo> = JSONArray.parseArray(response.body()?.string(), GitInfo::class.java)
                    projectList.add(0, GitInfo(ALLID, "all"))
                    projectsList = projectList
                    dialog?.setGitProjectList(projectList as java.util.ArrayList<GitInfo>?)
                }
            })
        }
    }

    /**
     * 指定了某个git project
     */
    private fun selectedSpecialProject(p0: ActionEvent?) {
        val index = p0?.source as Int
        if (index < 0) return
        val gitInfo = projectsList?.get(index)
        if (gitInfo != null && gitInfo.id != ALLID) {
            projectId = gitInfo.id
        }
    }
}

