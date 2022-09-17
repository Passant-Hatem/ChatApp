package com.example.chatapp.screen.channels_list

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.screen.chat.Chat
import com.example.chatapp.screen.login.LogIn
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelList : ComponentActivity() {
    private val viewModel: ChannelListViewModel by viewModels()
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            subscribeToEvents()

            setContent {

             ChatTheme{
                 val scaffoldState = rememberScaffoldState()
                 val scope = rememberCoroutineScope()
                 Scaffold(
                     scaffoldState = scaffoldState,
                     drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                     drawerContent = {
                         viewModel.getCurrentUser()?.let { DrawerHeader(user = it) }
                         DrawerBody(
                             items = listOf(
                                 MenuItem(
                                     id = "settings",
                                     title = "Settings",
                                     contentDescription = "Go to settings screen",
                                     icon = Icons.Default.Settings
                                 ),
                                 MenuItem(
                                     id = "help",
                                     title = "Help",
                                     contentDescription = "Get help",
                                     icon = Icons.Default.Info
                                 ),
                                 MenuItem(
                                     id = "log_out",
                                     title = "Log out",
                                     contentDescription = "Logging out",
                                     icon = Icons.Default.ArrowBack
                                 ),
                             ),
                             onItemClick = {
                                 if(it.id == "log_out") {
                                     viewModel.logout()
                                     finish()
                                     startActivity(Intent(applicationContext, LogIn::class.java))
                                 }
                             }
                         )
                     }
                 ){
                         var showDialog: Boolean by remember {
                             mutableStateOf(false)
                         }

                         if(showDialog){
                             CreateChannelDialog(
                                 dismiss = { name ->
                                     viewModel.createChannel(name)
                                     showDialog = false
                                 }
                             )
                         }
                         ChannelsScreen(
                             filters = Filters.`in`(
                                 fieldName = "type",
                                 values = listOf(
                                     "gaming", "messaging", "commerce", "team" ,"livestream"
                                 )
                             ),
                             title = "Channels",
                             isShowingSearch = true,
                             onItemClick = {
                                 startActivity(Chat.getIntent(this, channelId = it.cid))
                             },
                             onBackPressed = {finish()},
                             onHeaderActionClick = {
                                 showDialog = true
                             },
                             onHeaderAvatarClick = {
                                 scope.launch {
                                     scaffoldState.drawerState.open()
                                 }
                             }
                         )
                     }
                 }
             }
        }

    @Composable
    private fun CreateChannelDialog(dismiss: (String) -> Unit) {

        var channelName by remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = { dismiss(channelName) },
            text = {
                OutlinedTextField(
                    value = channelName,
                    label = { Text(text = "Enter Channel Name")},
                    onValueChange = {channelName = it}
                )
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(top = 4.dp , start = 16.dp , end = 16.dp , bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { dismiss(channelName) }
                    ) {
                        Text(text = "Create Channel")
                    }
                }
            }
        )
    }

    private fun subscribeToEvents() {

        lifecycleScope.launchWhenStarted {

            viewModel.createChannelEvent.collect { event ->

                when (event) {

                    is ChannelListViewModel.CreateChannelEvents.Error -> {
                        val errorMessage = event.error
                        showToast(errorMessage)
                    }

                    is ChannelListViewModel.CreateChannelEvents.Success -> {
                        showToast("Channel Created!")
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
