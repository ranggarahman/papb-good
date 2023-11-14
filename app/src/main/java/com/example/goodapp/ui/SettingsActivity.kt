package com.example.goodapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.goodapp.R
import com.example.goodapp.notification.NotificationWorker
import com.example.goodapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val notificationWorkTag = "notificationWorkTag"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)

                Log.d(TAG, "NEW VALUE : $newValue")

                if (newValue == true) {
                    scheduleDailyReminder(channelName)
                } else {
                    cancelDailyReminder()
                }
                true
            }
        }

        private fun scheduleDailyReminder(channelName: String) {

            Log.d(TAG , "REMINDER BUILDER CALLED")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(true)
                .build()

            val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .setInputData(createNotificationInputData(channelName))
                .build()

            WorkManager.getInstance(requireContext())
                .enqueueUniquePeriodicWork(notificationWorkTag, ExistingPeriodicWorkPolicy.KEEP, notificationWorkRequest)
        }

        private fun cancelDailyReminder() {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(notificationWorkTag)
        }

        private fun createNotificationInputData(channelName: String): Data {
            return Data.Builder()
                .putString(NOTIFICATION_CHANNEL_ID, channelName)
                .build()
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }

    companion object {
        private const val TAG = "SettingActivity"
    }
}