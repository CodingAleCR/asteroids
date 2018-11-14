package codingalecr.cr.asteroides.views.GameActivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import codingalecr.cr.asteroides.R
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    override fun onPause() {
        super.onPause()
        gameview.thread.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gameview.thread.resumeGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameview.thread.stopGame()
    }
}
