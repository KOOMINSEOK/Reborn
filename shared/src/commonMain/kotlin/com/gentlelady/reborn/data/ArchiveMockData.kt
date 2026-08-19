package com.gentlelady.reborn.data

import com.gentlelady.reborn.management.archive.domain.model.ArchivedPostItem
import reborn.shared.generated.resources.Res
import reborn.shared.generated.resources.img_memorial_album_dummy
import reborn.shared.generated.resources.img_memorial_bg_dummy
import reborn.shared.generated.resources.img_post_dummy1
import reborn.shared.generated.resources.img_post_dummy2
import reborn.shared.generated.resources.img_post_dummy3
import reborn.shared.generated.resources.img_profile_dummy_1
import reborn.shared.generated.resources.img_profile_dummy_2
import reborn.shared.generated.resources.img_profile_dummy_3
import reborn.shared.generated.resources.img_profile_dummy_4
import reborn.shared.generated.resources.img_profile_dummy_5

object ArchiveMockData {

    private val thumbnailPool = listOf(
        Res.drawable.img_post_dummy1,
        Res.drawable.img_post_dummy2,
        Res.drawable.img_post_dummy3,
        Res.drawable.img_memorial_bg_dummy,
        Res.drawable.img_memorial_album_dummy,
        Res.drawable.img_profile_dummy_1,
        Res.drawable.img_profile_dummy_2,
        Res.drawable.img_profile_dummy_3,
        Res.drawable.img_profile_dummy_4,
        Res.drawable.img_profile_dummy_5
    )

    val archivedPosts = (1..11).map { index ->
        ArchivedPostItem(
            id = index.toString(),
            thumbnail = thumbnailPool[(index - 1) % thumbnailPool.size]
        )
    }
}
