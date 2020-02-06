package io.keepcoding.eh_ho.topics.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.*
import io.keepcoding.eh_ho.data.*
import io.keepcoding.eh_ho.latest_news.LATEST_NEWS_FRAGMENT_TAG
import io.keepcoding.eh_ho.latest_news.LatestNewsFragment
import io.keepcoding.eh_ho.login.LoginActivity
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_ID
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_TITLE
import io.keepcoding.eh_ho.posts.PostsActivity
import io.keepcoding.eh_ho.topics.view.state.TopicManagementState
import io.keepcoding.eh_ho.topics.viewmodel.TopicViewModel
import kotlinx.android.synthetic.main.content_topic.*


const val TRANSACTION_CREATE_TOPIC = "create_topic"


class TopicsActivity : AppCompatActivity(),
    TopicsFragment.TopicsInteractionListener,
    CreateTopicFragment.CreateTopicInteractionListener,
    NavigationView.OnNavigationItemSelectedListener,
    LatestNewsFragment.LatestNewsInteractionListener {

    private val topicViewModel: TopicViewModel by lazy { TopicViewModel() }

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        initModel()
        initToolbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TopicsFragment(), TOPICS_FRAGMENT_TAG)
                .commit()
        }

        topicViewModel.onViewCreatedWithNoSavedData(this)

    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        titleActionBar.text = title
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    private fun initModel() {

        topicViewModel.topicManagementState.observe(this, Observer { state ->
            when (state) {
                TopicManagementState.Loading -> enableLoadingView()
                is TopicManagementState.LoadTopicList -> loadTopicList(list = state.topicList)
                is TopicManagementState.RequestErrorReported -> showRequestError(error = state.requestError)
                TopicManagementState.NavigateToCreateTopic -> navigateToCreateTopic()
                is TopicManagementState.NavigateToPostsOfTopic -> navigateToPostsOfTopic(topic = state.topic)
                TopicManagementState.NavigateToLoginAndExit -> navigateToLoginAndExit()
                is TopicManagementState.TopicCreatedSuccessfully -> showMessage(msg = state.msg)
                is TopicManagementState.TopicNotCreated -> showError(msg = state.createError)
                is TopicManagementState.CreateTopicFormErrorReported -> showError(msg = state.errorMsg)
                TopicManagementState.CreateTopicLoading -> toggleCreateTopicLoadingView(enable = true)
                TopicManagementState.CreateTopicCompleted -> {
                    toggleCreateTopicLoadingView(enable = false)
                    dismissCreateDialogFragment()
                }

            }
        })
    }

    private fun dismissCreateDialogFragment() {
        if (getCreateTopicFragmentIfAvailableOrNull() != null) {
            supportFragmentManager.popBackStack()
        }
    }


    private fun navigateToLoginAndExit() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToCreateTopic() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CreateTopicFragment(), CREATE_TOPIC_FRAGMENT_TAG)
            .addToBackStack(TRANSACTION_CREATE_TOPIC)
            .commit()
    }


    private fun enableLoadingView() {
        getTopicsFragmentIfAvailableOrNull()?.enableLoading(enabled = true)
    }

    private fun showRequestError(error: RequestError) {
        getTopicsFragmentIfAvailableOrNull()?.run {
            enableLoading(enabled = false)
            handleRequestError(requestError = error)
        }
    }

    private fun loadTopicList(list: List<Topic>) {
        getTopicsFragmentIfAvailableOrNull()?.run {
            enableLoading(enabled = false)
            loadTopicList(topicList = list)
        }
    }


    override fun onCreateTopicButtonClicked() {
        topicViewModel.onCreateTopicButtonClicked()
    }

    override fun onLatestNewSelected(latestNews: LatestNews) {
        goToTopicDetail(latestNews)
    }

    private fun goToTopicDetail(latestNews: LatestNews) {
        val intent = Intent(this, PostsActivity::class.java)

        intent.putExtra(EXTRA_TOPIC_ID, latestNews.topic_id.toString())
        intent.putExtra(EXTRA_TOPIC_TITLE, latestNews.topic_title)

        startActivity(intent)
        finish()
    }

    override fun onTopicSelected(topic: Topic) {
        topicViewModel.onTopicSelected(topic = topic)
    }

    override fun onRetryButtonClicked() {
        topicViewModel.onRetryButtonClicked(context = this)
    }

    override fun onTopicsFragmentResumed() {
        topicViewModel.onTopicsFragmentResumed(context = this)
    }


    override fun onSwipeRefreshLayoutClicked() {
        topicViewModel.onSwipeRefreshLayoutClicked(context = this)
    }


    override fun onLogOutOptionClicked() {
        topicViewModel.onLogOutOptionClicked(context = this)
    }

    override fun onCreateTopicOptionClicked(model: CreateTopicModel) {
        topicViewModel.onCreateTopicOptionClicked(context = this, createTopicModel = model)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_topics -> {
                titleActionBar.text = "Topics"
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragmentContainer,
                        TopicsFragment(),
                        TOPICS_FRAGMENT_TAG
                    )
                    .commit()
            }
            R.id.nav_latest_news -> {
                titleActionBar.text = "Latest News"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, LatestNewsFragment(), LATEST_NEWS_FRAGMENT_TAG)
                    .commit()
            }
            R.id.nav_logout -> {
                UserRepo.logOut(this)

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getTopicsFragmentIfAvailableOrNull(): TopicsFragment? {
        val fragment: Fragment? =
            supportFragmentManager.findFragmentByTag(TOPICS_FRAGMENT_TAG)

        return if (fragment != null && fragment.isVisible) {
            fragment as TopicsFragment
        } else {
            null
        }
    }

    private fun getCreateTopicFragmentIfAvailableOrNull(): CreateTopicFragment? {
        val fragment: Fragment? =
            supportFragmentManager.findFragmentByTag(CREATE_TOPIC_FRAGMENT_TAG)

        return if (fragment != null && fragment.isVisible) {
            fragment as CreateTopicFragment
        } else {
            null
        }
    }


    private fun showError(msg: String) {
        Snackbar.make(fragmentContainer, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun toggleCreateTopicLoadingView(enable: Boolean) {
        getCreateTopicFragmentIfAvailableOrNull()?.enableLoadingDialog(enable = enable)
    }


    private fun navigateToPostsOfTopic(topic: Topic) {
        val intent = Intent(this, PostsActivity::class.java)

        intent.putExtra(EXTRA_TOPIC_ID, topic.id)
        intent.putExtra(EXTRA_TOPIC_TITLE, topic.title)

        startActivity(intent)
        finish()
    }


}