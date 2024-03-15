package com.github.antonkonyshev.casaconnect.presentation.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.github.antonkonyshev.casaconnect.R

class SettingsFragment(private val context: Context) : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey)
    }
}

class UpdatePeriodPreference(
    context: Context, attrs: AttributeSet?
) : EditTextPreference(context, attrs) {
    init {
        val defaultValue = 15
        setIcon(R.drawable.ic_sync)
        try {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            summary = context.getString(
                R.string.int_second_s,
                preferences.getString(key, defaultValue.toString())?.toInt() ?: defaultValue
            )
        } catch (_: Exception) {
        }

        setOnPreferenceChangeListener { preference, value ->
            try {
                preference.summary = context.getString(
                    R.string.int_second_s, value?.toString()?.toInt() ?: defaultValue
                )
            } catch (_: Exception) {
            }
            true
        }

        setOnBindEditTextListener { etView ->
            etView.inputType = InputType.TYPE_CLASS_NUMBER
            etView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(value: Editable?) {
                    try {
                        if (value.toString().toInt() < 1) {
                            etView.error =
                                context.getString(R.string.period_must_be_greater_than_1_second)
                        }
                    } catch (err: Exception) {
                        etView.error = context.getString(R.string.only_digits_are_allowed)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }
    }
}
