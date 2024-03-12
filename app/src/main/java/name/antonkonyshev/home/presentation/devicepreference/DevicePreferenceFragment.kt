package name.antonkonyshev.home.presentation.devicepreference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.presentation.device.DevicesActivity

class DevicePreferenceFragment : Fragment() {
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backToDevices()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel: DevicePreferenceViewModel by viewModels()
        if (viewModel.selectedDevice == null) {
            backToDevices()
        }
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DevicePreferenceComposable(
                    viewModel,
                    viewModel.uiState.collectAsState().value,
                    onSave = {
                        viewModel.saveDevicePreference { result ->
                            if (result)
                                backToDevices()
                        }
                    }
                )
            }
        }
    }

    private fun backToDevices() {
        requireActivity().supportFragmentManager.popBackStack()
        (requireActivity() as? DevicesActivity)?.deselectDevice()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: DevicePreferenceViewModel by viewModels()
        viewModel.selectedDevice = arguments?.getParcelable("device", Device::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
        viewModel.prepareData { result ->
            if (!result)
                backToDevices()
        }
    }

    override fun onDestroy() {
        backPressedCallback.isEnabled = false
        backPressedCallback.remove()
        super.onDestroy()
    }

    companion object {
        fun newInstance(device: Device): DevicePreferenceFragment {
            return DevicePreferenceFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("device", device)
                }
            }
        }
    }
}
