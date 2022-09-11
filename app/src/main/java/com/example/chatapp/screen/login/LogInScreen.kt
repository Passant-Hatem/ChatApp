package com.example.chatapp.screen.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.R
import com.example.chatapp.screen.channels_list.ChannelList
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogIn : ComponentActivity() {
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        subscribeToEvents()

        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                LoginScreen(viewModel = viewModel ,this)
            }
        }
    }

    private fun subscribeToEvents() {

        lifecycleScope.launchWhenStarted {

            viewModel.logInEvent.collect { event ->

                when(event) {
                    is LoginViewModel.LogInEvent.ErrorInputTooShort -> {
                        showToast("Invalid! Enter more than 3 characters.")
                    }

                    is LoginViewModel.LogInEvent.ErrorLogIn -> {
                        val errorMessage = event.error
                        showToast("Error: $errorMessage")
                    }

                    is LoginViewModel.LogInEvent.Success -> {
                        showToast("Login Successful!")
                        startActivity(Intent(this@LogIn, ChannelList::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    getActivity: ComponentActivity
){
    var userName by remember{  mutableStateOf(TextFieldValue("")) }

    var showProgress: Boolean by remember {
        mutableStateOf(false)
    }

    viewModel.loadingState.observe(getActivity) { uiLoadingState ->
        showProgress = when (uiLoadingState) {
            is LoginViewModel.UiLoadingState.StartLoading -> {
                true
            }

            is LoginViewModel.UiLoadingState.LoadingFinished -> {
                false
            }
        }
    }

    //TODO TextField is hiding under the keyboard when focused
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        AppName()
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(text = "User Name")},
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Person,
                    contentDescription = "User Name")
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                viewModel.loginUser(userName.text,
                    getActivity.getString(R.string.jwt_token)
                )
                keyboardController?.hide()
            })
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 64.dp)
        ) {
            //log in as an user button
            Button(onClick = {
                viewModel.loginUser(userName.text,
                    getActivity.getString(R.string.jwt_token)
                )
            }, shape = RoundedCornerShape(20)
                , modifier = Modifier
                    .width(150.dp)
                    .height(48.dp)
            ) {
                Text(text = "User",
                    fontSize = MaterialTheme.typography.h6.fontSize)
            }
            //log in as a guest button
            Button(onClick = {
                viewModel.loginUser(userName.text)
            }, shape = RoundedCornerShape(20)
                , modifier = Modifier
                    .width(150.dp)
                    .height(48.dp)
            ) {
                Text(text = "Guest",
                    fontSize = MaterialTheme.typography.h6.fontSize)
            }
        }
        if (showProgress) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AppName(
) {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = MaterialTheme.typography.h3.fontSize
                )
            ){
                append("ChatApp")
            }
            withStyle(
                style = SpanStyle(
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    fontWeight = FontWeight.Normal,
                    baselineShift = BaselineShift.Subscript
                )
            ){
                append("welcome!")
            }
        },
        modifier = Modifier.padding(bottom = 56.dp)
    )
}
