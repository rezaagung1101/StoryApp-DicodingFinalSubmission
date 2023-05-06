package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryAdapter
import com.dicoding.storyapp.adapter.StoryPagingAdapter
import com.dicoding.storyapp.data.lib.story.Story
import com.dicoding.storyapp.data.remote.API.ApiConfig
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.ui.story.StoryActivity
import com.dicoding.storyapp.ui.story.StoryPagerViewModel
import com.dicoding.storyapp.ui.story.ViewModelStoryFactory
import com.dicoding.storyapp.ui.welcome.WelcomeActivity
import com.dicoding.storyapp.utils.preferences.UserPreference

class MainActivity : AppCompatActivity() {
//    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this)
        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, StoryActivity::class.java))
        }
        binding.apply {
            rvStory.layoutManager = layoutManager
            rvStory.addItemDecoration(itemDecoration)
        }
//        mainViewModel.getStoryList(this)
        setupAction()
    }

    private fun setStoryData(storyData: ArrayList<Story>) {
        val listStory = ArrayList<Story>()
        for (data in storyData) {
            listStory.add(data)
        }
        val adapter = StoryAdapter(listStory)
        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                binding.apply {
                    val intentToDetail = Intent(this@MainActivity, DetailActivity::class.java)
//                    intentToDetail.putExtra("STORY", data)
                    startActivity(intentToDetail)
                }
            }
        })
        adapter.notifyDataSetChanged()
        binding.rvStory.adapter = adapter
    }

    private fun setupAction() {
        val mainViewModel = getStoryViewModel()
        val adapter = StoryPagingAdapter()
        adapter.notifyDataSetChanged()
//        binding.rvStory.adapter = adapter
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.story.observe(this) {
            adapter.submitData(
                lifecycle,
                it
            )
        }
//        mainViewModel.apply {
//            isLoading.observe(this@MainActivity, {
//                showLoading(it)
//            })
//
//            listStory.observe(this@MainActivity, { listStory ->
//                setStoryData(listStory)
//            })
//        }

    }

    fun getStoryViewModel(): StoryPagerViewModel {
        val viewModel: StoryPagerViewModel by viewModels {
            ViewModelStoryFactory(
                this,
                ApiConfig.getApiService(this),
                userPreference.getToken() ?: ""
            )
        }
        return viewModel
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                userPreference.logout()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
            R.id.menu_upload -> {
                startActivity(Intent(this@MainActivity, StoryActivity::class.java))
                return true
            }
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            else -> return true
        }
    }

}

