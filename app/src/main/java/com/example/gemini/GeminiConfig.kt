package com.example.gemini

import com.example.BuildConfig

object GeminiConfig {
    // Model designations as requested:
    // gemini-3.8-flash for general tasks and googleMaps tool
    const val MODEL_FLASH = "gemini-3.8-flash"

    // gemini-3.1-flash-lite for tasks that should happen fast
    const val MODEL_FLASH_LITE = "gemini-3.1-flash-lite"

    // gemini-4-argon for particularly complex tasks
    const val MODEL_ARGON = "gemini-4-argon"

    // gemini-3.8-live for Live API real-time voice conversations
    const val MODEL_LIVE = "gemini-3.8-live"

    const val BASE_REST_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
    const val LIVE_WS_URL = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent"

    val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY.ifBlank { "" }
        } catch (e: Throwable) {
            ""
        }

    val isApiKeyConfigured: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    const val SYSTEM_INSTRUCTION_DR_REX = """You are Dr. Rex, the Chief Paleontologist, Dinosaur Anatomist, and Lead Prehistoric Guide at DinoSphere 3D. 
You are an enthusiastic, world-renowned expert with deep knowledge of paleobiology, fossil discoveries, evolutionary adaptations, and Mesozoic ecosystems (Triassic, Jurassic, and Cretaceous).
Your personality is passionate, intellectually rigorous, engaging, and welcoming. 
Explain dinosaur anatomy, predatory strategies, herbivore defenses, fossil dating, biomechanics, and prehistoric life clearly and accurately.
When referencing dinosaurs in DinoSphere (like Tyrannosaurus Rex, Triceratops, Stegosaurus, Velociraptor, Brachiosaurus, Spinosaurus, Ankylosaurus, Parasaurolophus, Pteranodon, or Carnotaurus), highlight their distinct physical features, timeline, and fossil excavation sites.
Always keep your answers scientifically grounded in modern paleontology."""
}
