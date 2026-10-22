package com.gentlelady.reborn.search

import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.CurrentUserIdUseCase
import com.gentlelady.reborn.core.auth.Session
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.home.domain.model.HomePost
import com.gentlelady.reborn.search.data.SearchRepository
import com.gentlelady.reborn.search.data.compactCount
import com.gentlelady.reborn.search.domain.entity.MemorialSearchItem
import com.gentlelady.reborn.search.presentation.SearchIntent
import com.gentlelady.reborn.search.presentation.SearchTab
import com.gentlelady.reborn.search.presentation.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() = Dispatchers.setMain(dispatcher)
    @AfterTest fun tearDown() = Dispatchers.resetMain()

    private class FakeSearch : SearchRepository {
        val asked = mutableListOf<Pair<String, SearchTab>>()
        var fail = false
        override suspend fun search(query: String, sort: SearchTab): List<MemorialSearchItem> {
            asked += query to sort
            if (fail) error("down")
            return listOf(MemorialSearchItem("$query-${sort.name}", 1, query, "", "", "", "0", null))
        }
        override suspend fun randomPosts() = listOf(HomePost(id = "r1", authorName = "a", caption = "c", postedAt = ""))
    }

    private fun setup(): Pair<SearchViewModel, FakeSearch> {
        val store = SessionStore().apply { current = Session("AT", "RT", 0, userId = "me") }
        val repo = FakeSearch()
        return SearchViewModel(repo, CurrentUserIdUseCase(store)) to repo
    }

    @Test
    fun typingSearchesOnceAfterPauseAndSortReQueriesServer() = runTest(dispatcher) {
        val (vm, repo) = setup()
        runCurrent()
        assertEquals(listOf("r1"), vm.state.value.randomFeed.map { it.id })
        vm.dispatch(SearchIntent.UpdateQuery("박"))
        advanceTimeBy(100)
        vm.dispatch(SearchIntent.UpdateQuery("박지"))
        advanceTimeBy(400)
        runCurrent()
        assertEquals(listOf("박지" to SearchTab.LIKES), repo.asked) // 입력이 멈춘 뒤 한 번만
        vm.dispatch(SearchIntent.ChangeTab(SearchTab.VIEWS))
        runCurrent()
        assertEquals("박지" to SearchTab.VIEWS, repo.asked.last())
        assertEquals(listOf("박지-VIEWS"), vm.state.value.results.map { it.id })
        vm.dispatch(SearchIntent.UpdateQuery(""))
        assertTrue(vm.state.value.results.isEmpty())
        vm.viewModelScope.cancel()
    }

    @Test
    fun failureShowsMessage() = runTest(dispatcher) {
        val (vm, repo) = setup()
        runCurrent()
        repo.fail = true
        vm.dispatch(SearchIntent.ExecuteSearch)
        vm.dispatch(SearchIntent.UpdateQuery("x"))
        advanceTimeBy(400)
        runCurrent()
        assertEquals("검색하지 못했어요. 잠시 후 다시 시도해 주세요.", vm.state.value.error)
        vm.viewModelScope.cancel()
    }

    @Test
    fun compactCounts() {
        assertEquals("0", compactCount(0))
        assertEquals("999", compactCount(999))
        assertEquals("1.2k", compactCount(1234))
        assertEquals("12k", compactCount(12_000))
        assertEquals("123k", compactCount(123_456))
        assertEquals("24.8k", compactCount(24_800))
        assertEquals("1.5m", compactCount(1_500_000))
    }
}
