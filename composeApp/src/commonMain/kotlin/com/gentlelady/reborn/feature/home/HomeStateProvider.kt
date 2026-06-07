import com.gentlelady.reborn.home.presentation.home.HomeState
import com.gentlelady.reborn.data.MockDataSource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class HomeStateProvider : PreviewParameterProvider<HomeState> {
    override val values = sequenceOf(
        // 1. 데이터가 꽉 찬 성공 상태
        HomeState(posts = MockDataSource.homePosts, isLoading = false),

        // 2. 로딩 중인 상태
        HomeState(isLoading = true),

        // 3. 에러가 발생한 상태
        HomeState(error = "네트워크 연결이 불안정합니다.")
    )
}