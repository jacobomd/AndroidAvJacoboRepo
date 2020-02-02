package io.keepcoding.eh_ho.topics

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.LoadingDialogFragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.CreateTopicModel
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_TITLE
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_create_topic.*
import kotlinx.android.synthetic.main.fragment_create_topic.parentLayout

const val TAG_LOADING_DIALOG = "Loading_dialog"
class CreateTopicFragment : Fragment () {

    var listener: CreateTopicInterationListener? = null
    lateinit var  loadingDialogFragment: LoadingDialogFragment

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreateTopicInterationListener)
            listener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        loadingDialogFragment = LoadingDialogFragment.newInstance(getString(R.string.label_create_topic))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_topic, container, false)

    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_create_topic, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_send -> createTopic()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createTopic() {
        if (isFormValid()) {
            postTopic()
        } else {
            showErrors()
        }
    }

    private fun postTopic() {
        val model = CreateTopicModel(
            inputTitle.text.toString(),
            inputContent.text.toString()
        )
        context?.let {
            enableLoadingDialog(true)
            TopicsRepo.createTopic(
                it,
                model,
                {
                    enableLoadingDialog(false)
                    listener?.onTopicCreated()
                },
                {
                    enableLoadingDialog(false)
                    handleError(it)
                }
            )
        }
    }

    private fun enableLoadingDialog(enable: Boolean) {
        if (enable)
            loadingDialogFragment.show(childFragmentManager, TAG_LOADING_DIALOG)
        else
            loadingDialogFragment.dismiss()
    }

    private fun handleError(requestError: RequestError) {
        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }


    private fun showErrors() {
        if (inputTitle.text?.isEmpty() == true)
            inputTitle.error = getString(R.string.error_empty)
        if (inputContent.text?.isEmpty() == true)
            inputContent.error = getString(R.string.error_empty)
    }

    private fun isFormValid() =
        inputTitle.text?.isNotEmpty() ?: false &&
                inputContent.text?.isNotEmpty() ?: false

    interface CreateTopicInterationListener {
        fun onTopicCreated()
    }
}