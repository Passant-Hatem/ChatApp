package com.example.chatapp.screen.signup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUp : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
               SignUpScreen()
            }
        }
    }
}


@Composable
fun SignUpScreen(){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeTxt()
        Column(verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            SetEmail()
            SetUserName()
            SetPassword()
            PasswordConfirmation()
        }
        GoBttn()
    }
}

@Composable
fun WelcomeTxt(){
    Text(text = "Welcome!"
        , fontSize = MaterialTheme.typography.h3.fontSize)
}

@Composable
fun SetEmail(){
    val focusManager = LocalFocusManager.current
    var email by remember{ mutableStateOf(" ") }
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
}

@Composable
fun SetUserName(){
    val focusManager = LocalFocusManager.current
    var userName by remember{ mutableStateOf(" ") }
    OutlinedTextField(
        value = userName,
        onValueChange = { userName = it },
        label = { Text(text = "User Name")},
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Person,
                contentDescription = "User Name")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}

@Composable
fun SetPassword(){
    val focusManager = LocalFocusManager.current
    var password by remember{ mutableStateOf(" ") }
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(text = "Password")},
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Lock,
                contentDescription = "Password")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordConfirmation(){
    val keyboardController = LocalSoftwareKeyboardController.current
    var password by remember{ mutableStateOf(" ") }
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(text = "Password Confirm")},
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Lock,
                contentDescription = "Password Confirm")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        )
    )
}


@Composable
fun GoBttn(){
    Button(onClick = {
        //TODO your onclick code here
    }, shape = RoundedCornerShape(20)
        , modifier = Modifier
            .width(275.dp)
            .height(48.dp)
    ) {
        Text(text = "Go",
            fontSize = MaterialTheme.typography.h6.fontSize)
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    ChatAppTheme {
        SignUpScreen()
    }
}
