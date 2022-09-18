package com.example.chatapp.screen.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {

}