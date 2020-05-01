package io.getstream.livestreamclone

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class LiveStreamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMockVideoStream()

        val viewModel: LivestreamViewModel by viewModels()
        viewModel.viewState.observe(this, Observer {
            when (it) {
                is State.Messages -> showToast("new messages: ${it.messages.size}")
                is State.Error -> showToast("error ${it.message}")
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

fun AppCompatActivity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
