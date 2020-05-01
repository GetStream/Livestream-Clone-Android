package io.getstream.livestreamclone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.controllers.ChannelController
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.client.utils.SyncStatus
import timber.log.Timber
import java.util.*

class LivestreamViewModel : ViewModel() {
    private val chatClient = ChatClient.instance()
    private val _viewState = MutableLiveData<State>()
    private lateinit var channelController: ChannelController
    private lateinit var channel: Channel
    private lateinit var user: User
    private lateinit var connectionId: String

    val viewState: LiveData<State> = _viewState

    init {
        chatClient.setUser(chatUser, USER_TOKEN, object : InitConnectionListener() {
            override fun onSuccess(data: ConnectionData) {
                user = data.user
                connectionId = data.connectionId
                Timber.d("client.setUser success, user: $user, connectionId: $connectionId")
                channelController = chatClient.channel(CHANNEL_TYPE, CHANNEL_ID)
                requestChannel()
                watchChannel()
                subscribeToNewMessageEvent()
                subscribeToChanelStateEvents()
            }

            override fun onError(error: ChatError) {
                Timber.e(error)
            }
        })
    }

    fun sendButtonClicked(message: String) {
        Message().run {
            text = message
            id = user.id + "-" + UUID.randomUUID().toString()
//            channel = this@LivestreamViewModel.channel
            cid = "%s:%s".format(CHANNEL_TYPE, channel.id)
            createdAt = Date()
            syncStatus = SyncStatus.SYNC_NEEDED
            channelController.sendMessage(this).enqueue {
                if (it.isSuccess) {
                    Timber.d("Received message send success")
                } else {
                    Timber.e(it.error())
                }
            }
        }
    }

    private fun subscribeToNewMessageEvent() {
        chatClient.events().subscribe { it ->
            if (it is NewMessageEvent) {
                Timber.d("Received new message event")
            }
        }
    }

    private fun subscribeToChanelStateEvents() {
        channelController.events().filter("channel.state").subscribe {
            Timber.d("Received channel.state event")
        }
    }

    private fun requestChannel() {
        val channelData = mapOf<String, Any>("name" to "Live stream chat")
        val request = QueryChannelRequest()
            .withData(channelData)
            .withMessages(20)
            .withWatch()

        channelController.query(request).enqueue {
            if (it.isSuccess) {
                channel = it.data()
                Timber.d("Received channel response")
                _viewState.postValue(State.Messages(it.data().messages))
            } else {
                Timber.e(it.error())
            }
        }
    }

    private fun watchChannel() {
        channelController.watch().enqueue() {
            Timber.d("Received channel watch result")
        }
    }

    companion object {
        private const val CHANNEL_TYPE = "messaging"//"livestream"
        private const val USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiZW1wdHktcXVlZW4tNSJ9.RJw-XeaPnUBKbbh71rV1bYAKXp6YaPARh68O08oRnOU"
        private const val CHANNEL_ID = "livestream-clone-cid-12345"
        private val chatUser = User("user-id")
    }
}

sealed class State {
    data class Messages(val messages: List<Message>) : State()
    data class Error(val message: String) : State()
}