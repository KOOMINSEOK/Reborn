package com.gentlelady.reborn.feature.management.profile_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.core.theme.RebornWhite
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 닉네임 / 한 줄 소개처럼 글자 하나를 고치는 공용 편집 화면.
 * 입력 중인 글자는 화면 로컬 상태, 저장 결과(진행·오류·완료)는 [isSaving] / [error] / [savedToken] 으로 받는다.
 * [savedToken] 이 화면을 연 뒤에 바뀌면 저장 성공 → [onSaved] (보통 뒤로 가기).
 */
@Composable
fun ProfileTextEditScreen(
    title: String,
    initialValue: String,
    maxLength: Int,
    singleLine: Boolean,
    placeholder: String,
    isSaving: Boolean,
    error: String?,
    savedToken: Int,
    onSave: (String) -> Unit,
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
) {
    var text by remember { mutableStateOf(initialValue) }
    val openedToken = remember { savedToken }
    LaunchedEffect(savedToken) {
        if (savedToken != openedToken) onSaved()
    }
    val changed = text.trim() != initialValue.trim()

    Scaffold(
        containerColor = RebornWhite,
        topBar = { RebornBackTopAppBar(title = title, onBackClick = onBackClick) },
        bottomBar = {
            Button(
                onClick = { onSave(text) },
                enabled = changed && !isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RebornCobaltBlue,
                    disabledContainerColor = RebornUnselectedGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(52.dp)
            ) {
                Text(
                    text = if (isSaving) "저장 중…" else "저장",
                    color = RebornWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            OutlinedTextField(
                value = text,
                // 한도 이하이거나 글자가 줄어드는 수정은 허용 — 기존 값이 이미 한도를 넘은 경우(예: 이메일로 만든 닉네임)도 지울 수 있게
                onValueChange = {
                    val fits = it.length <= maxLength || it.length < text.length
                    if (fits && (!singleLine || '\n' !in it)) text = it
                },
                enabled = !isSaving,
                singleLine = singleLine,
                minLines = if (singleLine) 1 else 3,
                placeholder = { Text(text = placeholder, color = RebornSlateGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${text.length} / $maxLength",
                color = RebornSlateGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = RebornDangerRed, fontSize = 13.sp)
            }
        }
    }
}

@Preview
@Composable
private fun ProfileTextEditScreenPreview() {
    MaterialTheme {
        ProfileTextEditScreen(
            title = "닉네임",
            initialValue = "홍길동",
            maxLength = 20,
            singleLine = true,
            placeholder = "닉네임을 입력해주세요",
            isSaving = false,
            error = null,
            savedToken = 0,
            onSave = {},
            onSaved = {},
            onBackClick = {}
        )
    }
}
