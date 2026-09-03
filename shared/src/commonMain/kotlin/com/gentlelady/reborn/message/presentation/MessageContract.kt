package com.gentlelady.reborn.message.presentation

/**
 * Reborn 프로젝트 메시지/방명록 화면 MVI Contract
 */

// 1. 상태(State) 정의: UI가 그려야 할 데이터 명세
data class MessageState(
    val searchQuery: String = "",               // 상단 검색창 입력 값
    val currentTab: MessageTab = MessageTab.MESSAGE, // 현재 선택된 탭 (메시지 vs 방명록)
    val isLoading: Boolean = false,             // 로딩 상태 변경 피드백용
    val chatRooms: List<ChatRoomItem> = emptyList(), // 메시지 탭에 보여줄 채팅방 목록
    val guestBooks: List<GuestBookItem> = emptyList() // 방명록 탭에 보여줄 스레드 목록
)

// 탭 종류를 명확하게 분기하기 위한 Enum 클래스
enum class MessageTab {
    MESSAGE,    // 메시지 탭 (isMemorialMode = false)
    GUEST_BOOK  // 방명록 탭 (isMemorialMode = true)
}

// 2. 인텐트(Intent) 정의: 유저가 메시지/방명록 화면에서 행하는 모든 액션
sealed interface MessageIntent {
    // 상단 검색창 텍스트 변경 이벤트
    data class UpdateSearchQuery(val query: String) : MessageIntent

    // 상단 탭 전환 이벤트 (메시지 탭 <-> 방명록 탭)
    data class SelectTab(val tab: MessageTab) : MessageIntent

    // 상단 검색창 클릭 이벤트 (탭에 따라 사용자 검색 / 방명록 검색 화면으로 전환)
    data object ClickSearchBar : MessageIntent

    // 메시지 채팅방 아이템 클릭 이벤트 (채팅 상세방 화면 전환용)
    data class ClickChatRoom(val roomId: String) : MessageIntent

    // 방명록 아이템 클릭 이벤트 (방명록 상세 혹은 추모 컨텍스트 전환용)
    data class ClickGuestBook(val bookId: String) : MessageIntent

    // 스와이프 혹은 메뉴를 통한 아이템 삭제 이벤트
    data class DeleteItem(val id: String, val tab: MessageTab) : MessageIntent

    object ClickWriteAction : MessageIntent
}

// 3. 도메인 모델 데이터 클래스 (공용 RebornChatRow에 주입될 매핑 데이터)

/**
 * 메시지 탭에 사용되는 채팅방 데이터 구조
 */
data class ChatRoomItem(
    val id: String,
    val name: String,               // 대화 상대방 이름
    val lastMessage: String,        // 최근 메시지 본문
    val timestamp: String,          // 시간 표시 (예: "2h", "오후 2:30")
    val isUnread: Boolean = false,  // 안읽음 여부 (true일 때 블루 배경 및 cobalt dot 노출)
    val avatarUrl: String = ""      // 아바타 이미지 경로 혹은 리소스 키
)

/**
 * 방명록 탭에 사용되는 스레드 데이터 구조
 */
data class GuestBookItem(
    val id: String,
    val deceasedName: String,       // 고인 이름 (UI 상에서 앞에 '故'가 붙고 이름 옆에 🌸가 노출됨)
    val recentContent: String,      // 방명록 내용 요약
    val relativeTime: String,       // 상대적 시간 묘사 (예: "1년 전", "방금 전")
    val avatarUrl: String = "",     // 고인 아바타 이미지 경로 혹은 리소스 키
    val daysAgo: Int = 0            // 최신순 정렬 기준 (작을수록 최근)
)

/**
 * 메시지 탭 검색 화면의 '추천 더보기' 사용자 항목
 */
data class SuggestedUser(
    val id: String,
    val name: String,
    val handle: String,             // 예: "lee_junnnn"
    val avatarUrl: String = ""
)

/**
 * 1:1 대화창의 말풍선 한 개
 */
data class ChatMessage(
    val id: String,
    val text: String,
    val isMine: Boolean,            // true면 우측 파란 말풍선, false면 좌측 회색 말풍선
    val showAvatar: Boolean = false // 상대 말풍선 묶음의 마지막에만 아바타 노출
)