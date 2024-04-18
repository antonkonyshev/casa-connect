package com.github.antonkonyshev.casaconnect.presentation.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit

@Composable
fun SettingsScreen() {
    Row(
        Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)
    ) {
        AndroidView(factory = { context ->
            FragmentContainerView(context).apply {
                id = ViewCompat.generateViewId()
                (context as AppCompatActivity).supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(id, SettingsFragment(context))
                }
            }
        })
    }
}