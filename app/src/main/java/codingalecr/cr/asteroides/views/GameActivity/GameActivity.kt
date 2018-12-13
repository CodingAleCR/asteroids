package codingalecr.cr.asteroides.views.GameActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import codingalecr.cr.asteroides.R
import codingalecr.cr.asteroides.views.GameView.GameView
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findViewById<GameView>(R.id.gameview).setParent(this)
    }

    override fun onResume() {
        super.onResume()
        gameview.thread.resumeGame()
        gameview.registerSensorListener()
    }

    override fun onPause() {
        gameview.unregisterSensorListener()
        gameview.thread.pauseGame()
        super.onPause()
    }

    override fun onDestroy() {
        gameview.thread.stopGame()
        super.onDestroy()
    }
}
