package com.example.gemini

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class GeminiLiveClient(private val context: Context) : WebSocketListener() {

    companion object {
        private const val TAG = "GeminiLiveClient"
        private const val INPUT_SAMPLE_RATE = 16000
        private const val OUTPUT_SAMPLE_RATE = 24000
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var recordingJob: Job? = null

    private var webSocket: WebSocket? = null
    private var audioTrack: AudioTrack? = null
    private var audioRecord: AudioRecord? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _voiceState = MutableStateFlow(LiveVoiceState.DISCONNECTED)
    val voiceState: StateFlow<LiveVoiceState> = _voiceState

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude

    private val _statusText = MutableStateFlow("Ready to connect with Dr. Rex via Live API")
    val statusText: StateFlow<String> = _statusText

    private val _liveTranscript = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // Pair of (Text, isUser)
    val liveTranscript: StateFlow<List<Pair<String, Boolean>>> = _liveTranscript

    init {
        initTts()
    }

    private fun initTts() {
        try {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isTtsReady = true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "TTS initialization skipped", e)
        }
    }

    fun startSession(initialTopic: String? = null) {
        val apiKey = GeminiConfig.apiKey
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            _voiceState.value = LiveVoiceState.ERROR
            _statusText.value = "Gemini API key missing. Configure GEMINI_API_KEY in Secrets panel."
            return
        }

        _voiceState.value = LiveVoiceState.CONNECTING
        _statusText.value = "Connecting to gemini-3.8-live..."

        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val wsUrl = "${GeminiConfig.LIVE_WS_URL}?key=$apiKey"
        val request = Request.Builder().url(wsUrl).build()

        setupAudioTrack()
        webSocket = client.newWebSocket(request, this)

        if (!initialTopic.isNullOrBlank()) {
            addTranscript("Let's talk about $initialTopic!", isUser = true)
        }
    }

    fun stopSession() {
        stopRecording()
        try {
            webSocket?.close(1000, "Session ended by user")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing websocket", e)
        }
        webSocket = null
        stopAudioTrack()
        tts?.stop()
        _voiceState.value = LiveVoiceState.DISCONNECTED
        _statusText.value = "Session disconnected"
        _amplitude.value = 0f
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "WebSocket connected successfully to gemini-3.8-live")
        _voiceState.value = LiveVoiceState.CONNECTED_LISTENING
        _statusText.value = "Connected to gemini-3.8-live! Speak or tap prompt to converse."

        // Send Initial Handshake Setup Message for gemini-3.8-live
        try {
            val setupJson = JSONObject().apply {
                val setupObj = JSONObject().apply {
                    put("model", "models/${GeminiConfig.MODEL_LIVE}")
                    val genConfig = JSONObject().apply {
                        val modalities = JSONArray().apply {
                            put("AUDIO")
                        }
                        put("responseModalities", modalities)
                        val speechConfig = JSONObject().apply {
                            val voiceConfig = JSONObject().apply {
                                val prebuilt = JSONObject().apply {
                                    put("voiceName", "Puck")
                                }
                                put("prebuiltVoiceConfig", prebuilt)
                            }
                            put("voiceConfig", voiceConfig)
                        }
                        put("speechConfig", speechConfig)
                    }
                    put("generationConfig", genConfig)

                    val sysInstruction = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", GeminiConfig.SYSTEM_INSTRUCTION_DR_REX))
                        }
                        put("parts", parts)
                    }
                    put("systemInstruction", sysInstruction)
                }
                put("setup", setupObj)
            }

            webSocket.send(setupJson.toString())
            Log.d(TAG, "Setup message sent to Live API")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send setup message", e)
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val msg = JSONObject(text)
            val serverContent = msg.optJSONObject("serverContent") ?: return

            val interrupted = serverContent.optBoolean("interrupted", false)
            if (interrupted) {
                // User interrupted the model
                audioTrack?.pause()
                audioTrack?.flush()
                audioTrack?.play()
                _voiceState.value = LiveVoiceState.CONNECTED_LISTENING
            }

            val modelTurn = serverContent.optJSONObject("modelTurn")
            if (modelTurn != null) {
                _voiceState.value = LiveVoiceState.MODEL_SPEAKING
                _statusText.value = "Dr. Rex is speaking..."

                val parts = modelTurn.optJSONArray("parts")
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)

                        // Check for text transcript
                        val textPart = part.optString("text", "")
                        if (textPart.isNotBlank()) {
                            addTranscript(textPart, isUser = false)
                        }

                        // Check for audio payload
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val base64Audio = inlineData.optString("data", "")
                            if (base64Audio.isNotBlank()) {
                                playAudioChunk(base64Audio)
                            }
                        }
                    }
                }
            }

            val turnComplete = serverContent.optBoolean("turnComplete", false)
            if (turnComplete) {
                _voiceState.value = LiveVoiceState.CONNECTED_LISTENING
                _statusText.value = "Dr. Rex finished speaking. Listening for your response..."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing server message", e)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "WebSocket error", t)
        _voiceState.value = LiveVoiceState.ERROR
        _statusText.value = "Live connection error: ${t.localizedMessage ?: "Network issue"}"
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "WebSocket closed: $code / $reason")
        _voiceState.value = LiveVoiceState.DISCONNECTED
        _statusText.value = "Voice session closed"
    }

    fun sendTextPrompt(text: String) {
        addTranscript(text, isUser = true)
        if (webSocket == null) {
            startSession()
        }

        scope.launch {
            try {
                val clientContent = JSONObject().apply {
                    val content = JSONObject().apply {
                        val turns = JSONArray().apply {
                            val turn = JSONObject().apply {
                                put("role", "user")
                                val parts = JSONArray().apply {
                                    put(JSONObject().put("text", text))
                                }
                                put("parts", parts)
                            }
                            put(turn)
                        }
                        put("turns", turns)
                        put("turnComplete", true)
                    }
                    put("clientContent", content)
                }

                webSocket?.send(clientContent.toString())
                _voiceState.value = LiveVoiceState.MODEL_SPEAKING
                _statusText.value = "Thinking..."
            } catch (e: Exception) {
                Log.e(TAG, "Error sending text prompt", e)
            }
        }
    }

    fun startRecording() {
        if (_voiceState.value == LiveVoiceState.DISCONNECTED) {
            startSession()
        }

        try {
            val minBuf = AudioRecord.getMinBufferSize(
                INPUT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBuf.coerceAtLeast(4096)

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                INPUT_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord not initialized")
                return
            }

            audioRecord?.startRecording()
            _voiceState.value = LiveVoiceState.USER_SPEAKING
            _statusText.value = "Listening to you... (streaming audio to gemini-3.8-live)"

            recordingJob = scope.launch {
                val buffer = ByteArray(2048)
                while (isActive && _voiceState.value == LiveVoiceState.USER_SPEAKING) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // Calculate RMS amplitude for visualizer
                        var sum = 0.0
                        for (i in 0 until read step 2) {
                            if (i + 1 < read) {
                                val sample = (buffer[i].toInt() and 0xFF) or (buffer[i + 1].toInt() shl 8)
                                val shortSample = sample.toShort()
                                sum += shortSample * shortSample
                            }
                        }
                        val rms = sqrt(sum / (read / 2)).toFloat()
                        _amplitude.value = (rms / 32768f).coerceIn(0f, 1f)

                        // Stream audio frame to WebSocket
                        val base64 = Base64.encodeToString(buffer, 0, read, Base64.NO_WRAP)
                        val realtimeInput = JSONObject().apply {
                            val input = JSONObject().apply {
                                val mediaChunks = JSONArray().apply {
                                    val chunk = JSONObject().apply {
                                        put("mimeType", "audio/pcm;rate=16000")
                                        put("data", base64)
                                    }
                                    put(chunk)
                                }
                                put("mediaChunks", mediaChunks)
                            }
                            put("realtimeInput", input)
                        }
                        webSocket?.send(realtimeInput.toString())
                    }
                }
            }
        } catch (e: SecurityException) {
            _voiceState.value = LiveVoiceState.ERROR
            _statusText.value = "Microphone permission required for Live voice conversation."
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecord error", e)
        }
    }

    fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecord stop error", e)
        }
        audioRecord = null
        _amplitude.value = 0f
        if (_voiceState.value == LiveVoiceState.USER_SPEAKING) {
            _voiceState.value = LiveVoiceState.CONNECTED_LISTENING
            _statusText.value = "Processing audio with Dr. Rex..."
        }
    }

    private fun setupAudioTrack() {
        try {
            val minBuf = AudioTrack.getMinBufferSize(
                OUTPUT_SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(OUTPUT_SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            Log.e(TAG, "AudioTrack init error", e)
        }
    }

    private fun stopAudioTrack() {
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "AudioTrack release error", e)
        }
        audioTrack = null
    }

    private fun playAudioChunk(base64: String) {
        try {
            val pcmBytes = Base64.decode(base64, Base64.DEFAULT)
            audioTrack?.write(pcmBytes, 0, pcmBytes.size)

            // Calculate playback amplitude for UI
            var sum = 0.0
            for (i in 0 until pcmBytes.size step 2) {
                if (i + 1 < pcmBytes.size) {
                    val sample = (pcmBytes[i].toInt() and 0xFF) or (pcmBytes[i + 1].toInt() shl 8)
                    val shortSample = sample.toShort()
                    sum += shortSample * shortSample
                }
            }
            val rms = sqrt(sum / (pcmBytes.size / 2)).toFloat()
            _amplitude.value = (rms / 32768f).coerceIn(0f, 1f)
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio chunk", e)
        }
    }

    private fun addTranscript(text: String, isUser: Boolean) {
        val current = _liveTranscript.value.toMutableList()
        current.add(Pair(text, isUser))
        _liveTranscript.value = current
    }

    fun cleanUp() {
        stopSession()
        tts?.shutdown()
        tts = null
    }
}
