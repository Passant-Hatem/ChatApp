package com.example.chatapp.screen.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) :ViewModel() {

    private val _loginEvent = MutableSharedFlow<LogInEvent>()
    val logInEvent = _loginEvent.asSharedFlow()

    private val _loadingState = MutableLiveData<UiLoadingState>()
    val loadingState : LiveData<UiLoadingState>
        get() = _loadingState

    fun loginUser(username: String, token: String? = null) {
        val trimmedUsername = username.trim()
        viewModelScope.launch {
            if (isValidUsername(trimmedUsername) && token != null) {
                loginRegisteredUser(trimmedUsername, token)
            } else if (isValidUsername(trimmedUsername) && token == null) {
                loginGuestUser(trimmedUsername)
            } else {
                _loginEvent.emit(LogInEvent.ErrorInputTooShort)
            }
        }
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length > Constants.MIN_USERNAME_LENGTH
    }

    private fun loginRegisteredUser(userName: String, token: String) {
        val user = User(id = userName ,name = userName)
        _loadingState.value = UiLoadingState.StartLoading
        client.connectUser(
            user = user,
            token = token
        ).enqueue{ result->

            _loadingState.value = UiLoadingState.LoadingFinished

            if (result.isSuccess){
                viewModelScope.launch {
                    _loginEvent.emit(LogInEvent.Success)
                }
            }else{
                viewModelScope.launch {
                    _loginEvent.emit(LogInEvent.ErrorLogIn(
                        result.error().message ?: "Unknown error"
                    ))
                }
            }
        }
    }

    private fun loginGuestUser(userName: String) {

        _loadingState.value = UiLoadingState.StartLoading

        client.connectGuestUser(
            userId = userName,
            username = userName
        ).enqueue{result->

            _loadingState.value = UiLoadingState.LoadingFinished

            if (result.isSuccess){
                viewModelScope.launch {
                    _loginEvent.emit(LogInEvent.Success)
                }
            }else{
                viewModelScope.launch {
                    _loginEvent.emit(LogInEvent.ErrorLogIn(
                        result.error().message ?: "Unknown error"
                    ))
                }
            }
        }
    }

    sealed class LogInEvent{
        object ErrorInputTooShort: LogInEvent()
        data class ErrorLogIn(val error: String): LogInEvent()
        object Success: LogInEvent()
    }

    sealed class UiLoadingState {
        object StartLoading : UiLoadingState()
        object LoadingFinished : UiLoadingState()
    }
}