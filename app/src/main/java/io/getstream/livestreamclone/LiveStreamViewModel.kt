package io.getstream.livestreamclone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.models.Message

class LiveStreamViewModel : ViewModel() {

    private val _viewState = MutableLiveData<State>()

    val viewState: LiveData<State> = _viewState

    fun sendButtonClicked(message: String) {
        TODO("Not implemented yet")
    }

    companion object {
        private fun getDummyAvatar(id: String) = "https://api.adorable.io/avatars/285/$id.png"
    }
}

sealed class State {
    data class Messages(val messages: List<Message>) : State()
    data class NewMessage(val message: Message) : State()
    data class Error(val message: String) : State()
}