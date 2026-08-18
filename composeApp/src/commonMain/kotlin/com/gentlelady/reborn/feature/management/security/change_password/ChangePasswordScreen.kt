package com.gentlelady.reborn.feature.management.security.change_password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.components.RebornBackTopAppBar
import com.gentlelady.reborn.core.theme.RebornBackgroundGray
import com.gentlelady.reborn.core.theme.RebornBlack
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.core.theme.RebornSlateGray
import com.gentlelady.reborn.core.theme.RebornUnselectedGray
import com.gentlelady.reborn.management.security.change_password.presentation.ChangePasswordIntent
import com.gentlelady.reborn.management.security.change_password.presentation.ChangePasswordState
import com.gentlelady.reborn.myprofile.presentation.MyProfileState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChangePasswordScreen(
    profileState: MyProfileState,
    state: ChangePasswordState,
    onIntent: (ChangePasswordIntent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "비밀번호 변경", onBackClick = { onIntent(ChangePasswordIntent.ClickBack) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
            Text(
                text = "${profileState.displayName} @${profileState.username}",
                color = RebornSlateGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "비밀번호 변경",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "비밀번호는 최소 6자 이상이어야 하며 숫자, 영문, 특수문자($@%)의 조합을 포함해야 합니다.",
                color = RebornSlateGray,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordField(
                value = state.currentPassword,
                placeholder = "현재 비밀번호[${state.lastUpdatedLabel}]",
                onValueChange = { onIntent(ChangePasswordIntent.CurrentPasswordChanged(it)) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField(
                value = state.newPassword,
                placeholder = "새 비밀번호",
                onValueChange = { onIntent(ChangePasswordIntent.NewPasswordChanged(it)) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            PasswordField(
                value = state.confirmPassword,
                placeholder = "새 비밀번호 재입력",
                onValueChange = { onIntent(ChangePasswordIntent.ConfirmPasswordChanged(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "비밀번호를 잊으셨나요?",
                color = RebornCobaltBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onIntent(ChangePasswordIntent.ClickForgotPassword) }
            )

            }

            if (state.resetEmailSent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                        .background(color = RebornBlack, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "비밀번호를 재설정할 수 있는 링크가 포함된 이메일을 ${state.resetEmailAddress} 주소로 보내드렸습니다.",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Button(
                onClick = { onIntent(ChangePasswordIntent.ClickSubmit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornDeepBlue)
            ) {
                Text(text = "비밀번호 변경", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = RebornUnselectedGray, fontSize = 13.sp) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = RebornBackgroundGray,
            unfocusedContainerColor = RebornBackgroundGray,
            focusedBorderColor = RebornDeepBlue,
            unfocusedBorderColor = RebornDividerGray
        ),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun ChangePasswordScreenPreview() {
    MaterialTheme {
        ChangePasswordScreen(
            profileState = MyProfileState(username = "hong_gild", displayName = "홍길동"),
            state = ChangePasswordState(),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun ChangePasswordScreenResetSentPreview() {
    MaterialTheme {
        ChangePasswordScreen(
            profileState = MyProfileState(username = "hong_gild", displayName = "홍길동"),
            state = ChangePasswordState(resetEmailSent = true),
            onIntent = {}
        )
    }
}
