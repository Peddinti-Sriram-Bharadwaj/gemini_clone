package com.example.gemini_clone

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    lateinit var searchField: EditText;
    lateinit var resultTV: TextView;
    lateinit var sendBtn: ImageButton;
    lateinit var generativeModel: GenerativeModel;

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
        resultTV = findViewById(R.id.textView)

        sendBtn.setOnClickListener{
            lifecycleScope.launch{
                performAction(searchField.text.toString())
            }

            searchField.text.clear()

        }

        loadModel();
    }
    private suspend fun performAction(question: String){
        val response = generativeModel.generateContent(question)
        print(response.text)
        resultTV.text = response.text
    }

    private fun loadModel(){
        // Initialize the Gemini Developer API backend service
        // Create a `GenerativeModel` instance with a model that supports your use case
        generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash")
    }
}