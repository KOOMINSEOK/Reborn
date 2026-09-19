package com.gentlelady.reborn.core.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File

private class AndroidAudioRecorderController(private val context: Context) : AudioRecorderController {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var outputFile: File? = null

    var isRecordingState by mutableStateOf(false)
    var isPlayingState by mutableStateOf(false)

    override val isRecording: Boolean get() = isRecordingState
    override val isPlaying: Boolean get() = isPlayingState

    /** rememberAudioRecorder가 매 리컴포지션마다 최신 launcher로 갱신한다. */
    var requestPermission: (() -> Unit)? = null

    fun hasRecordPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    override fun startRecording() {
        if (isRecordingState) return
        if (!hasRecordPermission()) {
            requestPermission?.invoke()
            return
        }
        startRecordingInternal()
    }

    fun startRecordingInternal() {
        val file = File.createTempFile("voice_", ".m4a", context.cacheDir)
        outputFile = file
        recorder = newMediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        isRecordingState = true
    }

    private fun newMediaRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else @Suppress("DEPRECATION") MediaRecorder()

    override fun stopRecording() {
        if (!isRecordingState) return
        try {
            recorder?.stop()
        } catch (_: RuntimeException) {
            // 녹음 시작 직후 바로 정지되는 등 유효한 프레임이 없으면 stop()이 예외를 던진다.
            outputFile?.delete()
            outputFile = null
        }
        recorder?.release()
        recorder = null
        isRecordingState = false
    }

    override fun play() {
        val file = outputFile ?: return
        stopPlayback()
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener { isPlayingState = false }
            prepare()
            start()
        }
        isPlayingState = true
    }

    override fun stopPlayback() {
        player?.apply { stop(); release() }
        player = null
        isPlayingState = false
    }

    override fun discard() {
        if (isRecordingState) stopRecording()
        stopPlayback()
        outputFile?.delete()
        outputFile = null
    }
}

@Composable
actual fun rememberAudioRecorder(): AudioRecorderController {
    val context = LocalContext.current
    val controller = remember { AndroidAudioRecorderController(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) controller.startRecordingInternal()
    }
    controller.requestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }

    return controller
}
