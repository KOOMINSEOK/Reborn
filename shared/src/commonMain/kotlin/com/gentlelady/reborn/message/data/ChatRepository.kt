package com.gentlelady.reborn.message.data

import com.gentlelady.reborn.core.network.RebornApi
import com.gentlelady.reborn.core.network.dto.ConversationDto
import com.gentlelady.reborn.core.network.dto.MessageDto
import com.gentlelady.reborn.core.network.dto.UserSummaryDto
import com.gentlelady.reborn.core.network.dto.toAbsoluteServerUrl
import com.gentlelady.reborn.message.presentation.ChatRoomItem
import com.gentlelady.reborn.message.presentation.SuggestedUser
import com.gentlelady.reborn.notification.relativeTime

/** 대화 상대 + 대화방 */
data class ChatPeer(val conversationId: String, val userId: String, val name: String, val avatarUrl: String?)

/** 서버 메시지 한 개. [cursor] = 이 메시지 다음(또는 이전)을 물을 때 쓰는 값 */
data class ChatLine(val id: String, val senderId: String, val text: String, val createdAt: String) {
    val cursor get() = "${createdAt}_$id"
}

/** 1:1 메시지. 목업 폴백 없음 — 실패는 예외 (화면이 알려준다). */
interface ChatRepository {
    suspend fun rooms(): List<ChatRoomItem>
    suspend fun hide(conversationId: String)
    /** 그 사람과의 대화방 (없으면 만든다) */
    suspend fun openWith(userId: String): ChatPeer
    suspend fun room(conversationId: String): ChatPeer
    /** 최신순 한 페이지 + 더 오래된 걸 받을 커서 (null 이면 끝) */
    suspend fun latest(conversationId: String, before: String? = null): Pair<List<ChatLine>, String?>
    /** [after] 다음에 온 메시지 (오래된 순) */
    suspend fun newer(conversationId: String, after: String): List<ChatLine>
    suspend fun send(conversationId: String, text: String): ChatLine
    suspend fun markRead(conversationId: String)
    suspend fun search(query: String): List<SuggestedUser>
    /** 내가 팔로우하는 사람 (검색어가 없을 때 추천으로) */
    suspend fun following(myId: String): List<SuggestedUser>
}

class ChatRepositoryImpl(private val api: RebornApi) : ChatRepository {
    override suspend fun rooms() = api.conversations().items.map { it.toRoom() }
    override suspend fun hide(conversationId: String) = api.hideConversation(conversationId)
    override suspend fun openWith(userId: String) = api.openConversation(userId).toPeer()
    override suspend fun room(conversationId: String) = api.conversation(conversationId).toPeer()

    override suspend fun latest(conversationId: String, before: String?) =
        api.messages(conversationId, before = before).let { page -> page.items.map { it.toLine() } to page.nextCursor }

    override suspend fun newer(conversationId: String, after: String) =
        api.messages(conversationId, after = after).items.map { it.toLine() }

    override suspend fun send(conversationId: String, text: String) = api.sendMessage(conversationId, text).toLine()
    override suspend fun markRead(conversationId: String) = api.markConversationRead(conversationId)
    override suspend fun search(query: String) = api.searchUsers(query).items.map { it.toSuggested() }
    override suspend fun following(myId: String) = api.userRelations(myId, following = true).items.map { it.toSuggested() }
}

private fun ConversationDto.toRoom() = ChatRoomItem(
    id = id,
    name = other.displayName,
    lastMessage = lastMessage.orEmpty(),
    timestamp = lastMessageAt?.let { relativeTime(it) }.orEmpty(),
    isUnread = unread,
    avatarUrl = other.avatarUrl.toAbsoluteServerUrl().orEmpty(),
)

private fun ConversationDto.toPeer() = ChatPeer(id, other.id, other.displayName, other.avatarUrl.toAbsoluteServerUrl())

private fun MessageDto.toLine() = ChatLine(id, senderId, body, createdAt)

private fun UserSummaryDto.toSuggested() =
    SuggestedUser(id = id, name = displayName, handle = handle, avatarUrl = avatarUrl.toAbsoluteServerUrl().orEmpty())
