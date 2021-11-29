package com.mml.plugin.utils

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.util.io.FileUtilRt
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path


object FileUtil {

    fun saveConfigInfo(key: String, value: String?) {
        PropertiesComponent.getInstance().setValue(key, value)
    }

    fun getConfigInfo(key: String): String {
        return PropertiesComponent.getInstance().getValue(key) ?: ""
    }

    fun getSavePath(): String {
        val file = File(FileUtilRt.getTempDirectory(), "gitCodeScan")
        if (!file.exists()) {
            file.mkdir()
        }
        return file.absolutePath
    }

    fun getCleanSaveFile(): File {
        val file = File(FileUtilRt.getTempDirectory(), "gitCodeScan")
        if (!file.exists()) {
            file.mkdir()
        } else {
            cleanSavePathFile(file.toPath())
        }
        return file
    }

    @Throws(IOException::class)
    fun cleanSavePathFile(path: Path?) {
        path?.apply {
            if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                Files.newDirectoryStream(path).use { entries ->
                    for (entry in entries) {
                        cleanSavePathFile(entry)
                    }
                }
            }
            if (path.toAbsolutePath().toString() != getSavePath()) {
                Files.delete(path)
            }
        }
    }

}