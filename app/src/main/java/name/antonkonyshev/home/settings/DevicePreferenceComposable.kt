package name.antonkonyshev.home.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun DevicePreferenceScreen() {
    var value = remember { mutableStateOf (TextFieldValue())}
    Column {
        OutlinedTextField(
            value = value.value,
            onValueChange = { value.value = it },
            enabled = true,
            readOnly = false,
            label = {
                Text(text = "Name")
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}