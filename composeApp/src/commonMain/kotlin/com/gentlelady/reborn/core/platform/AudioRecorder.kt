package com.gentlelady.reborn.core.platform

import androidx.compose.runtime.Composable

/**
 * 마이크 녹음/재생을 다루는 컨트롤러. 실제 녹음 파일은 플랫폼별 구현이 캐시 디렉토리 등에 보관한다.
 */
interface AudioRecorderController {
    val isRecording: Boolean
    val isPlaying: Boolean

    /** 마이크 권한이 없으면 권한 요청 후 승인 시 자동으로 녹음을 시작한다. */
    fun startRecording()
    fun stopRecording()
    fun play()
    fun stopPlayback()

    /** 녹음/재생을 정지하고 저장된 녹음 파일을 버린다. */
    fun discard()
}

@Composable
expect fun rememberAudioRecorder(): AudioRecorderController
