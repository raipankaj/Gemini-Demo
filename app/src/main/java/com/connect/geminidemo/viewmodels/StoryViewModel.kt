package com.connect.geminidemo.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connect.geminidemo.UiState
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoryViewModel: ViewModel() {

    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)

    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "......"
    )

    fun sendPrompt(prompt: String, bitmap: Bitmap?) {
        val message = "You are a helpful bot who loves to write creative story. Based on the provided information you have to write a very creative story. $prompt"
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(
                    content {
                        if (bitmap != null) {
                            image(bitmap)
                        }
                        text(message)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (exc: Exception) {
                _uiState.value = UiState.Error(exc.localizedMessage ?: "")
            }
        }
    }
}