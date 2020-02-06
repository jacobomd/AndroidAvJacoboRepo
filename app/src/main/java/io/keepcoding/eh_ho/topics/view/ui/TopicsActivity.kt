package io.keepcoding.eh_ho.topics.view.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import io.keepcoding.eh_ho.*
import io.keepcoding.eh_ho.data.LatestNews
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.UserRepo
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
            }
        })
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
        /*supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                CreateTopicFragment()
            )
            .addToBackStack(TRANSACTION_CREATE_TOPIC)
            .commit()*/
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
        //goToPosts(topic)
        topicViewModel.onTopicSelected(topic)
    }

    override fun onRetryButtonClicked() {
        topicViewModel.onRetryButtonClicked()
    }

    override fun onTopicsFragmentResumed(context: Context?) {
        topicViewModel.onTopicsFragmentResumed(context = context)
    }


    override fun onSwipeRefreshLayoutClicked() {
        topicViewModel.onSwipeRefreshLayoutClicked()
    }

    override fun onTopicCreated() {
        topicViewModel.onTopicCreated()
        //supportFragmentManager.popBackStack()
    }

    override fun onLogOutOptionClicked() {
        topicViewModel.onLogOutOptionClicked()
        /*UserRepo.logOut(this)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()*/
    }

    override fun onCreateTopicOptionClicked() {
        topicViewModel.onCreateTopicOptionClicked()
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

    private fun goToPosts(topic: Topic) {
        val intent = Intent(this, PostsActivity::class.java)

        intent.putExtra(EXTRA_TOPIC_ID, topic.id)
        intent.putExtra(EXTRA_TOPIC_TITLE, topic.title)

        startActivity(intent)
        finish()
    }


}