package com.gentlelady.reborn.feature.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.Res
import com.gentlelady.reborn.core.platform.rememberGalleryImagePicker
import com.gentlelady.reborn.core.platform.rememberNotificationPermissionRequester
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornFieldBg
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornKakaoYellow
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornNaverGreen
import com.gentlelady.reborn.core.theme.RebornNavy
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.ic_camera
import com.gentlelady.reborn.ic_google
import com.gentlelady.reborn.ic_kakao
import com.gentlelady.reborn.signup.Carrier
import com.gentlelady.reborn.signup.EMAIL_RULE
import com.gentlelady.reborn.signup.ExistingAccount
import com.gentlelady.reborn.signup.HandleCheck
import com.gentlelady.reborn.signup.SignupIntent
import com.gentlelady.reborn.signup.SignupMethod
import com.gentlelady.reborn.signup.SignupPurpose
import com.gentlelady.reborn.signup.SignupState
import com.gentlelady.reborn.signup.SignupStep
import com.gentlelady.reborn.signup.providerLabel
import org.jetbrains.compose.resources.painterResource

/** 가입 흐름 한 덩어리. 단계는 [SignupState.step] 이 정하고, 시트·확인창은 그 위에 뜬다. */
@Composable
fun SignupFlow(state: SignupState, onIntent: (SignupIntent) -> Unit) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            onIntent(SignupIntent.MessageShown)
        }
    }
    val back = { onIntent(SignupIntent.Back) }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        when (state.step) {
            SignupStep.START -> StartScreen(onIntent)
            SignupStep.TERMS -> TermsScreen(state, onIntent, back)
            SignupStep.EMAIL -> EmailScreen(state, onIntent, back)
            SignupStep.EMAIL_CODE -> CodeScreen(
                title = "메일로 받은 인증번호를\n입력해주세요",
                subtitle = "${state.email}으로 6자리 인증번호를 보냈어요.",
                code = state.emailCode,
                deadline = state.emailCodeDeadline,
                hint = "메일이 오지 않았다면 스팸함을 확인해주세요.",
                devCode = null,
                state = state,
                onCode = { onIntent(SignupIntent.UpdateEmailCode(it)) },
                onResend = { onIntent(SignupIntent.SendEmailCode) },
                onConfirm = { onIntent(SignupIntent.VerifyEmailCode) },
                onBack = back,
            )
            SignupStep.PASSWORD -> PasswordScreen(state, onIntent, back)
            SignupStep.IDENTITY -> IdentityScreen(state, onIntent, back)
            SignupStep.IDENTITY_CODE -> CodeScreen(
                title = "인증번호를 입력해주세요",
                subtitle = "${formatPhone(state.identity.phone)}로 보낸 6자리 숫자를 입력하세요.",
                code = state.identityCode,
                deadline = state.identityDeadline,
                hint = null,
                devCode = state.devCode,
                state = state,
                onCode = { onIntent(SignupIntent.UpdateIdentityCode(it)) },
                onResend = { onIntent(SignupIntent.SendIdentityCode) },
                onConfirm = { onIntent(SignupIntent.VerifyIdentityCode) },
                onBack = back,
            )
            SignupStep.HANDLE -> HandleScreen(state, onIntent, back)
            SignupStep.PURPOSE -> PurposeScreen(state, onIntent, back)
            SignupStep.NOTIFICATION -> NotificationScreen(onIntent)
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp))
    }

    state.existingAccount?.let { ExistingAccountSheet(it, state.isBusy, onIntent) }

    if (state.showAbandonConfirm) {
        AlertDialog(
            onDismissRequest = { onIntent(SignupIntent.DismissAbandon) },
            title = { Text("가입을 그만둘까요?") },
            text = { Text("지금까지 입력한 정보가 지워져요. 처음부터 다시 시작할 수 있어요.") },
            confirmButton = { TextButton(onClick = { onIntent(SignupIntent.ConfirmAbandon) }) { Text("그만두기", color = RebornDangerRed) } },
            dismissButton = { TextButton(onClick = { onIntent(SignupIntent.DismissAbandon) }) { Text("계속하기") } },
            containerColor = Color.White,
        )
    }
}

// ---------------------------------------------------------------- 시작

@Composable
private fun StartScreen(onIntent: (SignupIntent) -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Color.White).systemBarsPadding().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.9f))
        Box(Modifier.size(64.dp).background(RebornNavy, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
            Text("RE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(16.dp))
        Text("RE:BORN", color = RebornNavy, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(20.dp))
        Text("추모의 공간에서, 삶의 에너지를 얻는 곳", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        Text("가장 소중한 인연들과 깊이 교감하는 곳", fontSize = 13.sp, color = RebornSlateGray)
        Spacer(Modifier.weight(1.4f))

        StartButton("카카오로 시작하기", RebornKakaoYellow, Color(0xE6000000), icon = {
            Icon(painterResource(Res.drawable.ic_kakao), contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }) { onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "kakao")) }
        Spacer(Modifier.height(10.dp))
        StartButton("네이버로 시작하기", RebornNaverGreen, Color.White, icon = {
            Text("N", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }) { onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "naver")) }
        Spacer(Modifier.height(10.dp))
        StartButton(
            label = null, background = Color.White, content = Color.Black, border = BorderStroke(1.dp, RebornGridBorderGray),
            richLabel = {
                Text(buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Google") }
                    append("로 시작하기")
                }, fontSize = 15.sp, color = Color.Black)
            },
            icon = { Icon(painterResource(Res.drawable.ic_google), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(20.dp)) },
        ) { onIntent(SignupIntent.Start(SignupMethod.SOCIAL, "google")) }
        Spacer(Modifier.height(10.dp))
        StartButton("이메일로 시작하기", RebornFieldBg, Color.Black, icon = {
            Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
        }) { onIntent(SignupIntent.Start(SignupMethod.EMAIL, "email")) }

        Spacer(Modifier.height(20.dp))
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = RebornSlateGray)) { append("이미 계정이 있나요? ") }
                withStyle(SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) { append("로그인") }
            },
            fontSize = 13.sp,
            modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { onIntent(SignupIntent.OpenLogin) }.padding(8.dp)
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StartButton(
    label: String?,
    background: Color,
    content: Color,
    border: BorderStroke? = null,
    richLabel: (@Composable () -> Unit)? = null,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = background,
        border = border,
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(horizontal = 18.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.align(Alignment.CenterStart).size(24.dp), contentAlignment = Alignment.Center) { icon() }
            if (richLabel != null) richLabel() else Text(label.orEmpty(), color = content, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------- 약관

private enum class TermItem(val required: Boolean, val label: String) {
    AGE(true, "만 14세 이상이에요"),
    SERVICE(true, "서비스 이용약관"),
    PRIVACY(true, "개인정보 수집·이용"),
    MARKETING(false, "이벤트·혜택 알림 받기"),
}

@Composable
private fun TermsScreen(state: SignupState, onIntent: (SignupIntent) -> Unit, onBack: () -> Unit) {
    val t = state.terms
    var viewing by remember { mutableStateOf<String?>(null) }
    SignupPage(
        title = "리본 이용을 위해\n동의가 필요해요",
        subtitle = "필수 항목에 동의하면 시작할 수 있어요.",
        onBack = onBack,
        bottom = { PrimaryButton("동의하고 계속하기", enabled = t.requiredAgreed, loading = state.isBusy) { onIntent(SignupIntent.SubmitTerms) } }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(RebornFieldBg, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    val on = !t.allAgreed
                    onIntent(SignupIntent.UpdateTerms(t.copy(age = on, service = on, privacy = on, marketing = on)))
                }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckCircle(t.allAgreed)
            Spacer(Modifier.width(12.dp))
            Text("모두 동의하기", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
        TermItem.entries.forEach { item ->
            val checked = when (item) {
                TermItem.AGE -> t.age
                TermItem.SERVICE -> t.service
                TermItem.PRIVACY -> t.privacy
                TermItem.MARKETING -> t.marketing
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val v = !checked
                        onIntent(SignupIntent.UpdateTerms(when (item) {
                            TermItem.AGE -> t.copy(age = v)
                            TermItem.SERVICE -> t.copy(service = v)
                            TermItem.PRIVACY -> t.copy(privacy = v)
                            TermItem.MARKETING -> t.copy(marketing = v)
                        }))
                    }
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CheckCircle(checked, size = 20.dp)
                Spacer(Modifier.width(12.dp))
                RequiredTag(item.required)
                Spacer(Modifier.width(6.dp))
                Text(item.label, fontSize = 14.sp, modifier = Modifier.weight(1f))
                if (item != TermItem.AGE) {
                    Text("보기", fontSize = 13.sp, color = RebornSlateGray,
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)).clickable { viewing = item.label }.padding(4.dp))
                }
            }
        }
    }
    viewing?.let { TermsSheet(it) { viewing = null } }
}

/** 약관 "보기". ponytail: 약관 전문이 정해지면 여기에 넣는다 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermsSheet(title: String, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("약관 전문은 준비 중이에요.", fontSize = 14.sp, color = RebornSlateGray, lineHeight = 22.sp)
            Spacer(Modifier.height(24.dp))
            PrimaryButton("확인", enabled = true, onClick = onDismiss)
        }
    }
}

// ---------------------------------------------------------------- 이메일 · 비밀번호

@Composable
private fun EmailScreen(state: SignupState, onIntent: (SignupIntent) -> Unit, onBack: () -> Unit) {
    SignupPage(
        title = "이메일을 입력해주세요",
        subtitle = "인증 메일을 보내드릴게요.\n로그인할 때도 이 이메일을 사용해요.",
        onBack = onBack,
        bottom = {
            PrimaryButton("인증 메일 받기", enabled = EMAIL_RULE.matches(state.email), loading = state.isBusy) { onIntent(SignupIntent.SendEmailCode) }
        }
    ) {
        FieldLabel("이메일")
        SignupTextField(
            value = state.email,
            onValueChange = { onIntent(SignupIntent.UpdateEmail(it)) },
            placeholder = "example@email.com",
            keyboardType = KeyboardType.Email,
            trailing = {
                if (state.email.isNotEmpty()) {
                    Box(Modifier.size(20.dp).background(RebornGridBorderGray, CircleShape).clickable { onIntent(SignupIntent.UpdateEmail("")) },
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "지우기", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        )
    }
}

@Composable
private fun PasswordScreen(state: SignupState, onIntent: (SignupIntent) -> Unit, onBack: () -> Unit) {
    val rules = state.passwordRules
    SignupPage(
        title = "비밀번호를 설정해주세요",
        subtitle = "다음에 로그인할 때 사용해요.",
        onBack = onBack,
        bottom = { PrimaryButton("다음", enabled = rules.ok && state.passwordsMatch, loading = state.isBusy) { onIntent(SignupIntent.SubmitPassword) } }
    ) {
        FieldLabel("비밀번호")
        PasswordField(state.password) { onIntent(SignupIntent.UpdatePassword(it, state.passwordConfirm)) }
        Spacer(Modifier.height(10.dp))
        RuleLine("8자 이상", rules.longEnough)
        RuleLine("영문, 숫자, 특수문자 중 2가지 이상", rules.mixed)
        Spacer(Modifier.height(24.dp))
        FieldLabel("비밀번호 확인")
        PasswordField(state.passwordConfirm) { onIntent(SignupIntent.UpdatePassword(state.password, it)) }
        if (state.passwordConfirm.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                if (state.passwordsMatch) "비밀번호가 일치해요." else "비밀번호가 일치하지 않아요.",
                fontSize = 13.sp,
                color = if (state.passwordsMatch) Color.Black else RebornDangerRed
            )
        }
    }
}

@Composable
private fun PasswordField(value: String, onChange: (String) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    SignupTextField(
        value = value,
        onValueChange = onChange,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailing = {
            IconButton(onClick = { visible = !visible }, modifier = Modifier.size(28.dp)) {
                Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = "비밀번호 보기", tint = RebornSlateGray)
            }
        }
    )
}

@Composable
private fun RuleLine(text: String, ok: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
        Icon(Icons.Default.Check, contentDescription = null, tint = if (ok) Color.Black else RebornUnselectedGray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 13.sp, color = if (ok) Color.Black else RebornUnselectedGray)
    }
}

// ---------------------------------------------------------------- 인증번호

@Composable
private fun CodeScreen(
    title: String,
    subtitle: String,
    code: String,
    deadline: Long,
    hint: String?,
    devCode: String?,
    state: SignupState,
    onCode: (String) -> Unit,
    onResend: () -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    SignupPage(
        title = title,
        subtitle = subtitle,
        onBack = onBack,
        bottom = { PrimaryButton("확인", enabled = code.length == 6, loading = state.isBusy, onClick = onConfirm) }
    ) {
        CodeInput(code, onCode)
        CodeTimerRow(deadline, onResend)
        state.fieldError?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, fontSize = 13.sp, color = RebornDangerRed)
        }
        if (hint != null) {
            Spacer(Modifier.height(20.dp))
            Text(hint, fontSize = 13.sp, color = RebornSlateGray)
        }
        if (devCode != null) {
            // 실명 확인 업체가 붙기 전 개발용 — 문자 대신 여기서 번호를 보여준다
            Spacer(Modifier.height(20.dp))
            Text("개발용 인증번호: $devCode", fontSize = 13.sp, color = RebornNavy, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------- 본인인증

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentityScreen(state: SignupState, onIntent: (SignupIntent) -> Unit, onBack: () -> Unit) {
    val f = state.identity
    val update = { form: com.gentlelady.reborn.signup.IdentityForm -> onIntent(SignupIntent.UpdateIdentity(form)) }
    var viewing by remember { mutableStateOf(false) }
    SignupPage(
        title = "본인인증을 진행해주세요",
        subtitle = "리본은 실명으로 운영돼요.\n한 사람당 하나의 계정만 만들 수 있어요.",
        onBack = onBack,
        bottom = { PrimaryButton("인증번호 받기", enabled = f.isComplete, loading = state.isBusy) { onIntent(SignupIntent.SendIdentityCode) } }
    ) {
        FieldLabel("이름")
        SignupTextField(f.name, { update(f.copy(name = it.take(30))) }, placeholder = "이름")
        Spacer(Modifier.height(20.dp))

        FieldLabel("생년월일 / 성별")
        Row(
            Modifier.fillMaxWidth().height(54.dp).border(1.dp, RebornGridBorderGray, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = f.birth,
                onValueChange = { update(f.copy(birth = it.filter(Char::isDigit).take(6))) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = Color.Black),
                modifier = Modifier.width(76.dp),
                decorationBox = { inner -> Box { if (f.birth.isEmpty()) Text("YYMMDD", fontSize = 16.sp, color = RebornUnselectedGray); inner() } }
            )
            Text("  -  ", fontSize = 16.sp, color = RebornSlateGray)
            androidx.compose.foundation.text.BasicTextField(
                value = f.genderDigit,
                onValueChange = { update(f.copy(genderDigit = it.filter(Char::isDigit).take(1))) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = Color.Black),
                modifier = Modifier.width(14.dp),
            )
            // 뒷자리 나머지는 받지 않는다 — 모양만
            Text("●●●●●●", fontSize = 10.sp, color = Color.Black, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(20.dp))

        FieldLabel("통신사")
        var open by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = open, onExpandedChange = { open = it }) {
            Row(
                Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .height(54.dp)
                    .border(1.dp, RebornGridBorderGray, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(f.carrier.label, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = RebornSlateGray)
            }
            ExposedDropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = Color.White) {
                Carrier.entries.forEach { c ->
                    DropdownMenuItem(text = { Text(c.label) }, onClick = { update(f.copy(carrier = c)); open = false })
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        FieldLabel("휴대폰 번호")
        SignupTextField(
            value = formatPhone(f.phone),
            onValueChange = { update(f.copy(phone = it.filter(Char::isDigit).take(11))) },
            placeholder = "010-0000-0000",
            keyboardType = KeyboardType.Phone
        )
        state.fieldError?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, fontSize = 13.sp, color = RebornDangerRed)
        }
        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { update(f.copy(agreed = !f.agreed)) }.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckCircle(f.agreed, size = 20.dp)
            Spacer(Modifier.width(10.dp))
            Text("본인확인 서비스 이용약관에 모두 동의", fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text("보기", fontSize = 13.sp, color = RebornSlateGray,
                modifier = Modifier.clip(RoundedCornerShape(4.dp)).clickable { viewing = true }.padding(4.dp))
        }
    }
    if (viewing) TermsSheet("본인확인 서비스 이용약관") { viewing = false }
}

internal fun formatPhone(digits: String): String = when {
    digits.length <= 3 -> digits
    digits.length <= 7 -> "${digits.take(3)}-${digits.drop(3)}"
    digits.length == 10 -> "${digits.take(3)}-${digits.substring(3, 6)}-${digits.drop(6)}"
    else -> "${digits.take(3)}-${digits.substring(3, 7)}-${digits.drop(7)}"
}

// ---------------------------------------------------------------- 이미 가입된 계정

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExistingAccountSheet(account: ExistingAccount, busy: Boolean, onIntent: (SignupIntent) -> Unit) {
    val label = providerLabel(account.provider)
    ModalBottomSheet(onDismissRequest = { onIntent(SignupIntent.DismissExistingAccount) }, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text("이미 가입된 계정이 있어요", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("본인인증 정보로 가입된 계정을 찾았어요.\n기존 계정으로 로그인해주세요.", fontSize = 14.sp, color = RebornSlateGray, lineHeight = 21.sp)
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth().border(1.dp, RebornGridBorderGray, RoundedCornerShape(14.dp)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(40.dp).background(RebornFieldBg, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    when (account.provider) {
                        "kakao" -> Icon(painterResource(Res.drawable.ic_kakao), contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        "google" -> Icon(painterResource(Res.drawable.ic_google), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(20.dp))
                        "naver" -> Text("N", color = RebornNaverGreen, fontWeight = FontWeight.ExtraBold)
                        else -> Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${label}로 가입한 계정", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text("@${account.handle} · ${joinedMonth(account.joinedAt)} 가입", fontSize = 13.sp, color = RebornSlateGray)
                }
            }
            Spacer(Modifier.height(20.dp))
            PrimaryButton("${label}로 로그인", enabled = true, loading = busy) { onIntent(SignupIntent.UseExistingAccount) }
            // ponytail: 고객센터 화면이 생기면 연결
            Text(
                "고객센터 문의하기",
                fontSize = 13.sp,
                color = RebornSlateGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
            )
        }
    }
}

/** "2026-09-14T…" → "2026.09" */
private fun joinedMonth(iso: String): String = iso.take(7).replace('-', '.')

// ---------------------------------------------------------------- 아이디

@Composable
private fun HandleScreen(state: SignupState, onIntent: (SignupIntent) -> Unit, onBack: () -> Unit) {
    val pickPhoto = rememberGalleryImagePicker { onIntent(SignupIntent.UpdateAvatar(it)) }
    SignupPage(
        title = "리본에서 사용할\n아이디를 만들어주세요",
        subtitle = "다른 사람이 나를 찾을 때 쓰여요.",
        onBack = onBack,
        bottom = { PrimaryButton("다음", enabled = state.handleCheck == HandleCheck.AVAILABLE) { onIntent(SignupIntent.SubmitHandle) } }
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(92.dp).clip(CircleShape).clickable(onClick = pickPhoto)) {
                Box(Modifier.fillMaxSize().background(RebornFieldBg, CircleShape), contentAlignment = Alignment.Center) {
                    val avatar = state.avatar
                    if (avatar != null) {
                        AvatarPreview(avatar)
                    } else {
                        Icon(Icons.Outlined.Person, contentDescription = "프로필 사진", tint = RebornSlateGray, modifier = Modifier.size(40.dp))
                    }
                }
            }
            Box(Modifier.offset(x = 30.dp, y = (-26).dp).size(26.dp).background(Color.White, CircleShape)
                .border(1.dp, RebornGridBorderGray, CircleShape), contentAlignment = Alignment.Center) {
                Icon(painterResource(Res.drawable.ic_camera), contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            }
            Text(
                // ponytail: 소셜 로그인이 붙으면 카카오 프로필 사진을 받아온다. 지금은 갤러리에서
                if (state.provider == "kakao") "카카오 프로필 사진 불러오기" else "사진은 나중에 추가해도 돼요",
                fontSize = 13.sp,
                color = if (state.provider == "kakao") RebornNavy else RebornSlateGray,
                modifier = Modifier.offset(y = (-14).dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        FieldLabel("이름")
        SignupTextField(
            value = state.verifiedName,
            onValueChange = {},
            enabled = false,
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("인증됨", fontSize = 12.sp, color = RebornNavy, fontWeight = FontWeight.Medium,
                        modifier = Modifier.background(RebornLightBlueBg, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Lock, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(16.dp))
                }
            }
        )
        Spacer(Modifier.height(20.dp))
        FieldLabel("아이디")
        SignupTextField(
            value = state.handle,
            onValueChange = { onIntent(SignupIntent.UpdateHandle(it)) },
            placeholder = "아이디",
            keyboardType = KeyboardType.Ascii,
            leading = { Text("@ ", fontSize = 16.sp, color = RebornSlateGray) },
            trailing = {
                when (state.handleCheck) {
                    HandleCheck.CHECKING -> CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = RebornSlateGray)
                    HandleCheck.AVAILABLE -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RebornNavy, modifier = Modifier.size(20.dp))
                    else -> Unit
                }
            }
        )
        Spacer(Modifier.height(10.dp))
        val (status, color) = when (state.handleCheck) {
            HandleCheck.AVAILABLE -> "사용할 수 있는 아이디예요." to Color.Black
            HandleCheck.TAKEN -> "이미 사용 중인 아이디예요." to RebornDangerRed
            HandleCheck.INVALID -> "아이디 규칙을 확인해 주세요." to RebornDangerRed
            else -> null to Color.Black
        }
        if (status != null) Text(status, fontSize = 13.sp, color = color, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text("영문 소문자, 숫자, 밑줄(_), 마침표(.) 사용 가능, 4~20자", fontSize = 12.sp, color = RebornSlateGray)
    }
}

/** 고른 사진 미리보기 (아직 올리기 전 — 가입을 마칠 때 올린다). Coil 이 바이트를 바로 그린다 (글쓰기 화면과 같은 방식) */
@Composable
private fun AvatarPreview(jpeg: ByteArray) {
    AsyncImage(model = jpeg, contentDescription = "프로필 사진", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
}

// ---------------------------------------------------------------- 목적

@Composable
private fun PurposeScreen(state: SignupState, onIntent: (SignupIntent) -> Unit, onBack: () -> Unit) {
    SignupPage(
        title = "리본에서 무엇을\n하고 싶으세요?",
        subtitle = "나중에 언제든 다른 기능도 쓸 수 있어요.",
        onBack = onBack,
        bottom = { PrimaryButton("시작하기", enabled = state.purpose != null, loading = state.isBusy) { onIntent(SignupIntent.SubmitPurpose) } }
    ) {
        PurposeCard(
            selected = state.purpose == SignupPurpose.REMEMBER,
            icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = RebornNavy, modifier = Modifier.size(20.dp)) },
            title = "기억하고 싶은 분이 있어요",
            desc = "추모 공간을 만들고, 사람들과 함께 기억해요.",
        ) { onIntent(SignupIntent.SelectPurpose(SignupPurpose.REMEMBER)) }
        Spacer(Modifier.height(12.dp))
        PurposeCard(
            selected = state.purpose == SignupPurpose.RECORD,
            icon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(20.dp)) },
            title = "나의 기록을 남기고 싶어요",
            desc = "소중한 사람에게 전할 이야기를 미리 써둬요.",
        ) { onIntent(SignupIntent.SelectPurpose(SignupPurpose.RECORD)) }
        Spacer(Modifier.height(12.dp))
        PurposeCard(
            selected = state.purpose == SignupPurpose.BROWSE,
            icon = null,
            title = "먼저 둘러볼게요",
            desc = null,
        ) { onIntent(SignupIntent.SelectPurpose(SignupPurpose.BROWSE)) }
    }
}

@Composable
private fun PurposeCard(selected: Boolean, icon: (@Composable () -> Unit)?, title: String, desc: String?, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) RebornLightBlueBg else Color.White)
            .border(if (selected) 1.5.dp else 1.dp, if (selected) RebornNavy else RebornGridBorderGray, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = if (desc == null) 16.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(Modifier.size(40.dp).background(if (selected) Color.Transparent else RebornFieldBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.width(14.dp))
        }
        Column {
            Text(title, fontSize = 15.sp, fontWeight = if (desc != null) FontWeight.SemiBold else FontWeight.Normal,
                color = if (desc == null) RebornSlateGray else Color.Black)
            if (desc != null) {
                Spacer(Modifier.height(4.dp))
                Text(desc, fontSize = 13.sp, color = if (selected) RebornNavy else RebornSlateGray, lineHeight = 19.sp)
            }
        }
    }
}

// ---------------------------------------------------------------- 알림

@Composable
private fun NotificationScreen(onIntent: (SignupIntent) -> Unit) {
    val request = rememberNotificationPermissionRequester { onIntent(SignupIntent.Finish) }
    Column(
        Modifier.fillMaxSize().background(Color.White).systemBarsPadding().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.8f))
        Box(Modifier.size(width = 150.dp, height = 112.dp)) {
            Box(Modifier.size(104.dp).align(Alignment.BottomCenter).background(RebornLightBlueBg, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = RebornNavy, modifier = Modifier.size(40.dp))
            }
            Text("D-3 기일", fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.TopEnd).background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, RebornGridBorderGray, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 4.dp))
        }
        Spacer(Modifier.height(28.dp))
        Text("소중한 날을 놓치지 않도록\n알려드릴게요", fontSize = 22.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        Text("기일이 다가오거나, 메모리얼에\n새 방명록이 남겨지면 알려드려요.", fontSize = 14.sp, lineHeight = 21.sp, color = RebornSlateGray, textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1.2f))
        PrimaryButton("알림 받기", enabled = true, onClick = request)
        Text("나중에 할게요", fontSize = 14.sp, color = RebornSlateGray,
            modifier = Modifier.padding(vertical = 14.dp).clip(RoundedCornerShape(6.dp)).clickable { onIntent(SignupIntent.Finish) }.padding(6.dp))
    }
}
