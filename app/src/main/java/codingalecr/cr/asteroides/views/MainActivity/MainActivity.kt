package codingalecr.cr.asteroides.views.MainActivity

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import codingalecr.cr.asteroides.R
import codingalecr.cr.asteroides.utils.ScoreListManager
import codingalecr.cr.asteroides.views.AboutActivity.AboutActivity
import codingalecr.cr.asteroides.views.GameActivity.GameActivity
import codingalecr.cr.asteroides.views.PreferenceActivity.PreferencesActivity
import codingalecr.cr.asteroides.views.ScoresActivity.ScoresActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    companion object {
        val scoreManager: ScoreListManager = ScoreListManager(
            mutableListOf(
                "123000 - Pepito Domingez",
                "111000 - Pedro Martinez",
                "011000 - Paco Pérez"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_about.setOnClickListener { launchAboutActivity() }
        btn_scores.setOnClickListener { launchScoreList() }
        btn_settings.setOnClickListener { launchPreferences() }
        btn_play.setOnClickListener { launchGame() }

        //Hide the ActionBar title.
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //Start animations
        val rotationWithZoom = AnimationUtils.loadAnimation(this, R.anim.rotation_with_zoom)
        tv_game_title.startAnimation(rotationWithZoom)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        btn_play.startAnimation(fadeIn)
        btn_settings.startAnimation(fadeIn)
        btn_about.startAnimation(fadeIn)
        btn_scores.startAnimation(fadeIn)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_preferences) {
            launchPreferences()
            return true
        }
        if (id == R.id.action_about) {
            launchAboutActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchGame() {
        startActivity(intentFor<GameActivity>())
    }

    private fun launchPreferences() {
        startActivity(intentFor<PreferencesActivity>())
    }

    private fun launchAboutActivity() {
        startActivity(intentFor<AboutActivity>())
    }

    private fun launchScoreList() {
        startActivity(intentFor<ScoresActivity>())
    }

    private fun showPreferences() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val s = "Música: ${pref.getBoolean("music", true)}, " +
                "Gráficos: ${pref.getString("graphics", "?")}, " +
                "Fragmentos: ${pref.getString("fragments", "0")}, " +
                "Gráficos: ${pref.getString("graphics", "?")}, " +
                "Modo Multijugador: ${pref.getBoolean("multiplayer_mode", false)}, " +
                "Max. Jugadores: ${pref.getString("player_quantity", "0")}, " +
                "Tipo Conexion: ${pref.getString("connection", "?")}"
        toast(s)
    }
}
