package codingalecr.cr.asteroides.views.PreferenceActivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class PreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, PreferencesFragment()).commit()
    }
}
