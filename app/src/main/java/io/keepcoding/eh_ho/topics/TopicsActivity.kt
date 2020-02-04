package io.keepcoding.eh_ho.topics

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import io.keepcoding.eh_ho.*
import io.keepcoding.eh_ho.data.LatestNews
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.UserRepo
import io.keepcoding.eh_ho.latest_news.LatestNewsFragment
import io.keepcoding.eh_ho.login.LoginActivity
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_ID
import io.keepcoding.eh_ho.posts.EXTRA_TOPIC_TITLE
import io.keepcoding.eh_ho.posts.PostsActivity
import kotlinx.android.synthetic.main.content_topic.*


const val TRANSACTION_CREATE_TOPIC = "create_topic"


class TopicsActivity : AppCompatActivity(),
    TopicsFragment.TopicsInteractionListener,
    CreateTopicFragment.CreateTopicInterationListener,
NavigationView.OnNavigationItemSelectedListener,
LatestNewsFragment.LatestNewsInteractionListener{


    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TopicsFragment())
                .commit()
        }

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

   private fun goToPosts (topic: Topic) {
        val intent = Intent(this, PostsActivity::class.java)

        intent.putExtra(EXTRA_TOPIC_ID, topic.id)
        intent.putExtra(EXTRA_TOPIC_TITLE, topic.title)

        startActivity(intent)
       finish()
    }

    override fun onTopicSelected(topic: Topic) {
        goToPosts(topic)
    }

    override fun onGoToCreateTopic() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CreateTopicFragment())
            .addToBackStack(TRANSACTION_CREATE_TOPIC)
            .commit()
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

    override fun onTopicCreated() {
        supportFragmentManager.popBackStack()
    }

    override fun onLogOut() {
        UserRepo.logOut(this)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_topics -> {
                titleActionBar.text = "Topics"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, TopicsFragment())
                    .commit()
            }
            R.id.nav_latest_news -> {
                titleActionBar.text = "Latest News"
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, LatestNewsFragment())
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

}