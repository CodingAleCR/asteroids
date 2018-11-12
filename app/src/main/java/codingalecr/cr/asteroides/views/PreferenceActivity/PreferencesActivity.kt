package codingalecr.cr.asteroides.views.PreferenceActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class PreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, PreferencesFragment()).commit()
    }
}
