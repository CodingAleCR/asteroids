package codingalecr.cr.asteroides.views.PreferenceActivity

import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceFragment
import codingalecr.cr.asteroides.R
import org.jetbrains.anko.toast


class PreferencesFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        val fragmentsPreference = findPreference("fragments")

        fragmentsPreference.onPreferenceChangeListener =
                OnPreferenceChangeListener { _, newValue ->
                    val value: Int
                    try {
                        value = newValue.toString().toInt()
                    } catch (e: Exception) {
                        activity.toast(R.string.helper_not_number)
                        return@OnPreferenceChangeListener false
                    }
                    if (value in 0..9) {
                        fragmentsPreference.summary = "En cuantos trozos se divide un asteroide ($value)"; true
                    } else {
                        activity.toast(R.string.helper_max_fragments)
                        false
                    }
                }
    }
}