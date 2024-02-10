package name.antonkonyshev.home.presentation.devicepreference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import name.antonkonyshev.home.R
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference

@Composable
fun DevicePreferenceScreen(preference: DevicePreference) {
    AnimatedVisibility(visible = preference.device is Device) {
        Column {
            Row {
                val name = remember { mutableStateOf(preference.device!!.name) }
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    enabled = true,
                    readOnly = false,
                    label = {
                        Text(text = stringResource(R.string.device_name))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {
                val highPollution = remember { mutableIntStateOf(preference.highPollution) }
                OutlinedTextField(
                    value = highPollution.value.toString(),
                    onValueChange = {
                        try {
                            highPollution.value = it.toInt()
                        } catch (_: Exception) {}
                    },
                    label = {
                        Text(stringResource(R.string.carbon_monoxide_concentration_threshold))
                    },
                    supportingText = {
                        Text(stringResource(R.string.warn_about_exceeding_the_concentration_of_co))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Masks, contentDescription = null)
                    },
                    trailingIcon = {
                        Text(stringResource(R.string.mg_m3))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row {
                Button(onClick = {}) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}