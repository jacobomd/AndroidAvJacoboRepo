package io.keepcoding.eh_ho.latest_news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.LatestNews
import io.keepcoding.eh_ho.data.Topic
import kotlinx.android.synthetic.main.item_latest_news.view.*
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_post.view.textViewContent
import kotlinx.android.synthetic.main.item_post.view.textViewDate
import kotlinx.android.synthetic.main.item_post.view.textViewTitle


class LatestNewsAdapter (
    val latestNewClickListener: ((LatestNews) -> Unit)? = null
): RecyclerView.Adapter<LatestNewsAdapter.LatestNewsHolder>() {

    private val latestNews = mutableListOf<LatestNews>()
    private val listener : ((View) -> Unit) = {
        val latestNew = it.tag as LatestNews
        //Log.d(TopicsAdapter::class.java.simpleName, latestNew.toString())
        latestNewClickListener?.invoke(latestNew)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestNewsHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_latest_news, parent, false)

        return LatestNewsHolder(view)
    }

    override fun getItemCount(): Int {
        return latestNews.size
    }

    override fun onBindViewHolder(holder: LatestNewsHolder, position: Int) {
        val latestNew = latestNews[position]
        holder.latestNew= latestNew
        holder.itemView.setOnClickListener (listener)
    }

    fun setLatestNews (latestNews: List<LatestNews>) {
        this.latestNews.clear()
        this.latestNews.addAll(latestNews)
        notifyDataSetChanged()
    }

    inner  class LatestNewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var latestNew : LatestNews? = null
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

}