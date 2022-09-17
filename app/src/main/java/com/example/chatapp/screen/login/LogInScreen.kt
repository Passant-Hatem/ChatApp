package com.example.chatapp.screen.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.R
import com.example.chatapp.screen.channels_list.ChannelList
import com.example.chatapp.screen.signup.SignUp
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
    var email by remember{  mutableStateOf(TextFieldValue("")) }
    var password by remember{ mutableStateOf("")}

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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        if (showProgress) CircularProgressIndicator()
        AppName()
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            //Getting email
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email")},
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email,
                        contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
            //Getting password
            val keyboardController = LocalSoftwareKeyboardController.current

            var isVisible by remember { mutableStateOf(false) }

            val icon = if(isVisible)
                painterResource(id = R.drawable.ic_baseline_visibility_24)
            else
                painterResource(id = R.drawable.ic_baseline_visibility_off_24)

            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = "password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        viewModel.loginUser(email.text,
                            getActivity.getString(R.string.jwt_token)
                        )
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        isVisible = !isVisible
                    }) {
                        Icon(painter = icon,
                            contentDescription = "view password")
                    }
                },
                visualTransformation = if(isVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
        }
        ForgetPassword()
        Column(horizontalAlignment = Alignment.CenterHorizontally
            , modifier = Modifier.padding(top = 64.dp)
            ,verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                 viewModel.loginUser(
                     email.text, getActivity.getString(R.string.jwt_token)
                 )
            },
                shape = RoundedCornerShape(20),
                modifier = Modifier
                    .width(275.dp)
                    .height(50.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_login_24),
                    contentDescription ="Log in button icon",
                    modifier = Modifier.size(32.dp))

                Text(
                    text = "Log in",
                    modifier = Modifier.padding(start = 20.dp),
                    fontSize = MaterialTheme.typography.h6.fontSize
                )
            }
            Button(onClick = {
                getActivity.startActivity(Intent(getActivity, SignUp::class.java))
            },
                shape = RoundedCornerShape(20)
                , modifier = Modifier
                    .width(275.dp)
                    .height(48.dp)
            ) {
                Text(text = "Sign Up",
                    fontSize = MaterialTheme.typography.h6.fontSize)
            }
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
        modifier = Modifier.padding(bottom = 56.dp, top = 32.dp)
    )
}

@Composable
fun ForgetPassword(){
    val text = "Forgot password?"
    ClickableText(
        text = AnnotatedString(text),
        modifier = Modifier.padding(start = 167.dp),
        style = TextStyle(Color.Blue , textDecoration = TextDecoration.Underline),
        onClick = {
            Log.e("log in" ,"forgot pass")
        })
}
