package com.dicoding.storyapp.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.request.RegisterRequest
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.ui.insert.InsertActivity
import com.dicoding.storyapp.ui.login.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.edRegisterPassword.apply {
            setOnEditorActionListener { _, actionId, _ ->
                clearFocusOnDoneAction(actionId)
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    error = if (s!!.length < 8) {
                        "Password must be at least 8 character"
                    } else {
                        null
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            toggleLoginPasswordVisibility(isChecked)
        }

        binding.tvSignIn.setOnClickListener { moveToLoginFragment() }

        setupAction()

        return binding.root
    }

    private fun clearFocusOnDoneAction(actionId: Int) : Boolean {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            binding.edRegisterPassword.clearFocus()
            binding.edRegisterPassword.error = null
            imm.hideSoftInputFromWindow(binding.edRegisterPassword.windowToken, 0)
            return true
        }

        return false
    }

    private fun toggleLoginPasswordVisibility(isChecked: Boolean) {
        val selection = binding.edRegisterPassword.selectionEnd

        if (isChecked) {
            binding.edRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.edRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        binding.edRegisterPassword.setSelection(selection)
    }

    private fun setupAction() {
        binding.btnSignUp.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = "Masukkan name"
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = "Masukkan password"
                }
                password.length < 8 -> {
                    binding.edRegisterPassword.error = "Password must be at least 8 character"
                }
                else -> {
                    val client = ApiConfig.getApiService().register(
                        RegisterRequest(name, email, password)
                    )
                    client.enqueue(object : Callback<MessageResponse> {
                        override fun onResponse(
                            call: Call<MessageResponse>,
                            response: Response<MessageResponse>
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

                        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    private fun moveToLoginFragment() {
        val loginFragment = LoginFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
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

            else -> true
        }
    }
}