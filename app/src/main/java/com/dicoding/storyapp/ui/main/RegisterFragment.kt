package com.dicoding.storyapp.ui.main

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.source.remote.request.RegisterRequest
import com.dicoding.storyapp.data.repository.Result.Loading
import com.dicoding.storyapp.data.repository.Result.Success
import com.dicoding.storyapp.data.repository.Result.Error
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.insert.InsertActivity
import com.dicoding.storyapp.ui.setting.SettingActivity

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        setupViewModel()
        setupAction()
        playAnimation()

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]
    }

    private fun setupAction() {
        binding.apply {
            edRegisterPassword.apply {
                setOnEditorActionListener { _, actionId, _ ->
                    clearFocusOnDoneAction(actionId)
                }
            }

            cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                toggleLoginPasswordVisibility(isChecked)
            }

            tvSignIn.setOnClickListener { moveToLoginFragment() }

            btnSignUp.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                when {
                    name.isEmpty() -> {
                        edRegisterName.error = "Masukkan name"
                    }
                    email.isEmpty() -> {
                        edRegisterEmail.error = "Masukkan email"
                    }
                    password.isEmpty() -> {
                        edRegisterPassword.error = "Masukkan password"
                    }
                    password.length < 8 -> {
                        edRegisterPassword.error = "Password must be at least 8 character"
                    }
                    else -> {
                        executeRegister(name, email, password)
                    }
                }
            }
        }
    }

    private fun executeRegister(name: String, email: String, password: String) {
        viewModel.register(RegisterRequest(name, email, password)).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSignUp.isEnabled = false
                    }
                    is Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSignUp.isEnabled = true
                        Toast.makeText(context, "Create an account success", Toast.LENGTH_SHORT).show()
                        moveToLoginFragment()
                    }
                    is Error -> {
                        binding.btnSignUp.isEnabled = true
                        Toast.makeText(context, "Create an account failed", Toast.LENGTH_SHORT).show()
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
            binding.edRegisterPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.edRegisterPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        binding.edRegisterPassword.setSelection(selection)
        binding.edRegisterPassword.error = null
    }

    private fun moveToLoginFragment() {
        val fragmentManager = parentFragmentManager
        fragmentManager.popBackStack()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivAccount, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
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
            R.id.menu_setting -> {
                startActivity(Intent(context, SettingActivity::class.java))
                true
            }
            else -> true
        }
    }
}