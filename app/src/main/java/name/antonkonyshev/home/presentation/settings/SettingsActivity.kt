package name.antonkonyshev.home.presentation.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import name.antonkonyshev.home.presentation.BaseActivity
import name.antonkonyshev.home.presentation.NavigationDestinations
import name.antonkonyshev.home.presentation.NavigationWrapper
import name.antonkonyshev.home.ui.theme.HomeTheme

class SettingsActivity : BaseActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SettingsViewModel by viewModels()

        setContent {
            val windowSize = calculateWindowSizeClass(activity = this).widthSizeClass
            HomeTheme {
                NavigationWrapper(
                    windowSize,
                    devicePostureFlow().collectAsState().value,
                    NavigationDestinations.SETTINGS,
                    viewModel.navigationBackgroundResource,
                    viewModel.backgroundResource,
                    sectionScreenComposable = { onDrawerClicked: () -> Unit ->
                        SettingsScreen(
                            windowSize,
                            onDrawerClicked
                        )
                    }
                )
            }
        }
    }
}