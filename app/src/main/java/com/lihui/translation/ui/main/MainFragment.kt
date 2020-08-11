package com.lihui.translation.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator
import com.huawei.hms.mlsdk.tts.*
import com.lihui.translation.R
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    private var isPermissionGranted: Boolean = false
    private val TAG: String = MainFragment::class.java.simpleName

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mMainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMainViewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        // TODO: Use the ViewModel
        mMainViewModel.mSpeech.observe(activity!!, Observer {
            startASR()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_start.setOnClickListener {
            //Toast.makeText(activity,"source: ${mMainViewModel.mTransSource}",Toast.LENGTH_LONG).show()
            //Log.d(TAG,"zh: ${mMainViewModel.mSpeakerZh}")
            startASR()
        }

        textView_target_text.setOnClickListener {
            val content = textView_target_text.text.toString()
            if(content.isNotEmpty()) {
                startTTS(content)
            }
        }
    }

    private fun startASR() {
        //Toast.makeText(this, "当前： $languageType",Toast.LENGTH_LONG).show()
        textView_source_text.text = ""
        textView_target_text.text = ""
        val intent = Intent(activity, MLAsrCaptureActivity::class.java)
            .putExtra(MLAsrCaptureConstants.LANGUAGE, mMainViewModel.mSpeechRecognize)
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
        startActivityForResult(intent, 100)
    }

    private fun startTranslate(sourceText: String){
        val setting: MLRemoteTranslateSetting =
            MLRemoteTranslateSetting.Factory()
                .setSourceLangCode(mMainViewModel.mTransSource)
                .setTargetLangCode(mMainViewModel.mTransTarget)
                .create()
        val mlRemoteTranslator: MLRemoteTranslator =
            MLTranslatorFactory.getInstance().getRemoteTranslator(setting)

        if(!TextUtils.isEmpty(sourceText)){
            val task: Task<String> =
                mlRemoteTranslator.asyncTranslate(sourceText)
            task.addOnSuccessListener {
                //Toast.makeText(activity, it,Toast.LENGTH_LONG).show()
                textView_target_text.text = it
                startTTS(it)
            }.addOnFailureListener {
                //Got Error
            }
        } else {
            //Empty string
            //Toast.makeText(, "Enter text to speak",Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        val text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        //Toast.makeText(activity, text,Toast.LENGTH_LONG).show()
                        textView_source_text.text = text
                        startTranslate(text)
                    }
                }
                MLAsrCaptureConstants.ASR_FAILURE -> if (data != null) {
                    val bundle = data.extras
                    if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                        val errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE)
                        Log.d(TAG,"Error Code $errorCode")
                    }
                    if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                        val errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)
                        Log.d(TAG,"Error Code $errorMsg")
                    }
                }
                else -> {
                    Log.d(TAG, "Failed to get data")
                }
            }
        }
    }

    private fun startTTS(translatedText: String){
        val mlConfigs = MLTtsConfig()
            .setLanguage(mMainViewModel.mTTSLanguage)
            .setPerson(mMainViewModel.mPerson)
            .setSpeed(1.0f)
            .setVolume(mMainViewModel.mVolume)
        val mlTtsEngine = MLTtsEngine(mlConfigs)
        mlTtsEngine.setTtsCallback(callback)
        mlTtsEngine.speak(translatedText, MLTtsEngine.QUEUE_APPEND)
    }

    private var callback: MLTtsCallback = object : MLTtsCallback {
        override fun onError(taskId: String, err: MLTtsError) {
            Log.d(TAG,"error $err")
        }

        override fun onAudioAvailable(p0: String?, p1: MLTtsAudioFragment?, p2: Int, p3: Pair<Int, Int>?, p4: Bundle?) {
            Log.d(TAG,"audio available")
        }

        override fun onWarn(taskId: String, warn: MLTtsWarn) {
            Log.d(TAG,"onwarn")
        }

        override fun onRangeStart(taskId: String, start: Int, end: Int) {
            Log.d(TAG,"start")
        }

        override fun onEvent(taskId: String, eventName: Int, bundle: Bundle?) {
            if (eventName == MLTtsConstants.EVENT_PLAY_STOP) {
                val isStop = bundle?.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED)
                if (isStop!!)
                    Log.d(TAG, "Manually stopped")
            }
        }
    }
}
