package com.dicoding.picodiploma.loginwithanimation.view.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class SplashscreenActivity : AppCompatActivity() {
    private val apiService = ApiConfig().getApiService(null)
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, apiService)
    }

    private val SPLASH_DELAY = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            viewModel.getSession().observe(this) { user ->
                if (!user.isLogin) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }, SPLASH_DELAY)
    }
}