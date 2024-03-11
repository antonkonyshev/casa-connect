package name.antonkonyshev.home.presentation.devicepreference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Masks
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material.icons.outlined.WaterfallChart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import name.antonkonyshev.home.R
import name.antonkonyshev.home.domain.entity.Device

@Composable
fun DevicePreferenceScreen(
    viewModel: DevicePreferenceViewModel,
    onDrawerClicked: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(start = 7.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.navigation_label)
                )
            }
            Text(
                text = stringResource(R.string.device_preferences),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxHeight()
        ) {
            AnimatedVisibility(visible = viewModel.selectedDevice is Device) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {

                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.deviceName,
                            onValueChange = { viewModel.deviceName = it },
                            enabled = true,
                            readOnly = false,
                            label = {
                                Text(text = stringResource(R.string.device_name))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    PreferenceIntInput(
                        value = viewModel.highPollution,
                        onChange = { viewModel.highPollution = it.toIntOrNull() ?: 0 },
                        label = stringResource(R.string.carbon_monoxide_concentration_threshold),
                        supportingText = {
                            Text(stringResource(R.string.warn_about_exceeding_the_concentration_of_co))
                        },
                        leadingIcon = Icons.Outlined.Masks,
                        trailingIcon = stringResource(id = R.string.mg_m3)
                    )

                    PreferenceIntInput(
                        value = viewModel.minTemperature,
                        onChange = { viewModel.minTemperature = it.toIntOrNull() ?: 0 },
                        label = stringResource(id = R.string.minimal_temperature),
                        leadingIcon = Icons.Outlined.Thermostat,
                        trailingIcon = stringResource(id = R.string.c)
                    )

                    PreferenceIntInput(
                        value = viewModel.maxTemperature,
                        onChange = { viewModel.maxTemperature = it.toIntOrNull() ?: 0 },
                        label = stringResource(id = R.string.maximal_temperature),
                        leadingIcon = Icons.Outlined.Thermostat,
                        trailingIcon = stringResource(id = R.string.c)
                    )

                    PreferenceIntInput(
                        value = viewModel.measurementPeriod,
                        onChange = { viewModel.measurementPeriod = it.toIntOrNull() ?: 0 },
                        label = stringResource(id = R.string.measurement_period),
                        leadingIcon = Icons.Outlined.WatchLater,
                        trailingIcon = stringResource(id = R.string.sec)
                    )

                    PreferenceIntInput(
                        value = viewModel.historyLength,
                        onChange = { viewModel.historyLength = it.toIntOrNull() ?: 0 },
                        label = stringResource(id = R.string.history_length),
                        leadingIcon = Icons.Outlined.WaterfallChart,
                        trailingIcon = stringResource(id = R.string.sec)
                    )

                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Button(onClick = onSave) {
                            Text(stringResource(R.string.save), modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreferenceIntInput(
    value: Int,
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
            value = value.toString(),
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