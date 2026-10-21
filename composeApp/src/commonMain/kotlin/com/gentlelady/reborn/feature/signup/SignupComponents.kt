package com.gentlelady.reborn.feature.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornFieldBg
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornNavy
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

/** 가입 화면 공통 틀: 뒤로 · 제목 · 설명 · (내용) · 아래 큰 버튼 */
@Composable
internal fun SignupPage(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)?,
    bottom: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .imePadding()
    ) {
        Box(Modifier.height(56.dp).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.padding(start = 4.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기", tint = Color.Black)
                }
            }
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(title, fontSize = 24.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            if (subtitle != null) {
                Spacer(Modifier.height(10.dp))
                Text(subtitle, fontSize = 14.sp, lineHeight = 21.sp, color = RebornSlateGray)
            }
            Spacer(Modifier.height(32.dp))
            content()
        }
        Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            bottom()
        }
    }
}

/** 아래 큰 버튼 (남색, 비활성은 회색) */
@Composable
internal fun PrimaryButton(text: String, enabled: Boolean, loading: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RebornNavy,
            contentColor = Color.White,
            disabledContainerColor = RebornFieldBg,
            disabledContentColor = RebornUnselectedGray,
        ),
    ) {
        if (loading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun FieldLabel(text: String) {
    Text(text, fontSize = 13.sp, color = RebornSlateGray, modifier = Modifier.padding(bottom = 8.dp))
}

/** 둥근 입력칸. 포커스면 남색 테두리 */
@Composable
internal fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
        modifier = modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(if (enabled) Color.White else RebornFieldBg, RoundedCornerShape(12.dp))
                    .border(
                        width = if (focused) 1.5.dp else 1.dp,
                        color = if (focused) RebornNavy else if (enabled) RebornGridBorderGray else RebornFieldBg,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leading?.invoke()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(placeholder, fontSize = 16.sp, color = RebornUnselectedGray)
                    inner()
                }
                trailing?.invoke()
            }
        }
    )
}

/** 둥근 체크 (켜면 남색 + 흰 체크) */
@Composable
internal fun CheckCircle(checked: Boolean, size: Dp = 22.dp) {
    Box(
        Modifier
            .size(size)
            .background(if (checked) RebornNavy else Color.White, CircleShape)
            .border(1.5.dp, if (checked) RebornNavy else RebornGridBorderGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (checked) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(size * 0.65f))
    }
}

/** 6자리 인증번호 칸. 숨은 입력 하나에 칸 6개를 그린다 (붙여넣기·지우기가 자연스럽게) */
@Composable
internal fun CodeInput(code: String, onCodeChange: (String) -> Unit) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    BasicTextField(
        value = code,
        onValueChange = { onCodeChange(it.filter(Char::isDigit).take(6)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.focusRequester(focus),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(6) { i ->
                    val active = i == code.length.coerceAtMost(5)
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(0.92f)
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(if (active) 1.5.dp else 1.dp, if (active) RebornNavy else RebornGridBorderGray, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(code.getOrNull(i)?.toString().orEmpty(), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    )
}

/** 남은 시간 "04:52" (빨강) + "인증번호 다시 받기" */
@Composable
internal fun CodeTimerRow(deadlineMillis: Long, onResend: () -> Unit) {
    var remaining by remember(deadlineMillis) { mutableStateOf(left(deadlineMillis)) }
    LaunchedEffect(deadlineMillis) {
        while (remaining > 0) {
            delay(1_000)
            remaining = left(deadlineMillis)
        }
    }
    Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        val m = remaining / 60
        val s = remaining % 60
        Text("${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}", color = RebornDangerRed, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.weight(1f))
        Text(
            "인증번호 다시 받기",
            color = RebornNavy,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onResend)
        )
    }
}

private fun left(deadline: Long): Long = ((deadline - Clock.System.now().toEpochMilliseconds()) / 1000).coerceAtLeast(0)

/** "필수" · "선택" 글씨 */
@Composable
internal fun RequiredTag(required: Boolean) {
    Text(
        if (required) "필수" else "선택",
        color = if (required) RebornNavy else RebornSlateGray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
}
