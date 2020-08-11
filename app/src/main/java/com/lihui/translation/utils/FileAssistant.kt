package com.lihui.translation.utils

import java.io.File

class FileAssistant {
    companion object {
        @Throws(Exception::class)
        fun isExist(fileName: String): Boolean {
            try {
                val f = File(fileName)
                if (!f.exists()) {
                    //f.createNewFile()
                    return false
                }
            } catch (e: Exception) { // TODO: handle exception
                return false
            }
            return true
        }
    }
}