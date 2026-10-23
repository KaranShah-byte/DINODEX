package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.graphics3d.DinoMeshGenerator
import com.example.model.DinoDiet
import com.example.model.DinoEra
import com.example.model.DinosaurDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DinoSphere 3D", appName)
    }

    @Test
    fun `dinosaur database filtering by era and diet`() {
        val allDinos = DinosaurDatabase.dinosaurs
        assertTrue(allDinos.size >= 10)

        // Filter by Cretaceous era
        val cretaceousDinos = DinosaurDatabase.filter(
            searchQuery = "",
            era = DinoEra.CRETACEOUS,
            diet = DinoDiet.ALL
        )
        assertTrue(cretaceousDinos.isNotEmpty())
        assertTrue(cretaceousDinos.all { it.era == DinoEra.CRETACEOUS })

        // Filter by Carnivore diet
        val carnivores = DinosaurDatabase.filter(
            searchQuery = "",
            era = DinoEra.ALL,
            diet = DinoDiet.CARNIVORE
        )
        assertTrue(carnivores.isNotEmpty())
        assertTrue(carnivores.all { it.diet == DinoDiet.CARNIVORE })

        // Search by query
        val trexResults = DinosaurDatabase.filter(
            searchQuery = "Tyrannosaurus",
            era = DinoEra.ALL,
            diet = DinoDiet.ALL
        )
        assertEquals(1, trexResults.size)
        assertEquals("trex", trexResults[0].id)
    }

    @Test
    fun `3D mesh generator generates valid models`() {
        val modelTypes = listOf(
            "trex", "triceratops", "stegosaurus", "velociraptor",
            "brachiosaurus", "spinosaurus", "ankylosaurus", "parasaurolophus",
            "pteranodon", "carnotaurus"
        )

        for (type in modelTypes) {
            val model = DinoMeshGenerator.getModel(type)
            assertNotNull(model)
            assertTrue("Model for $type should have triangles", model.triangles.isNotEmpty())
            assertTrue("Model for $type should have baseScale > 0", model.baseScale > 0f)
        }
    }

    @Test
    fun `gemini model configuration contains required models`() {
        assertEquals("gemini-3.8-flash", com.example.gemini.GeminiConfig.MODEL_FLASH)
        assertEquals("gemini-4-argon", com.example.gemini.GeminiConfig.MODEL_ARGON)
        assertEquals("gemini-3.1-flash-lite", com.example.gemini.GeminiConfig.MODEL_FLASH_LITE)
        assertEquals("gemini-3.8-live", com.example.gemini.GeminiConfig.MODEL_LIVE)

        val choices = com.example.gemini.GeminiModelChoice.values()
        assertEquals(3, choices.size)
        assertTrue(choices.any { it.modelId == "gemini-3.8-flash" })
        assertTrue(choices.any { it.modelId == "gemini-4-argon" })
        assertTrue(choices.any { it.modelId == "gemini-3.1-flash-lite" })
    }

    @Test
    fun `chat view model initializes with welcome message and model switching`() {
        val chatViewModel = com.example.gemini.viewmodels.ChatViewModel()
        val initialMessages = chatViewModel.messages.value
        assertEquals(1, initialMessages.size)
        assertEquals(com.example.gemini.MessageSender.DR_REX, initialMessages[0].sender)
        assertTrue(initialMessages[0].text.contains("Dr. Rex"))

        chatViewModel.selectModel(com.example.gemini.GeminiModelChoice.ARGON)
        assertEquals(com.example.gemini.GeminiModelChoice.ARGON, chatViewModel.selectedModel.value)

        chatViewModel.selectModel(com.example.gemini.GeminiModelChoice.FLASH_LITE)
        assertEquals(com.example.gemini.GeminiModelChoice.FLASH_LITE, chatViewModel.selectedModel.value)
    }

    @Test
    fun `maps grounding view model provides preset paleontological queries`() {
        val mapsViewModel = com.example.gemini.viewmodels.MapsGroundingViewModel()
        assertTrue(mapsViewModel.presetQueries.isNotEmpty())
        assertTrue(mapsViewModel.presetQueries.any { it.contains("T-Rex") })
        assertTrue(mapsViewModel.presetQueries.any { it.contains("Hell Creek") })
    }
}

