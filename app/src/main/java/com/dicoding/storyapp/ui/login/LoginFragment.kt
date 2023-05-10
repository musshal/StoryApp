package com.dicoding.storyapp.ui.login

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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.preferences.UserPreference
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.home.HomeFragment
import com.dicoding.storyapp.ui.insert.InsertActivity
import com.dicoding.storyapp.ui.register.RegisterFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preference"
)

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

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

        setupViewModel()
        setupAction()
        initObserver()

        return binding.root
    }

    private fun initObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isSuccess.observe(viewLifecycleOwner) {
            showSuccessMessage(it)
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            showErrorMessage(it)
        }
    }

    private fun showErrorMessage(it: Boolean?) {
        if (it == true) {
            Toast.makeText(context, "Failed to sign in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuccessMessage(it: Boolean?) {
        if (it == true) {
            Toast.makeText(context, "Sign in success", Toast.LENGTH_SHORT).show()

            replaceToHomeFragment()
        }
    }

    private fun showLoading(it: Boolean?) {
        binding.progressBar.visibility = if (it == true) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val viewModelFactory = ViewModelFactory(userPreference)

        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
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
                    binding.edLoginEmail.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Masukkan password"
                }
                else -> {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun clearFocusOnDoneAction(actionId: Int) : Boolean {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
            binding.edLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.edLoginPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        binding.edLoginPassword.setSelection(selection)
    }

    private fun replaceToHomeFragment() {
        val homeFragment = HomeFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, homeFragment, HomeFragment::class.java.simpleName)
            commit()
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
            else -> true
        }
    }
}