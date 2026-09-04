package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.datetime.LocalDate

/**
 * 사후 게시글 예약 설정(공개 시점/범위, 대상 팔로워)을 화면 간 공유하기 위한 상태 홀더.
 * 서버 예약 API 연동 전까지는 화면 UI 상태만 들고 있는다.
 */
class FeedWriteViewModel : ViewModel() {
    var scheduleOption by mutableStateOf(ScheduleOption.ONE_WEEK)
    var customDate by mutableStateOf<LocalDate?>(null)
    var visibility by mutableStateOf(PostVisibility.FOLLOWERS)
    var selectedFollowerIds by mutableStateOf<Set<String>>(emptySet())
}
