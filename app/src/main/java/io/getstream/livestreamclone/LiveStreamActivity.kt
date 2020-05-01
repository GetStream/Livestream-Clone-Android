package io.getstream.livestreamclone

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class LiveStreamActivity : AppCompatActivity() {

    private val adapter = MessagesListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMockVideoStream()
        messagesList.adapter = adapter

        val viewModel: LiveStreamViewModel by viewModels()
        viewModel.viewState.observe(this, Observer {
            when (it) {
                is State.Messages -> {
                    adapter.submitList(it.messages)
                    adapter.notifyDataSetChanged()
                }
                is State.NewMessage -> {
                    val updatedMessages = adapter.currentList + it.message
                    adapter.submitList(updatedMessages)
                    adapter.notifyDataSetChanged()
                }
                is State.Error -> showToast("error: ${it.message}")
            }
        })

        sendMessageButton.setOnClickListener {
            viewModel.sendButtonClicked(messageInput.text.toString())
            messageInput.setText("")
            messageInput.clearFocus()
        }
    }

    private fun loadMockVideoStream() = Picasso.get().load(MOCK_IMAGE_URL).into(mockLiveStreamView)

    companion object {
        const val MOCK_IMAGE_URL =
            "https://images.unsplash.com/photo-1580343217802-e02386f04239?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=3634&q=80"
    }
}