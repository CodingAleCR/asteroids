package codingalecr.cr.asteroides.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import codingalecr.cr.asteroides.R
import codingalecr.cr.asteroides.views.MainActivity.MainActivity
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.item_score.view.*

class ScoreAdapter(private val scoreList: List<String>) : RecyclerView.Adapter<ScoreAdapter.ScoreViewholder>() {

    var onClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_score, parent, false)
        v.setOnClickListener(onClickListener)
        return ScoreViewholder(v)
    }

    override fun getItemCount(): Int {
        return scoreList.size
    }

    override fun onBindViewHolder(holder: ScoreViewholder, position: Int) {
        holder.itemView.titulo.text = scoreList[position]

        when (position) {
            0 -> {
                holder.icon.setImageResource(R.drawable.asteroide1)
            }

            1 -> {
                holder.icon.setImageResource(R.drawable.asteroide2)
            }

            else -> {
                holder.icon.setImageResource(R.drawable.asteroide3)
            }
        }

//        holder.icon.setImageUrl("http://mmoviles.upv.es/img/moviles.png", MainActivity.imageLoader)
    }

    class ScoreViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val title: TextView = itemview.titulo
        val subtitle: TextView = itemview.subtitulo
        val icon: ImageView = itemview.icono
//        val icon: NetworkImageView = itemview.icono
    }
}