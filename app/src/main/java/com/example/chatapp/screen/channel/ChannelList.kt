package com.example.chatapp.screen.channel

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@AndroidEntryPoint
class ChannelList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             ChatTheme{
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
                         //TODO
                         showToast("Item Clicked")
                     },
                     onBackPressed = {finish()},
                     onHeaderActionClick = {
                         //TODO
                         showToast("Create new Channel")
                     },
                     onHeaderAvatarClick = {
                         //TODO
                         showToast("Avatar Clicked")
                     }
                 )
             }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
