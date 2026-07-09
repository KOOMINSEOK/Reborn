package com.gentlelady.reborn.core.theme

import androidx.compose.ui.graphics.Color

// ========================================================================
// 1. Raw / Brand Colors (기본 정의값)
// ========================================================================
val RebornDeepBlue = Color(0xFF0047AB)      // 네비게이션 바 선택, 주요 브랜드 컬러
val RebornCobaltBlue = Color(0xFF2D62ED)    // 메모리얼 아이콘 등 포인트 컬러

// Light Blue Variations
val RebornLightBlueBg = Color(0xFFEFF4FF)   // 사후 게시글 정보 박스 등 연한 파랑 배경
val RebornBorderLightBlue = Color(0xFFB9D1FF) // 메모리얼 버튼 테두리
val RebornSoftBlue = Color(0xFFDCE8FF)
val RebornDarkBlue = Color(0xFF003D94)

// Grays & Neutrals
val RebornBackgroundGray = Color(0xFFF8F9FA) // 앱 공통 배경색
val RebornUnselectedGray = Color(0xFF9E9E9E) // 네비게이션 바 비활성 탭 색상
val RebornDividerGray = Color(0xFFEEEEEE)    // 구분선 색상
val RebornSlateGray = Color(0xFF64748B)      // 액션 아이콘 및 회색 텍스트용 슬레이트 그레이

// 🆕 신규 요청 그레이 색상 추가
val RebornGridIconGray = Color(0xFFF1F5F9)   // 일반 카드 아이콘 박스 배경
val RebornGridBorderGray = Color(0xFFE2E8F0) // 일반 카드 테두리 선

// 시스템 기본 축
val RebornWhite = Color(0xFFFFFFFF)
val RebornBlack = Color(0xFF111111)

// ========================================================================
// 2. Semantic Mapping (프로필 관리 그리드 전용 변수 등록)
// ========================================================================
val RebornBackground = RebornBackgroundGray
val RebornSurface = RebornWhite
val RebornSurfaceVariant = Color(0xFFE0E0E0)

// 타이포그래피 (글자색)
val RebornTextPrimary = RebornBlack
val RebornTextSecondary = RebornSlateGray
val RebornTextLink = RebornCobaltBlue

// 브랜드 및 메인 액센트
val RebornPrimary = RebornDeepBlue
val RebornSecondary = RebornCobaltBlue

// 🆕 [신규] 관리 메뉴 카드 전용 세맨틱 컬러 매핑 규칙
// 1) 하이라이트 안 된 경우 (Normal)
val RebornGridCardBgNormal = RebornWhite
val RebornGridCardBorderNormal = RebornGridBorderGray
val RebornGridCardIconBgNormal = RebornGridIconGray

// 2) 하이라이트 된 경우 (Highlighted)
val RebornGridCardBgHighlight = RebornLightBlueBg
val RebornGridCardBorderHighlight = RebornBorderLightBlue
val RebornGridCardIconBgHighlight = RebornSoftBlue
val RebornGridCardIconTintHighlight = RebornDeepBlue