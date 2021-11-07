package com.mml.plugin.remote.resp

class GitInfo {
    var id : Int? = null
    var name : String? = null
    var full_path : String? = null //group 参数
    var ssh_url_to_repo: String? = null //project参数
    var http_url_to_repo: String? = null //project参数

    constructor()
    constructor(id: Int?, name: String?) {
        this.id = id
        this.name = name
    }

    constructor(id: Int?, name: String?, ssh_url_to_repo: String?, http_url_to_repo: String?) {
        this.id = id
        this.name = name
        this.ssh_url_to_repo = ssh_url_to_repo
        this.http_url_to_repo = http_url_to_repo
    }


}