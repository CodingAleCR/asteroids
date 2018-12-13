package codingalecr.cr.asteroides.views.MainActivity

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import codingalecr.cr.asteroides.R
import codingalecr.cr.asteroides.utils.ScoreListManager
import codingalecr.cr.asteroides.views.AboutActivity.AboutActivity
import codingalecr.cr.asteroides.views.GameActivity.GameActivity
import codingalecr.cr.asteroides.views.GameView.GameView
import codingalecr.cr.asteroides.views.PreferenceActivity.PreferencesActivity
import codingalecr.cr.asteroides.views.ScoresActivity.ScoresActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


class MainActivity : AppCompatActivity() {

    companion object {
        val scoreManager: ScoreListManager = ScoreListManager(
            mutableListOf(
                "123000 - Pepito Domingez",
                "111000 - Pedro Martinez",
                "011000 - Paco Pérez"
            )
        )
        const val GAME_ACTIVITY = 0
    }

    private lateinit var mediaPlayer: MediaPlayer

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
//        val rotationWithZoom = AnimationUtils.loadAnimation(this, R.anim.rotation_with_zoom)
//        tv_game_title.startAnimation(rotationWithZoom)
//
//        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
//        btn_play.startAnimation(fadeIn)
//        btn_settings.startAnimation(fadeIn)
//        btn_about.startAnimation(fadeIn)
//        btn_scores.startAnimation(fadeIn)

        mediaPlayer = MediaPlayer.create(this, R.raw.audio)
    }

    private fun isMusicEnabled(): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        return pref.getBoolean("music", resources.getBoolean(R.bool.default_music))
    }

    override fun onStart() {
        super.onStart()
        if (isMusicEnabled())
            mediaPlayer.start()
    }

    override fun onStop() {
        if (isMusicEnabled())
            mediaPlayer.pause()
        super.onStop()
    }

    override fun onSaveInstanceState(saveInstanceState: Bundle) {
        super.onSaveInstanceState(saveInstanceState)
        if (isMusicEnabled()) {
            mediaPlayer.pause()
            saveInstanceState.putInt("musicMillisecond", mediaPlayer.currentPosition)
        }
    }

    override fun onRestoreInstanceState(saveInstanceState: Bundle?) {
        super.onRestoreInstanceState(saveInstanceState)
        if (saveInstanceState != null && isMusicEnabled()) {
            mediaPlayer.seekTo(saveInstanceState.getInt("musicMillisecond"))
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GAME_ACTIVITY
            && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            val score = data.extras?.get(GameView.SCORES_DATA) as Int
            val name = "Player"
            scoreManager.storeScore(score, name, System.currentTimeMillis())
            launchScoreList()
        }
    }

    private fun launchGame() {
        startActivityForResult<GameActivity>(GAME_ACTIVITY)
    }

    private fun launchPreferences() {
        startActivity<PreferencesActivity>()
    }

    private fun launchAboutActivity() {
        startActivity<AboutActivity>()
    }

    private fun launchScoreList() {
        startActivity<ScoresActivity>()
    }
}
