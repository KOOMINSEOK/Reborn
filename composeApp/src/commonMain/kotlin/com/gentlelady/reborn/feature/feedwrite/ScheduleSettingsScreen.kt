package com.gentlelady.reborn.feature.feedwrite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gentlelady.reborn.core.theme.RebornCobaltBlue
import com.gentlelady.reborn.core.theme.RebornGridBorderGray
import com.gentlelady.reborn.core.theme.RebornLightBlueBg
import com.gentlelady.reborn.core.theme.RebornSlateGray
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingsScreen(
    viewModel: FeedWriteViewModel,
    onBack: () -> Unit,
    onEditTargets: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCalendar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                title = { Text(text = "예약 설정", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            Surface(color = Color.White) {
                Button(
                    onClick = onComplete,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RebornCobaltBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp)
                ) {
                    Text(text = "업로드 예약 완료하기", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(
                icon = Icons.Filled.Schedule,
                title = "공개 시점 선택",
                subtitle = "사망이 확인된 이후, 이 게시글을 언제 공개할지 선택해주세요."
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                items(ScheduleOption.entries.toList()) { option ->
                    val isSelected = viewModel.scheduleOption == option
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) RebornLightBlueBg else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) RebornCobaltBlue else RebornGridBorderGray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                viewModel.scheduleOption = option
                                showCalendar = option == ScheduleOption.CUSTOM
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.label,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) RebornCobaltBlue else Color.Black
                        )
                    }
                }
            }

            if (showCalendar) {
                Spacer(modifier = Modifier.height(12.dp))
                MonthCalendar(
                    selectedDate = viewModel.customDate,
                    onCancel = { showCalendar = false },
                    onConfirm = { date ->
                        viewModel.customDate = date
                        showCalendar = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader(
                icon = Icons.Filled.Lock,
                title = "공개 범위 설정",
                subtitle = "이 게시글을 볼 수 있는 사람을 선택해주세요."
            )
            Spacer(modifier = Modifier.height(12.dp))

            VisibilityOptionRow(
                icon = Icons.Filled.Public,
                option = PostVisibility.PUBLIC,
                selected = viewModel.visibility,
                onSelect = { viewModel.visibility = PostVisibility.PUBLIC }
            )
            Spacer(modifier = Modifier.height(10.dp))
            VisibilityOptionRow(
                icon = Icons.Filled.Group,
                option = PostVisibility.FOLLOWERS,
                selected = viewModel.visibility,
                onSelect = { viewModel.visibility = PostVisibility.FOLLOWERS }
            )
            Spacer(modifier = Modifier.height(10.dp))
            VisibilityOptionRow(
                icon = Icons.Filled.Lock,
                option = PostVisibility.PRIVATE,
                selected = viewModel.visibility,
                onSelect = { viewModel.visibility = PostVisibility.PRIVATE }
            )

            if (viewModel.visibility == PostVisibility.PRIVATE) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(RebornLightBlueBg)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "지정한 팔로워 ${viewModel.selectedFollowerIds.size}명에게 공개",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "수정",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RebornCobaltBlue,
                        modifier = Modifier.clickable(onClick = onEditTargets)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "* 지정한 팔로워에게만 글이 보여지도록 설정합니다.",
                    fontSize = 12.sp,
                    color = RebornSlateGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = null, tint = RebornCobaltBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = RebornSlateGray)
        }
    }
}

@Composable
private fun VisibilityOptionRow(
    icon: ImageVector,
    option: PostVisibility,
    selected: PostVisibility,
    onSelect: () -> Unit
) {
    val isSelected = option == selected
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) RebornLightBlueBg else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) RebornCobaltBlue else RebornGridBorderGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = RebornCobaltBlue)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = option.label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(imageVector = icon, contentDescription = null, tint = RebornSlateGray, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun MonthCalendar(
    selectedDate: LocalDate?,
    onCancel: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var year by remember { mutableStateOf(selectedDate?.year ?: 2026) }
    var month by remember { mutableStateOf(selectedDate?.monthNumber ?: 9) }
    var pickedDay by remember { mutableStateOf(selectedDate?.takeIf { it.year == year && it.monthNumber == month }?.dayOfMonth) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = RebornGridBorderGray, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val (y, m) = addMonths(year, month, -1)
                year = y; month = m; pickedDay = null
            }) {
                Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "이전 달")
            }
            Text(text = "${year}년 ${month}월", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                val (y, m) = addMonths(year, month, 1)
                year = y; month = m; pickedDay = null
            }) {
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "다음 달")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { d ->
                Text(
                    text = d,
                    fontSize = 12.sp,
                    color = RebornSlateGray,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val cells = remember(year, month) { buildMonthGrid(year, month) }
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .then(
                                if (cell.inCurrentMonth && cell.day == pickedDay) {
                                    Modifier.background(RebornCobaltBlue)
                                } else Modifier
                            )
                            .clickable(enabled = cell.inCurrentMonth) { pickedDay = cell.day },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cell.day.toString(),
                            fontSize = 13.sp,
                            color = when {
                                !cell.inCurrentMonth -> RebornGridBorderGray
                                cell.day == pickedDay -> Color.White
                                else -> Color.Black
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) {
                Text(text = "취소", color = RebornSlateGray)
            }
            TextButton(
                onClick = { pickedDay?.let { onConfirm(LocalDate(year, month, it)) } },
                enabled = pickedDay != null
            ) {
                Text(text = "확인", color = RebornCobaltBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class DayCell(val day: Int, val inCurrentMonth: Boolean)

private fun buildMonthGrid(year: Int, month: Int): List<DayCell> {
    val daysInThisMonth = daysInMonth(year, month)
    val firstDayOfWeek = LocalDate(year, month, 1).dayOfWeek.sundayFirstIndex()
    val (prevYear, prevMonth) = addMonths(year, month, -1)
    val daysInPrevMonth = daysInMonth(prevYear, prevMonth)

    val leading = (firstDayOfWeek downTo 1).map { offset ->
        DayCell(day = daysInPrevMonth - offset + 1, inCurrentMonth = false)
    }
    val current = (1..daysInThisMonth).map { DayCell(day = it, inCurrentMonth = true) }
    val totalSoFar = leading.size + current.size
    val trailingCount = (7 - totalSoFar % 7) % 7
    val trailing = (1..trailingCount).map { DayCell(day = it, inCurrentMonth = false) }

    return leading + current + trailing
}

private fun DayOfWeek.sundayFirstIndex(): Int = (this.ordinal + 1) % 7 // MONDAY=0..SUNDAY=6 -> Sun=0..Sat=6

private fun addMonths(year: Int, month: Int, delta: Int): Pair<Int, Int> {
    val total = year * 12 + (month - 1) + delta
    val newYear = total.floorDiv(12)
    val newMonth = total.mod(12) + 1
    return newYear to newMonth
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
    else -> 30
}

@Preview
@Composable
private fun ScheduleSettingsScreenPreview() {
    MaterialTheme {
        Surface {
            ScheduleSettingsScreen(
                viewModel = FeedWriteViewModel(),
                onBack = {},
                onEditTargets = {},
                onComplete = {}
            )
        }
    }
}
