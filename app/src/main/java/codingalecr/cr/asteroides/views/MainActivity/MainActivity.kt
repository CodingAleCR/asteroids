package codingalecr.cr.asteroides.views.MainActivity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.LruCache
import android.view.Menu
import android.view.MenuItem
import codingalecr.cr.asteroides.R
import codingalecr.cr.asteroides.storage.*
import codingalecr.cr.asteroides.views.AboutActivity.AboutActivity
import codingalecr.cr.asteroides.views.GameActivity.GameActivity
import codingalecr.cr.asteroides.views.GameView.GameView
import codingalecr.cr.asteroides.views.PreferenceActivity.PreferencesActivity
import codingalecr.cr.asteroides.views.ScoresActivity.ScoresActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult


class MainActivity : AppCompatActivity() {

    companion object {
        var scoreManager: ScoreStorage =
            ListScoreManager(
                mutableListOf(
                    "123000 - Pepito Domingez",
                    "111000 - Pedro Martinez",
                    "011000 - Paco Pérez"
                )
            )
        const val GAME_ACTIVITY = 0
        const val PREFERENCE_ACTIVITY = 1
        var queue: RequestQueue? = null
        var imageLoader: ImageLoader? = null
    }

    private lateinit var mediaPlayer: MediaPlayer

    private val imageCache = object : ImageLoader.ImageCache {
        val cache = LruCache<String, Bitmap>(10)
        
        override fun putBitmap(url: String?, bitmap: Bitmap?) {
            cache.put(url, bitmap)
        }

        override fun getBitmap(url: String?): Bitmap {
            return cache.get(url)
        }

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

        mediaPlayer = MediaPlayer.create(this, R.raw.audio)

        refreshStorage()

        queue = Volley.newRequestQueue(this)
        imageLoader = ImageLoader(queue, imageCache)
        //Start animations
//        val rotationWithZoom = AnimationUtils.loadAnimation(this, R.anim.rotation_with_zoom)
//        tv_game_title.startAnimation(rotationWithZoom)
//
//        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
//        btn_play.startAnimation(fadeIn)
//        btn_settings.startAnimation(fadeIn)
//        btn_about.startAnimation(fadeIn)
//        btn_scores.startAnimation(fadeIn)
    }

    private fun isMusicEnabled(): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        return pref.getBoolean("music", resources.getBoolean(R.bool.default_music))
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getPlayerName(): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        return pref.getString("player", resources.getString(R.string.default_name))
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
        when (requestCode) {
            GAME_ACTIVITY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val score = data.extras?.get(GameView.SCORES_DATA) as Int
                        val name = getPlayerName()

                        scoreManager.storeScore(score, name, System.currentTimeMillis())
                        launchScoreList()
                    }
                }
            }

            PREFERENCE_ACTIVITY -> {
                //Do Score Manager refresh
                refreshStorage()
            }
        }
    }

    private fun refreshStorage() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val storageType = pref.getString("storage", resources.getString(R.string.default_name))

        when (storageType) {
            "array" -> {
                scoreManager = ListScoreManager(
                    mutableListOf(
                        "123000 - Pepito Domingez",
                        "111000 - Pedro Martinez",
                        "011000 - Paco Pérez"
                    )
                )
            }

            "preferences" -> {
                scoreManager = PreferenceScoreManager(this)
            }

            "internal" -> {
                scoreManager = IntStorageScoreManager(this)
            }

            "external" -> {
                scoreManager = ExtStorageScoreManager(this)
            }

            "raw" -> {
                scoreManager = RawStorageScoreManager(this)
            }

            "assets" -> {
                scoreManager = AssetsStorageScoreManager(this)
            }

            "xml" -> {
                scoreManager = XMLStorageScoreManager(this)
            }

            "gson" -> {
                scoreManager = GsonStorageScoreManager(this)
            }

            "json" -> {
                scoreManager = JsonStorageScoreManager(this)
            }

            "database" -> {
                scoreManager = DatabaseScoreManager(this)
            }

            "relational" -> {
                scoreManager = RelDatabaseScoreManager(this)
            }

            "sockets" -> {
                scoreManager = SocketScoreManager(this)
            }

            "socketasync" -> {
                scoreManager = AsyncSocketScoreManager(this)
            }

            "upv" -> {
                scoreManager = WebServiceScoreManager(this, "http://158.42.146.127/puntuaciones/")
            }

            "heroku" -> {
                scoreManager = WebServiceScoreManager(this, "http://asteroides-crc.herokuapp.com/puntuaciones/")
            }

            "async" -> {
                scoreManager = AsyncWebServiceScoreManager(this, "http://158.42.146.127/puntuaciones/")
            }
        }
    }

    private fun launchGame() {
        startActivityForResult<GameActivity>(GAME_ACTIVITY)
    }

    private fun launchPreferences() {
        startActivityForResult<PreferencesActivity>(PREFERENCE_ACTIVITY)
    }

    private fun launchAboutActivity() {
        startActivity<AboutActivity>()
    }

    private fun launchScoreList() {
        startActivity<ScoresActivity>()
    }
}
