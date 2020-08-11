package com.lihui.translation.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.mlsdk.tts.MLTtsConstants


class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    //中文音色
    var mSpeakerZh: String = MLTtsConstants.TTS_SPEAKER_MALE_ZH
    //英文音色
    var mSpeakerEn: String = MLTtsConstants.TTS_SPEAKER_MALE_EN
    //语速
    var mSpeed: Float = 1.0f
    //音量
    var mVolume: Float = 1.0f

    //文本翻译源语言
    var mTransSource: String = "zh"
    //文本翻译目标语言
    var mTransTarget: String = "en"
    //语音识别类型
    var mSpeechRecognize: String = "zh"
    //合成语言类型
    var mTTSLanguage: String = MLTtsConstants.TTS_EN_US
    //音色
    var mPerson: String = mSpeakerEn


    var mSpeech = MutableLiveData<Int>()



}
