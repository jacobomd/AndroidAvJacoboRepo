package io.keepcoding.eh_ho.topics


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.view_retry.*


class TopicsFragment : Fragment(){

    var listener: TopicsInteractionListener? = null
    lateinit var adapter: TopicsAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TopicsInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        adapter = TopicsAdapter{
            goToPosts(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_topics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        listTopics.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listTopics.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listTopics.adapter = adapter

        buttonCreate.setOnClickListener {
            goToCreateTopic()
        }

        buttonRetry.setOnClickListener {
            loadTopics()
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener { loadTopics() }


        // OPCION MAS SIMPLE PARA OCULTAR EL FLOATINBUTTONACTION AL HACER SCROLL

        /*listTopics.addOnScrollListener( object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == 1)
                    buttonCreate.hide()
                else
                    buttonCreate.show()
            }
        })*/

    }

    override fun onResume() {
        super.onResume()
        loadTopics()
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_log_out -> listener?.onLogOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadTopics() {

        enableLoading(true)

        context?.let {
            TopicsRepo.getTopics(
                it,
                {
                    enableLoading(false)
                    adapter.setTopics(it)
                    swipeRefreshLayout.isRefreshing = false
                },
                {
                    enableLoading(false)
                    handleRequestError(it)
                }
            )
        }

    }

    private fun enableLoading(enabled: Boolean) {
        viewRetry.visibility = View.INVISIBLE

        if (enabled) {
            listTopics.visibility = View.INVISIBLE
            buttonCreate.hide()
            viewLoading.visibility = View.VISIBLE
        } else {
            listTopics.visibility = View.VISIBLE
            buttonCreate.show()
            viewLoading.visibility = View.INVISIBLE
        }
    }


    private fun handleRequestError(requestError: RequestError) {

        listTopics.visibility = View.INVISIBLE
        viewRetry.visibility = View.VISIBLE

        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }


    private fun goToCreateTopic() {
        listener?.onGoToCreateTopic()
    }

    private fun goToPosts(it: Topic) {
        listener?.onTopicSelected(it)

    }

    interface TopicsInteractionListener {
        fun onTopicSelected(topic: Topic)
        fun onGoToCreateTopic()
        fun onLogOut()
    }

}




