package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.security.blocked_accounts.domain.model.BlockedAccountItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_profile_dummy_1
import reborn.shared.generated.resources.img_profile_dummy_2
import reborn.shared.generated.resources.img_profile_dummy_3

object BlockedAccountsMockData {

    val blockedAccounts = listOf(
        BlockedAccountItem(
            id = "1",
            name = "김민수",
            username = "min_su_99",
            avatar = Res.drawable.img_profile_dummy_1
        ),
        BlockedAccountItem(
            id = "2",
            name = "이영희",
            username = "younghee_lee",
            avatar = Res.drawable.img_profile_dummy_2
        ),
        BlockedAccountItem(
            id = "3",
            name = "최수연",
            username = "soo_yeon_c",
            avatar = Res.drawable.img_profile_dummy_3
        ),
        BlockedAccountItem(
            id = "4",
            name = "박지훈",
            username = "jh_park",
            avatar = null
        )
    )
}
