package com.gentlelady.reborn.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gentlelady.reborn.auth.presentation.LoginIntent
import com.gentlelady.reborn.auth.presentation.LoginState
import com.gentlelady.reborn.core.theme.RebornBackground
import com.gentlelady.reborn.core.theme.RebornTextPrimary
import com.gentlelady.reborn.core.theme.RebornTextSecondary
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit = {},
    onSkip: () -> Unit = {},
) {
    Surface(color = RebornBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Reborn", color = RebornTextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("로그인", color = RebornTextSecondary, modifier = Modifier.padding(top = 4.dp, bottom = 32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
                label = { Text("이메일") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                label = { Text("비밀번호") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )

            val error = state.error
            if (error != null) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
            }

            Button(
                onClick = { onIntent(LoginIntent.Submit) },
                enabled = !state.isLoading && state.email.isNotBlank() && state.password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(2.dp), strokeWidth = 2.dp)
                } else {
                    Text("로그인")
                }
            }

            TextButton(onClick = onSkip, modifier = Modifier.padding(top = 8.dp)) {
                Text("로그인 없이 둘러보기 (샘플 데이터)", color = RebornTextSecondary)
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(LoginState(email = "test1@test.com", password = "secret"))
    }
}
