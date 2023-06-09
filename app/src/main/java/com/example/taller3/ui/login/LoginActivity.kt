package com.example.taller3.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.example.taller3.MenuActivity
import com.example.taller3.databinding.ActivityLoginBinding
import com.example.taller3.R
import com.example.taller3.RegisterActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
         binding = ActivityLoginBinding.inflate(layoutInflater)
         setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        // Step 1: Check if the user is already logged in
        if (auth.currentUser != null) {
            val loggedInUser = auth.currentUser?.email?.let { LoggedInUserView(it) }
            if (loggedInUser != null) {
                updateUiWithUser(loggedInUser)
                startActivity(Intent(baseContext, MenuActivity::class.java))
                finish() // Optional: Prevent the user from going back to the login screen
            }
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
               password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }


        }
        /*login.setOnClickListener {
            loading.visibility = View.VISIBLE
            val usernameString = username.text.toString()
            val passwordString = password.text.toString()
            // Replace the Firebase authentication code with your custom login implementation
            // For testing purposes, you can simply compare the entered username and password with a hardcoded value
            if (usernameString == "test" && passwordString == "password") {
                val loggedInUser = LoggedInUserView(usernameString)
                updateUiWithUser(loggedInUser)
                startActivity(Intent(baseContext, MenuActivity::class.java))
            } else {
                showLoginFailed(R.string.login_failed)
            }
            loading.visibility = View.GONE
        }*/


        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            val usernameString = username.text.toString()
            val passwordString = password.text.toString()
            auth.signInWithEmailAndPassword(usernameString, passwordString).addOnCompleteListener(this)
            { task -> loading.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val loggedInUser = user.email?.let { it1 -> LoggedInUserView(it1) }
                        if (loggedInUser != null) {
                            updateUiWithUser(loggedInUser)
                            startActivity(Intent(baseContext,MenuActivity::class.java))
                        }
                    }
                } else {
                    showLoginFailed(R.string.login_failed)
                }
            }
        }

        binding.Registrarse?.setOnClickListener {
            startActivity(Intent(baseContext,
                RegisterActivity::class.java))
            Toast.makeText(this, "Bienvenido, porfavor ingresar la siguiente informacion", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }

}

private fun <TResult> Task<TResult>.addOnCompleteListener(editText: EditText, any: Any) {

}


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}