package com.lihui.translation

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.alibaba.fastjson.JSONObject
import com.huawei.hms.mlsdk.tts.MLTtsConstants
import com.lihui.translation.ui.main.MainFragment
import com.lihui.translation.ui.main.MainViewModel
import com.lihui.translation.utils.Constants
import com.lihui.translation.utils.FileAssistant
import com.lihui.translation.utils.JsonFileUtils


class MainActivity : AppCompatActivity() {

    private lateinit var mTranslationSource: TextView
    private lateinit var mTranslationTarget: TextView
    private lateinit var mSwap: Button
    private lateinit var mSetting: Button

    private lateinit var mFragViewModel: MainViewModel
    private lateinit var mAssetManager: AssetManager
    private val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        //获取actionbar 自定义actionbar
        val actionBar = supportActionBar
        lateinit var storageCode: String
        var sourceCode: String = "zh"
        var targetCode: String = "en"

        if(actionBar != null) {
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_layout)  //绑定自定义的布局：actionbar_layout.xml

            mTranslationSource = actionBar.customView.findViewById(R.id.textView_translation_source)
            mTranslationTarget = actionBar.customView.findViewById(R.id.textView_translation_target)
            mSwap = actionBar.customView.findViewById(R.id.button_swap)
            mSetting = actionBar.customView.findViewById(R.id.button_setting)


            //点击交换按钮事件，交换source和target
            mSwap.setOnClickListener {
                storageCode = sourceCode
                sourceCode = targetCode
                targetCode = storageCode

                mTranslationSource.text = Constants.mSourceTargetName[sourceCode]
                mTranslationTarget.text = Constants.mSourceTargetName[targetCode]

                mFragViewModel.mTransSource = sourceCode
                mFragViewModel.mTransTarget = targetCode

                if(sourceCode == "zh") {
                    mFragViewModel.mSpeechRecognize = "zh"
                    mFragViewModel.mTTSLanguage = MLTtsConstants.TTS_EN_US
                    mFragViewModel.mPerson = mFragViewModel.mSpeakerEn
                }else {
                    mFragViewModel.mSpeechRecognize = "en-US"
                    mFragViewModel.mTTSLanguage = MLTtsConstants.TTS_ZH_HANS
                    mFragViewModel.mPerson = mFragViewModel.mSpeakerZh
                }

            }

            mSetting.setOnClickListener {
                createSettingDialog()
            }

            //JsonFileUtils.saveJsonFile(this,"","setting.json")
            // 根据自己的报名修改目录。
            // 如果不存在配置文件，则新建
            if(FileAssistant.isExist("/data/data/com.lihui.translation/files/setting.json")) {
                Log.d(TAG,"文件存在")
            }else{
                Log.d(TAG,"文件不存在")
                JsonFileUtils.saveJsonFile(this,"","setting.json")
            }

        }

        mFragViewModel = ViewModelProvider(this).get<MainViewModel>(MainViewModel::class.java)

        mAssetManager = assets

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }

    // 新建设置对话框
    @SuppressLint("InflateParams")
    private fun createSettingDialog() {

        val dialogLayout = layoutInflater.inflate(R.layout.setting_dialog_layout,null)

        val speakerZH: RadioGroup = dialogLayout.findViewById(R.id.speaker_zh)
        val speakerZHMale: RadioButton = dialogLayout.findViewById(R.id.speaker_zh_male)
        val speakerZHFeMale: RadioButton = dialogLayout.findViewById(R.id.speaker_zh_female)

        val speakerEN: RadioGroup = dialogLayout.findViewById(R.id.speaker_en)
        val speakerENMale: RadioButton = dialogLayout.findViewById(R.id.speaker_en_male)
        val speakerENFeMale: RadioButton = dialogLayout.findViewById(R.id.speaker_en_female)

        val speakSpeedSeekBar: SeekBar = dialogLayout.findViewById(R.id.seekBar_speed)
        val speakVolumeSeekBar: SeekBar = dialogLayout.findViewById(R.id.seekBar_volume)

        val speakSpeedValue: TextView = dialogLayout.findViewById(R.id.textView_speed_value)
        val speakVolumeValue: TextView = dialogLayout.findViewById(R.id.textView_volume_value)

        val settingsJson = JsonFileUtils.readJsonFile(this,"setting.json")
        if(settingsJson.isNotEmpty()) {
            val settingsObject: JSONObject = JSONObject.parseObject(settingsJson)
            if(settingsObject.getString("speakerZH") == MLTtsConstants.TTS_SPEAKER_MALE_ZH) {
                speakerZHMale.isChecked = true
            }else {
                speakerZHFeMale.isChecked = true
            }
            if(settingsObject.getString("speakerEN") == MLTtsConstants.TTS_SPEAKER_MALE_EN) {
                speakerENMale.isChecked = true
            }else {
                speakerENFeMale.isChecked = true
            }

            speakSpeedSeekBar.progress = (settingsObject.getFloat("speed") * 10).toInt()
            speakSpeedValue.text = settingsObject.getFloat("speed").toString()

            speakVolumeSeekBar.progress = (settingsObject.getFloat("volume").toFloat() * 10).toInt()
            speakVolumeValue.text = settingsObject.getString("volume")

        }

        speakSpeedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG,"value $progress")
                speakSpeedValue.text = (progress.toFloat() / 10.0).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG,"start")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG,"stop")
            }

        })

        speakVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG,"value $progress")
                speakVolumeValue.text = (progress.toFloat() / 10.0).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG,"start")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG,"stop")
            }

        })

        /*speakerZH.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.speaker_zh_male -> {
                    Log.d(TAG,"radio: zh male")
                    mFragViewModel.mSpeakerZh = MLTtsConstants.TTS_SPEAKER_MALE_ZH
                }
                R.id.speaker_zh_female -> {
                    Log.d(TAG,"radio: zh female")
                    mFragViewModel.mSpeakerZh = MLTtsConstants.TTS_SPEAKER_FEMALE_ZH
                }
            }
        }*/
        val currentSettings: JSONObject = JSONObject()
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("设置")
            .setView(dialogLayout)
            .setPositiveButton(R.string.dialog_ok, DialogInterface.OnClickListener { _, _ ->
                //Toast.makeText(this,"对话框显示成功",Toast.LENGTH_LONG).show()
                //获取中文音色
                when(speakerZH.checkedRadioButtonId) {
                    R.id.speaker_zh_male -> {
                        //Log.d(TAG,"radio: zh male")
                        mFragViewModel.mSpeakerZh = MLTtsConstants.TTS_SPEAKER_MALE_ZH
                        if(mFragViewModel.mSpeechRecognize == "en-US") mFragViewModel.mPerson = MLTtsConstants.TTS_SPEAKER_MALE_ZH
                    }
                    R.id.speaker_zh_female -> {
                        //Log.d(TAG,"radio: zh female")
                        mFragViewModel.mSpeakerZh = MLTtsConstants.TTS_SPEAKER_FEMALE_ZH
                        if(mFragViewModel.mSpeechRecognize == "en-US") mFragViewModel.mPerson = MLTtsConstants.TTS_SPEAKER_FEMALE_ZH
                    }
                }
                //获取英文音色
                when(speakerEN.checkedRadioButtonId) {
                    R.id.speaker_en_male -> {
                        //Log.d(TAG,"radio: zh male")
                        mFragViewModel.mSpeakerEn = MLTtsConstants.TTS_SPEAKER_MALE_EN
                        if(mFragViewModel.mSpeechRecognize == "zh") mFragViewModel.mPerson = MLTtsConstants.TTS_SPEAKER_MALE_EN
                    }
                    R.id.speaker_en_female -> {
                        //Log.d(TAG,"radio: zh female")
                        mFragViewModel.mSpeakerEn = MLTtsConstants.TTS_SPEAKER_FEMALE_EN
                        if(mFragViewModel.mSpeechRecognize == "zh") mFragViewModel.mPerson = MLTtsConstants.TTS_SPEAKER_FEMALE_EN
                    }
                }
                mFragViewModel.mSpeed = speakSpeedValue.text.toString().toFloat()
                mFragViewModel.mVolume = speakVolumeValue.text.toString().toFloat()

                currentSettings["speakerZH"] = mFragViewModel.mSpeakerZh
                currentSettings["speakerEN"] = mFragViewModel.mSpeakerEn
                currentSettings["speed"] = mFragViewModel.mSpeed
                currentSettings["volume"] = mFragViewModel.mVolume

                JsonFileUtils.saveJsonFile(this, currentSettings.toString(),"setting.json")

            })
            .setNegativeButton(R.string.dialog_exit,null)
            .create()
            .show()
    }

}
