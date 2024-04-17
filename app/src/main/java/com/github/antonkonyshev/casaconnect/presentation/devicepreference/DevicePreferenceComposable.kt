package com.github.antonkonyshev.casaconnect.presentation.devicepreference

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material.icons.outlined.WaterfallChart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.antonkonyshev.casaconnect.R
import com.github.antonkonyshev.casaconnect.domain.entity.DevicePreference
import com.github.antonkonyshev.casaconnect.presentation.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicePreferenceScreen(
    preference: DevicePreference?,
    uiState: UiState,
    onSave: () -> Unit = {}
) {
    Crossfade(targetState = uiState.loading) { loading ->

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = stringResource(R.string.loading),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                )
            }
        } else {

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {

                var deviceName by rememberSaveable {
                    mutableStateOf(preference?.device?.name ?: "")
                }
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    OutlinedTextField(
                        value = deviceName,
                        onValueChange = { deviceName = it },
                        enabled = true,
                        readOnly = false,
                        label = {
                            Text(text = stringResource(R.string.device_name))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp)
                    )
                }

                var highPollution by rememberSaveable {
                    mutableStateOf(preference?.highPollution.toString())
                }
                PreferenceInput(
                    value = highPollution,
                    onChange = { highPollution = it.toIntOrNull()?.toString() ?: "" },
                    label = stringResource(R.string.carbon_monoxide_concentration_threshold),
                    supportingText = {
                        Text(stringResource(R.string.warn_about_exceeding_the_concentration_of_co))
                    },
                    leadingIcon = Icons.Outlined.Masks,
                    trailingIcon = stringResource(id = R.string.mg_m3)
                )

                var minTemperature by rememberSaveable {
                    mutableStateOf(preference?.minTemperature.toString())
                }
                PreferenceInput(
                    value = minTemperature,
                    onChange = { minTemperature = it.toIntOrNull()?.toString() ?: "" },
                    label = stringResource(id = R.string.minimal_temperature),
                    leadingIcon = Icons.Outlined.Thermostat,
                    trailingIcon = stringResource(id = R.string.c)
                )

                var maxTemperature by rememberSaveable {
                    mutableStateOf(preference?.maxTemperature.toString())
                }
                PreferenceInput(
                    value = maxTemperature,
                    onChange = { maxTemperature = it.toIntOrNull()?.toString() ?: "" },
                    label = stringResource(id = R.string.maximal_temperature),
                    leadingIcon = Icons.Outlined.Thermostat,
                    trailingIcon = stringResource(id = R.string.c)
                )

                var measurementPeriod by rememberSaveable {
                    mutableStateOf(preference?.measurementPeriod.toString())
                }
                PreferenceInput(
                    value = measurementPeriod,
                    onChange = { measurementPeriod = it.toIntOrNull()?.toString() ?: "" },
                    label = stringResource(id = R.string.measurement_period),
                    leadingIcon = Icons.Outlined.WatchLater,
                    trailingIcon = stringResource(id = R.string.sec)
                )

                var historyLength by rememberSaveable {
                    mutableStateOf(preference?.historyLength.toString())
                }
                PreferenceInput(
                    value = historyLength,
                    onChange = { historyLength = it.toIntOrNull()?.toString() ?: "" },
                    label = stringResource(id = R.string.history_length),
                    leadingIcon = Icons.Outlined.WaterfallChart,
                    trailingIcon = stringResource(id = R.string.sec)
                )

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Button(onClick = {
                        preference?.highPollution = highPollution.toIntOrNull() ?: 20
                        preference?.minTemperature = minTemperature.toIntOrNull() ?: 14
                        preference?.maxTemperature = maxTemperature.toIntOrNull() ?: 24
                        preference?.measurementPeriod = measurementPeriod.toIntOrNull() ?: 15
                        preference?.historyLength = historyLength.toIntOrNull() ?: 50
                        preference?.device?.name = deviceName
                        onSave()
                    }) {
                        Text(stringResource(R.string.save), modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceInput(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    trailingIcon: String,
    supportingText: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
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