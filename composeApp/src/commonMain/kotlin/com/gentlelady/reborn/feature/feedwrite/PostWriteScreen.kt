package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.designsystem.dashedBorder
import com.gentlelady.reborn.core.platform.rememberGalleryImagePicker
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornInputBorderGray
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * 생전/사후 게시글 작성 화면. 제목만 다르고 본문 UI는 동일하다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostWriteScreen(
    title: String,
    onClose: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var caption by remember { mutableStateOf("") }
    val images = remember { mutableStateListOf<ImageBitmap>() }
    var audioDurationSeconds by remember { mutableStateOf<Int?>(null) }
    var showAttachSheet by remember { mutableStateOf(false) }
    var showRecordSheet by remember { mutableStateOf(false) }

    // ponytail: 카메라/파일 선택은 별도 네이티브 연동이 없어 갤러리 선택기를 임시로 재사용한다.
    // 실제 카메라 촬영/파일 탐색기 연동은 필요해지면 추가.
    val launchGalleryPicker = rememberGalleryImagePicker(
        onImagePicked = { bitmap -> images.add(bitmap) }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "닫기")
                    }
                },
                title = {
                    Text(text = title, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = onSubmit) {
                        Text(text = "다음", color = RebornCobaltBlue, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (caption.isEmpty()) {
                    Text(
                        text = "어떤 메시지를 남기고 싶나요?\n당신의 의미 있는 이야기를 남겨보세요.",
                        fontSize = 16.sp,
                        color = RebornSlateGray,
                        lineHeight = 24.sp
                    )
                }
                BasicTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black, lineHeight = 24.sp),
                    modifier = Modifier.fillMaxSize()
                )
            }

            audioDurationSeconds?.let { seconds ->
                Spacer(modifier = Modifier.height(12.dp))
                RecordedAudioChip(seconds = seconds, onRemove = { audioDurationSeconds = null })
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (images.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .dashedBorder(color = RebornInputBorderGray, cornerRadius = 16.dp)
                        .clickable { showAttachSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            tint = RebornSlateGray,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "사진/비디오 추가", fontSize = 13.sp, color = RebornSlateGray)
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(images) { image ->
                        Box(modifier = Modifier.size(84.dp).clip(RoundedCornerShape(12.dp))) {
                            Image(
                                bitmap = image,
                                contentDescription = "첨부 사진",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { images.remove(image) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "사진 삭제",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .dashedBorder(color = RebornInputBorderGray, cornerRadius = 12.dp)
                                .clickable { showAttachSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "사진 추가", tint = RebornSlateGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showAttachSheet = true }) {
                    Icon(imageVector = Icons.Filled.Image, contentDescription = "사진/비디오 추가", tint = Color.Black)
                }
                IconButton(onClick = { showRecordSheet = true }) {
                    Icon(imageVector = Icons.Filled.Mic, contentDescription = "음성 녹음", tint = Color.Black)
                }
            }
        }
    }

    if (showAttachSheet) {
        AttachSourceSheet(
            onDismiss = { showAttachSheet = false },
            onPickFromGallery = {
                showAttachSheet = false
                launchGalleryPicker()
            },
            onPickFromCamera = {
                showAttachSheet = false
                launchGalleryPicker()
            },
            onPickFile = {
                showAttachSheet = false
                launchGalleryPicker()
            }
        )
    }

    if (showRecordSheet) {
        VoiceRecordSheet(
            onDismiss = { showRecordSheet = false },
            onFinish = { seconds ->
                audioDurationSeconds = seconds
                showRecordSheet = false
            }
        )
    }
}

@Composable
private fun RecordedAudioChip(seconds: Int, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RebornLightBlueBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(RebornCobaltBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "재생", tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "음성 메시지", fontSize = 13.sp, color = Color.Black, modifier = Modifier.weight(1f))
        Text(text = formatDuration(seconds), fontSize = 13.sp, color = RebornSlateGray)
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "음성 삭제", tint = RebornSlateGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachSourceSheet(
    onDismiss: () -> Unit,
    onPickFromCamera: () -> Unit,
    onPickFromGallery: () -> Unit,
    onPickFile: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            AttachSourceRow(icon = Icons.Filled.CameraAlt, label = "카메라", onClick = onPickFromCamera)
            AttachSourceRow(icon = Icons.Filled.PhotoLibrary, label = "사진 보관함", onClick = onPickFromGallery)
            AttachSourceRow(icon = Icons.Filled.Description, label = "파일 선택", onClick = onPickFile)
            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF5F5F5))
            AttachSourceRow(label = "취소", onClick = onDismiss, isCancel = true)
        }
    }
}

@Composable
private fun AttachSourceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    label: String,
    onClick: () -> Unit,
    isCancel: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCancel) Arrangement.Center else Arrangement.Start
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = label,
            fontSize = 15.sp,
            color = if (isCancel) RebornCobaltBlue else Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceRecordSheet(
    onDismiss: () -> Unit,
    onFinish: (seconds: Int) -> Unit
) {
    var isRecording by remember { mutableStateOf(true) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    // ponytail: 실제 마이크 캡처는 없고 경과 시간만 표시하는 타이머 UI다.
    // 실제 오디오 캡처가 필요해지면 플랫폼별 recorder expect/actual을 추가한다.
    androidx.compose.runtime.LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "음성 녹음", fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = formatDuration(elapsedSeconds), fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onDismiss) {
                    Text(text = "취소", color = RebornSlateGray)
                }
                IconButton(
                    onClick = { isRecording = !isRecording },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                        contentDescription = if (isRecording) "정지" else "녹음",
                        tint = Color.White
                    )
                }
                TextButton(onClick = { onFinish(elapsedSeconds) }, enabled = elapsedSeconds > 0) {
                    Text(text = "완료", color = RebornCobaltBlue, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

@Preview
@Composable
private fun PostWriteScreenPreview() {
    MaterialTheme {
        Surface {
            PostWriteScreen(title = "생전 게시글 작성", onClose = {}, onSubmit = {})
        }
    }
}
