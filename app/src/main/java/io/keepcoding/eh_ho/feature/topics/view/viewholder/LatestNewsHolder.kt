package io.keepcoding.eh_ho.feature.topics.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.domain.LatestNews
import kotlinx.android.synthetic.main.item_latest_news.view.*

class LatestNewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var latestNew: LatestNews? = null
        set(value) {
            field = value

            with(itemView) {
                tag = field

                field?.let {
                    textViewTitle.text = field?.topic_title
                    textViewContent.text = field?.topic_slug
                    textViewDate.text = field?.created_at.toString()
                    textViewPostNumber.text = field?.post_number.toString()
                    textViewScore.text = field?.score.toString()

                }

            }

        }

}