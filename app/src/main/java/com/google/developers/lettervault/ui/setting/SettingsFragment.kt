package com.google.developers.lettervault.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.developers.lettervault.R
import com.google.developers.lettervault.notification.NotificationWorker
import com.google.developers.lettervault.util.LETTER_ID
import com.google.developers.lettervault.util.NOTIFICATION_CHANNEL_ID
import com.google.developers.lettervault.util.NightMode
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    private val workManager by lazy { WorkManager.getInstance(requireActivity()) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>(getString(R.string.pref_key_night))
            ?.setOnPreferenceChangeListener { _, newValue ->
                newValue?.let {
                   val selectedValue =  when((it as String).uppercase()){
                        NightMode.ON.name -> NightMode.ON
                        NightMode.OFF.name -> NightMode.OFF
                        else -> NightMode.AUTO
                    }
                    updateTheme(nightMode = selectedValue.value)
                }
                true
            }
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }

//    private fun reminderVaultLetterBeOpen(){
//        val builder = PeriodicWorkRequestBuilder<NotificationWorker>(1,TimeUnit.DAYS)
//            .apply {
//                setInputData(
//                    Data.Builder()
//                        .putLong(LETTER_ID,)
//                )
//            }
//    }
}
