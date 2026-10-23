package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.graphics3d.Vector3D

enum class DinoEra(
    val title: String,
    val timelineText: String,
    val epochYears: String,
    val color: Color
) {
    ALL("All Eras", "Mesozoic Era", "252 – 66 Ma", Color(0xFFE5A93C)),
    TRIASSIC("Triassic", "Dawn of Dinosaurs", "252 – 201 Ma", Color(0xFFFF7043)),
    JURASSIC("Jurassic", "Age of Giants", "201 – 145 Ma", Color(0xFF4CAF50)),
    CRETACEOUS("Cretaceous", "Peak Diversity & Climax", "145 – 66 Ma", Color(0xFFFFB300))
}

enum class DinoDiet(
    val title: String,
    val emoji: String,
    val description: String,
    val color: Color
) {
    ALL("All Diets", "🦕", "All Feeding Types", Color(0xFFE5A93C)),
    CARNIVORE("Carnivore", "🥩", "Meat / Predator", Color(0xFFEF5350)),
    HERBIVORE("Herbivore", "🌿", "Plant Eater", Color(0xFF66BB6A)),
    OMNIVORE("Omnivore", "🫐", "Plants & Animals", Color(0xFFAB47BC)),
    PISCIVORE("Piscivore", "🐟", "Fish Specialist", Color(0xFF26C6DA))
}

data class Hotspot3D(
    val id: String,
    val title: String,
    val description: String,
    val position: Vector3D
)

data class Dinosaur(
    val id: String,
    val name: String,
    val pronunciation: String,
    val meaning: String,
    val era: DinoEra,
    val periodDetail: String,
    val diet: DinoDiet,
    val lengthMeters: Float,
    val heightMeters: Float,
    val weightTons: Float,
    val topSpeedKmh: Int,
    val discoveryCountry: String,
    val discoveredYear: Int,
    val discoveredBy: String,
    val description: String,
    val anatomicalFeatures: List<String>,
    val funFacts: List<String>,
    val modelType: String,
    val hotspots: List<Hotspot3D>
)
