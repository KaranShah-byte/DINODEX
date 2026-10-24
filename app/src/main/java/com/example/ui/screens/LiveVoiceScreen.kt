package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gemini.GeminiConfig
import com.example.gemini.LiveVoiceState
import com.example.gemini.viewmodels.LiveVoiceViewModel
import kotlin.math.sin

@Composable
fun LiveVoiceScreen(
    liveVoiceViewModel: LiveVoiceViewModel,
    modifier: Modifier = Modifier
) {
    val voiceState by liveVoiceViewModel.voiceState.collectAsStateWithLifecycle()
    val amplitude by liveVoiceViewModel.amplitude.collectAsStateWithLifecycle()
    val statusText by liveVoiceViewModel.statusText.collectAsStateWithLifecycle()
    val transcript by liveVoiceViewModel.liveTranscript.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            liveVoiceViewModel.startSession()
        }
    }

    val isConnected = voiceState != LiveVoiceState.DISCONNECTED && voiceState != LiveVoiceState.ERROR
    val isRecording = voiceState == LiveVoiceState.USER_SPEAKING

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F13))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header & Live API Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x33FFB300)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Model: ${GeminiConfig.MODEL_LIVE}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (voiceState) {
                        LiveVoiceState.USER_SPEAKING -> Color(0xFF00E676).copy(alpha = 0.2f)
                        LiveVoiceState.MODEL_SPEAKING -> Color(0xFF2979FF).copy(alpha = 0.2f)
                        LiveVoiceState.CONNECTED_LISTENING -> Color(0xFFFFB300).copy(alpha = 0.2f)
                        LiveVoiceState.CONNECTING -> Color(0xFFAB47BC).copy(alpha = 0.2f)
                        LiveVoiceState.ERROR -> Color(0xFFFF1744).copy(alpha = 0.2f)
                        LiveVoiceState.DISCONNECTED -> Color(0x33FFFFFF)
                    }
                ) {
                    Text(
                        text = when (voiceState) {
                            LiveVoiceState.USER_SPEAKING -> "● You are Speaking"
                            LiveVoiceState.MODEL_SPEAKING -> "● Dr. Rex Replying"
                            LiveVoiceState.CONNECTED_LISTENING -> "● Live Listening"
                            LiveVoiceState.CONNECTING -> "● Connecting..."
                            LiveVoiceState.ERROR -> "● Connection Issue"
                            LiveVoiceState.DISCONNECTED -> "● Idle"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (voiceState) {
                            LiveVoiceState.USER_SPEAKING -> Color(0xFF00E676)
                            LiveVoiceState.MODEL_SPEAKING -> Color(0xFF2979FF)
                            LiveVoiceState.CONNECTED_LISTENING -> Color(0xFFFFB300)
                            LiveVoiceState.CONNECTING -> Color(0xFFAB47BC)
                            LiveVoiceState.ERROR -> Color(0xFFFF1744)
                            LiveVoiceState.DISCONNECTED -> Color(0xFFAAAAAA)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Live Paleontology Conversation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // Center Pulsing Live Audio Orb Visualizer
        Box(
            modifier = Modifier
                .size(240.dp)
                .testTag("live_audio_orb"),
            contentAlignment = Alignment.Center
        ) {
            AudioVisualizerOrb(
                voiceState = voiceState,
                amplitude = amplitude
            )

            // Center Icon / Status
            Surface(
                shape = CircleShape,
                color = when (voiceState) {
                    LiveVoiceState.USER_SPEAKING -> Color(0xFF00E676)
                    LiveVoiceState.MODEL_SPEAKING -> Color(0xFF2979FF)
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(76.dp),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (voiceState == LiveVoiceState.MODEL_SPEAKING) {
                            Icons.AutoMirrored.Filled.VolumeUp
                        } else {
                            Icons.Default.Mic
                        },
                        contentDescription = "Microphone status",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Live Transcript / Preview Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161920)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Live Conversation Stream",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (transcript.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap 'Start Live Voice' or choose a prehistoric question below to begin talking with Dr. Rex in real time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(transcript) { (text, isUser) ->
                            Text(
                                text = "${if (isUser) "You: " else "Dr. Rex: "}$text",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isUser) Color.White else Color(0xFFFFD54F)
                            )
                        }
                    }
                }
            }
        }

        // Quick Topic Suggestion Chips for Voice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            liveVoiceViewModel.quickVoiceTopics.forEach { topic ->
                AssistChip(
                    onClick = {
                        if (!hasMicPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            liveVoiceViewModel.startSession(topic)
                            liveVoiceViewModel.sendTextTurn(topic)
                        }
                    },
                    label = { Text(topic, fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFF222630),
                        labelColor = Color.White
                    ),
                    modifier = Modifier.testTag("voice_topic_chip")
                )
            }
        }

        // Bottom Voice Control Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // End / Reset Session button
            if (isConnected) {
                FilledTonalIconButton(
                    onClick = { liveVoiceViewModel.stopSession() },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color(0x33FF1744),
                        contentColor = Color(0xFFFF5252)
                    ),
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("button_end_live_session")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Live Session"
                    )
                }
            }

            // Primary Mic / Push-to-Talk Button
            Button(
                onClick = {
                    if (!hasMicPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        if (!isConnected) {
                            liveVoiceViewModel.startSession()
                        } else if (isRecording) {
                            liveVoiceViewModel.stopRecording()
                        } else {
                            liveVoiceViewModel.startRecording()
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color(0xFF00E676) else MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .height(56.dp)
                    .testTag("button_primary_mic")
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Mic else Icons.Default.MicNone,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        !isConnected -> "Start Live Voice"
                        isRecording -> "Tap to Send Audio"
                        else -> "Speak to Dr. Rex"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun AudioVisualizerOrb(
    voiceState: LiveVoiceState,
    amplitude: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = size.minDimension / 4f

        val activeAmp = if (voiceState == LiveVoiceState.USER_SPEAKING || voiceState == LiveVoiceState.MODEL_SPEAKING) {
            (amplitude * 1.8f).coerceIn(0.1f, 1.2f)
        } else if (voiceState == LiveVoiceState.CONNECTED_LISTENING) {
            0.15f * pulseAnim
        } else {
            0.05f
        }

        val primaryColor = when (voiceState) {
            LiveVoiceState.USER_SPEAKING -> Color(0xFF00E676)
            LiveVoiceState.MODEL_SPEAKING -> Color(0xFF2979FF)
            LiveVoiceState.CONNECTED_LISTENING -> Color(0xFFFFB300)
            LiveVoiceState.ERROR -> Color(0xFFFF1744)
            else -> Color(0x66E5A93C)
        }

        // Outer glow wave
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.35f * activeAmp),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = baseRadius * (1.6f + activeAmp * 0.8f)
            ),
            center = Offset(centerX, centerY),
            radius = baseRadius * (1.6f + activeAmp * 0.8f)
        )

        // Middle animated ring
        drawCircle(
            color = primaryColor.copy(alpha = 0.5f),
            center = Offset(centerX, centerY),
            radius = baseRadius * (1.25f + activeAmp * 0.4f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Inner core halo
        drawCircle(
            color = primaryColor.copy(alpha = 0.7f),
            center = Offset(centerX, centerY),
            radius = baseRadius * (1.05f + activeAmp * 0.2f),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
