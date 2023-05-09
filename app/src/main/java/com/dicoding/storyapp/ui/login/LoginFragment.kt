package com.dicoding.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.ui.insert.InsertActivity
import com.dicoding.storyapp.ui.register.RegisterFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.tvSignUp.setOnClickListener { moveToRegisterFragment() }

        setupAction()

        return binding.root
    }

    private fun setupAction() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Masukkan password"
                }
                else -> {
                    val client = ApiConfig.getApiService().login(
                        LoginRequest(email, password)
                    )
                    client.enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()

                                if (responseBody != null) {
                                    Toast.makeText(
                                        context,
                                        responseBody.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    private fun moveToRegisterFragment() {
        val registerFragment = RegisterFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, registerFragment, RegisterFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu_1, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_insert -> {
                startActivity(Intent(context, InsertActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                val registerFragment = RegisterFragment()
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().apply {
                    replace(R.id.frame_container, registerFragment, RegisterFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
                true
            }
            else -> true
        }
    }
}