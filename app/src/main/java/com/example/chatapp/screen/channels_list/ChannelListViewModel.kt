package com.example.chatapp.screen.channels_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel(){

    private val _createChannelEvent = MutableSharedFlow<CreateChannelEvents>()
    val createChannelEvent = _createChannelEvent.asSharedFlow()

    fun createChannel(name: String, type: String = "messaging"){
        val trimmedName = name.trim()
        val channelID = UUID.randomUUID().toString()

        viewModelScope.launch {
            if (trimmedName.isEmpty()){
                _createChannelEvent.emit(
                    CreateChannelEvents.Error("The channel name can't be empty")
                )
                return@launch
            }

            client.createChannel(
                channelType = type,
                channelId = channelID,
                memberIds = emptyList(),
                extraData = mapOf(
                    "name" to name,
                )
            ).enqueue{ result ->

                if (result.isSuccess){
                    viewModelScope.launch {
                        _createChannelEvent.emit(
                            CreateChannelEvents.Success
                        )
                    }
                }else{
                    viewModelScope.launch {
                        _createChannelEvent.emit(
                            CreateChannelEvents.Error(result.error().message.toString())
                        )
                    }
                }
            }
        }
    }

    sealed class CreateChannelEvents{
        data class Error(val error: String) : CreateChannelEvents()
        object Success : CreateChannelEvents()
    }

    fun getCurrentUser(): User? {
        return client.getCurrentUser()
    }

    fun logout() {
        client.disconnect()
    }
}