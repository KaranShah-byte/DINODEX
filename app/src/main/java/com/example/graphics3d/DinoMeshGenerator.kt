package com.example.graphics3d

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object DinoMeshGenerator {

    // Common palette
    private val T_REX_BASE = Color(0xFF6B5848)
    private val T_REX_BELLY = Color(0xFF9E8E7D)
    private val T_REX_ACCENT = Color(0xFFB05035)

    private val TRI_BASE = Color(0xFF556052)
    private val TRI_BELLY = Color(0xFF8B9687)
    private val TRI_HORN = Color(0xFFDDD7CC)
    private val TRI_FRILL = Color(0xFFBC854B)

    private val STEG_BASE = Color(0xFF4A6B5D)
    private val STEG_BELLY = Color(0xFF869E92)
    private val STEG_PLATE = Color(0xFFC77838)
    private val STEG_SPIKE = Color(0xFFE8DCC4)

    private val BRACH_BASE = Color(0xFF5C6B73)
    private val BRACH_BELLY = Color(0xFF9DB4C0)
    private val BRACH_ACCENT = Color(0xFF76949F)

    private val SPINO_BASE = Color(0xFF385A64)
    private val SPINO_SAIL = Color(0xFFD35400)
    private val SPINO_BELLY = Color(0xFF7FA8A3)

    private val RAPTOR_BASE = Color(0xFF8E5B3D)
    private val RAPTOR_FEATHER = Color(0xFFD9822B)
    private val RAPTOR_CLAW = Color(0xFF2C2C2C)

    private val ANKY_BASE = Color(0xFF5A4D41)
    private val ANKY_ARMOR = Color(0xFF8D7B68)
    private val ANKY_CLUB = Color(0xFF3B332B)

    private val PTERO_BODY = Color(0xFF795548)
    private val PTERO_WING = Color(0xFFD7CCC8)
    private val PTERO_CREST = Color(0xFFE64A19)

    private val PARA_BASE = Color(0xFF6D7952)
    private val PARA_CREST = Color(0xFFE57373)
    private val PARA_BELLY = Color(0xFFB0BA93)

    private val ALLO_BASE = Color(0xFF5D5348)
    private val ALLO_RIDGE = Color(0xFFC0392B)
    private val ALLO_BELLY = Color(0xFF998B7C)

    private val EYE_COLOR = Color(0xFFFFD54F)
    private val TEETH_COLOR = Color(0xFFFFF9C4)
    private val HUMAN_COLOR = Color(0xFF4FC3F7)

    // Helper: Add a 3D box defined by min and max corners
    fun addBox(
        triangles: MutableList<Triangle3D>,
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        color: Color,
        partName: String? = null
    ) {
        val p000 = Vector3D(minX, minY, minZ)
        val p001 = Vector3D(minX, minY, maxZ)
        val p010 = Vector3D(minX, maxY, minZ)
        val p011 = Vector3D(minX, maxY, maxZ)
        val p100 = Vector3D(maxX, minY, minZ)
        val p101 = Vector3D(maxX, minY, maxZ)
        val p110 = Vector3D(maxX, maxY, minZ)
        val p111 = Vector3D(maxX, maxY, maxZ)

        // Front (maxZ)
        triangles.add(Triangle3D(p001, p101, p111, color, partName = partName))
        triangles.add(Triangle3D(p001, p111, p011, color, partName = partName))
        // Back (minZ)
        triangles.add(Triangle3D(p100, p000, p010, color, partName = partName))
        triangles.add(Triangle3D(p100, p010, p110, color, partName = partName))
        // Left (minX)
        triangles.add(Triangle3D(p000, p001, p011, color, partName = partName))
        triangles.add(Triangle3D(p000, p011, p010, color, partName = partName))
        // Right (maxX)
        triangles.add(Triangle3D(p101, p100, p110, color, partName = partName))
        triangles.add(Triangle3D(p101, p110, p111, color, partName = partName))
        // Top (maxY)
        triangles.add(Triangle3D(p010, p011, p111, color, partName = partName))
        triangles.add(Triangle3D(p010, p111, p110, color, partName = partName))
        // Bottom (minY)
        triangles.add(Triangle3D(p001, p000, p100, color, partName = partName))
        triangles.add(Triangle3D(p001, p100, p101, color, partName = partName))
    }

    // Helper: Add a tapered frustum / prism connecting two quads
    fun addTaperedSegment(
        triangles: MutableList<Triangle3D>,
        startCenter: Vector3D, startW: Float, startH: Float,
        endCenter: Vector3D, endW: Float, endH: Float,
        color: Color,
        partName: String? = null
    ) {
        val s0 = startCenter + Vector3D(-startW / 2, -startH / 2, 0f)
        val s1 = startCenter + Vector3D(startW / 2, -startH / 2, 0f)
        val s2 = startCenter + Vector3D(startW / 2, startH / 2, 0f)
        val s3 = startCenter + Vector3D(-startW / 2, startH / 2, 0f)

        val e0 = endCenter + Vector3D(-endW / 2, -endH / 2, 0f)
        val e1 = endCenter + Vector3D(endW / 2, -endH / 2, 0f)
        val e2 = endCenter + Vector3D(endW / 2, endH / 2, 0f)
        val e3 = endCenter + Vector3D(-endW / 2, endH / 2, 0f)

        // Top
        triangles.add(Triangle3D(s3, s2, e2, color, partName = partName))
        triangles.add(Triangle3D(s3, e2, e3, color, partName = partName))
        // Bottom
        triangles.add(Triangle3D(s0, e0, e1, color, partName = partName))
        triangles.add(Triangle3D(s0, e1, s1, color, partName = partName))
        // Left
        triangles.add(Triangle3D(s0, s3, e3, color, partName = partName))
        triangles.add(Triangle3D(s0, e3, e0, color, partName = partName))
        // Right
        triangles.add(Triangle3D(s1, e1, e2, color, partName = partName))
        triangles.add(Triangle3D(s1, e2, s2, color, partName = partName))
    }

    // Helper: Add a pyramid / spike
    fun addSpike(
        triangles: MutableList<Triangle3D>,
        baseCenter: Vector3D, tip: Vector3D, radius: Float,
        color: Color,
        partName: String? = null
    ) {
        val b0 = baseCenter + Vector3D(-radius, 0f, -radius)
        val b1 = baseCenter + Vector3D(radius, 0f, -radius)
        val b2 = baseCenter + Vector3D(radius, 0f, radius)
        val b3 = baseCenter + Vector3D(-radius, 0f, radius)

        triangles.add(Triangle3D(b0, b1, tip, color, partName = partName))
        triangles.add(Triangle3D(b1, b2, tip, color, partName = partName))
        triangles.add(Triangle3D(b2, b3, tip, color, partName = partName))
        triangles.add(Triangle3D(b3, b0, tip, color, partName = partName))
    }

    // Helper: Add a thin plate (for Stegosaurus or Crests)
    fun addPlate(
        triangles: MutableList<Triangle3D>,
        center: Vector3D, width: Float, height: Float, thickness: Float,
        color: Color,
        partName: String? = null
    ) {
        val tip = center + Vector3D(0f, height, 0f)
        val bl = center + Vector3D(-width / 2, 0f, -thickness / 2)
        val br = center + Vector3D(width / 2, 0f, -thickness / 2)
        val fl = center + Vector3D(-width / 2, 0f, thickness / 2)
        val fr = center + Vector3D(width / 2, 0f, thickness / 2)

        // Front face
        triangles.add(Triangle3D(fl, fr, tip, color, partName = partName))
        // Back face
        triangles.add(Triangle3D(br, bl, tip, color, partName = partName))
        // Sides
        triangles.add(Triangle3D(bl, fl, tip, color, partName = partName))
        triangles.add(Triangle3D(fr, br, tip, color, partName = partName))
    }

    // ==================== T-REX MODEL ====================
    fun createTRex(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Torso & Belly (Deep predatory barrel chest)
        addBox(triangles, -0.6f, -0.4f, -0.8f, 0.6f, 0.7f, 0.7f, T_REX_BASE, "Torso")
        addBox(triangles, -0.5f, -0.6f, -0.6f, 0.5f, -0.35f, 0.5f, T_REX_BELLY, "Belly")

        // 2. Neck & Powerful Throat
        addBox(triangles, -0.4f, 0.2f, 0.6f, 0.4f, 1.0f, 1.2f, T_REX_BASE, "Neck")

        // 3. Massive Tyrant Skull & Snout
        addBox(triangles, -0.42f, 0.6f, 1.1f, 0.42f, 1.25f, 1.9f, T_REX_ACCENT, "Head")
        addBox(triangles, -0.35f, 0.55f, 1.9f, 0.35f, 1.1f, 2.5f, T_REX_ACCENT, "Snout")
        // Lower Jaw & Open Mouth
        addBox(triangles, -0.32f, 0.25f, 1.2f, 0.32f, 0.55f, 2.45f, T_REX_BELLY, "Jaw")
        // Jagged teeth lines
        addBox(triangles, -0.33f, 0.52f, 1.95f, 0.33f, 0.58f, 2.42f, TEETH_COLOR, "Teeth")

        // Eyes
        addBox(triangles, -0.43f, 1.05f, 1.5f, -0.38f, 1.15f, 1.6f, EYE_COLOR, "Eye")
        addBox(triangles, 0.38f, 1.05f, 1.5f, 0.43f, 1.15f, 1.6f, EYE_COLOR, "Eye")

        // 4. Counterbalancing Heavy Tail (Segmented 5 parts)
        addBox(triangles, -0.45f, -0.1f, -1.5f, 0.45f, 0.5f, -0.8f, T_REX_BASE, "Tail Base")
        addBox(triangles, -0.35f, -0.05f, -2.2f, 0.35f, 0.4f, -1.5f, T_REX_BASE, "Tail Mid")
        addBox(triangles, -0.25f, 0.0f, -2.8f, 0.25f, 0.3f, -2.2f, T_REX_BASE, "Tail Extension")
        addBox(triangles, -0.15f, 0.05f, -3.4f, 0.15f, 0.25f, -2.8f, T_REX_BASE, "Tail Tip")

        // 5. Massive Hind Legs & Feet (Left & Right)
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.7f
            // Thigh (Muscular)
            addBox(triangles, posX - 0.2f, -0.3f, -0.3f, posX + 0.2f, 0.5f, 0.3f, T_REX_BASE, "Thigh")
            // Shin
            addBox(triangles, posX - 0.14f, -1.1f, -0.2f, posX + 0.14f, -0.3f, 0.1f, T_REX_BASE, "Shin")
            // Clawed Foot & Toes
            addBox(triangles, posX - 0.2f, -1.3f, -0.1f, posX + 0.2f, -1.1f, 0.5f, RAPTOR_CLAW, "Claws")
            // Tiny 2-fingered Arms
            addBox(triangles, side * 0.45f - 0.08f, 0.0f, 0.7f, side * 0.45f + 0.08f, 0.3f, 0.95f, T_REX_ACCENT, "Forearm")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.75f, "Tyrannosaurus Rex")
    }

    // ==================== TRICERATOPS MODEL ====================
    fun createTriceratops(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Heavy Quadruped Barrel Torso
        addBox(triangles, -0.7f, -0.4f, -0.9f, 0.7f, 0.6f, 0.8f, TRI_BASE, "Body")
        addBox(triangles, -0.6f, -0.6f, -0.7f, 0.6f, -0.35f, 0.6f, TRI_BELLY, "Belly")

        // 2. Iconic Neck Frill (Wide fan behind head)
        addPlate(triangles, Vector3D(0f, 0.5f, 0.85f), 1.6f, 0.9f, 0.15f, TRI_FRILL, "Neck Frill")

        // 3. Robust Head & Beak
        addBox(triangles, -0.45f, -0.1f, 0.9f, 0.45f, 0.6f, 1.7f, TRI_BASE, "Head")
        addBox(triangles, -0.25f, -0.2f, 1.7f, 0.25f, 0.3f, 2.2f, TRI_HORN, "Beak")

        // 4. Three Famous Horns
        // Left Brow Horn
        addSpike(triangles, Vector3D(-0.3f, 0.6f, 1.4f), Vector3D(-0.4f, 1.4f, 2.1f), 0.12f, TRI_HORN, "Brow Horn")
        // Right Brow Horn
        addSpike(triangles, Vector3D(0.3f, 0.6f, 1.4f), Vector3D(0.4f, 1.4f, 2.1f), 0.12f, TRI_HORN, "Brow Horn")
        // Nasal Horn
        addSpike(triangles, Vector3D(0f, 0.3f, 1.9f), Vector3D(0f, 0.8f, 2.2f), 0.09f, TRI_HORN, "Nasal Horn")

        // 5. Tail
        addBox(triangles, -0.35f, -0.1f, -1.6f, 0.35f, 0.35f, -0.9f, TRI_BASE, "Tail")
        addBox(triangles, -0.2f, -0.15f, -2.2f, 0.2f, 0.2f, -1.6f, TRI_BASE, "Tail Tip")

        // 6. 4 Sturdy Pillar Legs
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.65f
            // Front Leg
            addBox(triangles, posX - 0.18f, -1.2f, 0.3f, posX + 0.18f, -0.3f, 0.7f, TRI_BASE, "Front Leg")
            // Rear Leg
            addBox(triangles, posX - 0.2f, -1.2f, -0.7f, posX + 0.2f, -0.2f, -0.3f, TRI_BASE, "Hind Leg")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.8f, "Triceratops")
    }

    // ==================== STEGOSAURUS MODEL ====================
    fun createStegosaurus(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Arched Body
        addBox(triangles, -0.6f, -0.3f, -0.9f, 0.6f, 0.7f, 0.8f, STEG_BASE, "Body")
        addBox(triangles, -0.5f, -0.55f, -0.7f, 0.5f, -0.25f, 0.6f, STEG_BELLY, "Belly")

        // 2. Small Head on low neck
        addBox(triangles, -0.25f, -0.2f, 0.8f, 0.25f, 0.3f, 1.4f, STEG_BASE, "Neck")
        addBox(triangles, -0.2f, -0.35f, 1.4f, 0.2f, 0.15f, 2.0f, STEG_BASE, "Head")

        // 3. Alternating Double Row of Hexagonal Dorsal Plates
        val plateZPositions = listOf(-0.7f, -0.3f, 0.1f, 0.5f, 0.9f)
        for ((idx, z) in plateZPositions.withIndex()) {
            val offsetSide = if (idx % 2 == 0) -0.25f else 0.25f
            val plateHeight = if (idx in 1..3) 0.8f else 0.5f
            addPlate(
                triangles,
                Vector3D(offsetSide, 0.7f, z),
                0.55f, plateHeight, 0.08f,
                STEG_PLATE, "Dorsal Plate"
            )
        }

        // 4. Tail & Deadly Thagomizer Spikes
        addBox(triangles, -0.35f, -0.1f, -1.7f, 0.35f, 0.4f, -0.9f, STEG_BASE, "Tail Base")
        addBox(triangles, -0.2f, 0.0f, -2.4f, 0.2f, 0.3f, -1.7f, STEG_BASE, "Tail")

        // 4 Thagomizer Spikes pointing outwards and backwards
        addSpike(triangles, Vector3D(-0.2f, 0.2f, -2.2f), Vector3D(-0.9f, 0.5f, -2.5f), 0.08f, STEG_SPIKE, "Thagomizer")
        addSpike(triangles, Vector3D(-0.2f, 0.1f, -2.0f), Vector3D(-0.85f, 0.2f, -2.3f), 0.08f, STEG_SPIKE, "Thagomizer")
        addSpike(triangles, Vector3D(0.2f, 0.2f, -2.2f), Vector3D(0.9f, 0.5f, -2.5f), 0.08f, STEG_SPIKE, "Thagomizer")
        addSpike(triangles, Vector3D(0.2f, 0.1f, -2.0f), Vector3D(0.85f, 0.2f, -2.3f), 0.08f, STEG_SPIKE, "Thagomizer")

        // 5. 4 Columnar Legs
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.55f
            addBox(triangles, posX - 0.18f, -1.1f, 0.25f, posX + 0.18f, -0.2f, 0.65f, STEG_BASE, "Front Leg")
            addBox(triangles, posX - 0.22f, -1.1f, -0.8f, posX + 0.22f, -0.1f, -0.35f, STEG_BASE, "Rear Leg")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.8f, "Stegosaurus")
    }

    // ==================== BRACHIOSAURUS MODEL ====================
    fun createBrachiosaurus(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Massive Sauropod Torso with high shoulders
        addBox(triangles, -0.8f, -0.4f, -1.0f, 0.8f, 0.9f, 0.9f, BRACH_BASE, "Body")
        addBox(triangles, -0.7f, -0.65f, -0.8f, 0.7f, -0.35f, 0.7f, BRACH_BELLY, "Belly")

        // 2. Towering Sauropod Neck (Steep angle upwards)
        addBox(triangles, -0.4f, 0.6f, 0.7f, 0.4f, 1.4f, 1.2f, BRACH_BASE, "Neck Base")
        addBox(triangles, -0.3f, 1.3f, 1.0f, 0.3f, 2.1f, 1.4f, BRACH_BASE, "Mid Neck")
        addBox(triangles, -0.25f, 2.0f, 1.2f, 0.25f, 2.8f, 1.55f, BRACH_BASE, "High Neck")

        // 3. Rounded Crowned Head
        addBox(triangles, -0.25f, 2.7f, 1.4f, 0.25f, 3.15f, 2.0f, BRACH_ACCENT, "Head")

        // 4. Long Tapering Tail
        addBox(triangles, -0.45f, 0.0f, -1.8f, 0.45f, 0.6f, -1.0f, BRACH_BASE, "Tail Base")
        addBox(triangles, -0.3f, 0.1f, -2.6f, 0.3f, 0.45f, -1.8f, BRACH_BASE, "Mid Tail")
        addBox(triangles, -0.15f, 0.15f, -3.4f, 0.15f, 0.35f, -2.6f, BRACH_BASE, "Tail Tip")

        // 5. Towering Forelegs (Front legs longer than rear)
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.7f
            // Tall Front Pillar Leg
            addBox(triangles, posX - 0.22f, -1.5f, 0.35f, posX + 0.22f, -0.3f, 0.8f, BRACH_BASE, "Front Leg")
            // Shorter Rear Leg
            addBox(triangles, posX - 0.22f, -1.5f, -0.8f, posX + 0.22f, -0.3f, -0.4f, BRACH_BASE, "Rear Leg")
        }

        return Model3D(triangles, Vector3D(0f, -0.5f, 0f), 0.55f, "Brachiosaurus")
    }

    // ==================== SPINOSAURUS MODEL ====================
    fun createSpinosaurus(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Long Predatory Body
        addBox(triangles, -0.55f, -0.4f, -0.9f, 0.55f, 0.6f, 0.8f, SPINO_BASE, "Torso")
        addBox(triangles, -0.45f, -0.6f, -0.7f, 0.45f, -0.35f, 0.6f, SPINO_BELLY, "Belly")

        // 2. Magnificent Dorsal Neural Sail (High semicircular crest)
        addPlate(triangles, Vector3D(0f, 0.6f, 0.3f), 0.6f, 1.1f, 0.08f, SPINO_SAIL, "Dorsal Sail")
        addPlate(triangles, Vector3D(0f, 0.6f, -0.2f), 0.7f, 1.4f, 0.08f, SPINO_SAIL, "Dorsal Sail Peak")
        addPlate(triangles, Vector3D(0f, 0.6f, -0.7f), 0.6f, 1.0f, 0.08f, SPINO_SAIL, "Dorsal Sail")

        // 3. Elongated Crocodilian Snout & Jaws
        addBox(triangles, -0.35f, 0.1f, 0.8f, 0.35f, 0.7f, 1.4f, SPINO_BASE, "Neck")
        addBox(triangles, -0.3f, 0.2f, 1.4f, 0.3f, 0.65f, 2.1f, SPINO_BASE, "Head")
        addBox(triangles, -0.2f, 0.22f, 2.1f, 0.2f, 0.5f, 2.8f, SPINO_BASE, "Crocodile Snout")

        // 4. Broad Aquatic Fin-Like Tail
        addBox(triangles, -0.35f, -0.2f, -1.7f, 0.35f, 0.5f, -0.9f, SPINO_BASE, "Tail")
        addPlate(triangles, Vector3D(0f, 0.2f, -1.8f), 0.8f, 0.7f, 0.08f, SPINO_BASE, "Paddle Tail")
        addPlate(triangles, Vector3D(0f, 0.2f, -2.5f), 0.7f, 0.5f, 0.08f, SPINO_BASE, "Tail Fin")

        // 5. Strong Limbs
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.65f
            addBox(triangles, posX - 0.18f, -1.2f, -0.4f, posX + 0.18f, 0.1f, 0.1f, SPINO_BASE, "Hind Leg")
            addBox(triangles, side * 0.4f - 0.12f, -0.7f, 0.6f, side * 0.4f + 0.12f, 0.0f, 0.9f, SPINO_BASE, "Front Arm")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.7f, "Spinosaurus")
    }

    // ==================== VELOCIRAPTOR MODEL ====================
    fun createVelociraptor(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Sleek Agile Torso
        addBox(triangles, -0.35f, -0.3f, -0.6f, 0.35f, 0.4f, 0.5f, RAPTOR_BASE, "Body")

        // 2. Slender Neck & Bird-like Head
        addBox(triangles, -0.2f, 0.2f, 0.5f, 0.2f, 0.8f, 1.0f, RAPTOR_BASE, "Neck")
        addBox(triangles, -0.22f, 0.5f, 1.0f, 0.22f, 0.9f, 1.6f, RAPTOR_FEATHER, "Head")
        addBox(triangles, -0.15f, 0.45f, 1.6f, 0.15f, 0.75f, 2.0f, RAPTOR_BASE, "Snout")

        // 3. Stiff Balancing Tail
        addBox(triangles, -0.18f, 0.0f, -1.4f, 0.18f, 0.3f, -0.6f, RAPTOR_BASE, "Tail")
        addBox(triangles, -0.12f, 0.05f, -2.2f, 0.12f, 0.2f, -1.4f, RAPTOR_FEATHER, "Feathered Tail")

        // 4. Feathered Wing-Arms
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.4f
            addPlate(triangles, Vector3D(posX, 0.1f, 0.4f), 0.5f, 0.4f, 0.05f, RAPTOR_FEATHER, "Wing Feather")
        }

        // 5. Powerful Running Legs & Killing Sickle Claw
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.45f
            addBox(triangles, posX - 0.12f, -0.9f, -0.2f, posX + 0.12f, 0.1f, 0.15f, RAPTOR_BASE, "Leg")
            // Sickle Claw (Curved upwards on inner toe)
            addSpike(triangles, Vector3D(posX, -0.95f, 0.2f), Vector3D(posX, -0.7f, 0.35f), 0.06f, RAPTOR_CLAW, "Sickle Claw")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.9f, "Velociraptor")
    }

    // ==================== ANKYLOSAURUS MODEL ====================
    fun createAnkylosaurus(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Broad Low-Slung Armored Carapace
        addBox(triangles, -0.9f, -0.3f, -0.9f, 0.9f, 0.45f, 0.8f, ANKY_BASE, "Armor Carapace")
        addBox(triangles, -0.8f, -0.5f, -0.7f, 0.8f, -0.25f, 0.7f, ANKY_BASE, "Underbelly")

        // Rows of Osteoderm Spikes on armor
        val spikeZ = listOf(-0.6f, -0.1f, 0.4f)
        for (z in spikeZ) {
            addSpike(triangles, Vector3D(-0.9f, 0.2f, z), Vector3D(-1.25f, 0.35f, z), 0.12f, ANKY_ARMOR, "Armor Spike")
            addSpike(triangles, Vector3D(0.9f, 0.2f, z), Vector3D(1.25f, 0.35f, z), 0.12f, ANKY_ARMOR, "Armor Spike")
        }

        // 2. Broad Low Skull with Triangular Horns
        addBox(triangles, -0.5f, -0.2f, 0.8f, 0.5f, 0.35f, 1.5f, ANKY_ARMOR, "Head")
        addSpike(triangles, Vector3D(-0.45f, 0.2f, 1.0f), Vector3D(-0.75f, 0.4f, 0.85f), 0.1f, ANKY_ARMOR, "Cranial Horn")
        addSpike(triangles, Vector3D(0.45f, 0.2f, 1.0f), Vector3D(0.75f, 0.4f, 0.85f), 0.1f, ANKY_ARMOR, "Cranial Horn")

        // 3. Heavy Armored Tail & Bone Club
        addBox(triangles, -0.3f, -0.1f, -1.8f, 0.3f, 0.25f, -0.9f, ANKY_BASE, "Tail")
        // The Famous Tail Club (Massive fused osteoderm)
        addBox(triangles, -0.45f, -0.2f, -2.4f, 0.45f, 0.3f, -1.8f, ANKY_CLUB, "Tail Club")

        // 4. 4 Short Heavy Legs
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.75f
            addBox(triangles, posX - 0.18f, -0.95f, 0.3f, posX + 0.18f, -0.2f, 0.65f, ANKY_BASE, "Front Leg")
            addBox(triangles, posX - 0.2f, -0.95f, -0.65f, posX + 0.2f, -0.2f, -0.25f, ANKY_BASE, "Rear Leg")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.8f, "Ankylosaurus")
    }

    // ==================== PTERANODON MODEL ====================
    fun createPteranodon(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Aerodynamic Body
        addBox(triangles, -0.25f, -0.25f, -0.4f, 0.25f, 0.3f, 0.5f, PTERO_BODY, "Body")

        // 2. Dramatic Backward Crest & Beak
        addBox(triangles, -0.15f, 0.1f, 0.4f, 0.15f, 0.5f, 0.9f, PTERO_BODY, "Head")
        // Long Toothless Beak
        addSpike(triangles, Vector3D(0f, 0.25f, 0.9f), Vector3D(0f, 0.2f, 2.1f), 0.12f, PTERO_BODY, "Beak")
        // Backward Cranial Crest
        addPlate(triangles, Vector3D(0f, 0.5f, 0.2f), 0.5f, 0.8f, 0.05f, PTERO_CREST, "Cranial Crest")

        // 3. Wide Expansive Wings (Wingspan)
        // Left Wing
        triangles.add(Triangle3D(
            Vector3D(-0.25f, 0.1f, 0.3f),
            Vector3D(-2.5f, 0.2f, -0.2f),
            Vector3D(-0.25f, 0.0f, -0.3f),
            PTERO_WING, partName = "Left Wing"
        ))
        // Right Wing
        triangles.add(Triangle3D(
            Vector3D(0.25f, 0.1f, 0.3f),
            Vector3D(0.25f, 0.0f, -0.3f),
            Vector3D(2.5f, 0.2f, -0.2f),
            PTERO_WING, partName = "Right Wing"
        ))

        // Wing Bones
        addBox(triangles, -2.5f, 0.15f, -0.25f, -0.25f, 0.25f, -0.15f, PTERO_BODY, "Wing Arm")
        addBox(triangles, 0.25f, 0.15f, -0.25f, 2.5f, 0.25f, -0.15f, PTERO_BODY, "Wing Arm")

        // Feet / Tail
        addBox(triangles, -0.15f, -0.5f, -0.5f, 0.15f, -0.1f, -0.3f, PTERO_BODY, "Feet")

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.85f, "Pteranodon")
    }

    // ==================== PARASAUROLOPHUS MODEL ====================
    fun createParasaurolophus(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Duck-Billed Body
        addBox(triangles, -0.5f, -0.35f, -0.8f, 0.5f, 0.6f, 0.7f, PARA_BASE, "Body")
        addBox(triangles, -0.4f, -0.55f, -0.6f, 0.4f, -0.3f, 0.5f, PARA_BELLY, "Belly")

        // 2. Neck & Head
        addBox(triangles, -0.3f, 0.3f, 0.6f, 0.3f, 1.1f, 1.1f, PARA_BASE, "Neck")
        addBox(triangles, -0.25f, 0.7f, 1.1f, 0.25f, 1.25f, 1.7f, PARA_BASE, "Head")
        // Duck-bill Snout
        addBox(triangles, -0.22f, 0.65f, 1.7f, 0.22f, 0.95f, 2.3f, PARA_BASE, "Duck Bill")

        // 3. Tubular Backward Resonating Crest
        addPlate(triangles, Vector3D(0f, 1.3f, 0.9f), 0.4f, 1.0f, 0.08f, PARA_CREST, "Tubular Crest")

        // 4. Tail
        addBox(triangles, -0.35f, -0.1f, -1.6f, 0.35f, 0.45f, -0.8f, PARA_BASE, "Tail")
        addBox(triangles, -0.2f, 0.0f, -2.4f, 0.2f, 0.3f, -1.6f, PARA_BASE, "Tail Tip")

        // 5. Quadruped / Bipedal Limbs
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.55f
            addBox(triangles, posX - 0.16f, -1.1f, -0.3f, posX + 0.16f, 0.1f, 0.2f, PARA_BASE, "Hind Leg")
            addBox(triangles, side * 0.4f - 0.12f, -1.0f, 0.5f, side * 0.4f + 0.12f, -0.2f, 0.75f, PARA_BASE, "Front Leg")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.8f, "Parasaurolophus")
    }

    // ==================== ALLOSAURUS MODEL ====================
    fun createAllosaurus(): Model3D {
        val triangles = mutableListOf<Triangle3D>()

        // 1. Torso
        addBox(triangles, -0.5f, -0.35f, -0.8f, 0.5f, 0.65f, 0.7f, ALLO_BASE, "Torso")
        addBox(triangles, -0.4f, -0.55f, -0.6f, 0.4f, -0.3f, 0.5f, ALLO_BELLY, "Belly")

        // 2. Neck & Head with Brow Ridges
        addBox(triangles, -0.35f, 0.2f, 0.6f, 0.35f, 1.0f, 1.2f, ALLO_BASE, "Neck")
        addBox(triangles, -0.35f, 0.55f, 1.1f, 0.35f, 1.15f, 1.9f, ALLO_BASE, "Head")
        addBox(triangles, -0.28f, 0.5f, 1.9f, 0.28f, 0.95f, 2.5f, ALLO_BASE, "Snout")
        // Distinctive Red Supraorbital Horns/Ridges above eyes
        addSpike(triangles, Vector3D(-0.32f, 1.15f, 1.45f), Vector3D(-0.4f, 1.45f, 1.45f), 0.08f, ALLO_RIDGE, "Brow Horn")
        addSpike(triangles, Vector3D(0.32f, 1.15f, 1.45f), Vector3D(0.4f, 1.45f, 1.45f), 0.08f, ALLO_RIDGE, "Brow Horn")

        // 3. Balancing Tail
        addBox(triangles, -0.4f, -0.05f, -1.6f, 0.4f, 0.45f, -0.8f, ALLO_BASE, "Tail")
        addBox(triangles, -0.25f, 0.05f, -2.4f, 0.25f, 0.35f, -1.6f, ALLO_BASE, "Tail Mid")
        addBox(triangles, -0.15f, 0.1f, -3.1f, 0.15f, 0.25f, -2.4f, ALLO_BASE, "Tail Tip")

        // 4. Hind legs and 3-clawed grasping arms
        for (side in listOf(-1f, 1f)) {
            val posX = side * 0.6f
            addBox(triangles, posX - 0.18f, -1.2f, -0.3f, posX + 0.18f, 0.3f, 0.25f, ALLO_BASE, "Hind Leg")
            // Grasping Arm
            addBox(triangles, side * 0.42f - 0.1f, -0.4f, 0.65f, side * 0.42f + 0.1f, 0.1f, 0.9f, ALLO_RIDGE, "Grasping Arm")
        }

        return Model3D(triangles, Vector3D(0f, 0f, 0f), 0.75f, "Allosaurus")
    }

    // ==================== HUMAN SCALE COMPARISON MODEL ====================
    fun createHumanFigure(): List<Triangle3D> {
        val list = mutableListOf<Triangle3D>()
        val humanColor = HUMAN_COLOR
        // Human figure is ~1.8 units tall in human scale, placed at side (X = 1.8f)
        val x = 1.6f
        // Head
        addBox(list, x - 0.1f, 0.6f, 0f, x + 0.1f, 0.85f, 0.18f, humanColor, "Human Head")
        // Torso
        addBox(list, x - 0.18f, 0.0f, 0f, x + 0.18f, 0.6f, 0.15f, humanColor, "Human Torso")
        // Legs
        addBox(list, x - 0.16f, -0.9f, 0f, x - 0.04f, 0.0f, 0.12f, humanColor, "Human Leg")
        addBox(list, x + 0.04f, -0.9f, 0f, x + 0.16f, 0.0f, 0.12f, humanColor, "Human Leg")
        // Arms
        addBox(list, x - 0.26f, 0.0f, 0f, x - 0.18f, 0.55f, 0.1f, humanColor, "Human Arm")
        addBox(list, x + 0.18f, 0.0f, 0f, x + 0.26f, 0.55f, 0.1f, humanColor, "Human Arm")
        return list
    }

    // Main factory function
    fun getModel(type: String): Model3D {
        return when (type.uppercase()) {
            "TREX", "T_REX" -> createTRex()
            "TRICERATOPS" -> createTriceratops()
            "STEGOSAURUS" -> createStegosaurus()
            "BRACHIOSAURUS" -> createBrachiosaurus()
            "SPINOSAURUS" -> createSpinosaurus()
            "VELOCIRAPTOR" -> createVelociraptor()
            "ANKYLOSAURUS" -> createAnkylosaurus()
            "PTERANODON" -> createPteranodon()
            "PARASAUROLOPHUS" -> createParasaurolophus()
            "ALLOSAURUS" -> createAllosaurus()
            else -> createTRex()
        }
    }
}
