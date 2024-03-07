package name.antonkonyshev.home.presentation.devicepreference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.PunchClock
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material.icons.outlined.WaterfallChart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import name.antonkonyshev.home.R
import name.antonkonyshev.home.domain.entity.Device
import name.antonkonyshev.home.domain.entity.DevicePreference

@Composable
fun DevicePreferenceScreen(
    preference: DevicePreference,
    onDrawerClicked: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    AnimatedVisibility(visible = preference.device is Device) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
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

            val highPollution = remember { mutableIntStateOf(preference.highPollution) }
            PreferenceIntInput(
                valueState = highPollution,
                label = stringResource(R.string.carbon_monoxide_concentration_threshold),
                supportingText = {
                    Text(stringResource(R.string.warn_about_exceeding_the_concentration_of_co))
                },
                leadingIcon = Icons.Outlined.Masks,
                trailingIcon = stringResource(id = R.string.mg_m3)
            )

            val minTemperature = remember { mutableIntStateOf(preference.minTemperature) }
            PreferenceIntInput(
                valueState = minTemperature,
                label = stringResource(id = R.string.minimal_temperature),
                leadingIcon = Icons.Outlined.Thermostat,
                trailingIcon = stringResource(id = R.string.c)
            )

            val maxTemperature = remember { mutableIntStateOf(preference.maxTemperature) }
            PreferenceIntInput(
                valueState = maxTemperature,
                label = stringResource(id = R.string.maximal_temperature),
                leadingIcon = Icons.Outlined.Thermostat,
                trailingIcon = stringResource(id = R.string.c)
            )

            val measurementPeriod = remember { mutableIntStateOf(preference.measurementPeriod) }
            PreferenceIntInput(
                valueState = measurementPeriod,
                label = stringResource(id = R.string.measurement_period),
                leadingIcon = Icons.Outlined.WatchLater,
                trailingIcon = stringResource(id = R.string.sec)
            )

            val historyLength = remember { mutableIntStateOf(preference.historyLength) }
            PreferenceIntInput(
                valueState = historyLength,
                label = stringResource(id = R.string.history_length),
                leadingIcon = Icons.Outlined.WaterfallChart,
                trailingIcon = stringResource(id = R.string.sec)
            )

            val historyRecordPeriod = remember { mutableIntStateOf(preference.historyRecordPeriod) }
            PreferenceIntInput(
                valueState = historyRecordPeriod,
                label = stringResource(id = R.string.history_record_period),
                leadingIcon = Icons.Outlined.Watch,
                trailingIcon = stringResource(id = R.string.sec)
            )

            val timeSyncPeriod = remember { mutableIntStateOf(preference.timeSyncPeriod) }
            PreferenceIntInput(
                valueState = timeSyncPeriod,
                label = stringResource(id = R.string.time_sync_period),
                leadingIcon = Icons.Outlined.PunchClock,
                trailingIcon = stringResource(id = R.string.sec)
            )

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Button(onClick = onSave) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
fun PreferenceIntInput(
    valueState: MutableIntState,
    label: String,
    leadingIcon: ImageVector,
    trailingIcon: String,
    supportingText: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            value = valueState.value.toString(),
            onValueChange = {
                valueState.value = it.toInt()
            },
            label = { Text(label) },
            supportingText = supportingText,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
            trailingIcon = { Text(trailingIcon) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}