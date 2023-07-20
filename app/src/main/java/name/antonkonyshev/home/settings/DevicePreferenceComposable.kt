package name.antonkonyshev.home.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DevicePreferenceScreen() {
    Text(text = "Hello!")
    Column {
        OutlinedTextField(
            value = "11",
            onValueChange = { },
            enabled = true,
            readOnly = false,
            label = {
                Text(text = "name")
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}