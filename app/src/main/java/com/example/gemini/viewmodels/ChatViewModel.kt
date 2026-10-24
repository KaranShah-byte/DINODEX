package com.example.gemini.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gemini.ChatMessage
import com.example.gemini.GeminiApiService
import com.example.gemini.GeminiConfig
import com.example.gemini.GeminiModelChoice
import com.example.gemini.MessageSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val initialWelcomeMessage = ChatMessage(
        text = "Welcome to DinoSphere 3D! I'm Dr. Rex, your Chief Paleontologist guide. " +
                "I'm here to answer any questions about Mesozoic dinosaurs, biomechanics, " +
                "fossil discoveries, and extinction events. What prehistoric mystery shall we explore?",
        sender = MessageSender.DR_REX,
        modelUsed = GeminiConfig.MODEL_FLASH_LITE
    )

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(initialWelcomeMessage))
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _selectedModel = MutableStateFlow(GeminiModelChoice.FLASH_LITE)
    val selectedModel: StateFlow<GeminiModelChoice> = _selectedModel

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun selectModel(choice: GeminiModelChoice) {
        _selectedModel.value = choice
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val userMessage = ChatMessage(
            text = userText.trim(),
            sender = MessageSender.USER
        )

        val updatedHistory = _messages.value + userMessage
        _messages.value = updatedHistory
        _isLoading.value = true
        _errorMessage.value = null

        val currentModel = _selectedModel.value.modelId

        viewModelScope.launch {
            val result = GeminiApiService.sendChatMessage(
                messages = updatedHistory,
                model = currentModel,
                systemInstruction = GeminiConfig.SYSTEM_INSTRUCTION_DR_REX
            )

            result.fold(
                onSuccess = { reply ->
                    val aiMessage = ChatMessage(
                        text = reply,
                        sender = MessageSender.DR_REX,
                        modelUsed = currentModel
                    )
                    _messages.value = _messages.value + aiMessage
                    _isLoading.value = false
                },
                onFailure = { error ->
                    val errMsg = error.localizedMessage ?: "Unknown error while communicating with Gemini."
                    val errMessage = ChatMessage(
                        text = "Dr. Rex is temporarily offline: $errMsg",
                        sender = MessageSender.DR_REX,
                        modelUsed = currentModel,
                        isError = true
                    )
                    _messages.value = _messages.value + errMessage
                    _errorMessage.value = errMsg
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearChat() {
        _messages.value = listOf(initialWelcomeMessage)
        _errorMessage.value = null
    }

    fun retryLastMessage() {
        val lastUserMsg = _messages.value.lastOrNull { it.sender == MessageSender.USER } ?: return
        sendMessage(lastUserMsg.text)
    }
}
