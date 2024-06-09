package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addstory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val apiService = ApiConfig().getApiService(null)
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, apiService)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStory.layoutManager = LinearLayoutManager(this@MainActivity)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                getData(user.token)
            }
        }

        setupView()
        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                showLogoutConfirmationDialog()
                return true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.menu_map -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(getString(R.string.logout))
            setMessage(getString(R.string.askForLogout))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.logout()
            }
            setNegativeButton(getString(R.string.nope)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun getData(token: String) {
        showLoading(true)
        try {
            if (isNetworkOn(this@MainActivity)) {
                val adapter = StoryListAdapter()
                binding.rvStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                viewModel.story(token).observe(this) { pagingData ->
                    adapter.submitData(lifecycle, pagingData)
                }
                showLoading(false)
            } else {
                showToast(getString(R.string.networkOff))
            }
        } catch (e: Exception) {
            showToast("$e")
        }

    }

    private fun isNetworkOn(context: Context) : Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        return activeNetworkInfo?.isConnected == true
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressLinear.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

}