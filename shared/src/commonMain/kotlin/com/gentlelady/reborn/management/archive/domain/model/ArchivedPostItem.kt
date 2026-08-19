package com.gentlelady.reborn.management.archive.domain.model

import org.jetbrains.compose.resources.DrawableResource

/**
 * 피드에서 '숨기기' 처리했지만 삭제하지는 않은 게시물 한 건.
 * 보관함(Archive)에서만 다시 노출되며, 삭제 전까지는 사용자의 게시물로 유지된다.
 */
data class ArchivedPostItem(
    val id: String,
    val thumbnail: DrawableResource
)
