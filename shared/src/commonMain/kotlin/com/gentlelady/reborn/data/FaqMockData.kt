package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.app_settings.customer_support.faq.domain.model.FaqItem

object FaqMockData {

    val faqItems = listOf(
        FaqItem(
            id = "1",
            question = "메모리얼이 뭔가요?",
            answer = "메모리얼은 고인을 추모하고 기억하기 위해 마련하는 온라인 공간입니다. 방문자들은 방명록을 남기고 추모 화환을 헌화하며 애도의 마음을 전할 수 있습니다."
        ),
        FaqItem(
            id = "2",
            question = "리마인드가 뭔가요?",
            answer = "리마인드는 생전에 설정해 둔 메시지가 지정한 날짜에 소중한 사람에게 자동으로 전달되는 기능입니다."
        ),
        FaqItem(
            id = "3",
            question = "본인 메모리얼을 직접 만들 수 있나요?",
            answer = "네, 가능합니다."
        ),
        FaqItem(
            id = "4",
            question = "가족이나 지인이 아닌 사람도 타인의 메모리얼 페이지를 개설할 수 있나요?",
            answer = "네 가능합니다. 추모의 뜻을 기리기 위해 유관순 열사와 같은 역사적 인물의 메모리얼 페이지를 만드셔도 됩니다."
        ),
        FaqItem(
            id = "5",
            question = "추모 화환을 결제했는데, 취소하거나 환불받을 수 있나요?",
            answer = "결제 완료 후에는 원칙적으로 취소 및 환불이 불가합니다."
        ),
        FaqItem(
            id = "6",
            question = "회원이 탈퇴하면, 다른 사람의 메모리얼에 남겼던 방명록 기록도 모두 사라지나요?",
            answer = "네, 회원 탈퇴 시 작성하신 방명록을 포함한 모든 개인 데이터가 영구적으로 삭제됩니다."
        )
    )
}
