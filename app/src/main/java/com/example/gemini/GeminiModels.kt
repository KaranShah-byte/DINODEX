package com.example.gemini

import java.util.UUID

enum class MessageSender {
    USER,
    DR_REX
}

enum class GeminiModelChoice(
    val modelId: String,
    val displayName: String,
    val speedTag: String,
    val description: String
) {
    FLASH(
        modelId = GeminiConfig.MODEL_FLASH,
        displayName = "gemini-3.8-flash",
        speedTag = "Balanced & Grounded",
        description = "Recommended for general paleontological Q&A and Maps Grounding"
    ),
    ARGON(
        modelId = GeminiConfig.MODEL_ARGON,
        displayName = "gemini-4-argon",
        speedTag = "Complex Reasoning",
        description = "Deep paleobiological analysis, anatomy, and biomechanics"
    ),
    FLASH_LITE(
        modelId = GeminiConfig.MODEL_FLASH_LITE,
        displayName = "gemini-3.1-flash-lite",
        speedTag = "Ultra-Fast",
        description = "Instant answers for quick species queries and rapid trivia"
    )
}

data class GroundingPlace(
    val title: String,
    val address: String? = null,
    val uri: String? = null,
    val placeId: String? = null
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String? = null,
    val isError: Boolean = false,
    val groundingPlaces: List<GroundingPlace> = emptyList()
)

data class MapsGroundingResult(
    val query: String,
    val answer: String,
    val places: List<GroundingPlace>,
    val searchQueries: List<String> = emptyList()
)

enum class LiveVoiceState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED_LISTENING,
    USER_SPEAKING,
    MODEL_SPEAKING,
    ERROR
}
