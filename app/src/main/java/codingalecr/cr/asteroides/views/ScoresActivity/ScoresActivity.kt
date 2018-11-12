package codingalecr.cr.asteroides.views.ScoresActivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import codingalecr.cr.asteroides.R
import codingalecr.cr.asteroides.adapters.ScoreAdapter
import codingalecr.cr.asteroides.views.MainActivity.MainActivity
import kotlinx.android.synthetic.main.activity_scores.*
import org.jetbrains.anko.toast

class ScoresActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        val adapter = ScoreAdapter(
            MainActivity.scoreManager.scoresList(10)
        )

        val manager = LinearLayoutManager(this)

        rv_scores.adapter = adapter
        rv_scores.layoutManager = manager

        adapter.onClickListener = View.OnClickListener {
            val pos = rv_scores.getChildAdapterPosition(it)
            val s = MainActivity.scoreManager.scoresList(10)[pos]
            toast("Selección: $pos - $s")
        }
    }
}
