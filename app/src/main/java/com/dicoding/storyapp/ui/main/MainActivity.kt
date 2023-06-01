package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryPagingAdapter
import com.dicoding.storyapp.data.remote.API.ApiConfig
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.ui.maps.MapsActivity
import com.dicoding.storyapp.ui.story.StoryActivity
import com.dicoding.storyapp.ui.story.StoryPagerViewModel
import com.dicoding.storyapp.ui.story.StoryViewModelFactory
import com.dicoding.storyapp.utils.preferences.UserPreference
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreference = UserPreference(this)
        val layoutManager = LinearLayoutManager(this)
        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, StoryActivity::class.java))
        }
        binding.apply {
            rvStory.layoutManager = layoutManager
        }
        setupAction()
    }

    private fun setupAction() {
        val mainViewModel = getViewModel()
        val adapter = StoryPagingAdapter()
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            adapter.refresh()
            Timer().schedule(1000) {
                binding.swipeRefresh.isRefreshing = false
                binding.rvStory.smoothScrollToPosition(0)
            }
        }
        binding.rvStory.apply {
            setHasFixedSize(true)
            binding.rvStory.adapter =
                adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
        }
        mainViewModel.story.observe(this, {
            adapter.submitData(lifecycle, it)
        })

    }

    fun getViewModel(): StoryPagerViewModel {
        val viewModel: StoryPagerViewModel by viewModels {
            StoryViewModelFactory(
                this,
                ApiConfig.getApiService(this)
            )
        }
        return viewModel
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
            R.id.explore_maps -> {
                startActivity(Intent(this,MapsActivity::class.java))
                return true
            }
            else -> return true
        }
    }

}

