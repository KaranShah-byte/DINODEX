package com.example.gemini.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gemini.GeminiLiveClient
import com.example.gemini.LiveVoiceState
import kotlinx.coroutines.flow.StateFlow

class LiveVoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val liveClient = GeminiLiveClient(application.applicationContext)

    val voiceState: StateFlow<LiveVoiceState> = liveClient.voiceState
    val amplitude: StateFlow<Float> = liveClient.amplitude
    val statusText: StateFlow<String> = liveClient.statusText
    val liveTranscript: StateFlow<List<Pair<String, Boolean>>> = liveClient.liveTranscript

    val quickVoiceTopics = listOf(
        "Could T-Rex run as fast as a car?",
        "Why was Spinosaurus so unique?",
        "What was the largest dinosaur ever found?",
        "Tell me what happened 66 million years ago!"
    )

    fun startSession(topic: String? = null) {
        liveClient.startSession(topic)
    }

    fun stopSession() {
        liveClient.stopSession()
    }

    fun startRecording() {
        liveClient.startRecording()
    }

    fun stopRecording() {
        liveClient.stopRecording()
    }

    fun sendTextTurn(text: String) {
        liveClient.sendTextPrompt(text)
    }

    override fun onCleared() {
        super.onCleared()
        liveClient.cleanUp()
    }
}
