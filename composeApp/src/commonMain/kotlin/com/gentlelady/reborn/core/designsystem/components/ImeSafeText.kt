package com.gentlelady.reborn.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/** 아직 화면 상태에서 돌아오지 않은, 칸이 보낸 글을 이만큼까지 기억 */
private const val MAX_IN_FLIGHT = 64

/**
 * 입력칸용. 화면 상태(ViewModel)를 거쳐 한 박자 늦게 돌아오는 글을 그대로 칸에 넣으면
 * 한글 조합(받침 등)이 끊기고, 빨리 치면 늦게 돌아온 옛 글이 방금 친 글자를 지운다.
 * 그래서 칸이 글을 직접 들고 있고, 밖의 값은 "칸이 보낸 게 아닌 것"(지우기·보낸 뒤 비우기·하이픈 넣기)일 때만 받아들인다 — 커서는 끝으로.
 * [accept] 가 false 인 입력(글자 수 초과 등)은 칸에도 안 들어간다.
 * 쓰는 법: `val (text, onText) = rememberImeSafeText(value, onValueChange)` → `BasicTextField(value = text, onValueChange = onText)`
 */
@Composable
fun rememberImeSafeText(
    value: String,
    onValueChange: (String) -> Unit,
    accept: (String) -> Boolean = { true },
): Pair<TextFieldValue, (TextFieldValue) -> Unit> {
    var field by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    // 칸이 보냈고 아직 돌아오지 않은 글 (보낸 순서)
    val inFlight = remember { ArrayDeque<String>() }
    LaunchedEffect(value) {
        val i = inFlight.indexOf(value)
        if (i >= 0) {
            // 칸이 보낸 글이 돌아왔다 (그보다 먼저 보낸 것들은 건너뛰고 늦게 온 것) — 칸이 이미 앞서 있다
            repeat(i + 1) { inFlight.removeFirst() }
        } else if (value != field.text) {
            inFlight.clear()
            field = TextFieldValue(value, TextRange(value.length))
        }
    }
    return field to { next ->
        if (accept(next.text)) {
            field = next
            if (next.text != value) {
                inFlight.addLast(next.text)
                if (inFlight.size > MAX_IN_FLIGHT) inFlight.removeFirst()
                onValueChange(next.text)
            }
        }
    }
}
