package com.dicoding.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.ui.signup.SignUpActivity
import com.dicoding.storyapp.utils.preferences.UserPreference

class LoginActivity : AppCompatActivity() {
    private lateinit var userPreference: UserPreference
    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel(email: String, password: String) {
        userPreference = UserPreference(this@LoginActivity)
        loginViewModel.apply {
            login(this@LoginActivity, email, password)
            isLoading.observe(this@LoginActivity, {
                showLoading(it)
            })
            isLogin.observe(this@LoginActivity, { state ->
                if (state == true) {
                    user.observe(this@LoginActivity, { data ->
                        if (!data.error) {
                            with(data) {
                                userPreference.saveUser(
                                    loginResult.name,
                                    loginResult.token,
                                    loginResult.userId
                                )
                            }
                        }
                    })
                    showDialog(resources.getString(R.string.greeting, userPreference.getName()),
                        resources.getString(R.string.login_success),
                        "lanjut",state)
                } else{
                    showDialog(resources.getString(R.string.login_failed),
                        resources.getString(R.string.email_password_invalid),
                        "ulangi",state)
                }

            })
        }
    }

    private fun setupAction() {
        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (!email.isEmpty() && !password.isEmpty()) {
                setupViewModel(email, password)
            } else if (email.isEmpty()) binding.emailEditTextLayout.error =
                resources.getString(R.string.column_isEmpty_message)
            else binding.passwordEditTextLayout.error =
                resources.getString(R.string.column_isEmpty_message)
        }
    }

    private fun playAnimation() {
        binding.apply {
            val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(300)
            val message = ObjectAnimator.ofFloat(messageTextView, View.ALPHA, 1f).setDuration(300)
            val emailTextView =
                ObjectAnimator.ofFloat(emailTextView, View.ALPHA, 1f).setDuration(300)
            val emailEditTextLayout =
                ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
            val passwordTextView =
                ObjectAnimator.ofFloat(passwordTextView, View.ALPHA, 1f).setDuration(300)
            val passwordEditTextLayout =
                ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
            val login = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(300)
            val signUpQuestion =
                ObjectAnimator.ofFloat(signUpQuestion, View.ALPHA, 1f).setDuration(300)
            val signUpButton = ObjectAnimator.ofFloat(signUpButton, View.ALPHA, 1f).setDuration(300)
            AnimatorSet().apply {
                playSequentially(title,
                    message,
                    emailTextView,
                    emailEditTextLayout,
                    passwordTextView,
                    passwordEditTextLayout,
                    login,
                    signUpQuestion,
                    signUpButton)
                startDelay = 200
            }.start()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showDialog(title: String, message: String, instruction: String, state: Boolean) {
        AlertDialog.Builder(this).apply {
            AlertDialog.Builder(this@LoginActivity).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton(instruction) { _, _ ->
                    if (state) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                }
                create()
                show()
            }

        }

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
        supportActionBar?.hide()
    }
}
