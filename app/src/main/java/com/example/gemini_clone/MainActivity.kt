package com.example.gemini_clone

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    lateinit var searchField: EditText;
    lateinit var sendBtn: ImageButton;
    lateinit var generativeModel: GenerativeModel;
    lateinit var sendCard: CardView
    lateinit var messagesList: MessagesList
    lateinit var us: User
    lateinit var gemini: User
    lateinit var adapter: MessagesListAdapter<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchField = findViewById(R.id.editTextText)
        sendBtn = findViewById(R.id.imageButton)
        sendCard = findViewById(R.id.cardView3)
        messagesList = findViewById(R.id.messagesList)

        adapter =
            MessagesListAdapter<Message>("1", null)
        messagesList.setAdapter(adapter)

        us = User(id = "1", name = "User", avatar = "")
        gemini = User(id = "2", name = "Gemini", avatar = "")

        sendBtn.setOnClickListener{
            lifecycleScope.launch{
                performAction(searchField.text.toString())
            }

        }

        sendCard.setOnClickListener{
            lifecycleScope.launch{
                performAction(searchField.text.toString())
            }

        }

        loadModel();
    }

    private suspend fun performAction(question: String){
        searchField.text.clear()
        var message: Message = Message(id = "M1", text = question, createdAt = Calendar.getInstance().time, user = us)
        adapter.addToStart(message, true)
        val response = generativeModel.generateContent(question)
        var message2: Message = Message(id = "M2", text = response.text.toString(), createdAt = Calendar.getInstance().time, user = gemini )
        adapter.addToStart(message2, true)
        print(response.text)
    }

    private fun loadModel(){
        // Initialize the Gemini Developer API backend service
        // Create a `GenerativeModel` instance with a model that supports your use case
        generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash")
    }
}