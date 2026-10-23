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
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
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
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please set GEMINI_API_KEY in the AI Studio Secrets panel.")
            )
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            val systemInstructionJson = JSONObject()
            val sysParts = JSONArray()
            sysParts.put(JSONObject().put("text", systemInstruction))
            systemInstructionJson.put("parts", sysParts)
            rootJson.put("systemInstruction", systemInstructionJson)

            // Contents array for multi-turn history
            val contentsArray = JSONArray()
            for (msg in messages) {
                if (msg.isError) continue
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.sender == MessageSender.USER) "user" else "model")
                val partsArray = JSONArray()
                partsArray.put(JSONObject().put("text", msg.text))
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }
            rootJson.put("contents", contentsArray)

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
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please set GEMINI_API_KEY in the AI Studio Secrets panel.")
            )
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
                    return@withContext Result.failure(Exception("No candidate response from Gemini Maps Grounding."))
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
                    "No answer generated."
                }

                // Extract Grounding metadata
                val placesList = mutableListOf<GroundingPlace>()
                val searchQueriesList = mutableListOf<String>()

                val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
                if (groundingMetadata != null) {
                    // Search queries used
                    val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
                    if (webSearchQueries != null) {
                        for (i in 0 until webSearchQueries.length()) {
                            searchQueriesList.add(webSearchQueries.getString(i))
                        }
                    }

                    // Grounding chunks
                    val chunks = groundingMetadata.optJSONArray("groundingChunks")
                    if (chunks != null) {
                        for (i in 0 until chunks.length()) {
                            val chunk = chunks.getJSONObject(i)
                            // Check for maps object
                            val mapsObj = chunk.optJSONObject("maps")
                            if (mapsObj != null) {
                                val title = mapsObj.optString("title", "Paleontology Site")
                                val uri = if (mapsObj.has("uri")) mapsObj.getString("uri") else null
                                val address = if (mapsObj.has("address")) mapsObj.getString("address") else null
                                val placeId = if (mapsObj.has("placeId")) mapsObj.getString("placeId") else null
                                placesList.add(GroundingPlace(title = title, address = address, uri = uri, placeId = placeId))
                            }

                            // Also check for web source if it references a museum/site
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

                Result.success(
                    MapsGroundingResult(
                        query = query,
                        answer = answerText,
                        places = placesList,
                        searchQueries = searchQueriesList
                    )
                )
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Maps Grounding exception", e)
            Result.failure(e)
        }
    }
}
