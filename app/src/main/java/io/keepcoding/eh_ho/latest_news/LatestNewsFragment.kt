package io.keepcoding.eh_ho.latest_news


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.*
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_ID
import kotlinx.android.synthetic.main.content_topic.*
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.fragment_posts.swipeRefreshLayout
import kotlinx.android.synthetic.main.view_retry.*

const val LATEST_NEWS_FRAGMENT_TAG = "LATEST_NEWS_FRAGMENT"

class LatestNewsFragment : Fragment() {

    var listener: LatestNewsInteractionListener? = null
    lateinit var adapter : LatestNewsAdapter

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
            goToTopicDetail(it = it)
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

        //val texto = arguments?.getString(EXTRA_TOPIC_ID)
        //val topicId = texto?.toInt()

        listLatestNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listLatestNews.adapter = adapter


        swipeRefreshLayoutLatestNews.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayoutLatestNews.setOnRefreshListener { loadLatestNews() }
        buttonRetry.setOnClickListener { loadLatestNews() }

     /*   topicId?.let {
            loadPost(it)
            buttonRetry.setOnClickListener {
                loadPost(topicId)
            }
            swipeRefreshLayout.setOnRefreshListener {
                loadPost(topicId)
            }
        }*/

    }

    override fun onResume() {
        super.onResume()
        loadLatestNews()
    }

    private fun loadLatestNews() {

        enableLoading(true)

        context?.let {
            LatestNewsRepo.getLatestNews(
                it,
                {
                    enableLoading(false)
                    adapter.setLatestNews(it)
                    swipeRefreshLayoutLatestNews.isRefreshing = false
                },
                {
                    enableLoading(false)
                    handleRequestError(it)
                }
            )
        }

    }

    private fun enableLoading(enabled: Boolean) {
        viewRetryLatestNews.visibility = View.INVISIBLE

        if (enabled) {
            listLatestNews.visibility = View.INVISIBLE
            //buttonCreate.hide()
            viewLoadingLatestNews.visibility = View.VISIBLE
        } else {
            listLatestNews.visibility = View.VISIBLE
            //buttonCreate.show()
            viewLoadingLatestNews.visibility = View.INVISIBLE
        }
    }

    private fun handleRequestError(requestError: RequestError) {

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

    private fun goToTopicDetail(it: LatestNews) {
        listener?.onLatestNewSelected(it)

    }

    interface LatestNewsInteractionListener {
        fun onLatestNewSelected(latestNews: LatestNews)
    }

}
