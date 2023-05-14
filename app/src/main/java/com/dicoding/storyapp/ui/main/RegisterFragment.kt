package com.dicoding.storyapp.ui.main

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
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.request.RegisterRequest
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

        return binding.root
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]
    }

    private fun setupAction() {
        edRegisterPasswordBehavior()

        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            toggleLoginPasswordVisibility(isChecked)
        }

        binding.tvSignIn.setOnClickListener { moveToLoginFragment() }

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
                    viewModel.register(RegisterRequest(name, email, password)).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(context, "Create an account success", Toast.LENGTH_SHORT).show()
                                    moveToLoginFragment()
                                }
                                is Error -> {
                                    Toast.makeText(context, "Create an account failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun edRegisterPasswordBehavior() {
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