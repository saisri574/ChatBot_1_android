package com.example.chatbot_1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import okhttp3.MediaType.Companion.toMediaType

class ChatViewModel : ViewModel() {

    var isSending by mutableStateOf(false)
        private set
    var isBotTyping by mutableStateOf(false)
        private set

    val messageList = mutableStateListOf<MessageModel>()

    private val client = OkHttpClient()



    fun sendMessage(question: String) {
        viewModelScope.launch {
            isSending = true
            isBotTyping = true
//            messageList.add(MessageModel(question, "user"))

            val messagesArray = org.json.JSONArray().apply {
                messageList.forEach {
                    put(JSONObject().apply {
                        put("role", it.role)
                        put("content", it.message)
                    })
                }
                // Add the current user message too
                put(JSONObject().put("role", "user").put("content", question))
            }

            val requestBody = JSONObject().apply {
                put("model", "llama3-70b-8192")
                put("messages", messagesArray)
            }.toString()

            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Authorization", "Bearer ${Constants.apiKey}")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create("application/json".toMediaType(), requestBody))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    messageList.add(MessageModel("Failed to connect: ${e.message}", "model"))
                    isSending = false
                    isBotTyping = false
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            val errorBody = response.body?.string() ?: response.message
                            messageList.add(MessageModel("Error: $errorBody", "model"))
                        }
                        else {
                            val json = JSONObject(response.body!!.string())
                            val reply = json.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")

                            messageList.add(MessageModel(question, "user"))
                            messageList.add(MessageModel(reply, "assistant"))                        }
                        isSending = false
                        isBotTyping = false
                    }
                }
            })
        }
    }
}