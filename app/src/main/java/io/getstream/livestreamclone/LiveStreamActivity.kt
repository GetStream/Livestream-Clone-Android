package io.getstream.livestreamclone

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import io.getstream.chat.android.client.models.Message
import kotlinx.android.synthetic.main.activity_main.*

class LiveStreamActivity : AppCompatActivity(R.layout.activity_main) {
    private val adapter = MessagesListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadMockVideoStream()

        messagesList.adapter = adapter

        val viewModel: LiveStreamViewModel by viewModels()
        viewModel.viewState.observe(this, Observer {
            when (it) {
                is State.Messages -> updateMessagesList(it.messages)
                is State.NewMessage -> updateMessagesList(adapter.currentList + it.message)
                is State.Error -> showToast("Message loading error: ${it.message}")
            }
        })

        sendMessageButton.setOnClickListener {
            viewModel.sendButtonClicked(messageInput.text.toString())
            messageInput.setText("")
            messageInput.clearFocus()
            messageInput.hideKeyboard()
        }
    }

    private fun updateMessagesList(messages: List<Message>) {
        adapter.submitList(messages)
        adapter.notifyDataSetChanged()
    }

    private fun loadMockVideoStream() = Picasso.get().load(MOCK_IMAGE_URL).into(mockLiveStreamView)

    companion object {
        const val MOCK_IMAGE_URL =
            "https://images.unsplash.com/photo-1580343217802-e02386f04239?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=3634&q=80"
    }
}