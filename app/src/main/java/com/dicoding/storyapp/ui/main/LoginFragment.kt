package com.dicoding.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.entity.UserEntity
import com.dicoding.storyapp.data.remote.request.LoginRequest
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.insert.InsertActivity

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: MainViewModel
    private var backPressedTime: Long = 0
    private val BACK_PRESSED_INTERVAL = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupViewModel()
        setupAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + BACK_PRESSED_INTERVAL > System.currentTimeMillis()) {
                    // If back button is clicked twice within the time interval, close the app or fragment
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]
    }

    private fun setupAction() {
        binding.edLoginPassword.setOnEditorActionListener { _, actionId, _ ->
            clearFocusOnDoneAction(actionId)
        }

        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            toggleLoginPasswordVisibility(isChecked)
        }

        binding.tvSignUp.setOnClickListener { moveToRegisterFragment() }

        binding.btnSignIn.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Fill the email"
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Fill the password"
                }
                else -> {
                    viewModel.login(LoginRequest(email, password)).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(context, "Sign in success", Toast.LENGTH_SHORT).show()

                                    val userEntity = UserEntity(
                                        result.data.loginResult.userId,
                                        result.data.loginResult.name,
                                        result.data.loginResult.token
                                    )

                                    viewModel.setLogin(userEntity)
                                }
                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun clearFocusOnDoneAction(actionId: Int) : Boolean {
        val imm = requireContext().getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            binding.edLoginPassword.clearFocus()
            imm.hideSoftInputFromWindow(binding.edLoginPassword.windowToken, 0)
            return true
        }

        return false
    }

    private fun toggleLoginPasswordVisibility(isChecked: Boolean) {
        val selection = binding.edLoginPassword.selectionEnd

        if (isChecked) {
            binding.edLoginPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.edLoginPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        binding.edLoginPassword.setSelection(selection)
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
            else -> true
        }
    }
}