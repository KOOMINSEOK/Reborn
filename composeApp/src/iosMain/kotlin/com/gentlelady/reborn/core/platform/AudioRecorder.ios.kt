package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

// TODO: iOS 오디오 녹음 미구현 (프로젝트는 현재 Android 우선으로 진행 중)
private object NoopAudioRecorderController : AudioRecorderController {
    override val isRecording: Boolean = false
    override val isPlaying: Boolean = false
    override fun startRecording() {}
    override fun stopRecording() {}
    override fun play() {}
    override fun stopPlayback() {}
    override fun discard() {}
}

@Composable
actual fun rememberAudioRecorder(): AudioRecorderController = NoopAudioRecorderController
