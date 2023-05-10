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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.preferences.UserPreference
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.ui.insert.InsertActivity
import com.dicoding.storyapp.ui.login.LoginFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        edRegisterPasswordBehavior()

        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            toggleLoginPasswordVisibility(isChecked)
        }

        binding.tvSignIn.setOnClickListener { moveToLoginFragment() }

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
            Toast.makeText(context, "Failed to create an account", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuccessMessage(it: Boolean?) {
        if (it == true) {
            Toast.makeText(context, "Success create an account", Toast.LENGTH_SHORT).show()

            replaceToLoginFragment()
        }
    }

    private fun showLoading(it: Boolean?) {
        binding.progressBar.visibility = if (it == true) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val viewModelFactory = ViewModelFactory(userPreference)

        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]
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
                    viewModel.register(name, email, password)
                }
            }
        }
    }

    private fun replaceToLoginFragment() {
        val loginFragment = LoginFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
            commit()
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