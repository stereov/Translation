package com.lihui.translation.utils

import android.content.Context.MODE_PRIVATE
import android.content.res.AssetManager
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.lihui.translation.MainActivity
import java.io.*
import java.lang.Exception


class JsonFileUtils {

    companion object {

        fun readJsonFile(activity:MainActivity,fileName: String): String {
            val inputStream: FileInputStream = activity.openFileInput(fileName)
            val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
            val content: StringBuilder = StringBuilder()

            while(true){
                var line = bufferedReader.readLine()?:break
                content.append(line);
            }
            return content.toString()

        }

        fun getValueByKey(activity:MainActivity,fileName: String, key: String): String {
            val inputStream: FileInputStream = activity.openFileInput(fileName)
            val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
            val content: StringBuilder = StringBuilder()

            var line: String = ""

            while(true){
                line = bufferedReader.readLine()?:break
                content.append(line);
            }

            val settingsObject: JSONObject = JSONObject.parseObject(content.toString())

            return settingsObject.getString(key)
        }

        fun saveJsonFile(activity:MainActivity, data: String, fileName: String) {
            Log.d("utils",data)
            val outputStream: FileOutputStream = activity.openFileOutput(fileName, MODE_PRIVATE)

            val out = activity.openFileOutput(fileName, MODE_PRIVATE)
            //out.write(data.toByteArray())
            var writer = BufferedWriter(OutputStreamWriter(out))
            writer.write(data)
            writer.close()
        }
    }
}