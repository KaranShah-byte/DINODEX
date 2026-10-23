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
            query = "",
            era = DinoEra.CRETACEOUS,
            diet = DinoDiet.ALL
        )
        assertTrue(cretaceousDinos.isNotEmpty())
        assertTrue(cretaceousDinos.all { it.era == DinoEra.CRETACEOUS })

        // Filter by Carnivore diet
        val carnivores = DinosaurDatabase.filter(
            query = "",
            era = DinoEra.ALL,
            diet = DinoDiet.CARNIVORE
        )
        assertTrue(carnivores.isNotEmpty())
        assertTrue(carnivores.all { it.diet == DinoDiet.CARNIVORE })

        // Search by query
        val trexResults = DinosaurDatabase.filter(
            query = "Tyrannosaurus",
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
}
