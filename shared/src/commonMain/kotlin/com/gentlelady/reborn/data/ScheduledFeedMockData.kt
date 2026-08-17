package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledFeedItem
import com.gentlelady.reborn.management.scheduled_feed.domain.model.ScheduledReminderItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_profile_dummy
import reborn.shared.generated.resources.img_post_dummy1
import reborn.shared.generated.resources.img_post_dummy2
import reborn.shared.generated.resources.img_post_dummy3
import reborn.shared.generated.resources.img_profile_dummy_1
import reborn.shared.generated.resources.img_profile_dummy_2
import reborn.shared.generated.resources.img_profile_dummy_3

object ScheduledFeedMockData {

    val scheduledFeeds = listOf(
        ScheduledFeedItem(
            id = "1",
            title = "가을 산책을 하며",
            scheduleLabel = "사망 후 일주일 뒤 예약됨",
            thumbnail = Res.drawable.img_post_dummy1
        ),
        ScheduledFeedItem(
            id = "2",
            title = "소중한 사람들에게",
            scheduleLabel = "사망 후 한 달 뒤 예약됨",
            thumbnail = Res.drawable.img_post_dummy2
        ),
        ScheduledFeedItem(
            id = "3",
            title = "봄날의 인사",
            scheduleLabel = "2029/08/15 예약됨",
            thumbnail = Res.drawable.img_post_dummy3
        ),
        ScheduledFeedItem(
            id = "4",
            title = "기억의 풍경",
            scheduleLabel = "2035/01/01 예약됨",
            thumbnail = Res.drawable.img_memorial_profile_dummy
        )
    )

    val scheduledReminders = listOf(
        ScheduledReminderItem(
            id = "1",
            recipientName = "박희옥",
            scheduleLabel = "사망 후 3일 뒤 예약됨",
            messagePreview = "늘 감사하고 사랑합니다...",
            recipientAvatar = Res.drawable.img_profile_dummy_1
        ),
        ScheduledReminderItem(
            id = "2",
            recipientName = "김민지",
            scheduleLabel = "2030/02/19 예약됨",
            messagePreview = "우리 딸, 생일 축하해...",
            recipientAvatar = Res.drawable.img_profile_dummy_2
        ),
        ScheduledReminderItem(
            id = "3",
            recipientName = "이종기",
            scheduleLabel = "2039/08/01 예약됨",
            messagePreview = "항상 곁에 있어줘서...",
            recipientAvatar = Res.drawable.img_profile_dummy_3
        )
    )
}
