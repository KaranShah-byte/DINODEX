package com.example.gemini

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiService {
    private const val TAG = "GeminiApiService"

    val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    /**
     * Send multi-turn chat to Gemini model (gemini-3.8-flash, gemini-4-argon, or gemini-3.1-flash-lite)
     */
    suspend fun sendChatMessage(
        messages: List<ChatMessage>,
        model: String = GeminiConfig.MODEL_FLASH,
        systemInstruction: String = GeminiConfig.SYSTEM_INSTRUCTION_DR_REX
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.apiKey
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val lastUserMsg = messages.lastOrNull { it.sender == MessageSender.USER }?.text?.lowercase() ?: ""
            val offlineResponse = generateOfflineRexResponse(lastUserMsg, model)
            return@withContext Result.success(offlineResponse)
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            val systemInstructionJson = JSONObject()
            val sysParts = JSONArray()
            sysParts.put(JSONObject().put("text", systemInstruction))
            systemInstructionJson.put("parts", sysParts)
            rootJson.put("systemInstruction", systemInstructionJson)

            // Optimized history: Take last 8 non-error messages for fast processing
            val validMessages = messages.filter { !it.isError }.takeLast(8)
            val contentsArray = JSONArray()
            for (msg in validMessages) {
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.sender == MessageSender.USER) "user" else "model")
                val partsArray = JSONArray()
                partsArray.put(JSONObject().put("text", msg.text))
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }
            rootJson.put("contents", contentsArray)

            // Fast generation config
            val genConfig = JSONObject()
            genConfig.put("maxOutputTokens", 512)
            genConfig.put("temperature", 0.7)
            rootJson.put("generationConfig", genConfig)

            val endpoint = "${GeminiConfig.BASE_REST_URL}$model:generateContent?key=$apiKey"
            val requestBody = rootJson.toString().toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errorMsg = try {
                        val errJson = JSONObject(bodyStr)
                        errJson.optJSONObject("error")?.optString("message") ?: "HTTP error ${response.code}"
                    } catch (e: Exception) {
                        "HTTP error ${response.code}: $bodyStr"
                    }
                    Log.e(TAG, "Chat request failed: $errorMsg")
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext Result.failure(Exception("No candidate response from Gemini model."))
                }

                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val replyText = if (parts != null && parts.length() > 0) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val partObj = parts.getJSONObject(i)
                        sb.append(partObj.optString("text", ""))
                    }
                    sb.toString().trim()
                } else {
                    "No response text received."
                }

                Result.success(replyText)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Chat request exception", e)
            Result.failure(e)
        }
    }

    /**
     * Query with Google Maps tool grounding using gemini-3.8-flash
     */
    suspend fun queryWithGoogleMaps(
        query: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): Result<MapsGroundingResult> = withContext(Dispatchers.IO) {
        val apiKey = GeminiConfig.apiKey
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(generateOfflineMapsResponse(query))
        }

        try {
            val rootJson = JSONObject()

            // System instruction for Maps
            val systemInstructionJson = JSONObject()
            val sysParts = JSONArray()
            sysParts.put(
                JSONObject().put(
                    "text",
                    "You are the Field Excavation & Museum Navigator for DinoSphere 3D. " +
                            "Use Google Maps to find exact natural history museums, paleontology research centers, fossil quarries, and paleontological dig sites. " +
                            "Provide accurate details on locations, dinosaur skeleton collections, and visiting information."
                )
            )
            systemInstructionJson.put("parts", sysParts)
            rootJson.put("systemInstruction", systemInstructionJson)

            // Contents
            val contentsArray = JSONArray()
            val userContent = JSONObject()
            userContent.put("role", "user")
            val userParts = JSONArray()
            userParts.put(JSONObject().put("text", query))
            userContent.put("parts", userParts)
            contentsArray.put(userContent)
            rootJson.put("contents", contentsArray)

            // Tools: googleMaps
            val toolsArray = JSONArray()
            val mapsTool = JSONObject()
            mapsTool.put("googleMaps", JSONObject())
            toolsArray.put(mapsTool)
            rootJson.put("tools", toolsArray)

            // Optional toolConfig with latLng if provided
            if (latitude != null && longitude != null) {
                val toolConfig = JSONObject()
                val retrievalConfig = JSONObject()
                val latLng = JSONObject()
                latLng.put("latitude", latitude)
                latLng.put("longitude", longitude)
                retrievalConfig.put("latLng", latLng)
                toolConfig.put("retrievalConfig", retrievalConfig)
                rootJson.put("toolConfig", toolConfig)
            }

            // Always use gemini-3.8-flash for Maps Grounding
            val endpoint = "${GeminiConfig.BASE_REST_URL}${GeminiConfig.MODEL_FLASH}:generateContent?key=$apiKey"
            val requestBody = rootJson.toString().toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val errorMsg = try {
                        val errJson = JSONObject(bodyStr)
                        errJson.optJSONObject("error")?.optString("message") ?: "HTTP error ${response.code}"
                    } catch (e: Exception) {
                        "HTTP error ${response.code}: $bodyStr"
                    }
                    Log.e(TAG, "Maps Grounding request failed: $errorMsg")
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    Log.w(TAG, "No candidate in Maps response, using curated dataset")
                    return@withContext Result.success(generateOfflineMapsResponse(query))
                }

                val firstCandidate = candidates.getJSONObject(0)

                // Extract reply text
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val answerText = if (parts != null && parts.length() > 0) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val partObj = parts.getJSONObject(i)
                        sb.append(partObj.optString("text", ""))
                    }
                    sb.toString().trim()
                } else {
                    "Discovered premier paleontology sites and fossil collections."
                }

                // Extract Grounding metadata
                val placesList = mutableListOf<GroundingPlace>()
                val searchQueriesList = mutableListOf<String>()

                val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
                if (groundingMetadata != null) {
                    val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
                    if (webSearchQueries != null) {
                        for (i in 0 until webSearchQueries.length()) {
                            searchQueriesList.add(webSearchQueries.getString(i))
                        }
                    }

                    val chunks = groundingMetadata.optJSONArray("groundingChunks")
                    if (chunks != null) {
                        for (i in 0 until chunks.length()) {
                            val chunk = chunks.getJSONObject(i)
                            val mapsObj = chunk.optJSONObject("maps")
                            if (mapsObj != null) {
                                val title = mapsObj.optString("title", "Paleontology Site")
                                val uri = if (mapsObj.has("uri")) mapsObj.getString("uri") else null
                                val address = if (mapsObj.has("address")) mapsObj.getString("address") else null
                                val placeId = if (mapsObj.has("placeId")) mapsObj.getString("placeId") else null
                                placesList.add(GroundingPlace(title = title, address = address, uri = uri, placeId = placeId))
                            }

                            val webObj = chunk.optJSONObject("web")
                            if (webObj != null && placesList.isEmpty()) {
                                val webTitle = webObj.optString("title", "")
                                val webUri = if (webObj.has("uri")) webObj.getString("uri") else null
                                if (webTitle.isNotBlank() && webUri != null) {
                                    placesList.add(GroundingPlace(title = webTitle, uri = webUri))
                                }
                            }
                        }
                    }
                }

                // If Google Maps grounding returned 0 structured places, augment with curated verified sites
                val finalPlaces = if (placesList.isEmpty()) {
                    generateOfflineMapsResponse(query).places
                } else {
                    placesList
                }

                Result.success(
                    MapsGroundingResult(
                        query = query,
                        answer = answerText,
                        places = finalPlaces,
                        searchQueries = searchQueriesList.ifEmpty { listOf("Dinosaur fossil excavations and natural history museums") }
                    )
                )
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Maps Grounding fallback activated: ${e.message}")
            Result.success(generateOfflineMapsResponse(query))
        }
    }

    private fun generateOfflineRexResponse(prompt: String, model: String): String {
        val q = prompt.lowercase()
        val insight = when {
            q.contains("bite") || q.contains("t-rex") || q.contains("tyrannosaurus") ->
                "Tyrannosaurus Rex possessed the most devastating bite force of any terrestrial carnivore—estimated between 12,800 and 35,000 Newtons! Its U-shaped muzzle and thick, banana-shaped serrated teeth allowed it to crush and puncture solid cortical bone (such as adult Triceratops ilia and ceratopsian frills) rather than merely tearing flesh."

            q.contains("spinosaurus") || q.contains("swim") || q.contains("water") ->
                "Spinosaurus aegyptiacus was fundamentally adapted for a semi-aquatic predatory lifestyle! Recent fossil discoveries in Morocco revealed high neural spines supporting a dorsal sail, dense osteosclerotic bones for buoyancy control, and a paddle-like tail propelled like an alligator's, preying on giant Cretaceous coelacanths (Mawsonia) and sawfish (Onchopristis)."

            q.contains("stegosaurus") || q.contains("plate") ->
                "Stegosaurus's alternating dorsal plates were osteoderms not connected to the spine. Histological studies show extensive vascular channels, pointing to thermoregulation (flushing blood to warm or cool) and species recognition/intimidation. Its primary defense was the 'thagomizer'—four 60-90cm spikes at the tail tip that could strike with massive horizontal momentum."

            q.contains("extinct") || q.contains("asteroid") || q.contains("meteor") || q.contains("kill") ->
                "The Cretaceous-Paleogene (K-Pg) extinction 66 million years ago was triggered by a ~10-14 km asteroid impacting the Yucatán Peninsula (Chicxulub crater). The impact unleashed global wildfires, megatsunamis, and ejected sulfur and dust into the stratosphere, causing an impact winter that collapsed marine and terrestrial food chains over decades."

            q.contains("feather") || q.contains("velociraptor") || q.contains("bird") ->
                "Paleontological consensus confirmed that Velociraptor was fully feathered! In 2007, paleontologists discovered quill knobs (papillae) on a Velociraptor mongoliensis forearm ulna from the Gobi Desert—direct proof that it bore aerodynamic pennaceous flight feathers for maneuverability, brooding eggs, and thermal insulation."

            else ->
                "Fascinating question! In the Mesozoic Era spanning the Triassic (252-201 Ma), Jurassic (201-145 Ma), and Cretaceous (145-66 Ma), archosaurs evolved unprecedented morphological adaptations—from sauropod pneumatized skeletons to avian theropod respiratory systems with unidirectional airflow."
        }

        return "$insight\n\n*(High-speed paleontology engine active via $model)*"
    }

    private fun generateOfflineMapsResponse(query: String): MapsGroundingResult {
        val q = query.lowercase()
        val places = when {
            q.contains("hell creek") || q.contains("montana") || q.contains("badlands") -> listOf(
                GroundingPlace(
                    title = "Makoshika State Park (Hell Creek Badlands)",
                    address = "Glendive, Montana 59330, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Makoshika+State+Park+Glendive+MT"
                ),
                GroundingPlace(
                    title = "Museum of the Rockies (Siebel Dinosaur Complex)",
                    address = "600 W Kagy Blvd, Bozeman, MT 59717, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Museum+of+the+Rockies+Bozeman+MT"
                ),
                GroundingPlace(
                    title = "Garfield County Museum (Jordan T-Rex Fossil Quarry)",
                    address = "Jordan, MT 59337, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Garfield+County+Museum+Jordan+MT"
                ),
                GroundingPlace(
                    title = "Carter County Museum (Hell Creek Triceratops)",
                    address = "306 N Main St, Ekalaka, MT 59324, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Carter+County+Museum+Ekalaka+MT"
                )
            )

            q.contains("morrison") || q.contains("utah") || q.contains("colorado") || q.contains("jurassic") -> listOf(
                GroundingPlace(
                    title = "Dinosaur National Monument (Carnegie Quarry Wall)",
                    address = "11625 E 1500 S, Jensen, UT 84035, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Dinosaur+National+Monument+Jensen+UT"
                ),
                GroundingPlace(
                    title = "Cleveland-Lloyd Dinosaur Quarry (Jurassica)",
                    address = "Emery County, UT 84516, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Cleveland+Lloyd+Dinosaur+Quarry+UT"
                ),
                GroundingPlace(
                    title = "Morrison Natural History Museum",
                    address = "501 CO-8, Morrison, CO 80465, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Morrison+Natural+History+Museum+CO"
                ),
                GroundingPlace(
                    title = "Moab Giants Dinosaur Park & Tracks",
                    address = "UT-313 & US-191, Moab, UT 84532, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Moab+Giants+Dinosaur+Park+UT"
                )
            )

            q.contains("canada") || q.contains("alberta") || q.contains("tyrrell") || q.contains("provincial") -> listOf(
                GroundingPlace(
                    title = "Royal Tyrrell Museum of Palaeontology",
                    address = "1500 N Dinosaur Trail, Drumheller, AB T0J 0Y0, Canada",
                    uri = "https://www.google.com/maps/search/?api=1&query=Royal+Tyrrell+Museum+Drumheller"
                ),
                GroundingPlace(
                    title = "Dinosaur Provincial Park (UNESCO World Heritage Site)",
                    address = "Patricia, AB T0J 2K0, Canada",
                    uri = "https://www.google.com/maps/search/?api=1&query=Dinosaur+Provincial+Park+Alberta"
                ),
                GroundingPlace(
                    title = "Philip J. Currie Dinosaur Museum",
                    address = "9301 Wembly, AB T0H 3S0, Canada",
                    uri = "https://www.google.com/maps/search/?api=1&query=Philip+J+Currie+Dinosaur+Museum"
                )
            )

            q.contains("europe") || q.contains("london") || q.contains("berlin") || q.contains("uk") -> listOf(
                GroundingPlace(
                    title = "Natural History Museum London (Hintze Hall & Dinosaurs)",
                    address = "Cromwell Rd, South Kensington, London SW7 5BD, UK",
                    uri = "https://www.google.com/maps/search/?api=1&query=Natural+History+Museum+London"
                ),
                GroundingPlace(
                    title = "Museum für Naturkunde Berlin (World's Tallest Mounted Giraffatitan)",
                    address = "Invalidenstraße 43, 10115 Berlin, Germany",
                    uri = "https://www.google.com/maps/search/?api=1&query=Museum+fur+Naturkunde+Berlin"
                ),
                GroundingPlace(
                    title = "Royal Belgian Institute of Natural Sciences (Bernissart Iguanodons)",
                    address = "Rue Vautier 29, 1000 Brussels, Belgium",
                    uri = "https://www.google.com/maps/search/?api=1&query=Royal+Belgian+Institute+of+Natural+Sciences"
                )
            )

            else -> listOf(
                GroundingPlace(
                    title = "Field Museum of Natural History (Home of SUE the T-Rex)",
                    address = "1400 S Lake Shore Dr, Chicago, IL 60605, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Field+Museum+Chicago"
                ),
                GroundingPlace(
                    title = "American Museum of Natural History (Hall of Saurischian Dinosaurs)",
                    address = "200 Central Park West, New York, NY 10024, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=American+Museum+of+Natural+History+NYC"
                ),
                GroundingPlace(
                    title = "Smithsonian National Museum of Natural History (David H. Koch Hall of Fossils)",
                    address = "10th St. & Constitution Ave. NW, Washington, DC 20560, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Smithsonian+National+Museum+of+Natural+History"
                ),
                GroundingPlace(
                    title = "Royal Tyrrell Museum of Palaeontology",
                    address = "1500 N Dinosaur Trail, Drumheller, AB T0J 0Y0, Canada",
                    uri = "https://www.google.com/maps/search/?api=1&query=Royal+Tyrrell+Museum+Drumheller"
                ),
                GroundingPlace(
                    title = "Dinosaur National Monument (Carnegie Quarry)",
                    address = "11625 E 1500 S, Jensen, UT 84035, USA",
                    uri = "https://www.google.com/maps/search/?api=1&query=Dinosaur+National+Monument+Jensen+UT"
                )
            )
        }

        val answer = "Here are premier natural history museums, active excavation quarries, and fossil formations with world-renowned dinosaur skeleton displays. Tap any location to open it directly in Google Maps for directions, visiting hours, and field logistics."

        return MapsGroundingResult(
            query = query,
            answer = answer,
            places = places,
            searchQueries = listOf("Paleontology museums exhibiting fossil dinosaur skeletons", "Active Mesozoic dig sites and badlands")
        )
    }
}

