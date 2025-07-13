package com.example.gemini_clone

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.core.net.toUri
import com.google.firebase.ai.type.content
import java.net.URI


class MainActivity : AppCompatActivity() {
    lateinit var searchField: EditText;
    lateinit var sendBtn: ImageButton;
    lateinit var generativeModel: GenerativeModel;
    lateinit var sendCard: CardView
    lateinit var messagesList: MessagesList
    lateinit var us: User
    lateinit var gemini: User
    lateinit var adapter: MessagesListAdapter<Message>
    var imageSelected: Boolean = false
    lateinit var selectedImageUri: Uri

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

        var imageLoader:ImageLoader = object:ImageLoader{
            override fun loadImage(imageView: ImageView?, url: String?, payload: Any?) {
                imageView!!.setImageURI(Uri.parse(url))
            }
        }


        adapter =
            MessagesListAdapter<Message>("1", imageLoader)
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

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                selectedImageUri = uri
                imageSelected = true
                var message: Message = Message(id = "M1", text = "", createdAt = Calendar.getInstance().time, user = us, imageUrl = uri.toString())
                adapter.addToStart(message, true)
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        findViewById<ImageView>(R.id.imageView).setOnClickListener{
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


        }

        loadModel();
    }

    private suspend fun performAction(question: String){
        searchField.text.clear()
        var message: Message = Message(
            id = "M1",
            text = question,
            createdAt = Calendar.getInstance().time,
            user = us,
            imageUrl = null
        )
        adapter.addToStart(message, true)
        if(imageSelected){
            imageSelected = false
            var source = ImageDecoder.createSource(contentResolver, selectedImageUri)
            val image1: Bitmap = ImageDecoder.decodeBitmap(source)

            val inputContent = content{
                image(image1)
                text(question)

            }

            val response = generativeModel.generateContent(inputContent)
            var message2: Message = Message(
                id = "M2",
                text = response.text.toString(),
                createdAt = Calendar.getInstance().time,
                user = gemini,
                imageUrl = null
            )
            adapter.addToStart(message2, true)
            print(response.text)



        }else {

            val response = generativeModel.generateContent(question)
            var message2: Message = Message(
                id = "M2",
                text = response.text.toString(),
                createdAt = Calendar.getInstance().time,
                user = gemini,
                imageUrl = null
            )
            adapter.addToStart(message2, true)
            print(response.text)
        }
    }

    private fun loadModel(){
        // Initialize the Gemini Developer API backend service
        // Create a `GenerativeModel` instance with a model that supports your use case
        generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash")
    }
}