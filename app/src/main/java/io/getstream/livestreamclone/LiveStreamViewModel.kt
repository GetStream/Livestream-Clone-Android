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
import timber.log.Timber

class LiveStreamViewModel : ViewModel() {
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
                channelController = chatClient.channel(CHANNEL_TYPE, CHANNEL_ID)
                requestChannel()
                watchChannel()
                subscribeToNewMessageEvent()
            }

            override fun onError(error: ChatError) {
                Timber.e(error)
            }
        })
    }

    fun sendButtonClicked(message: String) {
        Message().run {
            text = message
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
        chatClient.events().subscribe {
            if (it is NewMessageEvent) {
                _viewState.postValue(State.NewMessage(it.message))
            }
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
        private const val USER_ID = "user-id"
        private const val CHANNEL_TYPE = "livestream"
        private const val CHANNEL_ID = "livestream-clone-android-cid"
        private const val USER_TOKEN = BuildConfig.USER_TOKEN
        private val chatUser = User(id = USER_ID).apply {
            name = "Jack"
            image ="https://getstream.io/random_svg/?id=broken-waterfall-5&amp;name=$name"
        }
    }
}

sealed class State {
    data class Messages(val messages: List<Message>) : State()
    data class NewMessage(val message: Message) : State()
    data class Error(val message: String) : State()
}