package com.gentlelady.reborn.feature.management.security.device_management

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.gentlelady.reborn.core.theme.RebornDangerRed
import com.gentlelady.reborn.core.theme.RebornDeepBlue
import com.gentlelady.reborn.core.theme.RebornDividerGray
import com.gentlelady.reborn.management.security.device_management.presentation.DeviceManagementIntent
import com.gentlelady.reborn.management.security.device_management.presentation.DeviceManagementState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DevicePasswordScreen(
    state: DeviceManagementState,
    onIntent: (DeviceManagementIntent) -> Unit,
    onVerified: () -> Unit
) {
    LaunchedEffect(state.isVerified) {
        if (state.isVerified) onVerified()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            RebornBackTopAppBar(title = "로그인 기기 관리", onBackClick = { onIntent(DeviceManagementIntent.ClickBack) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Text(
                text = "본인 확인을 위해\n비밀번호를 입력해주세요.",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { onIntent(DeviceManagementIntent.PasswordChanged(it)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                isError = state.isPasswordError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RebornBackgroundGray,
                    unfocusedContainerColor = RebornBackgroundGray,
                    errorContainerColor = RebornBackgroundGray,
                    focusedBorderColor = RebornDeepBlue,
                    unfocusedBorderColor = RebornDividerGray,
                    errorBorderColor = RebornDangerRed
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.isPasswordError) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "비밀번호가 틀렸습니다. 다시 입력해주십시오.",
                    color = RebornDangerRed,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onIntent(DeviceManagementIntent.ClickVerifyPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RebornDeepBlue)
            ) {
                Text(text = "다음", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview
@Composable
private fun DevicePasswordScreenPreview() {
    MaterialTheme {
        DevicePasswordScreen(
            state = DeviceManagementState(password = "12345678"),
            onIntent = {},
            onVerified = {}
        )
    }
}

@Preview
@Composable
private fun DevicePasswordScreenErrorPreview() {
    MaterialTheme {
        DevicePasswordScreen(
            state = DeviceManagementState(password = "wrongpass", isPasswordError = true),
            onIntent = {},
            onVerified = {}
        )
    }
}
