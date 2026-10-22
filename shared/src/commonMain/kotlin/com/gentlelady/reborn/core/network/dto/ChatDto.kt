package com.gentlelady.reborn.core.network.dto

import kotlinx.serialization.Serializable

/** 서버 chat/ChatRepository 응답들과 1:1. */
@Serializable
data class ChatUserDto(val id: String, val handle: String, val displayName: String, val avatarUrl: String? = null)

@Serializable
data class ConversationDto(
    val id: String,
    val other: ChatUserDto,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unread: Boolean = false,
)

@Serializable
data class ConversationListDto(val items: List<ConversationDto>)

@Serializable
data class MessageDto(val id: String, val senderId: String, val body: String, val createdAt: String)

@Serializable
data class MessageListDto(val items: List<MessageDto>, val nextCursor: String? = null)

@Serializable
data class OpenConversationDto(val userId: String)

/** [clientId] = 메시지마다 앱이 만든 id — 응답을 못 받아 다시 보내도 서버에 한 번만 */
@Serializable
data class SendMessageDto(val body: String, val clientId: String)
