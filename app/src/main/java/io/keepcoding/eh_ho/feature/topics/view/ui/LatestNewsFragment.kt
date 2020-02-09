package io.keepcoding.eh_ho.feature.topics.view.ui


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.LatestNewsRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.LatestNews
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.feature.topics.view.adapter.LatestNewsAdapter
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.view_retry.*

const val LATEST_NEWS_FRAGMENT_TAG = "LATEST_NEWS_FRAGMENT"

class LatestNewsFragment : Fragment() {

    var listener: LatestNewsInteractionListener? = null
    lateinit var adapter: LatestNewsAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LatestNewsInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
        adapter = LatestNewsAdapter {
            latestNewItemClicked(it = it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listLatestNews.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listLatestNews.adapter = adapter

        swipeRefreshLayoutLatestNews.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayoutLatestNews.setOnRefreshListener { swipeRefreshLayoutLatestNewsClicked() }
        buttonRetry.setOnClickListener { retryButtonClicked() }

    }

    private fun retryButtonClicked() {
        listener?.onRetryButtonClicked()
    }

    private fun swipeRefreshLayoutLatestNewsClicked() {
        listener?.onSwipeRefreshLayoutLatestNewsClicked()
    }

    private fun latestNewItemClicked(it: LatestNews) {
        listener?.onLatestNewSelected(it)

    }

    override fun onResume() {
        super.onResume()
        listener?.onLatestNewResumed()
    }

    fun loadLatestNewsListt(latestNews: List<LatestNews>) {
        enableLoading(false)
        adapter.setLatestNews(latestNews = latestNews)
        swipeRefreshLayoutLatestNews.isRefreshing = false
    }


    fun enableLoading(enabled: Boolean) {
        viewRetryLatestNews.visibility = View.INVISIBLE

        if (enabled) {
            listLatestNews.visibility = View.INVISIBLE
            viewLoadingLatestNews.visibility = View.VISIBLE
        } else {
            listLatestNews.visibility = View.VISIBLE
            viewLoadingLatestNews.visibility = View.INVISIBLE
        }
    }

    fun handleRequestError(requestError: RequestError) {

        listLatestNews.visibility = View.INVISIBLE
        viewRetryLatestNews.visibility = View.VISIBLE

        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayoutLatest, message, Snackbar.LENGTH_LONG).show()
    }


    interface LatestNewsInteractionListener {
        fun onLatestNewSelected(latestNews: LatestNews)
        fun onLatestNewResumed()
        fun onRetryButtonClicked()
        fun onSwipeRefreshLayoutLatestNewsClicked()
    }

}
