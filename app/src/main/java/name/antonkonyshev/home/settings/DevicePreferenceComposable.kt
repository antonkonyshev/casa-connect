package name.antonkonyshev.home.settings

import android.graphics.drawable.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import name.antonkonyshev.home.R
import name.antonkonyshev.home.UiState
import name.antonkonyshev.home.devices.Device

@Composable
fun DevicePreferenceScreen(uiState: UiState, preference: DevicePreference) {
    AnimatedVisibility(visible = preference.device is Device) {
        Column {
            Row {
                var name = remember { mutableStateOf(preference.device!!.name) }
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
                var highPollution = remember { mutableStateOf(preference.highPollution) }
                OutlinedTextField(
                    value = highPollution.value.toString(),
                    onValueChange = {
                        try {
                            highPollution.value = it.toInt()
                        } catch (err: Exception) {}
                    },
                    label = {
                        Text(text = "Порог концентрации окиси углерода")
                    },
                    supportingText = {
                        Text("Предупреждать при превышении указанной концентрации CO")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Masks, contentDescription = null)
                    },
                    trailingIcon = {
                        Text("mg/m3")
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