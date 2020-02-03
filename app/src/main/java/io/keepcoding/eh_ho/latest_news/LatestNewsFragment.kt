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
import io.keepcoding.eh_ho.data.PostsRepo
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_ID
import io.keepcoding.eh_ho.posts.PostsAdapter
import io.keepcoding.eh_ho.posts.PostsFragment
import kotlinx.android.synthetic.main.fragment_latest_news.*
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.view_retry.*


class LatestNewsFragment : Fragment() {

    //var listener: LatestNewsFragment.PostsInteractionListener? = null
    lateinit var adapter : LatestNewsAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        //if (context is LatestNewsFragment.PostsInteractionListener) {
          //  listener = context
        //}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
        adapter = LatestNewsAdapter()
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

        val texto = arguments?.getString(EXTRA_TOPIC_ID)
        val topicId = texto?.toInt()

        listLatestNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listLatestNews.adapter = adapter

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)

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

        //enableLoading(true)

        context?.let {
            PostsRepo.getLatestNews(
                it,
                {
                    //enableLoading(false)
                    adapter.setLatestNews(it)
                    swipeRefreshLayout.isRefreshing = false
                },
                {
                    //enableLoading(false)
                    handleRequestError(it)
                }
            )
        }

    }

    private fun handleRequestError(requestError: RequestError) {

        //listTopics.visibility = View.INVISIBLE
        //viewRetry.visibility = View.VISIBLE

        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayoutLatest, message, Snackbar.LENGTH_LONG).show()
    }

}
