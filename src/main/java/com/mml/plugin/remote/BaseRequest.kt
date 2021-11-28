package com.mml.plugin.remote

import com.alibaba.fastjson.JSONObject
import com.intellij.ide.plugins.PluginManager
import com.mml.plugin.utils.HttpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.apache.http.client.HttpResponseException
import java.io.IOException

object BaseRequest {

    var token: String = ""
    var gitHost: String = ""

    fun commonGetRequest(params: JSONObject?, url: String, listener: MCallback) {
        val request = Request.Builder()
            .addHeader("PRIVATE-TOKEN", token)
            .url(url)
            .build()

        HttpUtil.getInstance().newCall(request).enqueue(object : MCallback() {
            override fun onResponse(call: Call, response: Response) {
                if (response.body() != null && response.code() == 200) {
                    listener.onSuccess(response)
                } else {
                    listener.onFailure(call, HttpResponseException(response.code(), response.message()))
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                listener.onFailure(call, e)
            }
        })
    }

}

open class MCallback : Callback {
    override fun onFailure(call: Call, e: IOException) {
        PluginManager.getLogger().error("MyPluginLog", e.message)
    }

    override fun onResponse(call: Call, response: Response) {}

    open fun onSuccess(response: Response) {
        PluginManager.getLogger().info("NetLog${response.body()?.string()}")
    }

}