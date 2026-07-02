package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.FreedomGoal
import com.example.data.VictoryLog
import com.example.data.ChatMessage
import com.example.data.SavedChat
import com.example.ui.OverComerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Speech-to-Text, Text-to-Speech, Permissions, and Dialog imports
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

fun cleanTextForSpeech(text: String): String {
    if (text.isBlank()) return text
    var clean = text
    // Replace slash, long dash, or bullets with space first to prevent word concatenation
    clean = clean.replace("—", " ")
    clean = clean.replace("-", " ")
    clean = clean.replace("/", " ")
    clean = clean.replace("•", " ")
    
    // Retain only letters, numbers, spaces, and natural reading punctuation
    clean = clean.replace(Regex("[^\\p{L}\\p{N}\\s.,!?'\"():;]"), "")
    
    // Clean up multiple spaces
    clean = clean.replace(Regex("\\s+"), " ").trim()
    return clean
}

enum class ActiveTab {
    FREEDOM, INSPIRATIONAL, CHAT, JOURNAL, BIBLE
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FocusSelectionScreen(
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AppCoverBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Text(
                text = "Welcome to OverComer",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Are you having an all around tough day, Are you struggling with your substance recovery or are you struggling with your mental health wellness?",
                    style = MaterialTheme.typography.titleMedium.copy(lineHeight = 24.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Text(
                text = "Please select a focus path below to customize your experience:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            // Rearranged order: 1. All Around Tough Day, 2. Substance Recovery, 3. Mental Health Wellness
            // Option 1: All Around Tough Day
            FocusOptionCard(
                title = "All Around Tough Day",
                description = "Direct encouragement, simplified calming breathing, and a compassionate space to vent your stress instantly.",
                icon = Icons.Default.Cloud,
                color = Color(0xFFD32F2F),
                onClick = { onSelect("TOUGH_DAY") },
                testTag = "focus_tough_day_card"
            )

            // Option 2: Substance Recovery
            FocusOptionCard(
                title = "Substance Recovery",
                description = "Our core Christian choice-based freedom theology, sobriety tracker, and relapse prevention thought reframing logs.",
                icon = Icons.Default.Shield,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onSelect("SUBSTANCE_RECOVERY") },
                testTag = "focus_substance_recovery_card"
            )

            // Option 3: Mental Health Wellness
            FocusOptionCard(
                title = "Mental Health Wellness",
                description = "Peace scriptures, anxiety logs, emotional distress resources, and beautiful breath exercises.",
                icon = Icons.Default.FavoriteBorder,
                color = Color(0xFF00796B),
                onClick = { onSelect("MENTAL_HEALTH") },
                testTag = "focus_mental_health_card"
            )

            // Option 4: Today is a Testimony/Victory Day
            FocusOptionCard(
                title = "Today is a Testimony/Victory Day",
                description = "Celebrate what God has done! Enjoy victorious scriptures, battle-winning quotes, and share your triumphs with your OverComer companion.",
                icon = Icons.Default.Star,
                color = Color(0xFFFFA000),
                onClick = { onSelect("TESTIMONY_VICTORY") },
                testTag = "focus_testimony_victory_card"
            )
        }
    }
}

@Composable
fun FocusOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun AppCoverBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // 1. Draw Sunset Sky Gradient
                val sunsetBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E1C47), // Deep purple night sky
                        Color(0xFF6B2D5C), // Warm rich magenta
                        Color(0xFFD84A5A), // Soft peach pink
                        Color(0xFFFF9E79)  // Warm horizon glow
                    )
                )
                drawRect(brush = sunsetBrush)

                // 2. Draw Sun rising/setting at the bottom center horizon
                val sunX = width * 0.5f
                val sunY = height * 0.75f
                val sunRadius = height * 0.18f
                
                // Outer soft glow
                drawCircle(
                    color = Color(0xFFFFEB3B).copy(alpha = 0.25f),
                    radius = sunRadius * 1.8f,
                    center = Offset(sunX, sunY)
                )
                // Inner bright sun core
                drawCircle(
                    color = Color(0xFFFFFDE7).copy(alpha = 0.8f),
                    radius = sunRadius,
                    center = Offset(sunX, sunY)
                )

                // 3. Draw Shore/Beach Ground Silhouette at the bottom
                val groundPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, height * 0.8f)
                    quadraticTo(
                        width * 0.35f, height * 0.76f,
                        width * 0.7f, height * 0.82f
                    )
                    quadraticTo(
                        width * 0.85f, height * 0.85f,
                        width, height * 0.78f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = groundPath,
                    color = Color(0xFF160E22) // Near-black dark sand silhouette
                )

                // 4. Draw Reflection on the Wet Sand / Waves
                val waveColor = Color(0xFFFFF8E1).copy(alpha = 0.2f)
                val wavePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, height * 0.82f)
                    quadraticTo(
                        width * 0.4f, height * 0.80f,
                        width * 0.75f, height * 0.84f
                    )
                    quadraticTo(
                        width * 0.9f, height * 0.86f,
                        width, height * 0.80f
                    )
                    lineTo(width, height * 0.82f)
                    quadraticTo(
                        width * 0.88f, height * 0.88f,
                        width * 0.73f, height * 0.86f
                    )
                    quadraticTo(
                        width * 0.38f, height * 0.83f,
                        0f, height * 0.85f
                    )
                    close()
                }
                drawPath(path = wavePath, color = waveColor)

                // 5. Draw Victory Silhouette of the Man (centered at sunX)
                val personHeight = height * 0.38f
                val groundY = height * 0.78f
                val headY = groundY - personHeight
                val headRadius = personHeight * 0.12f
                val headCenter = Offset(sunX, headY + headRadius)

                // Draw Head
                drawCircle(
                    color = Color(0xFF160E22),
                    radius = headRadius,
                    center = headCenter
                )
                // Hoody extra outline
                drawCircle(
                    color = Color(0xFF160E22),
                    radius = headRadius * 1.2f,
                    center = headCenter,
                    style = Stroke(width = 3f)
                )

                // Torso
                val shoulderY = headCenter.y + headRadius * 1.2f
                val waistY = groundY - personHeight * 0.4f
                val torsoPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX - headRadius * 1.4f, shoulderY)
                    lineTo(sunX + headRadius * 1.4f, shoulderY)
                    lineTo(sunX + headRadius * 1.1f, waistY)
                    lineTo(sunX - headRadius * 1.1f, waistY)
                    close()
                }
                drawPath(path = torsoPath, color = Color(0xFF160E22))

                // Legs stand
                val legWidth = headRadius * 0.5f
                val leftFootX = sunX - headRadius * 0.9f
                val rightFootX = sunX + headRadius * 0.9f

                // Left leg
                val leftLegPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX - headRadius * 0.9f, waistY)
                    lineTo(sunX - headRadius * 0.2f, waistY)
                    lineTo(sunX - legWidth * 0.5f, groundY)
                    lineTo(leftFootX, groundY)
                    close()
                }
                drawPath(path = leftLegPath, color = Color(0xFF160E22))

                // Right leg
                val rightLegPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX + headRadius * 0.2f, waistY)
                    lineTo(sunX + headRadius * 0.9f, waistY)
                    lineTo(rightFootX, groundY)
                    lineTo(sunX + legWidth * 0.5f, groundY)
                    close()
                }
                drawPath(path = rightLegPath, color = Color(0xFF160E22))

                // Outstretched arms (Wide Open in T-pose representing victory)
                val leftArmPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX - headRadius * 1.2f, shoulderY + 2f)
                    val handX = sunX - personHeight * 0.55f
                    val handY = shoulderY - personHeight * 0.08f
                    lineTo(handX, handY)
                    lineTo(handX - 5f, handY - 12f)
                    lineTo(handX + 3f, handY + 15f)
                    lineTo(sunX - headRadius * 1.2f, shoulderY + headRadius * 1.3f)
                    close()
                }
                drawPath(path = leftArmPath, color = Color(0xFF160E22))

                val rightArmPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(sunX + headRadius * 1.2f, shoulderY + 2f)
                    val handX = sunX + personHeight * 0.55f
                    val handY = shoulderY - personHeight * 0.08f
                    lineTo(handX, handY)
                    lineTo(handX + 5f, handY - 12f)
                    lineTo(handX - 3f, handY + 15f)
                    lineTo(sunX + headRadius * 1.2f, shoulderY + headRadius * 1.3f)
                    close()
                }
                drawPath(path = rightArmPath, color = Color(0xFF160E22))
            }

            // Write "I AM a OverComer" across the banner with "Rev. 12:11" right under it
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "I AM a OverComer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(2f, 2f),
                            blurRadius = 6f
                        )
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rev. 12:11",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(1.5f, 1.5f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: OverComerViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(ActiveTab.FREEDOM) }
    val focusManager = LocalFocusManager.current
    var showPanicOverlay by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showApiSettingsDialog by remember { mutableStateOf(false) }

    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isFirebaseLive by viewModel.isFirebaseLive.collectAsStateWithLifecycle()
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()

    if (userPath.isNullOrEmpty()) {
        FocusSelectionScreen(onSelect = { viewModel.selectUserPath(it) })
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        TextButton(
                            onClick = { showPanicOverlay = true },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .testTag("panic_sos_top_left_btn"),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "🚨 SOS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "OverComer",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp, letterSpacing = 1.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    actions = {
                        AssistChip(
                            onClick = { viewModel.selectUserPath("") },
                            label = {
                                Text(
                                    text = when (userPath) {
                                        "SUBSTANCE_RECOVERY" -> "Recovery"
                                        "MENTAL_HEALTH" -> "Wellness"
                                        "TOUGH_DAY" -> "Tough Day"
                                        else -> "Select"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            modifier = Modifier.padding(end = 6.dp).testTag("change_focus_chip")
                        )
                        IconButton(
                            onClick = { showAuthDialog = true },
                            modifier = Modifier.testTag("auth_profile_button")
                        ) {
                            Box {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Account credentials and sync statistics",
                                    tint = if (isLoggedIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isLoggedIn) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = if (isFirebaseLive) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                                shape = CircleShape
                                            )
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { showApiSettingsDialog = true },
                            modifier = Modifier.testTag("api_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Secure AI API Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("app_top_bar")
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(86.dp).testTag("app_bottom_bar")
                ) {
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.FREEDOM,
                        onClick = { activeTab = ActiveTab.FREEDOM; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Freedom home") },
                        label = { Text("Freedom", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_freedom")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.INSPIRATIONAL,
                        onClick = { activeTab = ActiveTab.INSPIRATIONAL; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.FormatQuote, contentDescription = "Inspirational Quotes") },
                        label = { Text("Inspiration", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_inspirational")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.CHAT,
                        onClick = { activeTab = ActiveTab.CHAT; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.Send, contentDescription = "Overcomer’s Companion") },
                        label = { 
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Overcomer’s",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        lineHeight = 10.5.sp,
                                        fontWeight = if (activeTab == ActiveTab.CHAT) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = "Companion",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        lineHeight = 10.5.sp,
                                        fontWeight = if (activeTab == ActiveTab.CHAT) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        },
                        modifier = Modifier.testTag("nav_tab_chat")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.JOURNAL,
                        onClick = { activeTab = ActiveTab.JOURNAL; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.List, contentDescription = "Victory logs") },
                        label = { Text("Victory Logs", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_journal")
                    )
                    NavigationBarItem(
                        selected = activeTab == ActiveTab.BIBLE,
                        onClick = { activeTab = ActiveTab.BIBLE; focusManager.clearFocus() },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Bible App") },
                        label = { Text("Bible", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, softWrap = false) },
                        modifier = Modifier.testTag("nav_tab_bible")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (activeTab) {
                    ActiveTab.FREEDOM -> FreedomTabScreen(viewModel, onNavigateToChat = { activeTab = ActiveTab.CHAT })
                    ActiveTab.INSPIRATIONAL -> InspirationalQuotesTabScreen(viewModel)
                    ActiveTab.CHAT -> ChatTabScreen(viewModel)
                    ActiveTab.JOURNAL -> JournalLogsTabScreen(viewModel)
                    ActiveTab.BIBLE -> BibleTabScreen(viewModel)
                }

            if (showAuthDialog) {
                AuthSyncDialog(
                    viewModel = viewModel,
                    onDismissRequest = { showAuthDialog = false }
                )
            }

            if (showApiSettingsDialog) {
                SecureApiSettingsDialog(
                    viewModel = viewModel,
                    onDismissRequest = { showApiSettingsDialog = false }
                )
            }

            if (showPanicOverlay) {
                PanicOverlayDialog(onDismiss = { showPanicOverlay = false })
            }
        }
    }
}
}

// ==========================================
// PANELS/SCREEN IMPLEMENTATIONS
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FreedomTabScreen(
    viewModel: OverComerViewModel,
    onNavigateToChat: () -> Unit
) {
    val goal by viewModel.freedomGoal.collectAsStateWithLifecycle()
    val logs by viewModel.victoryLogs.collectAsStateWithLifecycle()
    val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()
    
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var showBreathingExercise by remember { mutableStateOf(false) }
    var showDbtSkillsLibrary by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val pathKey = remember(userPath) { userPath ?: "SUBSTANCE_RECOVERY" }
    val pathPrefs = remember(pathKey) {
        context.getSharedPreferences("overcomer_path_settings_v3", Context.MODE_PRIVATE)
    }

    val defaultStartDate = remember(pathKey) {
        System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 3)
    }
    val defaultStruggle = when (pathKey) {
        "MENTAL_HEALTH" -> "Anxiety & Depression"
        "TESTIMONY_VICTORY" -> "Victorious Breakthrough"
        else -> "Substance Use"
    }
    val defaultDeclaration = when (pathKey) {
        "MENTAL_HEALTH" -> "In Christ, My mind is filled with quietness and stability. I declare that worry has no dominion over my thoughts, and He keeps me in perfect peace."
        "TESTIMONY_VICTORY" -> "I am more than a conqueror through Him who loved me! Today, I walk in absolute victory, sharing my testimony and glorifying Christ!"
        else -> "An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory!"
    }
    val defaultMilestone = when (pathKey) {
        "MENTAL_HEALTH" -> "The date my mental health began to feel good"
        "TESTIMONY_VICTORY" -> "since I celebrated my victory testimony"
        else -> "since I OverCome addiction"
    }

    var savedStartDate by remember { mutableStateOf(pathPrefs.getLong("start_date_$pathKey", defaultStartDate)) }
    var savedStruggle by remember { mutableStateOf(pathPrefs.getString("struggle_$pathKey", defaultStruggle) ?: defaultStruggle) }
    var savedDeclaration by remember { mutableStateOf(pathPrefs.getString("declaration_$pathKey", defaultDeclaration) ?: defaultDeclaration) }
    var savedMilestone by remember {
        val raw = pathPrefs.getString("milestone_$pathKey", defaultMilestone) ?: defaultMilestone
        mutableStateOf(raw)
    }

    LaunchedEffect(pathKey, goal) {
        if (goal != null) {
            if (!pathPrefs.contains("start_date_$pathKey")) {
                pathPrefs.edit()
                    .putLong("start_date_$pathKey", goal!!.startDate)
                    .putString("struggle_$pathKey", goal!!.struggleType)
                    .putString("declaration_$pathKey", goal!!.customDeclaration)
                    .apply()
            }
        }
        savedStartDate = pathPrefs.getLong("start_date_$pathKey", goal?.startDate ?: defaultStartDate)
        savedStruggle = pathPrefs.getString("struggle_$pathKey", goal?.struggleType ?: defaultStruggle) ?: defaultStruggle
        savedDeclaration = pathPrefs.getString("declaration_$pathKey", goal?.customDeclaration ?: defaultDeclaration) ?: defaultDeclaration
        savedMilestone = pathPrefs.getString("milestone_$pathKey", defaultMilestone) ?: defaultMilestone
    }

    val daysCount = remember(savedStartDate) {
        val delta = System.currentTimeMillis() - savedStartDate
        if (delta <= 0) 0 else (delta / (1000 * 60 * 60 * 24)).toInt()
    }

    val headerText = when (userPath) {
        "SUBSTANCE_RECOVERY" -> "Remember: You are not defined by your struggle, but by His grace. You are a new creation in Christ. Cleanse your mind, breathe deep, and walk in absolute victory today."
        "MENTAL_HEALTH" -> "Remember: You are loved, cherished, and chosen by God. You are a new creation in Christ. Cleanse your mind, rest in His peace, and walk in emotional resilience today."
        "TOUGH_DAY" -> "Today might feel like an all round tough day, but God is your present help in times of trouble. Let His supernatural grace carry your load today."
        "TESTIMONY_VICTORY" -> "Today is a Testimony and Victory Day! Let's praise God for His absolute faithfulness, rejoice in His mercy, and walk in the fullness of His triumph today."
        else -> "Remember: You are not defined by your struggle, but by His grace. You are a new creation in Christ. Cleanse your mind, breathe deep, and walk in absolute victory today."
    }

    val greetingWord = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val greetingTitle = when (userPath) {
        "TOUGH_DAY" -> "Dear $currentUserName,"
        else -> "$greetingWord, $currentUserName"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppCoverBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        item {
            // Styled Greeting Card matching the HTML
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(24.dp), // rounded-3xl
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp) // p-6
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = greetingTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = headerText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            val verseState by viewModel.verseOfTheDay.collectAsStateWithLifecycle()
            val isVerseLoading by viewModel.isLoadingVerse.collectAsStateWithLifecycle()
            val context = LocalContext.current

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("verse_of_the_day_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row with Title and Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Scripture icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "VERSE OF THE DAY",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Copy Action
                            IconButton(
                                onClick = {
                                    verseState?.let { verse ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Verse of the Day", "\"${verse.text}\"\n— ${verse.reference}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Scripture copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(32.dp),
                                enabled = verseState != null && !isVerseLoading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Verse",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Refresh Action
                            IconButton(
                                onClick = { viewModel.fetchVerseOfTheDay(forceGenerate = true) },
                                modifier = Modifier.size(32.dp),
                                enabled = !isVerseLoading
                            ) {
                                if (isVerseLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Verse",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (isVerseLoading && verseState == null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Anointing you with daily grace...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        verseState?.let { verse ->
                            // The actual verse text
                            Text(
                                text = "\"${verse.text}\"",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )
                            
                            // Reference citation
                            Text(
                                text = "— ${verse.reference}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Devotional reflection container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "MENTAL RESILIENCE INSIGHT:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        text = verse.reflection,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (userPath != "TOUGH_DAY") {
            item {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Numeric count (25% larger than 58.sp, let's use 74.sp)
                        Text(
                            text = "$daysCount",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 74.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("days_counter_text")
                        )
                        
                        // Days label row with 25% larger text and the Edit button clearly visible
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (userPath == "MENTAL_HEALTH") {
                                    if (daysCount == 1) "DAY OF TRANQUILITY" else "DAYS OF PEACE"
                                } else {
                                    if (daysCount == 1) "DAY OF FREEDOM" else "DAYS OF VICTORY"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalIconButton(
                                onClick = { showEditGoalDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("edit_freedom_goal_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit settings",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        if (userPath == "MENTAL_HEALTH") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sovereign walk in $savedStruggle",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        val dateFormatted = remember(savedStartDate) {
                            val format = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
                            format.format(java.util.Date(savedStartDate))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = savedMilestone,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Started: $dateFormatted",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                val creedTitle = if (userPath == "MENTAL_HEALTH") "My Mental Peace Covenant" else "My OverComer Creed"
                val creedText = savedDeclaration

                // OverComer declaration card - Clean Minimal Secondary Container scheme
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "My Oath",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = creedTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"$creedText\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        item {
            BibleAffirmationsSection()
        }

        if (userPath == "TESTIMONY_VICTORY") {
            item {
                TestimonyVictoryBoard(viewModel = viewModel, onNavigateToChat = onNavigateToChat)
            }
        }

        if (userPath == "SUBSTANCE_RECOVERY") {
            item {
                RecoveryLessonsSection()
            }
        }

        item {
            SupportGroupLocatorSection(viewModel = viewModel)
        }

        item {
            PostIncarcerationSupportSection()
        }

        item {
            // Calm Grounding Quick Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (userPath != "TESTIMONY_VICTORY") {
                    Text(
                        text = "Distress Tolerance & Grounding",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Calm Breathing trigger block
                    Button(
                        onClick = { showBreathingExercise = !showBreathingExercise },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dbt_breathing_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showBreathingExercise) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (showBreathingExercise) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showBreathingExercise) "Close Calming Breathing" else "Open Calming Breathing Support",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Breathing Exercise UI
                    AnimatedVisibility(
                        visible = showBreathingExercise,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        PacedBreathingGuide()
                    }

                    // Calming Skills Library Toggle Button
                    Button(
                        onClick = { showDbtSkillsLibrary = !showDbtSkillsLibrary },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dbt_skills_library_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showDbtSkillsLibrary) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (showDbtSkillsLibrary) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showDbtSkillsLibrary) "Close Calming Skills Library" else "Open Calming Grounding Skills ✨",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Calming Skills Library UI Panel
                    AnimatedVisibility(
                        visible = showDbtSkillsLibrary,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        DbtSkillsLibrarySection()
                    }
                }

                // AI Counselor clean button layout matching the HTML
                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("ai_counselor_card"), // keep the same test tag to avoid breaking existing/future tests
                    shape = RoundedCornerShape(28.dp), // h-14 rounded-full
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Chat",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Talk to your Companion",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
                Text(
                    text = "AI-DRIVEN EMPATHETIC SUPPORT • ABSOLUTE PRIVACY GUARANTEED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                )

                if (userPath != "TESTIMONY_VICTORY") {
                    Spacer(modifier = Modifier.height(8.dp))
                    SupportConnectionSmsCard()
                }
            }
        }

        item {
            // "What to do when triggered" - Scriptures
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (userPath) {
                        "TESTIMONY_VICTORY" -> {
                            Text(
                                text = "🏆 Today is a Testimony & Victory Day! Celebrate:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. But thanks be to God! He gives us the victory through our Lord Jesus Christ. (1 Corinthians 15:57)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. They triumphed over him by the blood of the Lamb and by the word of their testimony. (Revelation 12:11)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. No, in all these things we are more than conquerors through Him who loved us. (Romans 8:37)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        "MENTAL_HEALTH" -> {
                            Text(
                                text = "🕊️ Rest in His Perfect Peace:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. You will keep in perfect peace those whose minds are steadfast, because they trust in you. (Isaiah 26:3)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. Do not be anxious about anything, but in every situation, make your requests known to God... and the peace of God will guard your hearts and minds. (Philippians 4:6-7)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. Peace I leave with you; my peace I give you. Do not let your hearts be troubled. (John 14:27)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        "TOUGH_DAY" -> {
                            Text(
                                text = "🌤️ Comfort for a Hard Day:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. Cast all your anxiety on Him because He cares for you with deepest affection. (1 Peter 5:7)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. Come to me, all you who are weary and burdened, and I will give you rest. (Matthew 11:28)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. The Lord is near to the brokenhearted and saves those who are crushed in spirit. (Psalm 34:18)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                        else -> {
                            Text(
                                text = "🔥 When Tempted or Triggered:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "1. Submit to God, Resist the devil, and he will flee from you! (James 4:7)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2. Look for the exit! No temptation has overtaken you except what is common... God will also provide a way out so that you can endure it. (1 Corinthians 10:13)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. Cast all anxiety! Remember, Christ suffered when tempted, and He is fully able to help those being tempted today. (Hebrews 2:18)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- Goal Edit Dialog ---
    if (showEditGoalDialog) {
        var tempStruggleType by remember { mutableStateOf(savedStruggle) }
        var tempDeclaration by remember { mutableStateOf(savedDeclaration) }

        val cal = remember {
            java.util.Calendar.getInstance().apply {
                timeInMillis = savedStartDate
            }
        }
        var tempYear by remember { mutableStateOf(cal.get(java.util.Calendar.YEAR).toString()) }
        var tempMonth by remember { mutableStateOf((cal.get(java.util.Calendar.MONTH) + 1).toString()) }
        var tempDay by remember { mutableStateOf(cal.get(java.util.Calendar.DAY_OF_MONTH).toString()) }

        val preselectedMilestones = if (userPath == "MENTAL_HEALTH") {
            listOf(
                "The date my mental health began to feel good",
                "The date I started having good days",
                "The date I stopped having anxiety/panic"
            )
        } else {
            listOf(
                "since I OverCome addiction",
                "The date I stopped using substances / drugs",
                "The date I started having good days",
                "The date my mental health was good"
            )
        }

        var selectedMilestoneChip by remember { 
            mutableStateOf(preselectedMilestones.find { it.trim().lowercase() == savedMilestone.trim().lowercase() } ?: preselectedMilestones.first()) 
        }
        var customMilestoneActive by remember { 
            mutableStateOf(preselectedMilestones.none { it.trim().lowercase() == savedMilestone.trim().lowercase() }) 
        }
        var tempMilestoneText by remember { 
            mutableStateOf(if (customMilestoneActive) savedMilestone else "") 
        }

        val isValidDate = remember(tempYear, tempMonth, tempDay) {
            val y = tempYear.toIntOrNull() ?: 0
            val m = tempMonth.toIntOrNull() ?: 0
            val d = tempDay.toIntOrNull() ?: 0
            y in 1900..2100 && m in 1..12 && d in 1..31
        }

        AlertDialog(
            onDismissRequest = { showEditGoalDialog = false },
            title = { Text(if (userPath == "MENTAL_HEALTH") "Walk of Peace Settings" else "Walk of Freedom Settings") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Configure your specific focus area, journey start date, and custom covenant declaration statement.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Struggle focus area
                    Text("My Focus Area:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    val struggleTypes = if (userPath == "MENTAL_HEALTH") {
                        listOf("Anxiety", "Depression", "Fear", "Anger", "Trauma / PTSD", "Mental Peace")
                    } else {
                        listOf("Substance Use", "Alcohol / Drinking", "Drugs", "Nicotine / Smoking", "Habitual Temptations")
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        struggleTypes.forEach { type ->
                            FilterChip(
                                selected = tempStruggleType == type,
                                onClick = { tempStruggleType = type },
                                label = { Text(type) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Date setup inputs
                    Text("Journey Start Date:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tempYear,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) tempYear = it },
                            label = { Text("Year") },
                            placeholder = { Text("YYYY") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.3f)
                        )
                        OutlinedTextField(
                            value = tempMonth,
                            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) tempMonth = it },
                            label = { Text("Month") },
                            placeholder = { Text("1-12") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = tempDay,
                            onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) tempDay = it },
                            label = { Text("Day") },
                            placeholder = { Text("1-31") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isValidDate) {
                        val previewCal = java.util.Calendar.getInstance()
                        previewCal.set(tempYear.toInt(), tempMonth.toInt() - 1, tempDay.toInt(), 0, 0, 0)
                        val previewDate = previewCal.time
                        val previewDaysStr = remember(previewCal.timeInMillis) {
                            val diff = System.currentTimeMillis() - previewCal.timeInMillis
                            if (diff <= 1) 0 else (diff / 86400000L).toInt()
                        }
                        val previewFormatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        Text(
                            text = "Starts: ${previewFormatter.format(previewDate)} ($previewDaysStr Victory days)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Please enter a valid start date (e.g., 2026, 06, 05)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Milestone Event Picker
                    Text("What describes this start date?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Column {
                        preselectedMilestones.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMilestoneChip = option
                                        customMilestoneActive = false
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = !customMilestoneActive && selectedMilestoneChip == option,
                                    onClick = {
                                        selectedMilestoneChip = option
                                        customMilestoneActive = false
                                    }
                                )
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { customMilestoneActive = true }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = customMilestoneActive,
                                onClick = { customMilestoneActive = true }
                            )
                            Text(
                                text = "Custom celebration milestone...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (customMilestoneActive) {
                        OutlinedTextField(
                            value = tempMilestoneText,
                            onValueChange = { tempMilestoneText = it },
                            label = { Text("Custom Milestone") },
                            placeholder = { Text("e.g. My first peaceful day, or Stopped drinking") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Custom declaration covenant statement
                    OutlinedTextField(
                        value = tempDeclaration,
                        onValueChange = { tempDeclaration = it },
                        label = { Text("Custom Covenant Declaration") },
                        placeholder = { Text("e.g. In Christ, I have been set free indeed!") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_freedom_declaration_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalMilestone = if (customMilestoneActive) tempMilestoneText.ifBlank { "My Victory Date" } else selectedMilestoneChip
                        val finalDate = if (isValidDate) {
                            val finalCal = java.util.Calendar.getInstance()
                            finalCal.set(tempYear.toInt(), tempMonth.toInt() - 1, tempDay.toInt(), 0, 0, 0)
                            finalCal.timeInMillis
                        } else {
                            savedStartDate
                        }

                        pathPrefs.edit()
                            .putLong("start_date_$pathKey", finalDate)
                            .putString("struggle_$pathKey", tempStruggleType)
                            .putString("declaration_$pathKey", tempDeclaration)
                            .putString("milestone_$pathKey", finalMilestone)
                            .apply()

                        savedStartDate = finalDate
                        savedStruggle = tempStruggleType
                        savedDeclaration = tempDeclaration
                        savedMilestone = finalMilestone

                        viewModel.updateFreedomGoal(
                            startDateMillis = finalDate,
                            struggleType = tempStruggleType,
                            customDeclaration = tempDeclaration
                        )
                        showEditGoalDialog = false
                    },
                    modifier = Modifier.testTag("save_freedom_settings_btn"),
                    enabled = isValidDate
                ) {
                    Text("Apply Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ------------------------------------------
// DBT PACED BREATHING COMPOSABLE
// ------------------------------------------

@Composable
fun PacedBreathingGuide() {
    var isBreathingActive by remember { mutableStateOf(false) }
    var scaleFraction by remember { mutableStateOf(1f) }
    var breathingPhase by remember { mutableStateOf("Ready") }
    var secondsRemaining by remember { mutableStateOf(4) }

    // Launch periodic breathing animation when active
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (isBreathingActive) {
                // Inhale Phase
                breathingPhase = "INHALE (Fill your soul with God's Spirit)"
                secondsRemaining = 4
                animateScale(target = 2f) { scaleFraction = it }
                for (i in 4 downTo 1) {
                    secondsRemaining = i
                    delay(1000)
                }
                
                if (!isBreathingActive) break
                
                // Exhale Phase
                breathingPhase = "EXHALE (Release all fears into Christ's hands)"
                secondsRemaining = 4
                animateScale(target = 1f) { scaleFraction = it }
                for (i in 4 downTo 1) {
                    secondsRemaining = i
                    delay(1000)
                }
            }
        } else {
            scaleFraction = 1f
            breathingPhase = "Ready"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DBT Distress Tolerance: Paced Breathing",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "During intense distress or cravings, taking paced deep breaths immediately down-regulates your body's survival hijack (midbrain amygdala reactivity). Use this pacing tool:",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            // Dynamic expanding circle representing breath
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        // Drawing circles based on scale fraction
                        val maxRadius = size.minDimension / 2f
                        drawCircle(
                            color = Color(0xFF14B8A6).copy(alpha = 0.3f),
                            radius = maxRadius * (scaleFraction / 2f)
                        )
                        drawCircle(
                            color = Color(0xFF14B8A6).copy(alpha = 0.1f),
                            radius = maxRadius
                        )
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBreathingActive) "$secondsRemaining" else "Ready",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = breathingPhase,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            // Activator button
            Button(
                onClick = { isBreathingActive = !isBreathingActive },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBreathingActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isBreathingActive) "Stop Tool" else "Start Breathing Loop",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

// --- Custom simple animator utility to run without complex Compose transition declarations
private suspend fun animateScale(target: Float, update: (Float) -> Unit) {
    val duration = 4000
    val start = if (target == 2f) 1f else 2f
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < duration) {
        val fraction = (System.currentTimeMillis() - startTime).toFloat() / duration
        val currentScale = start + (target - start) * fraction
        update(currentScale)
        delay(16) // ~60fps
    }
    update(target)
}

// ------------------------------------------
// DBT DISTRESS TOLERANCE SKILLS LIBRARY
// ------------------------------------------

data class DbtSkill(
    val name: String,
    val acronym: String,
    val motto: String,
    val description: String,
    val moods: List<String>,
    val intensity: String, // "Low", "Moderate", "Extreme"
    val steps: List<String>,
    val bibleVerse: String,
    val bibleRef: String,
    val practicePrompt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbtSkillsLibrarySection() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("ALL") }
    var selectedIntensity by remember { mutableStateOf("ALL") }
    
    // Remember expanded status of each skill by acronym
    var expandedSkillAcronym by remember { mutableStateOf<String?>(null) }
    
    val moods = listOf("ALL", "Overwhelmed", "Anxious", "Angry", "Urge", "Lonely", "Sad")
    val intensities = listOf("ALL", "Low", "Moderate", "Extreme")
    
    val skills = remember {
        listOf(
            DbtSkill(
                name = "STOP Skill",
                acronym = "STOP",
                motto = "Pause before you act to keep control.",
                description = "This key distress tolerance skill helps you pause rather than acting impulsively on overwhelming emotions or unhealthy urges.",
                moods = listOf("Overwhelmed", "Angry", "Urge"),
                intensity = "Moderate",
                steps = listOf(
                    "S - STOP: Freeze! Do not react. Impulsive feelings will try to dictate actions.",
                    "T - Take a Step Back: Pause, take a physical or mental step away, and take a deep breath.",
                    "O - Observe: Notice your physical sensations, thoughts, and environment objectively without judgment.",
                    "P - Proceed Mindfully: Ask yourself what the most effective, Christ-honoring thing to do is right now."
                ),
                bibleVerse = "My dear brothers and sisters, take note of this: Everyone should be quick to listen, slow to speak and slow to get angry.",
                bibleRef = "James 1:19 (NIV)",
                practicePrompt = "Close your eyes, take one slow breath, and think of your next small step."
            ),
            DbtSkill(
                name = "TIPP Crisis Management",
                acronym = "TIPP",
                motto = "Quickly alter your physical state to de-escalate crisis.",
                description = "Designed for extreme, high-stress moments (Level 3 Crisis) to immediately down-regulate your nervous system using rapid biological resets.",
                moods = listOf("Anxious", "Overwhelmed", "Urge"),
                intensity = "Extreme",
                steps = listOf(
                    "T - Temperature: Splash ice-cold water on your face or hold an ice cube to activate your body's calming reflex.",
                    "I - Intense Exercise: Do 60 seconds of high-intensity movement (jumping jacks, high knees) to burn off stress adrenaline.",
                    "P - Paced Breathing: Breathe slowly and deeply. Inhale into your belly for 4 seconds, exhale fully for 6 seconds.",
                    "P - Paired Muscle Relaxation: Tense a muscle group tightly for 5 seconds, then release and notice the release."
                ),
                bibleVerse = "He makes my feet like the feet of a deer; he causes me to stand on the heights. He trains my hands for battle...",
                bibleRef = "Psalm 18:33-34 (NIV)",
                practicePrompt = "Hold a cold object or do 10 jumping jacks now to reset your physiology!"
            ),
            DbtSkill(
                name = "IMPROVE the Moment",
                acronym = "IMPROVE",
                motto = "Shift your mental atmosphere during painful times.",
                description = "Helps you replace immediate painful emotions and situations with more positive, comforting, or stabilizing feelings.",
                moods = listOf("Anxious", "Lonely", "Sad"),
                intensity = "Moderate",
                steps = listOf(
                    "I - Imagery: Mentally visualize a safe, peaceful pasture under God's warm, protective sky.",
                    "M - Meaning: Find a small purpose or lessons in the current struggle.",
                    "P - Prayer: Cast this specific heavy load onto Jesus. Ask Him for His supernatural strength.",
                    "R - Relaxing: Stretch, drink some herbal tea, or listen to a soft acoustic worship melody.",
                    "O - One thing: Focus on only this exact moment. Don't worry about tonight or tomorrow.",
                    "V - Vacation: Take a brief 15-minute healthy break from your environment or phone.",
                    "E - Encouragement: Say a faith-filled affirmation aloud: 'I can do all things through Christ!'"
                ),
                bibleVerse = "Cast all your anxiety on him because he cares for you.",
                bibleRef = "1 Peter 5:7 (NIV)",
                practicePrompt = "Identify one thing in this current room and describe its texture to ground yourself."
            ),
            DbtSkill(
                name = "WISE ACCEPTS Distractions",
                acronym = "ACCEPTS",
                motto = "Temporarily divert your attention from painful distress.",
                description = "Provides a structured set of safe distractions to help emotional storms pass safely without engaging in destructive behaviors.",
                moods = listOf("Urge", "Anxious", "Lonely"),
                intensity = "Low",
                steps = listOf(
                    "Activities: Keep active. Clean your room, write a note, make a meal, or read an encouraging book.",
                    "Contributing: Shift focus outward by praying for a friend or doing a small act of kindness.",
                    "Comparisons: Look back at where you were months ago and thank God for the small graces.",
                    "Emotions (Opposite): Create a different feeling. Watch a wholesome comedy or listen to upbeat worship music.",
                    "Pushing away: Put the trigger out of sight. Put your phone away, lock the app, or walk outside.",
                    "Thoughts: Occupy your mind with a structured cognitive task like repeating an encouraging scripture verse.",
                    "Sensations: Engage your body's senses. Sip freezing-cold water, wrap yourself in a heavy blanket."
                ),
                bibleVerse = "Fix your thoughts on what is true, and honorable, and right, and pure, and lovely, and admirable...",
                bibleRef = "Philippians 4:8 (NLT)",
                practicePrompt = "Perform one random act of kindness or text a friend a word of encouragement right now."
            ),
            DbtSkill(
                name = "Self-Soothing Technique",
                acronym = "5-SENSES",
                motto = "Nurture your soul and body using God's goodness.",
                description = "Provides deep physical comfort and emotional reassurance by mindfully stimulating your primary five physiological senses.",
                moods = listOf("Lonely", "Anxious", "Sad"),
                intensity = "Low",
                steps = listOf(
                    "Sight: Walk outdoors and gaze at God's beautiful sky, the trees, or a calming scenery photo.",
                    "Sound: Turn on peaceful acoustic strings, classical melodies, or steady falling rain sounds.",
                    "Smell: Ignite an aromatic candle, diffuse lavender oil, or breathe in fresh coffee beans.",
                    "Taste: Savor a square of dark chocolate, drink cozy chamomile tea, or chew refreshing mint gum.",
                    "Touch: Put on exceptionally soft socks, cuddle your pet, or rub cool smooth stones."
                ),
                bibleVerse = "Taste and see that the Lord is good; blessed is the one who takes refuge in him.",
                bibleRef = "Psalm 34:8 (NIV)",
                practicePrompt = "Touch something nearby and slowly name 3 things you can see right now."
            ),
            DbtSkill(
                name = "REST Decision Maker",
                acronym = "REST",
                motto = "Pause, process, and proceed in soundness of mind.",
                description = "A comprehensive framework to make deliberate, value-based decisions when tempted or under stress.",
                moods = listOf("Overwhelmed", "Angry", "Urge"),
                intensity = "Moderate",
                steps = listOf(
                    "R - Relax: Pause and take 3 deep full breaths. Release the tension from your shoulders and jaw.",
                    "E - Evaluate: Ask yourself, 'Is there an immediate danger? What emotion or trigger is causing the urge?'",
                    "S - Set a Plan: Formulate a healthy, God-honoring choice (e.g. text a mentor, use breathing exercise).",
                    "T - Take Action: Step out in courage and carry out the chosen behavior, trusting God for the outcome."
                ),
                bibleVerse = "For God has not given us a spirit of fear, but of power and of love and of a sound mind.",
                bibleRef = "2 Timothy 1:7 (NKJV)",
                practicePrompt = "Take 3 deep, steady breaths, exhaling longer than you inhale."
            )
        )
    }
    
    // Filtering logic
    val filteredSkills = remember(skills, searchQuery, selectedMood, selectedIntensity) {
        skills.filter { skill ->
            val matchQuery = skill.name.contains(searchQuery, ignoreCase = true) ||
                             skill.acronym.contains(searchQuery, ignoreCase = true) ||
                             skill.description.contains(searchQuery, ignoreCase = true) ||
                             skill.motto.contains(searchQuery, ignoreCase = true)
            val matchMood = selectedMood == "ALL" || skill.moods.contains(selectedMood)
            val matchIntensity = selectedIntensity == "ALL" || skill.intensity.equals(selectedIntensity, ignoreCase = true)
            matchQuery && matchMood && matchIntensity
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dbt_skills_library_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Grounding Tools",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Calming Distress Grounding Library",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Interactive grounding skills to conquer urges & intense feelings",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search skills e.g., TIPP, STOP...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dbt_skills_search_input"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            
            // Mood Filter Chips Header
            Text(
                text = "FILTER BY MOOD / CURRENT STATE:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Mood Filters Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                moods.forEach { mood ->
                    val isSelected = selectedMood == mood
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMood = mood },
                        label = { Text(mood) },
                        modifier = Modifier.testTag("dbt_mood_filter_chip_$mood")
                    )
                }
            }
            
            // Intensity Filter Chips Header
            Text(
                text = "FILTER BY CHALLENGE INTENSITY:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            // Intensity Filters Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                intensities.forEach { intensity ->
                    val isSelected = selectedIntensity == intensity
                    val label = when (intensity) {
                        "Low" -> "🟢 Low (Level 1)"
                        "Moderate" -> "🟡 Moderate (Level 2)"
                        "Extreme" -> "🔴 Extreme Crisis (Level 3)"
                        else -> "All Levels"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedIntensity = intensity },
                        label = { Text(label) },
                        modifier = Modifier.testTag("dbt_intensity_filter_chip_$intensity")
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Skill List Output list
            if (filteredSkills.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No clinical grounding skills found",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Try adjusting your search terms, current mood filter, or challenge level.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredSkills.forEach { skill ->
                        val isExpanded = expandedSkillAcronym == skill.acronym
                        DbtSkillCard(
                            skill = skill,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedSkillAcronym = if (isExpanded) null else skill.acronym
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DbtSkillCard(
    skill: DbtSkill,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    // Keep local states for the steps checklist
    val checkedStates = remember(skill.acronym) { 
        mutableStateMapOf<Int, Boolean>().apply {
            skill.steps.indices.forEach { put(it, false) }
        }
    }
    
    // Let's remember user's brief reflection notes
    var userReflectionInput by remember(skill.acronym) { mutableStateOf("") }
    val context = LocalContext.current
    
    val allChecked = checkedStates.values.all { it }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dbt_skill_card_${skill.acronym}")
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Title, Acronym pill, Intensity indicator, Expand arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = skill.acronym,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        
                        val levelBadgeColor = when(skill.intensity) {
                            "Low" -> Color(0xFF10B981)
                            "Moderate" -> Color(0xFFF59E0B)
                            "Extreme" -> Color(0xFFEF4444)
                            else -> MaterialTheme.colorScheme.primary
                        }
                        Surface(
                            shape = CircleShape,
                            color = levelBadgeColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = skill.intensity,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = levelBadgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = skill.motto,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { onToggleExpand() }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Expandable Area
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    // Main description explanation
                    Text(
                        text = skill.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Interactive checklist header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PRACTICAL GROUNDING CHECKLIST:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Show completion badge if done
                        if (allChecked) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "GROUNDED!",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Checklist steps
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        skill.steps.forEachIndexed { index, step ->
                            val isChecked = checkedStates[index] ?: false
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                                        else Color.Transparent
                                    )
                                    .clickable { checkedStates[index] = !isChecked }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checkedStates[index] = it },
                                    modifier = Modifier.size(20.dp).testTag("step_checkbox_${skill.acronym}_$index"),
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.outline
                                    )
                                )
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                        fontStyle = if (isChecked) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                                    ),
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Sacred Biblical Anchor Block
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "SACRED BIBLICAL ANCHOR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "\"${skill.bibleVerse}\"",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "— ${skill.bibleRef}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // Action box: Practice notes and Submission button
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "PRACTICE & BRIEF REFLECTION NOTES:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = userReflectionInput,
                            onValueChange = { userReflectionInput = it },
                            placeholder = { Text(skill.practicePrompt) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp, max = 100.dp)
                                .testTag("practice_notes_input_${skill.acronym}"),
                            maxLines = 3,
                            singleLine = false,
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        Button(
                            onClick = {
                                val message = if (allChecked) {
                                    "Stupendous job completing all practical steps! You successfully deployed ${skill.acronym} to anchor your mind."
                                } else {
                                    "Excellent job practicing the ${skill.acronym} skill. Keep taking proactive steps towards grounding!"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                userReflectionInput = ""
                                skill.steps.indices.forEach { checkedStates[it] = false }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("practice_submit_${skill.acronym}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("Complete & Log Grounding Victory 🛡️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Small credit citation for public usage
                        Text(
                            text = "Note: Calming Grounding Steps are based on evidence-based distress tolerance exercises from Dialectical Behavior Therapy (DBT).",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------
// GEMINI LIVE CONVERSATIONAL CORE
// ------------------------------------------

enum class LiveModeState {
    INITIALIZING,
    LISTENING,
    THINKING,
    SPEAKING,
    PAUSED,
    ERROR
}

enum class CounselorVoice(
    val id: String,
    val displayName: String,
    val introEmoji: String,
    val pitch: Float,
    val rate: Float,
    val locale: java.util.Locale,
    val subtitle: String,
    val gender: String,
    val voiceKeywords: List<String>
) {
    CALMING_SAGE("calming_sage", "Calming Sage", "👴🏼", 1.00f, 0.92f, java.util.Locale.US, "Deep, slower, reassuring style", "Male", listOf("iod", "com", "scg", "masculine")),
    GENTLE_GUIDE("gentle_guide", "Gentle Guide", "👩🏻", 1.00f, 1.00f, java.util.Locale.US, "Soft, comforting, warm style", "Female", listOf("sfg", "ioe", "iog", "feminine")),
    VICTORIOUS_COACH("victorious_coach", "Victorious Coach", "🥊", 1.00f, 1.02f, java.util.Locale.US, "Inspiring, steady, coaching style", "Male", listOf("iom", "scg", "-guy-", "masculine")),
    GRACEFUL_BRITISH("graceful_british", "Graceful British", "🇬🇧", 1.00f, 1.00f, java.util.Locale.UK, "Peaceful, elegant UK style", "Female", listOf("fis", "female", "feminine", "-gb-")),
    ENCOURAGING_BROTHER("encouraging_brother", "Encouraging Brother", "🧔🏽", 1.00f, 0.98f, java.util.Locale.US, "Friendly, motivating brotherly style", "Male", listOf("iol", "com", "masculine")),
    SERENE_SISTER("serene_sister", "Serene Sister", "👱🏼‍♀️", 1.00f, 0.96f, java.util.Locale.US, "Peaceful, quiet, serene sisterly style", "Female", listOf("tpf", "lpf", "iog", "feminine"))
}

class LiveVoiceController(
    private val context: Context,
    private val onUserSpoken: (String) -> Unit,
    private val onStateChanged: (LiveModeState) -> Unit,
    private val onUserTextPartial: (String) -> Unit,
    private val onRmsChanged: (Float) -> Unit,
    private val onErrorMsg: (String) -> Unit
) {
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecognizerAvailable = SpeechRecognizer.isRecognitionAvailable(context)
    private val mainLooperHandler = Handler(Looper.getMainLooper())
    var isMuted = false
        private set

    var currentVoice: CounselorVoice = CounselorVoice.GENTLE_GUIDE
        set(value) {
            field = value
            applyVoiceSettings()
        }

    var isThinking = false
        set(value) {
            field = value
            if (value) {
                stopListening()
                mainLooperHandler.removeCallbacksAndMessages(null)
            }
        }

    private var isCurrentlyListening = false
    private var isTtsSpeaking = false
    private var consecutiveErrors = 0
    private val maxConsecutiveErrors = 3
    private var lastErrorTime = 0L

    // Intelligent speech accumulator to support seamless, pause-friendly hands-free speaking
    private val accumulatedSpeech = StringBuilder()
    private val finalizeSpeechRunnable = Runnable {
        finalizeAndSendSpeech()
    }

    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        
        // Timeout customization: Set BOTH Int and Long entries so that all device architectures
        // and Google App updates read them successfully without fallback cast errors.
        putExtra("android.speech.extra.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extra.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000L)
        putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 5000L)
        
        putExtra("android.speech.extra.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extra.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        
        putExtra("android.speech.extra.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extra.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000)
        putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 5000L)
        
        putExtra("android.speech.extra.DICTATION_MODE", true)
    }

    fun applyVoiceSettings() {
        tts?.let { engine ->
            try {
                val result = engine.setLanguage(currentVoice.locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(java.util.Locale.US)
                }
                engine.setPitch(currentVoice.pitch)
                engine.setSpeechRate(currentVoice.rate)

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val availableVoices = try { engine.voices } catch (t: Throwable) { null }
                    if (!availableVoices.isNullOrEmpty()) {
                        val isMaleTarget = currentVoice.gender == "Male"

                        // Filter voices by locale language (e.g. "en")
                        val langMatch = availableVoices.filter { voice ->
                            voice.locale.language.equals(currentVoice.locale.language, ignoreCase = true)
                        }

                        // Filter out uninstalled voices to prevent silent TTS failures on missing high-quality assets
                        val installedLangMatch = langMatch.filter { voice ->
                            val features = try { voice.features } catch (_: Throwable) { null }
                            features?.contains("notInstalled") != true
                        }.ifEmpty { langMatch }

                        // Score voices: strongly prioritize high-quality natural, neural, wavenet, premium, or hq voices
                        // This prevents utilizing default digitized-sounding synthesizer voices
                        val prioritizedLangMatch = installedLangMatch.sortedWith(compareByDescending { voice ->
                            val voiceName = voice.name.lowercase()
                            var score = 0
                            if (voiceName.contains("premium")) score += 80
                            if (voiceName.contains("natural")) score += 80
                            if (voiceName.contains("neural")) score += 80
                            if (voiceName.contains("wavenet")) score += 80
                            if (voiceName.contains("-x-")) score += 60
                            if (voiceName.contains("hq")) score += 40
                            if (voiceName.contains("network")) score += 50
                            
                            val quality = try { voice.quality } catch (_: Throwable) { 300 }
                            if (quality >= 400) { // QUALITY_HIGH or QUALITY_VERY_HIGH
                                score += 100
                            }
                            if (voice.isNetworkConnectionRequired) {
                                score += 30 // Strongly prefer network-based high-fidelity voices for ultra realism
                            }
                            score
                        })

                        // Strictly partition the voices so Male selections only use actual male voices,
                        // and Female selections only use actual female voices, preventing robotic gender leakages
                        val genderCorrectMatch = prioritizedLangMatch.filter { voice ->
                            val name = voice.name.lowercase()
                            
                            var reflectiveIsMale = false
                            var reflectiveIsFemale = false
                            try {
                                val voiceClass = Class.forName("android.speech.tts.Voice")
                                val getGenderMethod = voiceClass.getMethod("getGender")
                                val genderMaleVal = voiceClass.getField("GENDER_MALE").get(null) as Int
                                val genderFemaleVal = voiceClass.getField("GENDER_FEMALE").get(null) as Int
                                val gender = getGenderMethod.invoke(voice) as? Int
                                if (gender == genderMaleVal) {
                                    reflectiveIsMale = true
                                } else if (gender == genderFemaleVal) {
                                    reflectiveIsFemale = true
                                }
                            } catch (_: Throwable) {}
                            
                            val nameSaysMale = name.contains("male") || name.contains("masculine") || name.contains("-guy-") ||
                                    name.contains("iom") || name.contains("iod") || name.contains("iol") || name.contains("rgm") ||
                                    name.contains("rjs") || name.contains("scg") || name.contains("bdf") || name.contains("com") || name.contains("ctg")
                                    
                            val nameSaysFemale = name.contains("female") || name.contains("feminine") || name.contains("-girl-") ||
                                    name.contains("sfg") || name.contains("tpf") || name.contains("ioe") || name.contains("iog") ||
                                    name.contains("fis") || name.contains("gfm") || name.contains("jfm") || name.contains("lpf")
                            
                            if (isMaleTarget) {
                                (reflectiveIsMale || nameSaysMale) && !reflectiveIsFemale && !nameSaysFemale
                            } else {
                                (reflectiveIsFemale || nameSaysFemale) && !reflectiveIsMale && !nameSaysMale
                            }
                        }.ifEmpty {
                            // Tier 2 fallback: Filter solely by reflective gender from the OS without name keywords
                            prioritizedLangMatch.filter { voice ->
                                var reflectiveIsMale = false
                                var reflectiveIsFemale = false
                                try {
                                    val voiceClass = Class.forName("android.speech.tts.Voice")
                                    val getGenderMethod = voiceClass.getMethod("getGender")
                                    val genderMaleVal = voiceClass.getField("GENDER_MALE").get(null) as Int
                                    val genderFemaleVal = voiceClass.getField("GENDER_FEMALE").get(null) as Int
                                    val gender = getGenderMethod.invoke(voice) as? Int
                                    if (gender == genderMaleVal) reflectiveIsMale = true
                                    else if (gender == genderFemaleVal) reflectiveIsFemale = true
                                } catch (_: Throwable) {}
                                
                                if (isMaleTarget) reflectiveIsMale && !reflectiveIsFemale
                                else reflectiveIsFemale && !reflectiveIsMale
                            }
                        }.ifEmpty {
                            // Tier 3 fallback: Filter solely by name keywords
                            prioritizedLangMatch.filter { voice ->
                                val name = voice.name.lowercase()
                                val nameSaysMale = name.contains("male") || name.contains("masculine") || name.contains("-guy-") ||
                                        name.contains("iom") || name.contains("iod") || name.contains("iol") || name.contains("scg") || name.contains("com")
                                val nameSaysFemale = name.contains("female") || name.contains("feminine") || name.contains("-girl-") ||
                                        name.contains("sfg") || name.contains("tpf") || name.contains("ioe") || name.contains("iog")
                                        
                                if (isMaleTarget) nameSaysMale && !nameSaysFemale
                                else nameSaysFemale && !nameSaysMale
                            }
                        }.ifEmpty { prioritizedLangMatch }

                        var matchedVoice: android.speech.tts.Voice? = null

                        // 1. First, search for high-quality voices matching our specific voiceKeywords
                        for (keyword in currentVoice.voiceKeywords) {
                            matchedVoice = genderCorrectMatch.find { voice ->
                                val name = voice.name.lowercase()
                                name.contains(keyword) && (name.contains("natural") || name.contains("neural") || name.contains("-x-"))
                            }
                            if (matchedVoice != null) break
                        }

                        // 2. Fallback to any voice matching our specific voiceKeywords
                        if (matchedVoice == null) {
                            for (keyword in currentVoice.voiceKeywords) {
                                matchedVoice = genderCorrectMatch.find { voice ->
                                    voice.name.lowercase().contains(keyword)
                                }
                                if (matchedVoice != null) break
                            }
                        }

                        // 3. Fallback to any voice of that gender
                        if (matchedVoice == null) {
                            matchedVoice = genderCorrectMatch.firstOrNull()
                        }

                        if (matchedVoice != null) {
                            engine.setVoice(matchedVoice)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        val listener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                applyVoiceSettings()
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        isTtsSpeaking = true
                        onStateChanged(LiveModeState.SPEAKING)
                    }
                    override fun onDone(utteranceId: String?) {
                        isTtsSpeaking = false
                        mainLooperHandler.post {
                            if (!isMuted) {
                                onStateChanged(LiveModeState.LISTENING)
                                startListening()
                            }
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        isTtsSpeaking = false
                        mainLooperHandler.post {
                            if (!isMuted) {
                                onStateChanged(LiveModeState.LISTENING)
                                startListening()
                            }
                        }
                    }
                })
            } else {
                onErrorMsg("TextToSpeech initialization failed.")
            }
        }

        // Strongly prefer Google's high-fidelity natural Text-to-Speech voices
        tts = try {
            TextToSpeech(context, listener, "com.google.android.tts")
        } catch (_: Throwable) {
            TextToSpeech(context, listener)
        }

        if (isRecognizerAvailable) {
            setupRecognizer()
        }
    }

    private fun setupRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        if (!isMuted && !isTtsSpeaking) {
                            isCurrentlyListening = true
                            onStateChanged(LiveModeState.LISTENING)
                            this@LiveVoiceController.onRmsChanged(0f)
                        }
                    }
                    override fun onBeginningOfSpeech() {
                        if (!isMuted && !isTtsSpeaking) {
                            isCurrentlyListening = true
                            this@LiveVoiceController.onRmsChanged(0f)
                        }
                    }
                    override fun onRmsChanged(rmsdB: Float) {
                        if (!isMuted && !isTtsSpeaking) {
                            this@LiveVoiceController.onRmsChanged(rmsdB)
                        }
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    
                    override fun onError(error: Int) {
                        isCurrentlyListening = false
                        this@LiveVoiceController.onRmsChanged(0f)
                        
                        if (isMuted) return
                        if (isTtsSpeaking || isThinking) {
                            // Suppress errors caused by stopping/starting recognition while TTS is active or while thinking
                            return
                        }

                        val now = System.currentTimeMillis()
                        val isRapidError = (now - lastErrorTime) < 1200L
                        lastErrorTime = now

                        // Log error for debugging internally
                        android.util.Log.e("VoiceController", "Speech recognition error: $error (rapid: $isRapidError)")

                        // Handle permission error explicitly
                        if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                            onErrorMsg("Microphone permission denied.")
                            onStateChanged(LiveModeState.ERROR)
                            return
                        }

                        // Seamlessly restart for common speech timeouts and no-match pauses
                        // This prevents cutting the user off or frustratingly halting the continuous session
                        if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                            consecutiveErrors = 0
                            val retryInterval = 400L // Fast seamless restart
                            mainLooperHandler.removeCallbacksAndMessages(null)
                            mainLooperHandler.postDelayed({
                                if (!isMuted && !isTtsSpeaking && !isThinking) {
                                    startListening()
                                }
                            }, retryInterval)
                            return
                        }

                        // Solve immediate start lag/glitch by treating BUSY status with an immediate reset and fast restart
                        if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                            try { speechRecognizer?.cancel() } catch (_: Exception) {}
                            mainLooperHandler.removeCallbacksAndMessages(null)
                            mainLooperHandler.postDelayed({
                                if (!isMuted && !isTtsSpeaking && !isThinking) {
                                    startListening()
                                }
                            }, 100L) // Ultra fast 100ms retry for busy states
                            return
                        }

                        consecutiveErrors++
                        if (consecutiveErrors >= 6) { // Generous threshold to prevent minor mic/noise jitters from pausing
                            android.util.Log.e("VoiceController", "Repeated speech errors. Pausing auto-restart to prevent beeping/crash.")
                            onStateChanged(LiveModeState.PAUSED)
                            onErrorMsg("Session paused. Tap 'TAP TO TALK' below to resume speaking.")
                            consecutiveErrors = 0
                            return
                        }

                        val retryInterval = 1000L
                        mainLooperHandler.removeCallbacksAndMessages(null)
                        mainLooperHandler.postDelayed({
                            if (!isMuted && !isTtsSpeaking && !isThinking) {
                                startListening()
                            }
                        }, retryInterval)
                    }

                    override fun onResults(results: Bundle?) {
                        isCurrentlyListening = false
                        consecutiveErrors = 0
                        if (isTtsSpeaking) return
                        
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            appendSpeechChunk(text)
                        } else {
                            if (!isMuted && !isTtsSpeaking && !isThinking) startListening()
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        if (isTtsSpeaking) return
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            val displayText = if (accumulatedSpeech.isNotEmpty()) {
                                "$accumulatedSpeech $text"
                            } else {
                                text
                            }
                            onUserTextPartial(displayText)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isRecognizerAvailable = false
        }
    }

    private fun appendSpeechChunk(text: String) {
        mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
        if (accumulatedSpeech.isNotEmpty()) {
            accumulatedSpeech.append(" ")
        }
        accumulatedSpeech.append(text)
        
        onUserTextPartial(accumulatedSpeech.toString())
        
        // 1.8 seconds grace period of silence allows the user to pause, breathe, or pace without early cutoff
        val speakDelay = 1800L
        mainLooperHandler.postDelayed(finalizeSpeechRunnable, speakDelay)
        
        // Continuous listening: restart recognizer immediately to catch next phrase
        if (!isMuted && !isTtsSpeaking && !isThinking) {
            startListening()
        }
    }

    private fun finalizeAndSendSpeech() {
        val completedText = accumulatedSpeech.toString().trim()
        if (completedText.isNotEmpty()) {
            accumulatedSpeech.clear()
            isThinking = true
            onUserSpoken(completedText)
        }
    }

    fun startListening(force: Boolean = false) {
        if (force) {
            isMuted = false
            isTtsSpeaking = false
            isThinking = false
            isCurrentlyListening = false
            accumulatedSpeech.clear()
            mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
            try { tts?.stop() } catch (_: Exception) {}
        } else {
            if (isMuted) return
            if (isTtsSpeaking) return // Never listen / interrupt TTS during speaking unless explicitly requested
            if (isThinking) return // Never listen while AI is generating response
            if (isCurrentlyListening) {
                // Already listening, don't trigger startListening to avoid double start and beeping
                return
            }
        }

        if (isRecognizerAvailable) {
            mainLooperHandler.post {
                try {
                    // Only cancel if we are currently listening, avoiding disrupting first start
                    if (isCurrentlyListening) {
                        speechRecognizer?.cancel()
                    }
                    speechRecognizer?.startListening(recognizerIntent)
                    isCurrentlyListening = true
                    // Set to INITIALIZING first. Only transition to LISTENING when onReadyForSpeech callback is received from the OS.
                    onStateChanged(LiveModeState.INITIALIZING)
                } catch (e: Exception) {
                    e.printStackTrace()
                    isCurrentlyListening = false
                }
            }
        } else {
            onStateChanged(LiveModeState.ERROR)
            onErrorMsg("Voice Recognition is not supported on this device.")
        }
    }

    fun stopListening() {
        isCurrentlyListening = false
        if (isRecognizerAvailable) {
            mainLooperHandler.post {
                try {
                    speechRecognizer?.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun speak(text: String) {
        isTtsSpeaking = true
        isThinking = false
        stopListening()
        // Cancel any pending speech listening retries or finalization runnables immediately when we transition to speaking
        mainLooperHandler.removeCallbacksAndMessages(null)
        accumulatedSpeech.clear()
        
        mainLooperHandler.post {
            try {
                onStateChanged(LiveModeState.SPEAKING)
                applyVoiceSettings()
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "LiveReply")
                }
                val cleanedText = cleanTextForSpeech(text)
                tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, "LiveReply")
            } catch (e: Exception) {
                e.printStackTrace()
                isTtsSpeaking = false
            }
        }
    }

    fun stopSpeak() {
        isTtsSpeaking = false
        accumulatedSpeech.clear()
        mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
        mainLooperHandler.post {
            try {
                tts?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        if (isMuted) {
            isCurrentlyListening = false
            accumulatedSpeech.clear()
            mainLooperHandler.removeCallbacks(finalizeSpeechRunnable)
            stopListening()
            stopSpeak()
            onStateChanged(LiveModeState.PAUSED)
        } else {
            consecutiveErrors = 0
            startListening()
        }
        return isMuted
    }

    fun shutdown() {
        isCurrentlyListening = false
        isTtsSpeaking = false
        mainLooperHandler.removeCallbacksAndMessages(null)
        stopSpeak()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopListening()
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun LiveVoiceSessionDialog(
    viewModel: OverComerViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var liveState by remember { mutableStateOf(LiveModeState.INITIALIZING) }
    var rmsLevel by remember { mutableStateOf(0f) }
    var userSpeechText by remember { mutableStateOf("") }
    var aiSpeakingText by remember { mutableStateOf("Guide here. I am listening. Speak freely...") }
    var errorMsg by remember { mutableStateOf("") }
    var isMutedState by remember { mutableStateOf(false) }

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    // Keep instance reference of the controller
    var controller by remember { mutableStateOf<LiveVoiceController?>(null) }
    
    // Prevent duplicated speaking or race condition triggers
    var lastSpokenMessageText by remember {
        mutableStateOf(chatMessages.lastOrNull { !it.isUser }?.text ?: "")
    }

    // Synchronize response complete and reading state immediately when message arrives and controller is ready
    LaunchedEffect(chatMessages, controller) {
        val lastMsg = chatMessages.lastOrNull()
        if (lastMsg != null && !lastMsg.isUser) {
            if (lastMsg.text != lastSpokenMessageText) {
                lastSpokenMessageText = lastMsg.text
                aiSpeakingText = lastMsg.text
                liveState = LiveModeState.SPEAKING
                controller?.isThinking = false
                controller?.speak(lastMsg.text)
            }
        }
    }

    val sharedPrefs = remember { context.getSharedPreferences("overcomer_path_settings_v3", Context.MODE_PRIVATE) }

    // Initialize controller on mount, clean on unmount
    DisposableEffect(Unit) {
        val initialVoiceId = sharedPrefs.getString("counselor_selected_voice", CounselorVoice.GENTLE_GUIDE.id) ?: CounselorVoice.GENTLE_GUIDE.id
        val initialVoice = CounselorVoice.values().find { it.id == initialVoiceId } ?: CounselorVoice.GENTLE_GUIDE

        var activeController: LiveVoiceController? = null

        val voiceController = LiveVoiceController(
            context = context,
            onUserSpoken = { prompt ->
                if (!viewModel.isChatLoading.value && prompt.isNotBlank()) {
                    liveState = LiveModeState.THINKING
                    // Set thinking true to completely prevent listening or retries
                    try {
                        activeController?.isThinking = true
                    } catch (_: Exception) {}
                    viewModel.sendChatMessage(prompt)
                }
            },
            onStateChanged = { state ->
                // Keep paused in sync
                if (liveState != LiveModeState.THINKING || state == LiveModeState.SPEAKING) {
                    liveState = state
                }
            },
            onUserTextPartial = { partial ->
                userSpeechText = partial
            },
            onRmsChanged = { rms ->
                rmsLevel = rms
            },
            onErrorMsg = { err ->
                errorMsg = err
                liveState = LiveModeState.ERROR
            }
        ).apply {
            currentVoice = initialVoice
        }
        activeController = voiceController
        controller = voiceController

        // Gentle delay before starting to let context settle
        Handler(Looper.getMainLooper()).postDelayed({
            voiceController.startListening()
        }, 500)

        onDispose {
            voiceController.shutdown()
        }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141218)) // Matte Deep Charcoal background for studio vibe
                .testTag("gemini_live_dialog_overlay")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .safeDrawingPadding(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEMINI LIVE SESSION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .testTag("close_live_session_btn")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close session",
                            tint = Color.White
                        )
                    }
                }

                // Middle area: Transcripts & Visual Orbit
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Soft user text bubble
                    AnimatedVisibility(
                        visible = userSpeechText.isNotEmpty(),
                        enter = fadeIn() + expandVertically()
                    ) {
                        Text(
                            text = "\"$userSpeechText\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            ),
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    // THE BEAUTIFUL INTERACTIVE VOICE ORBIT
                    VoiceAnimationOrbit(liveState = liveState, rmsLevel = rmsLevel)

                    Spacer(modifier = Modifier.height(30.dp))

                    // Large Guide output bubble
                    VerticalScrollCard(
                        text = aiSpeakingText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(horizontal = 16.dp)
                    )
                }

                // Bottom Controls Trays
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var selectedVoice by remember {
                        mutableStateOf(
                            CounselorVoice.values().find { it.id == sharedPrefs.getString("counselor_selected_voice", CounselorVoice.GENTLE_GUIDE.id) }
                                ?: CounselorVoice.GENTLE_GUIDE
                        )
                    }

                    var selectedGender by remember {
                        mutableStateOf(sharedPrefs.getString("counselor_selected_gender", "ALL") ?: "ALL")
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "COMPANION VOICE: ${selectedVoice.displayName.uppercase()} (${selectedVoice.gender.uppercase()})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = Color.White.copy(alpha = 0.5f)
                        )

                        // Interactive Male / Female Options Filter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("ALL" to "👥 All", "MALE" to "🧔🏽 Male Only", "FEMALE" to "👩🏻 Female Only").forEach { (genderKey, label) ->
                                val isSelected = selectedGender == genderKey
                                val containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else Color.White.copy(alpha = 0.05f)
                                val contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondary else Color.White.copy(alpha = 0.7f)
                                val borderColor = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.12f)
                                
                                Row(
                                    modifier = Modifier
                                        .background(containerColor, RoundedCornerShape(12.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable {
                                            selectedGender = genderKey
                                            sharedPrefs.edit().putString("counselor_selected_gender", genderKey).apply()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("gender_filter_$genderKey"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = contentColor
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val filteredVoices = CounselorVoice.values().filter {
                                selectedGender == "ALL" || it.gender.uppercase() == selectedGender
                            }
                            filteredVoices.forEach { voice ->
                                val isSelected = voice == selectedVoice
                                val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f)
                                val contentColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
                                val borderColor = if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.15f)
                                
                                Column(
                                    modifier = Modifier
                                        .width(165.dp)
                                        .background(containerColor, RoundedCornerShape(16.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                        .clickable {
                                            selectedVoice = voice
                                            sharedPrefs.edit().putString("counselor_selected_voice", voice.id).apply()
                                            controller?.currentVoice = voice
                                            Toast.makeText(context, "${voice.displayName} selected!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .testTag("voice_chip_${voice.id}"),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(voice.introEmoji, fontSize = 16.sp)
                                        Text(
                                            text = voice.displayName,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = contentColor,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = voice.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp,
                                            textAlign = TextAlign.Center
                                        ),
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.5f),
                                        maxLines = 2,
                                        minLines = 2
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Current active guidance note
                    Text(
                        text = when (liveState) {
                            LiveModeState.INITIALIZING -> "CONNECTING TO COMPANION..."
                            LiveModeState.LISTENING -> "LISTENING... SPEAK FREELY"
                            LiveModeState.THINKING -> "COMPASSIONATE ALIGNING..."
                            LiveModeState.SPEAKING -> "TALKING TO YOU ENCOURAGINGLY"
                            LiveModeState.PAUSED -> "PAUSED"
                            LiveModeState.ERROR -> "CONNECTION HALTED"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = when (liveState) {
                            LiveModeState.LISTENING -> Color(0xFF66BB6A)
                            LiveModeState.THINKING -> MaterialTheme.colorScheme.primaryContainer
                            LiveModeState.SPEAKING -> MaterialTheme.colorScheme.primary
                            LiveModeState.ERROR -> MaterialTheme.colorScheme.error
                            else -> Color.White.copy(alpha = 0.5f)
                        }
                    )

                    if (errorMsg.isNotEmpty()) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💡 Companion Speaking Tips:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFA000)
                            )
                            Text(
                                text = "1. Wait for the 'LISTENING... SPEAK FREELY' status to ensure the microphone picks up your first words.\n" +
                                       "2. To make voices sound incredibly realistic instead of robotic, go to Android Settings -> Language & Input -> Text-to-Speech output, and choose 'Speech Services by Google' as your Preferred Engine.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute button
                        IconButton(
                            onClick = {
                                val muted = controller?.toggleMute() ?: false
                                isMutedState = muted
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (isMutedState) MaterialTheme.colorScheme.error else Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                                .testTag("mute_live_session_btn")
                        ) {
                            Icon(
                                imageVector = if (isMutedState) Icons.Default.PlayArrow else Icons.Default.Pause, // Toggle play pause mic
                                contentDescription = if (isMutedState) "Unmute Microphone" else "Mute Microphone",
                                tint = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))

                        // Touch to hold / Interrupt button
                        Button(
                            onClick = {
                                // Instantly force clean and start a new recognizer session
                                controller?.startListening(force = true)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = if (liveState == LiveModeState.SPEAKING) "INTERRUPT TO SPEAK" else "TAP TO TALK",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceAnimationOrbit(liveState: LiveModeState, rmsLevel: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    
    // Smooth infinite breathe scale
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Orbit speed rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing background halo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val scaleFactor = when (liveState) {
                        LiveModeState.LISTENING -> breatheScale + (rmsLevel.coerceIn(0f, 10f) / 10f)
                        LiveModeState.SPEAKING -> breatheScale + 0.08f
                        LiveModeState.THINKING -> 1.1f
                        else -> 1f
                    }
                    val haloColor = when (liveState) {
                        LiveModeState.LISTENING -> Color(0xFF14B8A6).copy(alpha = 0.15f) // Teal
                        LiveModeState.SPEAKING -> Color(0xFFD0BCFF).copy(alpha = 0.20f) // Soft purple
                        LiveModeState.THINKING -> Color(0xFFCCC2DC).copy(alpha = 0.10f) // Muted purple
                        LiveModeState.ERROR -> Color(0xFFEF4444).copy(alpha = 0.15f) // Red
                        else -> Color.White.copy(alpha = 0.05f)
                    }
                    drawCircle(
                        color = haloColor,
                        radius = size.minDimension / 2f * scaleFactor
                    )
                }
        )

        // Visual design: Render based on active voice state
        when (liveState) {
            LiveModeState.THINKING -> {
                // Circular loading ring spinning beautifully
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFFD0BCFF),
                                Color(0xFF6750A4),
                                Color(0xFF141218)
                            )
                        ),
                        startAngle = rotation,
                        sweepAngle = 280f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Thinking ring",
                    tint = Color(0xFFD0BCFF),
                    modifier = Modifier.size(48.dp)
                )
            }
            LiveModeState.SPEAKING -> {
                // 5 Premium Purple animated sound bars mimicking natural output speech waves
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val heights = listOf(0.4f, 0.9f, 0.6f, 0.85f, 0.5f)
                    val specDurations = listOf(700, 1100, 900, 1300, 800)
                    
                    heights.forEachIndexed { index, baseVal ->
                        val scaleHeight by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = baseVal,
                            animationSpec = infiniteRepeatable(
                                animation = tween(specDurations[index], easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "bar_$index"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .width(8.dp)
                                .fillMaxHeight(scaleHeight)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFFD0BCFF),
                                            Color(0xFF6750A4)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
            LiveModeState.LISTENING -> {
                // Interactive ripple wave that expands with actual mic RMS volume
                val micAura = (rmsLevel.coerceIn(0f, 12f) / 12f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Pulsing breathing circle
                    drawCircle(
                        color = Color(0xFF14B8A6), // Green teal
                        radius = (size.minDimension / 5f) * (breatheScale + micAura / 2f)
                    )
                    
                    // Ripple 1
                    drawCircle(
                        color = Color(0xFF14B8A6).copy(alpha = 0.3f),
                        radius = (size.minDimension / 3.2f) * (breatheScale + micAura),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Ripple 2 (only displays when talking)
                    if (micAura > 0.15f) {
                        drawCircle(
                            color = Color(0xFF14B8A6).copy(alpha = 0.15f),
                            radius = (size.minDimension / 2.2f) * (breatheScale + micAura * 1.5f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Listening",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            LiveModeState.PAUSED -> {
                // Static grey locked circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = size.minDimension / 4.5f
                    )
                }
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            else -> {
                // Warm, static circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(0xFFFFB59F).copy(alpha = 0.4f),
                        radius = size.minDimension / 4.5f
                    )
                }
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFFFB59F),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun VerticalScrollCard(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ------------------------------------------
// AI SUPPORT COUNSELOR CHAT
// ------------------------------------------

@Composable
fun ChatTabScreen(viewModel: OverComerViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val savedChats by viewModel.savedChats.collectAsStateWithLifecycle()
    
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var isLiveSessionOpen by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitleText by remember { mutableStateOf("") }
    var showSavedSessionsList by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isLiveSessionOpen = true
        } else {
            Toast.makeText(context, "Microphone permission is required for Gemini Live.", Toast.LENGTH_LONG).show()
        }
    }

    // Auto-scroll to the end of conversation whenever messages expand or typing status triggers
    LaunchedEffect(chatMessages.size, isChatLoading) {
        if (chatMessages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    // Show Gemini Live Dialog Session when active
    if (isLiveSessionOpen) {
        LiveVoiceSessionDialog(
            viewModel = viewModel,
            onDismiss = { isLiveSessionOpen = false }
        )
    }

    // Save current session dialog
    if (showSaveDialog) {
        val defaultName = "Support Session - " + java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Companion Session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Give this discussion a title. The Overcomer’s Companion recalls saved conversations to remind you of past victories, battles, and thoughts you overcame!",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = saveTitleText,
                        onValueChange = { saveTitleText = it },
                        placeholder = { Text(defaultName) },
                        label = { Text("Session Title") },
                        modifier = Modifier.fillMaxWidth().testTag("save_session_title_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTitle = saveTitleText.trim().ifBlank { defaultName }
                        viewModel.saveCurrentChat(finalTitle)
                        saveTitleText = ""
                        showSaveDialog = false
                        Toast.makeText(context, "Session saved and Companion updated of achievements!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Overcomer's Companion Header & Micro-copy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Overcomer’s Companion",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your AI Overcomer’s companion for biblical encouragement. For professional medical or clinical crisis support, please see our resource page.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
            )
        }

        // Safe privacy banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Lock icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Absolute Privacy. Chats are cached locally & never shared.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )
            
            // Save Session Button
            TextButton(
                onClick = { 
                    if (chatMessages.size <= 1) {
                        Toast.makeText(context, "Start chatting first before saving!", Toast.LENGTH_SHORT).show()
                    } else {
                        showSaveDialog = true 
                    }
                },
                modifier = Modifier.testTag("save_counselor_session_button")
            ) {
                Text(
                    "Save",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))

            TextButton(
                onClick = { viewModel.clearChatHistory() },
                modifier = Modifier.testTag("clear_chat_history_button")
            ) {
                Text(
                    "Clear",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Lazy scroll of bubbles
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // High-fidelity Hands-Free Voice card banner at the top of the chat area
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("gemini_live_callout_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎙️ Walk in Victory Hands-Free",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                              )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Talk live with OverComer Guide in real-time. The app listens, processes, and speaks back comfortingly with scriptural wisdom.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val permCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                if (permCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    isLiveSessionOpen = true
                                } else {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("start_live_voice_btn")
                        ) {
                            Text("Talk", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Saved Sessions History section inside Lazy list
            if (savedChats.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("saved_sessions_card_container"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = "Past Sessions Symbol",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Saved Sessions (${savedChats.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                TextButton(
                                    onClick = { showSavedSessionsList = !showSavedSessionsList },
                                    modifier = Modifier.testTag("toggle_saved_sessions_btn")
                                ) {
                                    Text(
                                        text = if (showSavedSessionsList) "Hide Sessions" else "View Past",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            if (showSavedSessionsList) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    savedChats.forEach { savedChat ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = savedChat.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(savedChat.timestamp))
                                                val pathLabel = savedChat.userPath.replace("_", " ").lowercase()
                                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
                                                Text(
                                                    text = "$dateStr • Focus: $pathLabel",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { 
                                                        viewModel.loadSavedChat(savedChat)
                                                        Toast.makeText(context, "Session Restored!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(36.dp).testTag("restore_session_${savedChat.id}")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Refresh,
                                                        contentDescription = "Restore Session",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { viewModel.deleteSavedChat(savedChat.id) },
                                                    modifier = Modifier.size(36.dp).testTag("delete_session_${savedChat.id}")
                                                ) {
                                                    Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = "Delete Session",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(chatMessages) { message ->
                ChatBubble(message = message)
            }

            if (isChatLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                "OverComer Guide is holding you in prayer and writing...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("chat_loading_indicator")
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Standard dynamic prompt helper chips
        val prompts = listOf(
            "I'm feeling triggered right now...",
            "What if I mess up?",
            "Can we do a CBT reframing?",
            "Give me a scripture for anxiety"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            prompts.forEach { prompt ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendChatMessage(prompt)
                    },
                    label = { Text(prompt, maxLines = 1, fontSize = 12.sp) },
                    modifier = Modifier.testTag("chip_prompt_${prompt.take(15).replace(" ", "_")}")
                )
            }
        }

        // Input bottom tray
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Talk through your struggle...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                leadingIcon = {
                    IconButton(
                        onClick = {
                            val permCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            if (permCheck == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                isLiveSessionOpen = true
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier.testTag("chat_mic_leading_icon")
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Talk with voice",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendChatMessage(inputText)
                            inputText = ""
                        }
                    }
                ),
                maxLines = 4,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            FloatingActionButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendChatMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("chat_send_button"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send text",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleBg = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val align = if (message.isUser) Alignment.End else Alignment.Start
    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleBg)
                .padding(14.dp)
                .widthIn(max = 280.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Header tag to make roles explicit
                Text(
                    text = if (message.isUser) "ME" else "OVERCOMER GUIDE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (message.isUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ------------------------------------------
// VICTORY LOGS & CBT RECORDS PANEL
// ------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JournalLogsTabScreen(viewModel: OverComerViewModel) {
    val logs by viewModel.victoryLogs.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingDistortion.collectAsStateWithLifecycle()
    val analysisResult by viewModel.distortionAnalysisResult.collectAsStateWithLifecycle()
    val goal by viewModel.freedomGoal.collectAsStateWithLifecycle()
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()

    val isOnlyJournal = userPath == "MENTAL_HEALTH" || userPath == "TOUGH_DAY"

    var showAddLogForm by remember { mutableStateOf(false) }
    var selectedLogType by remember { mutableStateOf("CBT") } // "REFLECT", "TRIGGER", "CBT"
    var logFilter by remember { mutableStateOf("ALL") } // "ALL", "REFLECT", "TRIGGER", "CBT"

    // Export/backup states
    var showExportDialog by remember { mutableStateOf(false) }
    var exportIncludePublic by remember { mutableStateOf(true) }
    var exportIncludePrivate by remember { mutableStateOf(false) }

    // Inputs
    var notesInput by remember { mutableStateOf("") }
    var triggerContextInput by remember { mutableStateOf("") }
    var autoThoughtInput by remember { mutableStateOf("") }
    var identifiedDistortionInput by remember { mutableStateOf("") }
    var reframedTruthInput by remember { mutableStateOf("") }
    var scriptureRefInput by remember { mutableStateOf("") }

    // Dual-panel state
    var subTabMode by remember(userPath) { mutableStateOf(if (isOnlyJournal) "SECURE_JOURNAL" else "PUBLIC_TRACKER") } // "PUBLIC_TRACKER", "SECURE_JOURNAL"

    // PIN lock security states
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("overcomer_journal_prefs", Context.MODE_PRIVATE) }
    var pinLockEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("pin_enabled", false)) }
    var savedPin by remember { mutableStateOf(sharedPrefs.getString("pin_value", "") ?: "") }
    var isJournalUnlocked by remember { mutableStateOf(false) }

    // Temp PIN inputs
    var pinSetupInput by remember { mutableStateOf("") }
    var pinUnlockInput by remember { mutableStateOf("") }
    var pinSetupError by remember { mutableStateOf("") }
    var pinUnlockError by remember { mutableStateOf("") }
    var showSetPinDialog by remember { mutableStateOf(false) }

    // Secure Journal Input
    var journalBodyInput by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, logFilter) {
        val publicLogs = logs.filter { it.type != "JOURNAL_SECURE" }
        if (logFilter == "ALL") publicLogs else publicLogs.filter { it.type == logFilter }
    }

    val secureJournalLogs = remember(logs) {
        logs.filter { it.type == "JOURNAL_SECURE" }
    }

    Scaffold(
        floatingActionButton = {
            if (subTabMode == "PUBLIC_TRACKER") {
                FloatingActionButton(
                    onClick = { showAddLogForm = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_log_record_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add victory record")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isOnlyJournal) {
                            if (userPath == "TOUGH_DAY") "My Private Journal" else "Locked Private Journal"
                        } else {
                            if (subTabMode == "PUBLIC_TRACKER") "My Victory Tracker" else "Locked Private Journal"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        // Export Action Card button in the header
                        TextButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier.testTag("export_trigger_btn"),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share, 
                                    contentDescription = "Export logs", 
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Export", 
                                    style = MaterialTheme.typography.labelMedium, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = if (subTabMode == "PUBLIC_TRACKER") "${filteredLogs.size} Logs" else "${secureJournalLogs.size} Entries",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isOnlyJournal) {
                        if (userPath == "TOUGH_DAY") {
                            "A private, safe space to write down your thoughts, release your cares, and let OverComer AI check for any cognitive distortions with supportive scripture reframing."
                        } else {
                            "A completely private, passcode-secured sanctuary to pour out your thoughts. Let OverComer AI check for cognitive distortions using CBT principles and scripture reframing."
                        }
                    } else {
                        if (subTabMode == "PUBLIC_TRACKER") {
                            "Tracking your struggles physically proves they are behavioral choices. Record automatic thoughts, defeat lies with biblically cognitive reframing, and log the victory."
                        } else {
                            "A completely private, passcode-secured sanctuary to pour out your thoughts. Let OverComer AI check for cognitive distortions using CBT principles and scripture reframing."
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                if (!isOnlyJournal) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Beautiful custom M3 styled Segment Control
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("PUBLIC_TRACKER" to "Public Step Tracker 🛡️", "SECURE_JOURNAL" to "🔐 Private AI Journal").forEach { (mode, label) ->
                            val isSelected = subTabMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { subTabMode = mode }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Body Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                if (subTabMode == "PUBLIC_TRACKER") {
                    // Current step tracker list
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Horizontal Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("ALL", "CBT", "TRIGGER", "REFLECT").forEach { filter ->
                                FilterChip(
                                    selected = logFilter == filter,
                                    onClick = { logFilter = filter },
                                    label = { Text(filter) },
                                    modifier = Modifier.testTag("log_filter_chip_$filter")
                                )
                            }
                        }

                        if (filteredLogs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.List,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Text(
                                        text = "No logs recorded for $logFilter",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = "Tap the '+' bubble to log your first step in victory.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredLogs) { log ->
                                    VictoryLogCard(log = log, onDelete = {
                                        viewModel.deleteVictoryLog(log.id)
                                    })
                                }
                            }
                        }
                    }
                } else {
                    // SECURE_JOURNAL mode!
                    if (pinLockEnabled && !isJournalUnlocked) {
                        // --- UNLOCK SCREEN PANEL ---
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 12.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Journal Locked",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Secure Journal Locked",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your personal thoughts & feelings are private. Enter your 4-digit passcode PIN to unlock them safely.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            OutlinedTextField(
                                value = pinUnlockInput,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                                        pinUnlockInput = newValue
                                        if (newValue == savedPin) {
                                            isJournalUnlocked = true
                                            pinUnlockError = ""
                                            pinUnlockInput = ""
                                        } else if (newValue.length == 4) {
                                            pinUnlockError = "Incorrect PIN code. Try again."
                                        } else {
                                            pinUnlockError = ""
                                        }
                                    }
                                },
                                label = { Text("4-Digit PIN") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .width(180.dp)
                                    .testTag("journal_pin_unlock_field"),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                            )

                            if (pinUnlockError.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pinUnlockError,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            TextButton(
                                onClick = {
                                    sharedPrefs.edit().clear().apply()
                                    pinLockEnabled = false
                                    savedPin = ""
                                    isJournalUnlocked = false
                                    Toast.makeText(context, "Secure passcode has been reset.", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Forgot PIN? Clear Passcode (Clears PIN Lock)")
                            }
                        }
                    } else {
                        // --- UNLOCKED / CONFIGURED JOURNAL BODY ---
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Section Header Configuration Panel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (pinLockEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = if (pinLockEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (pinLockEnabled) "Passcode Guard Enabled" else "Insecure (No Passcode)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (pinLockEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                    )
                                }
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Security Settings Trigger Button
                                    TextButton(
                                        onClick = {
                                            if (pinLockEnabled) {
                                                // Disable Lock
                                                sharedPrefs.edit()
                                                    .putBoolean("pin_enabled", false)
                                                    .putString("pin_value", "")
                                                    .apply()
                                                pinLockEnabled = false
                                                savedPin = ""
                                                isJournalUnlocked = false
                                                Toast.makeText(context, "Passcode lock disabled.", Toast.LENGTH_SHORT).show()
                                            } else {
                                                // Trigger Setup Dialog
                                                showSetPinDialog = true
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (pinLockEnabled) "Disable Guard ⚠️" else "Setup Lock PIN 🔒",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (pinLockEnabled) {
                                        // Lock immediately item
                                        IconButton(
                                            onClick = { isJournalUnlocked = false },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Lock,
                                                contentDescription = "Lock journal now",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Write new entry field
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "DOCUMENT YOUR RAW THOUGHTS & FEELINGS:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    OutlinedTextField(
                                        value = journalBodyInput,
                                        onValueChange = { journalBodyInput = it },
                                        placeholder = {
                                            Text(
                                                "Write what is on your mind today... e.g., 'I feel completely overwhelmed. I messed up at work and now I feel like I'm a failure and everyone is going to judge me.'"
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 100.dp, max = 150.dp)
                                            .testTag("secure_journal_thought_input"),
                                        maxLines = 8
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Standard Save
                                        Button(
                                            onClick = {
                                                if (journalBodyInput.isNotBlank()) {
                                                    viewModel.addVictoryLog(
                                                        type = "JOURNAL_SECURE",
                                                        notes = journalBodyInput
                                                    )
                                                    journalBodyInput = ""
                                                    Toast.makeText(context, "Journal entry saved securely! 📖", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = journalBodyInput.isNotBlank() && !isAnalyzing,
                                            modifier = Modifier.weight(1f).testTag("secure_journal_quick_save_btn"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        ) {
                                            Text("Save Quietly 📖", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Premium AI Analyze
                                        Button(
                                            onClick = {
                                                if (journalBodyInput.isNotBlank()) {
                                                    viewModel.analyzeJournalDistortion(journalBodyInput)
                                                }
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = journalBodyInput.isNotBlank() && !isAnalyzing,
                                            modifier = Modifier.weight(1.3f).testTag("secure_journal_analyze_btn"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text("Analyze Lie AI ✨", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Loading screen for AI scan
                            if (isAnalyzing) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = "Renewing mind alignment using cognitive mapping & biblical truths...",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            ),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            // Dynamic AI CBT analysis display container
                            analysisResult?.let { result ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "🎯 COGNITIVE THOUGHT RENEWAL ANALYSIS",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            IconButton(
                                                onClick = { viewModel.clearDistortionAnalysis() },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Clear analysis",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }

                                        // Detected Distortions Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Distortions: ",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                            ) {
                                                Text(
                                                    text = result.distortions,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        // Clinical advice explanations
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = "HOW THIS LIE TRICKS THE MIND:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                            Text(
                                                text = result.explanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Reframed truth
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                                .padding(10.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Text(
                                                    text = "✝ THE TRUTH (REFRAMED COGNITIVELY):",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "\"${result.reframedTruth}\"",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                
                                                if (result.scriptureReference.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = "⚓ Scripture: ${result.scriptureReference}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.secondary
                                                    )
                                                }
                                            }
                                        }

                                        // Save Reframe Button
                                        Button(
                                            onClick = {
                                                viewModel.addVictoryLog(
                                                    type = "JOURNAL_SECURE",
                                                    notes = journalBodyInput,
                                                    automaticThought = journalBodyInput,
                                                    identifiedDistortion = result.distortions,
                                                    reframedTruth = result.reframedTruth,
                                                    scriptureReference = result.scriptureReference
                                                )
                                                journalBodyInput = ""
                                                viewModel.clearDistortionAnalysis()
                                                Toast.makeText(context, "Mind renewal reframe archived securely! 🛡️📖", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Save & Archive Renewed Thought 🛡️", fontWeight = FontWeight.Bold)
                                        }

                                        // Small credit citation for public usage
                                        Text(
                                            text = "Note: Cognitive Reframing tool applies clinical principles of Cognitive Behavioral Therapy (CBT) integrated with biblical truths.",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Secure journal ledger list
                            Text(
                                text = "MY SECURE JOURNAL ENTRIES:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.outline
                            )

                            if (secureJournalLogs.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = "Your Secure Journal is completely empty",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = "Document your thoughts above to build a timeline of mental health and victory.",
                                            style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(horizontal = 24.dp)
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(secureJournalLogs) { entry ->
                                        SecureJournalEntryCard(
                                            entry = entry,
                                            onDelete = { viewModel.deleteVictoryLog(entry.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- SETUP PIN CODE DIALOG ---
    if (showSetPinDialog) {
        AlertDialog(
            onDismissRequest = { showSetPinDialog = false; pinSetupError = ""; pinSetupInput = "" },
            title = { Text("Setup Secure PIN Lock") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose a secure 4-digit passcode PIN. This PIN will be required to decrypt and view your secure journaling entries.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = pinSetupInput,
                        onValueChange = { newValue ->
                            if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                                pinSetupInput = newValue
                            }
                        },
                        label = { Text("4-Digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_pin_field"),
                        singleLine = true
                    )
                    if (pinSetupError.isNotEmpty()) {
                        Text(
                            text = pinSetupError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinSetupInput.length == 4) {
                            sharedPrefs.edit()
                                .putBoolean("pin_enabled", true)
                                .putString("pin_value", pinSetupInput)
                                .apply()
                            pinLockEnabled = true
                            savedPin = pinSetupInput
                            isJournalUnlocked = true
                            pinSetupInput = ""
                            pinSetupError = ""
                            showSetPinDialog = false
                            Toast.makeText(context, "Secure passcode activated! 🔐", Toast.LENGTH_SHORT).show()
                        } else {
                            pinSetupError = "PIN must be exactly 4 digits."
                        }
                    }
                ) {
                    Text("Save & Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetPinDialog = false; pinSetupError = ""; pinSetupInput = "" }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- Add Log Dialog ---
    if (showAddLogForm) {
        AlertDialog(
            onDismissRequest = { showAddLogForm = false },
            title = { Text("Log a Victory Milestone") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Type Selector Tabs
                    Text("Select Record Schema:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("CBT", "TRIGGER", "REFLECT").forEach { type ->
                            ElevatedFilterChip(
                                selected = selectedLogType == type,
                                onClick = { selectedLogType = type },
                                label = { Text(type) },
                                modifier = Modifier.testTag("form_log_type_$type")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when (selectedLogType) {
                        "CBT" -> {
                            Text(
                                "Cognitive Thought Record helps trace the lie. Reframing alignment captures the negative thoughts mid-flight.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            OutlinedTextField(
                                value = autoThoughtInput,
                                onValueChange = { autoThoughtInput = it },
                                label = { Text("Automatic Lie / Negative Thought") },
                                placeholder = { Text("e.g. \"I need this drink to clear my anxiety\"") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_thought")
                            )
                            OutlinedTextField(
                                value = identifiedDistortionInput,
                                onValueChange = { identifiedDistortionInput = it },
                                label = { Text("Cognitive Distortion Type") },
                                placeholder = { Text("e.g. Emotional Reasoning, Catastrophizing") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_distortion")
                            )
                            OutlinedTextField(
                                value = reframedTruthInput,
                                onValueChange = { reframedTruthInput = it },
                                label = { Text("Biblical Reframing & God's Truth") },
                                placeholder = { Text("e.g. \"God has not given me a spirit of fear, but of power!\"") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_truth")
                            )
                            OutlinedTextField(
                                value = scriptureRefInput,
                                onValueChange = { scriptureRefInput = it },
                                label = { Text("Scripture Reference Anchor") },
                                placeholder = { Text("e.g. 2 Timothy 1:7, John 8:36") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_scripture")
                            )
                        }
                        "TRIGGER" -> {
                            Text(
                                "Confronting triggers helps map social environments, physical exhaustion, or emotional triggers directly.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = triggerContextInput,
                                onValueChange = { triggerContextInput = it },
                                label = { Text("Describe the Trigger Event") },
                                placeholder = { Text("e.g. Had a fight with a colleague, entered stressful atmosphere.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_trigger_context")
                            )
                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Immediate Escape plan executed") },
                                placeholder = { Text("e.g. Stepped out of the office, called an accountability partner, prayed.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_notes_trigger")
                            )
                        }
                        "REFLECT" -> {
                            Text(
                                "Repentance, daily logs, and testimonies of God's limitless grace.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("What did God's grace show you today?") },
                                placeholder = { Text("He healed my heart. I confessed my slips (1 John 1:9), and stood firm.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_form_notes_reflect")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addVictoryLog(
                            type = selectedLogType,
                            notes = notesInput,
                            triggerContext = triggerContextInput,
                            automaticThought = autoThoughtInput,
                            identifiedDistortion = identifiedDistortionInput,
                            reframedTruth = reframedTruthInput,
                            scriptureReference = scriptureRefInput
                        )
                        // Clear buffers
                        notesInput = ""
                        triggerContextInput = ""
                        autoThoughtInput = ""
                        identifiedDistortionInput = ""
                        reframedTruthInput = ""
                        scriptureRefInput = ""
                        showAddLogForm = false
                    },
                    modifier = Modifier.testTag("dialog_submit_log_btn")
                ) {
                    Text("Save To Shield")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLogForm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- CLINICAL EXPORT & BACKUP DIALOG ---
    if (showExportDialog) {
        val isPrivateUnlocked = !pinLockEnabled || isJournalUnlocked
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Clinical Export & Backup 📋",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Share a readable formatting of your progress with your therapist, counseling practitioner, or keep offline as a backup.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "SELECT DATA TO INCLUDE:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Checkbox for Public Logs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { exportIncludePublic = !exportIncludePublic }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = exportIncludePublic,
                            onCheckedChange = { exportIncludePublic = it },
                            modifier = Modifier.testTag("export_include_public_checkbox")
                        )
                        Column {
                            Text(
                                text = "Public Step Tracker Logs",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "CBT Reframing, triggers, grace notes (${filteredLogs.size} logs)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Checkbox for Private Journal Logs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = isPrivateUnlocked) { exportIncludePrivate = !exportIncludePrivate }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = exportIncludePrivate && isPrivateUnlocked,
                            onCheckedChange = { exportIncludePrivate = it && isPrivateUnlocked },
                            enabled = isPrivateUnlocked,
                            modifier = Modifier.testTag("export_include_private_checkbox")
                        )
                        Column {
                            Text(
                                text = "🔐 Locked Private Journal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isPrivateUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                            Text(
                                text = if (isPrivateUnlocked) {
                                    "Deep secure emotional entries & AI CBT analyses (${secureJournalLogs.size} entries)"
                                } else {
                                    "⚠️ PASSCODE LOCKED. Unlock the private journal tab to include secure entries."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPrivateUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    if (!exportIncludePublic && (!exportIncludePrivate || !isPrivateUnlocked)) {
                        Text(
                            text = "Please select at least one data source to export.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                val isExportEnabled = exportIncludePublic || (exportIncludePrivate && isPrivateUnlocked)
                Button(
                    onClick = {
                        val buildExportString = buildString {
                            appendLine("==================================================")
                            appendLine("               OVERCOMER VICTORY REPORT           ")
                            appendLine("==================================================")
                            appendLine("Generated on: ${SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date())}")
                            
                            val g = goal
                            if (g != null) {
                                val diff = System.currentTimeMillis() - g.startDate
                                val daysCount = if (diff > 0) (diff / 86400000L).toInt() else 0
                                appendLine("Sovereign Target Struggle: ${g.struggleType}")
                                appendLine("Faith-based Covenant Declaration: \"${g.customDeclaration}\"")
                                appendLine("Days Walked in Absolute Freedom: $daysCount days")
                            }
                            appendLine("--------------------------------------------------")
                            appendLine()

                            if (exportIncludePublic) {
                                appendLine("--- PUBLIC STEP TRACKER LOGS ---")
                                appendLine("Total Count: ${logs.filter { it.type != "JOURNAL_SECURE" }.size}")
                                appendLine()
                                logs.filter { it.type != "JOURNAL_SECURE" }.sortedByDescending { it.timestamp }.forEach { log ->
                                    val logDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                                    appendLine("📅 EVENT DATE: $logDateStr")
                                    when (log.type) {
                                        "CBT" -> {
                                            appendLine("🏷️ TYPE: Cognitive CBT Restructuring")
                                            if (log.automaticThought.isNotBlank()) {
                                                appendLine("🧠 Automatic Thought: \"${log.automaticThought}\"")
                                            }
                                            if (log.identifiedDistortion.isNotBlank()) {
                                                appendLine("⚠️ Cognitive Distortion: ${log.identifiedDistortion}")
                                            }
                                            if (log.reframedTruth.isNotBlank()) {
                                                appendLine("✨ Reframed Covenant Truth: \"${log.reframedTruth}\"")
                                            }
                                            if (log.scriptureReference.isNotBlank()) {
                                                appendLine("⚓ Bible Anchor: ${log.scriptureReference}")
                                            }
                                            if (log.notes.isNotBlank() && log.notes != log.automaticThought) {
                                                appendLine("📝 Context Notes: ${log.notes}")
                                            }
                                        }
                                        "TRIGGER" -> {
                                            appendLine("🏷️ TYPE: Trigger Identification Tracker")
                                            if (log.triggerContext.isNotBlank()) {
                                                appendLine("💥 Trigger Scenario: ${log.triggerContext}")
                                            }
                                            if (log.notes.isNotBlank()) {
                                                appendLine("📝 Recovery Action Taken: ${log.notes}")
                                            }
                                        }
                                        "REFLECT" -> {
                                            appendLine("🏷️ TYPE: Self Reflection & Grace Note")
                                            if (log.notes.isNotBlank()) {
                                                appendLine("📝 Reflection Notes: ${log.notes}")
                                            }
                                        }
                                        else -> {
                                            appendLine("🏷️ TYPE: Grounding Event (${log.type})")
                                            if (log.notes.isNotBlank()) {
                                                appendLine("📝 Notes: ${log.notes}")
                                            }
                                        }
                                    }
                                    appendLine("--------------------------------------------------")
                                }
                                appendLine()
                            }

                            if (exportIncludePrivate && isPrivateUnlocked) {
                                appendLine("--- SECURE MIND JOURNAL ENTRIES ---")
                                appendLine("Total Count: ${secureJournalLogs.size}")
                                appendLine()
                                secureJournalLogs.sortedByDescending { it.timestamp }.forEach { entry ->
                                    val entryDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))
                                    appendLine("📅 ENTRY DATE: $entryDateStr")
                                    appendLine("📝 Thoughts: ${entry.notes}")
                                    if (entry.identifiedDistortion.isNotBlank() && entry.identifiedDistortion != "None") {
                                        appendLine("🧠 Detected Cognitive Distortion: ${entry.identifiedDistortion}")
                                        appendLine("✨ Covenant Truth Reframe: ${entry.reframedTruth}")
                                        if (entry.scriptureReference.isNotBlank()) {
                                            appendLine("⚓ Scripture Anchor: ${entry.scriptureReference}")
                                        }
                                    }
                                    appendLine("--------------------------------------------------")
                                }
                                appendLine()
                            }

                            appendLine("Report generated safely via OverComer.")
                            appendLine("Walk in absolute victory & sovereign light.")
                            appendLine("==================================================")
                        }

                        // Share via Android System Chooser Send Intent
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, buildExportString)
                            type = "text/plain"
                        }
                        try {
                            val shareIntent = Intent.createChooser(sendIntent, "Export OverComer clinical study")
                            context.startActivity(shareIntent)
                            showExportDialog = false
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to initialize standard share sheet: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = isExportEnabled,
                    modifier = Modifier.testTag("export_action_share_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Share / Export 📤")
                    }
                }
            },
            dismissButton = {
                // Copy to Clipboard option for offline backup
                val isExportEnabled = exportIncludePublic || (exportIncludePrivate && isPrivateUnlocked)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            if (isExportEnabled) {
                                val buildExportString = buildString {
                                    appendLine("==================================================")
                                    appendLine("               OVERCOMER VICTORY REPORT           ")
                                    appendLine("==================================================")
                                    appendLine("Generated on: ${SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date())}")
                                    
                                    val g = goal
                                    if (g != null) {
                                        val diff = System.currentTimeMillis() - g.startDate
                                        val daysCount = if (diff > 0) (diff / 86400000L).toInt() else 0
                                        appendLine("Sovereign Target Struggle: ${g.struggleType}")
                                        appendLine("Faith-based Covenant Declaration: \"${g.customDeclaration}\"")
                                        appendLine("Days Walked in Absolute Freedom: $daysCount days")
                                    }
                                    appendLine("--------------------------------------------------")
                                    appendLine()

                                    if (exportIncludePublic) {
                                        appendLine("--- PUBLIC STEP TRACKER LOGS ---")
                                        appendLine("Total Count: ${logs.filter { it.type != "JOURNAL_SECURE" }.size}")
                                        appendLine()
                                        logs.filter { it.type != "JOURNAL_SECURE" }.sortedByDescending { it.timestamp }.forEach { log ->
                                            val logDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                                            appendLine("📅 EVENT DATE: $logDateStr")
                                            when (log.type) {
                                                "CBT" -> {
                                                    appendLine("🏷️ TYPE: Cognitive CBT Restructuring")
                                                    if (log.automaticThought.isNotBlank()) {
                                                        appendLine("🧠 Automatic Thought: \"${log.automaticThought}\"")
                                                    }
                                                    if (log.identifiedDistortion.isNotBlank()) {
                                                        appendLine("⚠️ Cognitive Distortion: ${log.identifiedDistortion}")
                                                    }
                                                    if (log.reframedTruth.isNotBlank()) {
                                                        appendLine("✨ Reframed Covenant Truth: \"${log.reframedTruth}\"")
                                                    }
                                                    if (log.scriptureReference.isNotBlank()) {
                                                        appendLine("⚓ Bible Anchor: ${log.scriptureReference}")
                                                    }
                                                    if (log.notes.isNotBlank() && log.notes != log.automaticThought) {
                                                        appendLine("📝 Context Notes: ${log.notes}")
                                                    }
                                                }
                                                "TRIGGER" -> {
                                                    appendLine("🏷️ TYPE: Trigger Identification Tracker")
                                                    if (log.triggerContext.isNotBlank()) {
                                                        appendLine("💥 Trigger Scenario: ${log.triggerContext}")
                                                    }
                                                    if (log.notes.isNotBlank()) {
                                                        appendLine("📝 Recovery Action Taken: ${log.notes}")
                                                    }
                                                }
                                                "REFLECT" -> {
                                                    appendLine("🏷️ TYPE: Self Reflection & Grace Note")
                                                    if (log.notes.isNotBlank()) {
                                                        appendLine("📝 Reflection Notes: ${log.notes}")
                                                    }
                                                }
                                                else -> {
                                                    appendLine("🏷️ TYPE: Grounding Event (${log.type})")
                                                    if (log.notes.isNotBlank()) {
                                                        appendLine("📝 Notes: ${log.notes}")
                                                    }
                                                }
                                            }
                                            appendLine("--------------------------------------------------")
                                        }
                                        appendLine()
                                    }

                                    if (exportIncludePrivate && isPrivateUnlocked) {
                                        appendLine("--- SECURE MIND JOURNAL ENTRIES ---")
                                        appendLine("Total Count: ${secureJournalLogs.size}")
                                        appendLine()
                                        secureJournalLogs.sortedByDescending { it.timestamp }.forEach { entry ->
                                            val entryDateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(entry.timestamp))
                                            appendLine("📅 ENTRY DATE: $entryDateStr")
                                            appendLine("📝 Thoughts: ${entry.notes}")
                                            if (entry.identifiedDistortion.isNotBlank() && entry.identifiedDistortion != "None") {
                                                appendLine("🧠 Detected Cognitive Distortion: ${entry.identifiedDistortion}")
                                                appendLine("✨ Covenant Truth Reframe: ${entry.reframedTruth}")
                                                if (entry.scriptureReference.isNotBlank()) {
                                                    appendLine("⚓ Scripture Anchor: ${entry.scriptureReference}")
                                                }
                                            }
                                            appendLine("--------------------------------------------------")
                                        }
                                        appendLine()
                                    }

                                    appendLine("Report generated safely via OverComer.")
                                    appendLine("Walk in absolute victory & sovereign light.")
                                    appendLine("==================================================")
                                }

                                try {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("OverComer Journal Report", buildExportString)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Journal report copied to clipboard! 📋 Successfully backed up.", Toast.LENGTH_SHORT).show()
                                    showExportDialog = false
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Clipboard copy failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = isExportEnabled,
                        modifier = Modifier.testTag("export_action_copy_btn")
                    ) {
                        Text("Copy to Clipboard")
                    }
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Close", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}

@Composable
fun SecureJournalEntryCard(entry: VictoryLog, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val dateStr = remember(entry.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(entry.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("secure_journal_card_${entry.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Encrypted Mind Journal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Path badge helper for merged journal entries
            val pathInfo = remember(entry.userPath) {
                when (entry.userPath) {
                    "TOUGH_DAY" -> Triple("All-Around Tough Day", Icons.Default.Cloud, Color(0xFFD32F2F))
                    "SUBSTANCE_RECOVERY" -> Triple("Substance Recovery", Icons.Default.Shield, Color(0xFF1976D2))
                    "MENTAL_HEALTH" -> Triple("Mental Health Wellness", Icons.Default.FavoriteBorder, Color(0xFF00796B))
                    "TESTIMONY_VICTORY" -> Triple("Today is a Testimony Day", Icons.Default.Star, Color(0xFFFFA000))
                    else -> null
                }
            }

            pathInfo?.let { (name, icon, color) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.08f),
                    border = BorderStroke(0.5.dp, color.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = color
                        )
                    }
                }
            }

            // Note context snippet / full
            Text(
                text = entry.notes,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
            )

            // If there's AI CBT analysis on this secure log, display it below
            if (entry.identifiedDistortion.isNotBlank() && entry.identifiedDistortion != "None") {
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "AI CBT: ${entry.identifiedDistortion}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Expanded detail section
            if (expanded) {
                // Showing fully if CBT was processed on this
                if (entry.identifiedDistortion.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        if (entry.automaticThought.isNotBlank()) {
                            Text(
                                text = "Analyzed Thought:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "\"${entry.automaticThought}\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        if (entry.reframedTruth.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "✝ Biblical CBT Reframe:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = entry.reframedTruth,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    
                                    if (entry.scriptureReference.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "⚓ Anchor Scripture: ${entry.scriptureReference}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Delete entry button row
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete journal entry",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "Tap to expand details and CBT Reframing...",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun VictoryLogCard(log: VictoryLog, onDelete: () -> Unit) {
    val dateStr = remember(log.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        sdf.format(Date(log.timestamp))
    }

    val typeColor = when (log.type) {
        "CBT" -> MaterialTheme.colorScheme.secondary
        "TRIGGER" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_card_${log.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, typeColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header tag row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = typeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = log.type,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = typeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (log.type) {
                "CBT" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Automatic Thought (The Lie):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "\"${log.automaticThought}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Text(
                            text = "God's Truth (The Shield Reframe):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "\"${log.reframedTruth}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (log.scriptureReference.isNotBlank()) {
                            Text(
                                text = "⚓ Anchor Scribe: ${log.scriptureReference}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                "TRIGGER" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Trigger Event Context:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = log.triggerContext,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Text(
                            text = "Escape & Grounding Plan Executed:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = log.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                else -> { // REFLECT
                    Text(
                        text = log.notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("delete_log_btn_${log.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete log entry",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ------------------------------------------
// LOCAL SUPPORT & CORE BELIEFS PANEL
// ------------------------------------------

data class InspirationalQuote(
    val id: Int,
    val text: String,
    val reference: String,
    val contextReflection: String,
    val category: String
)

val inspirationalQuotes = listOf(
    InspirationalQuote(
        id = 1,
        text = "So if the Son sets you free, you will be free indeed.",
        reference = "John 8:36",
        contextReflection = "True freedom isn't about trial and error; it is an identity. In Christ, you are declared free from past bondages.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 2,
        text = "No temptation has overtaken you except what is common to mankind. And God is faithful; he will not let you be tempted beyond what you can bear.",
        reference = "1 Corinthians 10:13",
        contextReflection = "Every urge has an expiration date. God is providing a way out right now—take a slow breath and step forward.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 3,
        text = "Submit yourselves, then, to God. Resist the devil, and he will flee from you.",
        reference = "James 4:7",
        contextReflection = "You are not powerless. Submit your current urge to God, stand in His authority, and the temptation must yield.",
        category = "Overcoming Cravings"
    ),
    InspirationalQuote(
        id = 4,
        text = "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
        reference = "Philippians 4:6-7",
        contextReflection = "Anxiety is a warning light, not your master. Exchange your worries for the peace of God that guards your mind.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 5,
        text = "For God has not given us a spirit of fear, but of power and of love and of a sound mind.",
        reference = "2 Timothy 1:7",
        contextReflection = "Anxiety is not from your Father. He has supplied you with power to stand, love to comfort, and a calm, quiet mind.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 6,
        text = "Peace I leave with you; my peace I give you. I do not give to you as the world gives. Do not let your hearts be troubled and do not be afraid.",
        reference = "John 14:27",
        contextReflection = "Jesus doesn't give transactional peace that depends on circumstances. He gives structural, spiritual peace. Rest in it.",
        category = "Peace & Anxiety"
    ),
    InspirationalQuote(
        id = 7,
        text = "I can do all things through Christ who strengthens me.",
        reference = "Philippians 4:13",
        contextReflection = "This is not about self-sufficiency, but Christ-sufficiency. His resurrection power resides in you for today's tasks.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 8,
        text = "The Lord is my strength and my shield; my heart trusts in him, and he helps me.",
        reference = "Psalm 28:7",
        contextReflection = "God is active in your defense. Trust Him to shield your weak areas when you feel vulnerable or tired.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 9,
        text = "But those who hope in the Lord will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint.",
        reference = "Isaiah 40:31",
        contextReflection = "Power is gifted to those who wait in His presence. If you feel empty, take a silent moment to renew your hope in Him.",
        category = "Strength & Faith"
    ),
    InspirationalQuote(
        id = 10,
        text = "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here!",
        reference = "2 Corinthians 5:17",
        contextReflection = "Your past failures are dead. You do not have to carry the ghost of past mistakes into today.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 11,
        text = "If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness.",
        reference = "1 John 1:9",
        contextReflection = "A struggle doesn't cancel His love. Grace is immediately active the moment we step back into His presence.",
        category = "Grace & Forgiveness"
    ),
    InspirationalQuote(
        id = 12,
        text = "But he said to me, 'My grace is sufficient for you, for my power is made perfect in weakness.'",
        reference = "2 Corinthians 12:9",
        contextReflection = "Weakness is not a disqualification; it is a canvas for His strength. Admit your struggle and let His grace power you through.",
        category = "Grace & Forgiveness"
    )
)

enum class BibleSubTab {
    READER,
    AI_SEARCH,
    CREED
}

data class BiblePassage(
    val book: String,
    val chapter: String,
    val verses: List<Pair<Int, String>>,
    val theme: String,
    val context: String
)

val curatedPassages = listOf(
    BiblePassage(
        book = "Psalms",
        chapter = "23",
        verses = listOf(
            1 to "The Lord is my shepherd, I lack nothing.",
            2 to "He makes me lie down in green pastures, he leads me beside quiet waters,",
            3 to "he refreshes my soul. He guides me along the right paths for his name’s sake.",
            4 to "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.",
            5 to "You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows.",
            6 to "Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the Lord forever."
        ),
        theme = "Comfort & Peace",
        context = "When walking through the dark valleys of fear, isolation, or heavy temptations, this psalm anchors us in God's protective presence."
    ),
    BiblePassage(
        book = "Philippians",
        chapter = "4",
        verses = listOf(
            4 to "Rejoice in the Lord always. I will say it again: Rejoice!",
            5 to "Let your gentleness be evident to all. The Lord is near.",
            6 to "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
            7 to "And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
            8 to "Finally, brothers and sisters, whatever is true, whatever is noble, whatever is right, whatever is pure, whatever is lovely, whatever is admirable—if anything is excellent or praiseworthy—think about such things.",
            9 to "Whatever you have learned or received or heard from me, or seen in me—put it into practice. And the God of peace will be with you.",
            13 to "I can do all things through Christ who strengthens me."
        ),
        theme = "Anxiety & Resilience",
        context = "The ultimate cognitive reframing guide. It directs us to cast anxieties on God, focus deliberately on wholesome thoughts, and draw strength from Christ."
    ),
    BiblePassage(
        book = "John",
        chapter = "3",
        verses = listOf(
            16 to "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.",
            17 to "For God did not send his Son into the world to condemn the world, but to save the world through him."
        ),
        theme = "Unconditional Love",
        context = "Dethrones guilt and shame. Christ's primary mission is rescue and deliverance, not condemnation. Surrender brings instant security."
    ),
    BiblePassage(
        book = "Romans",
        chapter = "8",
        verses = listOf(
            1 to "Therefore, there is now no condemnation for those who are in Christ Jesus,",
            2 to "because through Christ Jesus the law of the Spirit who gives life has set you free from the law of sin and death.",
            31 to "What, then, shall we say in response to these things? If God is for us, who can be against us?",
            35 to "Who shall separate us from the love of Christ? Shall trouble or hardship or persecution or famine or nakedness or danger or sword?",
            37 to "No, in all these things we are more than conquerors through him who loved us.",
            38 to "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers,",
            39 to "neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord."
        ),
        theme = "Sovereign Victory",
        context = "Establishes our permanent, unshakable status in Christ. We do not fight FOR victory; we stand in the finished victory of the cross."
    ),
    BiblePassage(
        book = "James",
        chapter = "1",
        verses = listOf(
            2 to "Consider it pure joy, my brothers and sisters, whenever you face trials of many kinds,",
            3 to "because you know that the testing of your faith produces perseverance.",
            4 to "Let perseverance finish its work so that you may be mature and complete, not lacking anything.",
            12 to "Blessed is the one who perseveres under trial because, having stood the test, that person will receive the crown of life that the Lord has promised to those who love him."
        ),
        theme = "Trials, Faith & Endurance",
        context = "Encourages us through trials. Temptations and cravings are testing grounds where our character is strengthened by God's grace."
    ),
    BiblePassage(
        book = "Psalms",
        chapter = "91",
        verses = listOf(
            1 to "Whoever dwells in the shelter of the Most High will rest in the shadow of the Almighty.",
            2 to "I will say of the Lord, 'He is my refuge and my fortress, my God, in whom I trust.'",
            3 to "Surely he will save you from the fowler's snare and from the deadly pestilence.",
            4 to "He will cover you with his feathers, and under his wings you will find refuge; his faithfulness will be your shield and rampart."
        ),
        theme = "Protection & Safety",
        context = "A powerful prayer of physical and mental safety. It reminds us of angelic protection and shields our minds from dread."
    ),
    BiblePassage(
        book = "James",
        chapter = "3",
        verses = listOf(
            2 to "We all stumble in many ways. Anyone who is never at fault in what they say is perfect, able to keep their whole body in check.",
            5 to "Likewise, the tongue is a small part of the body, but it makes great boasts. Consider what a great forest is set on fire by a small spark."
        ),
        theme = "Self-Control & Intent",
        context = "Gives practical wisdom on managing words, speech, and impulses to walk uprightly and avoid self-destructive behavior."
    )
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BibleTabScreen(viewModel: OverComerViewModel) {
    var activeSubTab by remember { mutableStateOf(BibleSubTab.READER) }
    var selectedPassageIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    val aiResult by viewModel.aiScriptureResult.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearchingScripture.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // App Header Banner
        AppCoverBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // Tab row header title
        Text(
            text = "📖 Holy Scripture Study",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        
        // Custom segmented selector
        BibleSubTabSelector(
            activeSubTab = activeSubTab,
            onSubTabSelected = { tab ->
                activeSubTab = tab
            }
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Tab contents
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSubTab) {
                BibleSubTab.READER -> {
                    val activePassage = curatedPassages[selectedPassageIndex]
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "Select Curated Healing Scriptures:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(curatedPassages.size) { index ->
                                    val passage = curatedPassages[index]
                                    val isSelected = selectedPassageIndex == index
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedPassageIndex = index },
                                        label = { Text("${passage.book} ${passage.chapter}") },
                                        modifier = Modifier.testTag("bible_reader_chip_${index}")
                                    )
                                }
                            }
                        }
                        
                        item {
                            // Theme and context explainers
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = "Sovereign Theme", tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Core Theme: ${activePassage.theme}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activePassage.context,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                        
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${activePassage.book} ${activePassage.chapter}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "NIV",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    
                                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    
                                    activePassage.verses.forEach { (number, text) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "$number",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.width(28.dp),
                                                textAlign = TextAlign.Start
                                            )
                                            Text(
                                                text = text,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        item {
                            Button(
                                onClick = {
                                    activeSubTab = BibleSubTab.AI_SEARCH
                                    searchQuery = "${activePassage.book} ${activePassage.chapter}"
                                    viewModel.lookupScripture("${activePassage.book} ${activePassage.chapter}")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bible_explain_ai_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Explain ${activePassage.book} ${activePassage.chapter} with OverComer AI")
                            }
                        }
                    }
                }
                
                BibleSubTab.AI_SEARCH -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "Ask OverComer AI Study Guide to search and explain any scripture:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Bible Passage (e.g. John 8:36 or Psalms 91:4)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bible_search_input"),
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { searchQuery = ""; viewModel.clearScriptureSearch() }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear text")
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.lookupScripture(searchQuery)
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                        }
                        
                        item {
                            Button(
                                onClick = { viewModel.lookupScripture(searchQuery) },
                                enabled = searchQuery.isNotBlank() && !isSearching,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bible_search_submit_btn")
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Refining study plans...")
                                } else {
                                    Text("Seek Scripture & AI Explanation")
                                }
                            }
                        }
                        
                        item {
                            Text(
                                text = "Quick Study Suggestions:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val templates = listOf("John 8:36", "Romans 8:37", "James 4:7", "Proverbs 3:5-6", "1 Corinthians 10:13")
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                templates.forEach { label ->
                                    ElevatedFilterChip(
                                        selected = searchQuery == label,
                                        onClick = {
                                            searchQuery = label
                                            viewModel.lookupScripture(label)
                                        },
                                        label = { Text(label, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                            }
                        }
                        
                        if (isSearching) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(36.dp))
                                        Text(
                                            text = "Consulting the OverComer Scripture Archives...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Bringing down context, translations, and clinical pastoral reflections.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else if (aiResult != null) {
                            val result = aiResult!!
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bible_search_result_card"),
                                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Text(
                                            text = result.reference,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                        
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                text = "\"${result.text}\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                lineHeight = 20.sp
                                            )
                                        }
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "AI Study Commentary:",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        Text(
                                            text = result.explanation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = "Ready for Bible study!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Tap any suggestion chip above or write standard citations. Let God's word fuel your recovery.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                BibleSubTab.CREED -> {
                    ShieldCreedContent(viewModel)
                }
            }
        }
    }
}

@Composable
fun BibleSubTabSelector(
    activeSubTab: BibleSubTab,
    onSubTabSelected: (BibleSubTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = listOf(
            BibleSubTab.READER to "📖 Bible Reader",
            BibleSubTab.AI_SEARCH to "🔍 AI Study Guide",
            BibleSubTab.CREED to "🛡️ Creed & Shield"
        )
        tabs.forEach { (tab, label) ->
            val isSelected = activeSubTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                    .clickable { onSubTabSelected(tab) }
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ShieldCreedContent(viewModel: OverComerViewModel) {
    val userPath by viewModel.userPath.collectAsStateWithLifecycle()

    val beliefs = remember(userPath) {
        val original = listOf(
            "True freedom begins in a personal, transformational relationship with Jesus Christ.",
            "The Holy Bible is where we learn who Christ is, who we are in Him, and find directional wisdom for our walk.",
            "Christ can completely deliver you from any addiction or struggle. John 8:36: \"So if the Son sets you free, you will be free indeed!\"",
            "Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage, but Christ sets us completely free of past chains.",
            "You have not gone too far nor been involved too much for Christ to accept you, clean you up, and crown you an OverComer.",
            "As soon as you repent and surrender your heart, your past is dead! You are a new creation! (2 Corinthians 5:17)",
            "You are NOT still an addict or permanently sick. You are now and forever an OverComer! (Revelation 12:11)",
            "When temptations trigger you, Christ is faithful to help you resist... He has suffered temptations too, and He always provides a way out."
        )
        when (userPath) {
            "MENTAL_HEALTH", "TOUGH_DAY" -> {
                original.map { belief ->
                    belief
                        .replace("addiction or struggle", "anxiety, depression, or heavy struggle")
                        .replace("Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage", "Your anxiety or distress is NOT your permanent emotional identity. It is a temporary weight")
                        .replace("addict or permanently sick", "defined by your distress or permanently broken")
                }
            }
            "TESTIMONY_VICTORY" -> {
                original.map { belief ->
                    belief
                        .replace("addiction or struggle", "any struggle, trial, or challenge")
                        .replace("Your struggle is NOT your permanent biological identity. It is a behavioral choice that leads to bondage", "Your battle is already won, and your identity is a victorious child of God")
                        .replace("addict or permanently sick", "defeated, broken, or discouraged")
                }
            }
            else -> original
        }
    }

    val mottoText = remember(userPath) {
        val originalMotto = "\"A OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory through the Power of our Savior and King Jesus Christ.\""
        when (userPath) {
            "MENTAL_HEALTH", "TOUGH_DAY" -> {
                originalMotto.replace("over addiction", "over fear, worry, and depression")
            }
            "TESTIMONY_VICTORY" -> {
                originalMotto.replace("over addiction", "over every battle, trial, and challenge")
            }
            else -> originalMotto
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛡️ OverComer Shield & Creed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Our Theological Foundation of Sovereign Victory",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            // Master Motto Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "THE OVERCOMER MOTTO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.3.sp
                    )
                    Text(
                        text = mottoText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "What an OverComer Believes:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        items(beliefs.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = beliefs[index],
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        }

        item {
            // Mission statement
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "OUR HOLY MISSION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "1. Be a safe place for those that are struggling with issues controlling their lives.\n" +
                               "2. Lead those struggling into a life-transforming relationship with Christ.\n" +
                               "3. Make Disciples for The Kingdom of God.\n" +
                               "4. Teach how to reproduce the life-transforming relationship we have had with Christ to others.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ShieldCreedTabScreen(viewModel: OverComerViewModel) {
    ShieldCreedContent(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspirationalQuotesTabScreen(viewModel: OverComerViewModel) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Overcoming Cravings", "Peace & Anxiety", "Strength & Faith", "Grace & Forgiveness")

    // Setup TextToSpeech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                     localTts?.setLanguage(Locale.getDefault())
                     localTts?.setSpeechRate(0.85f)
                     isTtsReady = true
                } catch (_: Exception) {}
            }
        }
        tts = localTts
        onDispose {
            localTts?.stop()
            localTts?.shutdown()
        }
    }

    fun speakQuote(text: String, reference: String) {
        if (isTtsReady) {
            val speechText = cleanTextForSpeech("\"$text\" from $reference")
            tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "InspirationalTabTTS")
        } else {
            Toast.makeText(context, "Voice reader is initializing...", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌟 Inspirational Quotes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reframing our minds with Truth and Victory",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("inspirational_category_${categories.indexOf(cat)}")
                )
            }
        }

        val filteredQuotes = inspirationalQuotes.filter { selectedCategory == "All" || it.category == selectedCategory }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredQuotes.size) { quoteIdx ->
                val quote = filteredQuotes[quoteIdx]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inspirational_quote_card_${quote.id}"),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = quote.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("Inspirational Quote", "\"${quote.text}\"\n— ${quote.reference}")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Quote copied! 📋", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Quote",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { speakQuote(quote.text, quote.reference) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen to quote",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "\"${quote.text}\"",
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "— ${quote.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "RENEW MY MIND:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = quote.contextReflection,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// DAILY BIBLE-BASED AFFIRMATIONS
// ==========================================

data class BibleAffirmation(
    val id: Int,
    val text: String,
    val reference: String,
    val contextReflection: String
)

val bibleAffirmationsDefaults = listOf(
    BibleAffirmation(
        id = 1,
        text = "I am a new creation in Christ; the old is gone, the new has come.",
        reference = "2 Corinthians 5:17",
        contextReflection = "Because of Jesus, my past fails do not define me. Today, I walk in absolute brand-new beginnings!"
    ),
    BibleAffirmation(
        id = 2,
        text = "I can do all things through Christ who strengthens me.",
        reference = "Philippians 4:13",
        contextReflection = "My own willpower might falter, but Christ's internal resurrection power inside me is limitless. I am capable of resisting today's temptations."
    ),
    BibleAffirmation(
        id = 3,
        text = "God has not given me a spirit of fear, but of power, love, and a sound mind.",
        reference = "2 Timothy 1:7",
        contextReflection = "Anxiety and triggers have no legal hold over me. I claim a quiet, focused, disciplined mind under God's protection."
    ),
    BibleAffirmation(
        id = 4,
        text = "I am more than a conqueror through Him who loved me.",
        reference = "Romans 8:37",
        contextReflection = "I do not struggle FOR victory; I stand and fight FROM the victory already won for me on the Cross."
    ),
    BibleAffirmation(
        id = 5,
        text = "He who is in me is greater than he who is in the world.",
        reference = "1 John 4:4",
        contextReflection = "The Holy Spirit inside me is infinitely stronger than any chemical, habit, or sensory trigger in the surrounding environment."
    ),
    BibleAffirmation(
        id = 6,
        text = "Sin shall no longer have dominion over me, for I am under grace.",
        reference = "Romans 6:14",
        contextReflection = "I am completely delivered from the cage of guilt. Grace is my defense, my shield, and my master now."
    ),
    BibleAffirmation(
        id = 7,
        text = "The Lord is my strength and my shield; my heart trusts in Him, and I am helped.",
        reference = "Psalm 28:7",
        contextReflection = "I do not have to carry this burden alone. God is shielding my weak spots while I take one step at a time."
    ),
    BibleAffirmation(
        id = 8,
        text = "My body is a temple of the Holy Spirit, bought with a price to honor God.",
        reference = "1 Corinthians 6:19-20",
        contextReflection = "I declare my eyes, my thoughts, and my hands are tools of righteousness and holiness to bring God praise."
    ),
    BibleAffirmation(
        id = 9,
        text = "God's grace is sufficient for me, for His power is made perfect in weakness.",
        reference = "2 Corinthians 12:9",
        contextReflection = "When I feel exhausted and close to giving in, God's grace steps in to do what I cannot. I am strong in Him."
    ),
    BibleAffirmation(
        id = 10,
        text = "I am chosen, holy, and dearly loved by God.",
        reference = "Colossians 3:12",
        contextReflection = "My worth is set by my Father above. No bad day, relapse, or negative comment can change how much I am cherished."
    )
)

@Composable
fun BibleAffirmationsSection() {
    val items = bibleAffirmationsDefaults
    var currentIndex by remember { mutableStateOf(0) }
    val currentItem = items[currentIndex]
    
    // Track spoken/declared affirmation IDs in a stateful set
    var declaredIds by remember { mutableStateOf(setOf<Int>()) }
    val isDeclared = declaredIds.contains(currentItem.id)
    
    // Setup Local Text To Speech
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    val result = localTts?.setLanguage(Locale.getDefault())
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        localTts?.setLanguage(Locale.US)
                    }
                    localTts?.setSpeechRate(0.82f) // Comfortably slower, very natural reading pace
                    localTts?.setPitch(1.0f)
                } catch (_: Exception) {}
                isTtsReady = true
            }
        }
        tts = localTts
        onDispose {
            localTts?.stop()
            localTts?.shutdown()
        }
    }
    
    fun speakCurrent() {
        if (isTtsReady) {
            val speechText = cleanTextForSpeech("Affirmation: ${currentItem.text}. Scripture reference: ${currentItem.reference}.")
            tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "AffirmationTTS")
        } else {
            Toast.makeText(context, "Text to speech is initializing...", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("bible_affirmations_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VICTORY AFFIRMED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Read Aloud / Speaker Action
                IconButton(
                    onClick = { speakCurrent() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape)
                        .testTag("affirmation_speak_btn")
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Read affirmation aloud",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Carousel Text View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\"${currentItem.text}\"",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "— ${currentItem.reference}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Practical Devotional Reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "RENEW MY MIND:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = currentItem.contextReflection,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Interactive Declaration & Swiping Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Return previous index
                IconButton(
                    onClick = {
                        if (currentIndex > 0) currentIndex-- else currentIndex = items.size - 1
                    },
                    modifier = Modifier.testTag("affirmation_prev_btn")
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous affirmation"
                    )
                }

                // Center Declaration active stamp
                Button(
                    onClick = {
                        declaredIds = if (isDeclared) {
                            declaredIds - currentItem.id
                        } else {
                            declaredIds + currentItem.id
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDeclared) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isDeclared) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("affirmation_declare_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDeclared) Icons.Default.CheckCircle else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isDeclared) "DECLARED VICTORY! ✝" else "AFFIRM & DECLARE 🛡️",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Go to next index
                IconButton(
                    onClick = {
                        if (currentIndex < items.size - 1) currentIndex++ else currentIndex = 0
                    },
                    modifier = Modifier.testTag("affirmation_next_btn")
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next affirmation"
                    )
                }
            }

            // Scoreboard Progress bar metric of total affirmations completed
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Devotional Progress:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${declaredIds.size} of ${items.size} Spoken",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                val progress = declaredIds.size.toFloat() / items.size.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .testTag("affirmations_progress_bar"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            }
        }
    }
}

// ==========================================
// SUPPORT NETWORK SOS SMS CONNECTION CARD
// ==========================================

@Composable
fun SupportConnectionSmsCard() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("overcomer_sos_contacts", Context.MODE_PRIVATE) }

    // Retrieve custom role name (defaults to "Other/Add New")
    var customRoleName by remember {
        mutableStateOf(sharedPrefs.getString("custom_role_name", "Other/Add New") ?: "Other/Add New")
    }

    // Role options including Trusted Family and customRoleName
    val roles = remember(customRoleName) {
        listOf("Sponsor", "Mentor", "Spouse", "Best Friend", "Trusted Family", customRoleName)
    }
    
    var selectedRoleIndex by remember { mutableStateOf(0) }
    val selectedRole = roles.getOrElse(selectedRoleIndex) { "Sponsor" }

    // Helper to get phone key for selected role
    val phoneKey = remember(selectedRole, customRoleName) {
        if (selectedRole == customRoleName) "phone_custom_slot" else "phone_$selectedRole"
    }

    // Retrieve saved phone number for the selected role
    var phoneNumber by remember(phoneKey) {
        mutableStateOf(sharedPrefs.getString(phoneKey, "") ?: "")
    }

    // Default template options
    val templates = listOf(
        "I'm struggling pretty bad right now",
        "Please pray for me",
        "You said if I ever needed you to let you know. I need you"
    )
    var selectedTemplateIndex by remember { mutableStateOf(0) }
    var customMessageText by remember { mutableStateOf("") }

    // The message that will actually be sent
    val finalMessage = if (customMessageText.isNotBlank()) customMessageText else templates.getOrNull(selectedTemplateIndex) ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("support_sos_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title block
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "🚨 SOS SUPPORT NETWORK TEXT",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Add phone numbers for your inner circle below. In moments of temptation, tap a pre-built message or type your own to text them instantly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Horizontal Selectors for Roles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                roles.forEachIndexed { index, role ->
                    val isSelected = selectedRoleIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedRoleIndex = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = role,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Name Customization for custom role
            val isCustomRoleSelected = selectedRole == customRoleName
            if (isCustomRoleSelected) {
                var tempRoleName by remember { mutableStateOf(if (customRoleName == "Other/Add New") "" else customRoleName) }
                OutlinedTextField(
                    value = tempRoleName,
                    onValueChange = { newValue ->
                        tempRoleName = newValue
                        val savedName = if (newValue.trim().isBlank()) "Other/Add New" else newValue.trim()
                        customRoleName = savedName
                        sharedPrefs.edit().putString("custom_role_name", savedName).apply()
                    },
                    label = { Text("Customize Label (e.g. Pastor, Coach, Brother)") },
                    placeholder = { Text("Other/Add New") },
                    modifier = Modifier.fillMaxWidth().testTag("sos_custom_role_name_field"),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                )
            }

            // Phone Field corresponding to selected role
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    phoneNumber = newValue
                    sharedPrefs.edit().putString(phoneKey, newValue).apply()
                },
                label = { Text("$selectedRole's Phone / Contact") },
                placeholder = { Text("e.g. 704-555-0199") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().testTag("sos_phone_field"),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                },
                singleLine = true
            )

            // Preset templates header
            Text(
                text = "CHOOSE A QUICK TEMPLATE:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.secondary
            )

            // Vertical list of presets
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                templates.forEachIndexed { index, template ->
                    val isCurrentPreset = selectedTemplateIndex == index && customMessageText.isEmpty()
                    Surface(
                        onClick = {
                            selectedTemplateIndex = index
                            customMessageText = "" // clear custom text to default to chosen template
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrentPreset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isCurrentPreset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isCurrentPreset,
                                onClick = {
                                    selectedTemplateIndex = index
                                    customMessageText = ""
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = template,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCurrentPreset) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Space to create their own short text
            OutlinedTextField(
                value = customMessageText,
                onValueChange = { customMessageText = it },
                label = { Text("Or Type Your Custom SOS Message") },
                placeholder = { Text("e.g. I am in a trigger area. Please call me ASAP.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sos_custom_msg_field"),
                maxLines = 3
            )

            // Submit Button to open Text App
            Button(
                onClick = {
                    try {
                        val smsUriStr = if (phoneNumber.isNotBlank()) {
                            "smsto:$phoneNumber"
                        } else {
                            "smsto:"
                        }
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(smsUriStr)).apply {
                            putExtra("sms_body", finalMessage)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open messaging client.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sos_send_sms_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEXT YOUR ${selectedRole.uppercase()}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

// ==========================================
// CLINICAL EMERGENCY SOVEREIGN SHIELD (PANIC OVERLAY)
// ==========================================

@Composable
fun PanicOverlayDialog(onDismiss: () -> Unit) {
    var selectedPanicTab by remember { mutableStateOf(0) } // 0 = Grounding, 1 = Support Contacts, 2 = Global Help

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Block with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(12.dp)
                        ) {}
                        Text(
                            text = "SOVEREIGN SHIELD SOS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("panic_close_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close overlay")
                    }
                }
                
                Text(
                    text = "Pause immediately. Breathe deep. God is your refuge and strength, a very present help in trouble.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Beautiful Custom Segmented Tabs Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("🧘 Grounding", "📞 Inner Circle", "🛡️ Helplines")
                    tabs.forEachIndexed { i, title ->
                        val isSelected = selectedPanicTab == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .clickable { selectedPanicTab = i }
                                .padding(vertical = 10.dp)
                                .testTag("panic_tab_$i"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedPanicTab) {
                        0 -> GroundingExerciseTabContent()
                        1 -> SupportContactsTabContent()
                        2 -> GlobalHelplinesTabContent()
                    }
                }
            }
        }
    }
}

@Composable
fun GroundingExerciseTabContent() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            BreathingPulseAssist()
        }
        item {
            SensoryGroundingAssist()
        }
    }
}

@Composable
fun BreathingPulseAssist() {
    var phase by remember { mutableStateOf("Ready") }
    var secondsLeft by remember { mutableStateOf(0) }
    var pulseActive by remember { mutableStateOf(false) }

    LaunchedEffect(pulseActive) {
        if (pulseActive) {
            while (true) {
                // Inhale
                phase = "Breathe In (Fill your lungs)"
                secondsLeft = 4
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
                // Hold
                phase = "Hold (Be still)"
                secondsLeft = 4
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
                // Exhale
                phase = "Exhale (Release stress)"
                secondsLeft = 6
                while (secondsLeft > 0) {
                    delay(1000)
                    secondsLeft--
                }
            }
        } else {
            phase = "Ready to Reset"
            secondsLeft = 0
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("breathing_assist_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Paced Breathing Assist 💨",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            val animatedScale by animateFloatAsState(
                targetValue = if (!pulseActive) 1.0f 
                else when {
                    phase.startsWith("Breathe") -> 1.3f
                    phase.startsWith("Hold") -> 1.3f
                    else -> 0.8f
                },
                animationSpec = tween(durationMillis = if (phase.startsWith("Ready")) 500 else 4000)
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (pulseActive) {
                        Text(
                            text = "$secondsLeft s",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Text(
                text = phase,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { pulseActive = !pulseActive },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pulseActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("panic_breathing_toggle_btn")
            ) {
                Text(
                    text = if (pulseActive) "Stop Breathing Guide" else "Begin Deep Breathing",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SensoryGroundingAssist() {
    var currentStep by remember { mutableStateOf(5) }
    val stepText = when (currentStep) {
        5 -> "👁️ Gaze around and name of 5 things you can see (e.g. lamp, table, wall photograph, clock, book)."
        4 -> "🖐️ Touch 4 distinct physical textures near you. Feel your clothing, table edge, phone bezel, soft pillow."
        3 -> "👂 Listen closely. Identify 3 sounds you can hear right now (e.g. air conditioning humming, generic ticking, birds chirping)."
        2 -> "👃 Actively inhale and notice 2 distinct aromas or scents (e.g. skin lotion, warm beverage, dry wood)."
        1 -> "👅 Name 1 taste inside your mouth (or take a comforting sip of fresh water to reset taste buds)."
        else -> "🎉 Magnificently Grounded! Your nervous system is de-escalating. You are safe in the present moment."
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("sensory_grounding_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "5-4-3-2-1 Sensory Grounding Method 🧘‍♀️",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Text(
                text = "This clinically proven exercise physically slows physiological distress responses by re-focusing active attention onto real-world sensations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (currentStep in 1..5) "STEP $currentStep OF 5" else "GROUNDING COMPLETE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 1) {
                    Button(
                        onClick = { currentStep-- },
                        modifier = Modifier.testTag("panic_grounding_next_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("I can sense this. Next →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (currentStep == 1) {
                    Button(
                        onClick = { currentStep = 0 },
                        modifier = Modifier.testTag("panic_grounding_finish_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Complete Exercise ✔️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = { currentStep = 5 },
                        modifier = Modifier.testTag("panic_grounding_restart_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Restart Grounding", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SupportContactsTabContent() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("overcomer_sos_contacts", Context.MODE_PRIVATE) }
    
    var customRoleName by remember {
        mutableStateOf(sharedPrefs.getString("custom_role_name", "Other/Add New") ?: "Other/Add New")
    }

    val roles = remember(customRoleName) {
        listOf("Sponsor", "Mentor", "Spouse", "Best Friend", "Trusted Family", customRoleName)
    }
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "My Personal Supporter Circle 🤝",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "These are your pre-defined supporters. Tap to dial or text immediately.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(roles.size) { index ->
            val role = roles[index]
            val isCustomRole = index == roles.size - 1
            val phoneKey = if (isCustomRole) "phone_custom_slot" else "phone_$role"

            var phone by remember(phoneKey) {
                mutableStateOf(sharedPrefs.getString(phoneKey, "") ?: "")
            }
            var showEditPhone by remember { mutableStateOf(false) }
            var editPhoneInput by remember { mutableStateOf(phone) }
            var editRoleNameInput by remember { mutableStateOf(customRoleName) }

            Card(
                modifier = Modifier.fillMaxWidth().testTag("supporter_card_$role"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = role,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (phone.isNotBlank()) {
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                Text(
                                    text = "No phone number saved",
                                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Call Button
                            IconButton(
                                onClick = {
                                    if (phone.isNotBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Cannot open dialer.", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Please configure phone first.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = phone.isNotBlank(),
                                modifier = Modifier.testTag("panic_call_$role")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call $role",
                                    tint = if (phone.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }

                            // SMS button
                            IconButton(
                                onClick = {
                                    if (phone.isNotBlank()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
                                                putExtra("sms_body", "I'm experiencing high temptation / distress. Please reach out to me!")
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Cannot open messenger", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Please configure phone first.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = phone.isNotBlank(),
                                modifier = Modifier.testTag("panic_sms_$role")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Message $role",
                                    tint = if (phone.isNotBlank()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                                )
                            }

                            // Edit Button
                            IconButton(
                                onClick = { 
                                    editPhoneInput = phone
                                    editRoleNameInput = customRoleName
                                    showEditPhone = !showEditPhone 
                                },
                                modifier = Modifier.testTag("panic_edit_btn_$role")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit phone",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    if (showEditPhone) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isCustomRole) {
                                OutlinedTextField(
                                    value = editRoleNameInput,
                                    onValueChange = { editRoleNameInput = it },
                                    label = { Text("Custom Supporter Label") },
                                    placeholder = { Text("Other/Add New") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("phone_edit_name_input_$role")
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = editPhoneInput,
                                    onValueChange = { editPhoneInput = it },
                                    label = { Text("Phone Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("phone_edit_input_$role")
                                )
                                Button(
                                    onClick = {
                                        phone = editPhoneInput
                                        sharedPrefs.edit().putString(phoneKey, editPhoneInput).apply()
                                        if (isCustomRole) {
                                            val savedName = if (editRoleNameInput.trim().isBlank()) "Other/Add New" else editRoleNameInput.trim()
                                            customRoleName = savedName
                                            sharedPrefs.edit().putString("custom_role_name", savedName).apply()
                                        }
                                        showEditPhone = false
                                    },
                                    modifier = Modifier.testTag("phone_save_btn_$role")
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalHelplinesTabContent() {
    val context = LocalContext.current
    
    val helplines = listOf(
        CrisisHelpline(
            name = "988 Suicide & Crisis Lifeline",
            description = "Confidential, 24/7 free emotional support for anyone in high distress or experiencing triggering crises.",
            contact = "988",
            isSms = true,
            smsValue = "HOME"
        ),
        CrisisHelpline(
            name = "SAMHSA National Helpline",
            description = "Government mental health agency providing immediate guidance, referrals, and localized treatment resources.",
            contact = "1-800-662-4357",
            isSms = false
        ),
        CrisisHelpline(
            name = "Crisis Text Line",
            description = "Instantly correspond with a live certified mental health specialist via text messaging.",
            contact = "741741",
            isSms = true,
            smsValue = "HOME"
        ),
        CrisisHelpline(
            name = "National Emergency Services (911)",
            description = "Immediately request urgent tactical dispatcher assistance if you are in physical danger.",
            contact = "911",
            isSms = false
        )
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Professional Crisis Lifelines 🛡️",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Free, confidential, professional service networks available 24 hours a day, 7 days a week.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(helplines.size) { index ->
            val helpline = helplines[index]
            Card(
                modifier = Modifier.fillMaxWidth().testTag("helpline_card_${helpline.contact}"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = helpline.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = helpline.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${helpline.contact}"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot dial helpline.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("helpline_call_btn_${helpline.contact}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call ${helpline.contact}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (helpline.isSms) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${helpline.contact}")).apply {
                                            putExtra("sms_body", helpline.smsValue)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cannot open messenger.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("helpline_sms_btn_${helpline.contact}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (helpline.smsValue == "HOME") "Sms 'HOME'" else "Text ${helpline.contact}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CrisisHelpline(
    val name: String,
    val description: String,
    val contact: String,
    val isSms: Boolean,
    val smsValue: String = ""
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecoveryLessonsSection() {
    val context = LocalContext.current
    var activeLessonIndex by remember { mutableStateOf<Int?>(null) }
    var isAcademyExpanded by remember { mutableStateOf(false) }
    val expandedFolders = remember { mutableStateMapOf<Int, Boolean>() }
    
    val prefs = remember { context.getSharedPreferences("overcomer_lessons_data", Context.MODE_PRIVATE) }
    var completedCount by remember { mutableStateOf(0) }
    
    fun isLessonCompleted(index: Int): Boolean {
        return prefs.getBoolean("lesson_completed_$index", false)
    }
    
    fun setLessonCompleted(index: Int, completed: Boolean) {
        prefs.edit().putBoolean("lesson_completed_$index", completed).apply()
        var count = 0
        for (i in 0 until 14) {
            if (prefs.getBoolean("lesson_completed_$i", false)) count++
        }
        completedCount = count
    }

    LaunchedEffect(Unit) {
        var count = 0
        for (i in 0 until 14) {
            if (prefs.getBoolean("lesson_completed_$i", false)) count++
        }
        completedCount = count
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("recovery_academy_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeLessonIndex == null) {
                // Header (Click to expand)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAcademyExpanded = !isAcademyExpanded }
                        .padding(bottom = if (isAcademyExpanded) 8.dp else 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Overcomer",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Obedience Academy",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Substance Recovery Steps & Lessons",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = "$completedCount / 14 Completed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Reflectively study and answer guided lessons directly adapted, organized within our foldered Overcomer steps.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (isAcademyExpanded) "Click to collapse" else "Click here",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            )
                        )
                        Icon(
                            imageVector = if (isAcademyExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isAcademyExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                val courses = listOf(
                    CourseSummary("The 7-Step Recovery Program", "Full theological choice-based 7-Step program with scriptural foundations.", Icons.Default.Shield, Color(0xFF0D47A1)),
                    CourseSummary("Trusting God's Process", "Waiting, Preparation, and Anointing vs Appointment (David & Bamboo Tree).", Icons.Default.Star, Color(0xFF00796B)),
                    CourseSummary("I AM An OverComer Affirmations", "Scripture values & identity declarations of who God says you are.", Icons.Default.Favorite, Color(0xFFE65100)),
                    CourseSummary("Trigger Prevention & Management", "Avoiding pitfalls, fleeing temptations, and divine escape options.", Icons.Default.Warning, Color(0xFFC62828)),
                    CourseSummary("OverComer Ministry & Fellowship", "Recovery fellowship, community meetings on Thursdays at The Refuge.", Icons.Default.Person, Color(0xFF512DA8)),
                    CourseSummary("Family & Parents Restoration", "Appreciating Mom and Dad, whether on Earth or in Heaven.", Icons.Default.Home, Color(0xFF4CAF50)),
                    CourseSummary("I Can't Catch My Breath", "Biblical and medical insights on Anxiety, Depression, and Trauma (PTSD).", Icons.Default.Warning, Color(0xFF00BCD4)),
                    CourseSummary("Freedom from Fear (Nakia's Miracle)", "Shane Merrill's testimony of divine preparation, crash, and miraculous healing.", Icons.Default.Star, Color(0xFFFF9800)),
                    CourseSummary("Overcoming Guilt & Shame", "Differentiating conduct (guilt) from identity (shame) with Brené Brown research.", Icons.Default.Favorite, Color(0xFF9C27B0)),
                    CourseSummary("The Emotions of the Journey", "The 12 emotions from Fear to Giving Back & The Rich Young Ruler (Matt 19).", Icons.Default.List, Color(0xFF009688)),
                    CourseSummary("True Repentance & Your Value", "What repentance truly means & our value shown by the Gilded Dollar Bill story.", Icons.Default.Star, Color(0xFFE91E63)),
                    CourseSummary("Facing Your Faults & Confession", "Pastor Wayne Cordeiro's study and Biblical principles of Confession.", Icons.Default.Check, Color(0xFF3F51B5)),
                    CourseSummary("Overcoming Temptation", "Wilderness temptation, Gethsemane anguish & Pastor DJ Byrd's tribute.", Icons.Default.Lock, Color(0xFF795548)),
                    CourseSummary("Follow the Voice (Anchorage Storm)", "The miracle landing in Alaska—the Cross of lights is the way home.", Icons.Default.Phone, Color(0xFF673AB7))
                )

                val stepFolders = listOf(
                    StepFolder(1, "Step 1: Admit Your Weakness", "Acknowledge powerless habits and reach for Him.", Icons.Default.Shield, Color(0xFF0D47A1), listOf(0, 6)),
                    StepFolder(2, "Step 2: Repent to God", "Deep sincere repentance and cleansing.", Icons.Default.Star, Color(0xFFE91E63), listOf(10)),
                    StepFolder(3, "Step 3: Release to God", "Sovereign surrender of control to God's lead.", Icons.Default.Favorite, Color(0xFF00796B), listOf(1, 7, 13)),
                    StepFolder(4, "Step 4: Examine Yourself", "Taking an honest moral inventory of habits.", Icons.Default.List, Color(0xFF009688), listOf(9, 8)),
                    StepFolder(5, "Step 5: Acknowledge & Apologize", "Confessing to God and making human amends.", Icons.Default.Check, Color(0xFF3F51B5), listOf(11, 5)),
                    StepFolder(6, "Step 6: Seek God's Presence", "Continuous morning devotion and combatting triggers.", Icons.Default.Search, Color(0xFF795548), listOf(2, 3, 12)),
                    StepFolder(7, "Step 7: Help Others", "Sowing seeds of restoration into other lives.", Icons.Default.Person, Color(0xFF512DA8), listOf(4))
                )

                if (isAcademyExpanded) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    stepFolders.forEach { folder ->
                        val completedInFolder = folder.lessonIndices.count { isLessonCompleted(it) }
                        val totalInFolder = folder.lessonIndices.size
                        val isExpanded = expandedFolders[folder.stepNumber] ?: false

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_step_folder_${folder.stepNumber}"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (completedInFolder == totalInFolder) folder.color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (completedInFolder == totalInFolder) folder.color.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                            )
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedFolders[folder.stepNumber] = !isExpanded }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = folder.icon,
                                        contentDescription = null,
                                        tint = folder.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = folder.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = folder.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$completedInFolder / $totalInFolder Completed",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (completedInFolder == totalInFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = "Expand folder",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                if (isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 2.dp)
                                            .padding(bottom = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        
                                        folder.lessonIndices.forEach { idx ->
                                            val course = courses[idx]
                                            val isDone = isLessonCompleted(idx)
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("recovery_lesson_option_$idx")
                                                    .clickable { activeLessonIndex = idx },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isDone) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                                ),
                                                border = BorderStroke(0.5.dp, if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .background(course.accentColor.copy(alpha = 0.12f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = course.icon,
                                                            contentDescription = null,
                                                            tint = course.accentColor,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = course.title,
                                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = course.description,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    if (isDone) {
                                                        Text(
                                                            text = "✓ DONE",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    } else {
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowRight,
                                                            contentDescription = "Start",
                                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                }
            } else {
                val currentIdx = activeLessonIndex!!
                val isDone = isLessonCompleted(currentIdx)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { activeLessonIndex = null },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to lesson list"
                        )
                    }
                    Text(
                        text = "Lesson ${currentIdx + 1} of 14",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isDone) {
                        Text(
                            text = "✓ COMPLETED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                when (currentIdx) {
                    0 -> LessonOneContent(prefs) { setLessonCompleted(0, it) }
                    1 -> LessonTwoContent(prefs) { setLessonCompleted(1, it) }
                    2 -> LessonThreeContent(prefs) { setLessonCompleted(2, it) }
                    3 -> LessonFourContent(prefs) { setLessonCompleted(3, it) }
                    4 -> LessonFiveContent(prefs) { setLessonCompleted(4, it) }
                    5 -> LessonSixContent(prefs) { setLessonCompleted(5, it) }
                    6 -> LessonSevenContent(prefs) { setLessonCompleted(6, it) }
                    7 -> LessonEightContent(prefs) { setLessonCompleted(7, it) }
                    8 -> LessonNineContent(prefs) { setLessonCompleted(8, it) }
                    9 -> LessonTenContent(prefs) { setLessonCompleted(9, it) }
                    10 -> LessonElevenContent(prefs) { setLessonCompleted(10, it) }
                    11 -> LessonTwelveContent(prefs) { setLessonCompleted(11, it) }
                    12 -> LessonThirteenContent(prefs) { setLessonCompleted(12, it) }
                    13 -> LessonFourteenContent(prefs) { setLessonCompleted(13, it) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        setLessonCompleted(currentIdx, !isDone)
                        Toast.makeText(context, if (!isDone) "Lesson Marked as Completed! 🎉" else "Lesson Status Reset.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("toggle_lesson_completion_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isDone) "Reset Completion Status" else "Mark Lesson as Completed ✓", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class CourseSummary(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

data class StepFolder(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val lessonIndices: List<Int>
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LessonOneContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var activeStep by remember { mutableStateOf(1) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🛡️ Overcomers Recovery Ministries 7 Step Program",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Complete each step's study and write down your guided reflections under the guidance of the Holy Spirit.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(7) { sIdx ->
                val stepNum = sIdx + 1
                val isSelected = activeStep == stepNum
                FilterChip(
                    selected = isSelected,
                    onClick = { activeStep = stepNum },
                    label = { Text("Step $stepNum") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        when (activeStep) {
            1 -> {
                StepSummaryCard(
                    title = "Step 1: Admit Your Weakness",
                    description = "Admit you have a problem and are powerless over addiction/weight.",
                    scriptures = listOf(
                        ScriptureQuote("Romans 7:18", "For I know that in me (that is, in my flesh,) dwells no good thing. I want to do what is right, but I can’t."),
                        ScriptureQuote("1 John 1:9", "If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness."),
                        ScriptureQuote("Proverbs 28:13", "Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.")
                    ),
                    questions = listOf(
                        "What addiction/weight are you struggling with? (Drugs, alcohol, food, weight, etc.)",
                        "Briefly share your story or testimony of how this struggle took hold of your life."
                    ),
                    stepIndex = 1,
                    prefs = prefs
                )
            }
            2 -> {
                StepSummaryCard(
                    title = "Step 2: Repent to God",
                    description = "Deep genuine repentance before God, wiping the slate completely clean.",
                    scriptures = listOf(
                        ScriptureQuote("1 John 1:9", "If we confess our sins, he is faithful and just and will forgive us our sins and purify us from all unrighteousness."),
                        ScriptureQuote("Psalm 51:1-2", "Have mercy upon me, O God, according to your lovingkindness; according unto the multitude of thy tender mercies blot out my transgressions. Wash me thoroughly from mine iniquity, and cleanse me from my sin."),
                        ScriptureQuote("2 Chronicles 7:14", "If my people who are called by my name shall humble themselves and pray and seek my face and turn from their wicked ways; then, I will hear from heaven and forgive their sin and heal their land."),
                        ScriptureQuote("Acts 3:19", "Repent, then and turn to God, so that your sins may be wiped out, that times of refreshing may come from the Lord."),
                        ScriptureQuote("Psalm 32:5", "Then I acknowledged my sin to you and did not cover up my iniquity. I said, 'I will confess my transgressions to the Lord.' And you forgave the guilt of my sin.")
                    ),
                    questions = listOf(
                        "Explain in your own heart what Repentance is and actually means?",
                        "What happens if you slip up or have a bad moment? What is God's grace attitude toward you then?"
                    ),
                    stepIndex = 2,
                    prefs = prefs
                )
            }
            3 -> {
                StepSummaryCard(
                    title = "Step 3: Release to God",
                    description = "Turn the complete control of your life over to God as living worship.",
                    scriptures = listOf(
                        ScriptureQuote("Romans 12:1", "I urge you brothers and sisters, in the view of God’s mercy, to offer you bodies as a living sacrifice, holy and pleasing to God – this is your true and proper worship."),
                        ScriptureQuote("1 Peter 5:7-8", "casting all your care upon him; for he careth for you. Be sober, be vigilant; because your adversary the devil, as a roaring lion, walketh about, seeking whom he may devour."),
                        ScriptureQuote("Matthew 11:28-30", "Come unto me, all ye that labor and are heavy laden, and I will give you rest. Take my yoke upon you and learn of me... For my yoke is easy and my burden is light.")
                    ),
                    questions = listOf(
                        "Explain what a life fully turned over to God looks like daily.",
                        "Explain what Him taking control of your impulses and choices looks like.",
                        "Write down how Christ already helped you through a previous decision or victory."
                    ),
                    stepIndex = 3,
                    prefs = prefs
                )
            }
            4 -> {
                StepSummaryCard(
                    title = "Step 4: Examine Yourself",
                    description = "Take a fully honest, moral inventory of yourself, your habits, and your decisions.",
                    scriptures = listOf(
                        ScriptureQuote("Lamentations 3:40", "Let us examine our ways and test them, and let us return to the Lord."),
                        ScriptureQuote("2 Corinthians 13:5", "Examine yourselves to see whether you are in the faith; test yourselves. Do you not realize that Christ Jesus is in you—unless, of course, you fail the test?")
                    ),
                    questions = listOf(
                        "Begin to list your choices, secrets, or past events that you need to examine under His light. (No one sees this but you)."
                    ),
                    stepIndex = 4,
                    prefs = prefs
                )
            }
            5 -> {
                StepSummaryCard(
                    title = "Step 5: Acknowledge & Apologize",
                    description = "Admit to God, ourselves, and someone else our wrongdoings.",
                    scriptures = listOf(
                        ScriptureQuote("James 5:16", "Therefore, confess your sins to each other and pray for each other so that you may be healed."),
                        ScriptureQuote("Proverbs 28:13", "Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.")
                    ),
                    questions = listOf(
                        "Write down people you have offended or hurt during your struggle.",
                        "Write down exactly what you would like to say to apologize humbly to them.",
                        "When and how will you call or message them to make amends this week?"
                    ),
                    stepIndex = 5,
                    prefs = prefs
                )
            }
            6 -> {
                StepSummaryCard(
                    title = "Step 6: Seek God's Presence",
                    description = "Seek God through daily prayer and meditation on His Word and His Works.",
                    scriptures = listOf(
                        ScriptureQuote("Colossians 3:16", "Let the message of Christ dwell among you richly as you teach and admonish one another with all wisdom through Psalms, hymns and songs from the spirit...")
                    ),
                    questions = listOf(
                        "Explain what it means to actively SEEK God every single morning.",
                        "How would you explain prayer and meditation to someone who is struggling?"
                    ),
                    stepIndex = 6,
                    prefs = prefs
                )
            }
            7 -> {
                StepSummaryCard(
                    title = "Step 7: Help Others",
                    description = "Help other struggling addicts and lift their loads with gentle restoration.",
                    scriptures = listOf(
                        ScriptureQuote("Galatians 6:1", "Brothers and sisters, if someone is caught in a sin, you who live by the Spirit should restore that person gently. But watch yourselves, or you also may be tempted.")
                    ),
                    questions = listOf(
                        "How can you actively support and serve as a beacon of Hope to another recovering OverComer?"
                    ),
                    stepIndex = 7,
                    prefs = prefs
                )
            }
        }
    }
}

@Composable
fun StepSummaryCard(
    title: String,
    description: String,
    scriptures: List<ScriptureQuote>,
    questions: List<String>,
    stepIndex: Int,
    prefs: android.content.SharedPreferences
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "📖 FOUNDATION SCRIPTURES:",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            scriptures.forEach { script ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "\"${script.text}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "— ${script.reference}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Text(
                text = "🙋 QUESTIONS TO ASK (Guided by the Holy Spirit):",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )

            questions.forEachIndexed { qIdx, question ->
                val answerKey = "step_${stepIndex}_q_${qIdx}"
                var answerText by remember(answerKey) {
                    mutableStateOf(prefs.getString(answerKey, "") ?: "")
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${qIdx + 1}. $question",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = {
                            answerText = it
                            prefs.edit().putString(answerKey, it).apply()
                        },
                        placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.fillMaxWidth().testTag("recovery_input_step_${stepIndex}_q_$qIdx"),
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = false,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

data class ScriptureQuote(
    val reference: String,
    val text: String
)

@Composable
fun LessonTwoContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌱 Lesson 2: Trusting God's Process",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Adapted from 'Trust the Process' by Shane Merrill & Micah Cartee.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "📖 Reflection Scripture:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "\"Hope deferred makes the heart sick, but a longing fulfilled is a tree of life. Whoever scorns instruction will pay for it, but whoever respects a command is rewarded.\"",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "— Proverbs 13:12-13",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🌱 The Chinese Bamboo Tree",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The Chinese Bamboo Tree must be watered and fertilized every single day for five years before it breaks through the ground. If at any time the process stops, the tree dies inside the soil.\n\n" +
                           "But in the fifth year, it breaks through the soil and grows to nearly ninety feet tall in just six weeks! The tree spent five long years developing an incredibly deep and wide root system so that when it grows, it will never topple over. Do you allow waiting to develop you or embitter you?",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🛡️ Anointed, Not Yet Appointed",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "David was anointed by Samuel as king, but he was sent right back to watching sheep. Preparation must precede opportunity! David had to master the lyre in the quiet of the fields before playing for King Saul, and slay the bear and lion before fighting Goliath. If you shortcut the process, you short-circuit the product!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "What area of your recovery or mental life feels like a slow, invisible 'root growth' waiting process right now?",
            "What are your current 'bear and lion' routines that are preparing you to face your upcoming giants?",
            "How can you choose to 'get better, not bitter' in your faith walk today?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_2_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_2_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonThreeContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌟 Lesson 3: I AM An OverComer Affirmations",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Declare who God says you are. Re-script your self-worth in Christ.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "🗣️ SPEAK THESE ALOUD DAILY:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "I AM Loved By God • I AM NOT Who Others Say I Am • I AM NOT Who I Used To Be • I AM Who God Says I Am",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                val scriptures = listOf(
                    ScriptureQuote("Genesis 1:27", "I am created in the image of God."),
                    ScriptureQuote("Deuteronomy 28", "I am Blessed."),
                    ScriptureQuote("Psalms 17:8", "I am the apple of God's Eye."),
                    ScriptureQuote("Jeremiah 1:5", "I am known by Him, I am set apart, I am appointed."),
                    ScriptureQuote("Matthew 5:14", "I am the light of the world. A city set on a hill."),
                    ScriptureQuote("1 Corinthians 6:19-20", "I am the temple of the Holy Spirit. I am bought with a price."),
                    ScriptureQuote("2 Corinthians 5:17", "I am a New Creation. The old has gone, the new is here!"),
                    ScriptureQuote("1 John 3:1", "Behold what great love the Father has lavished on us, that we should be called children of God!"),
                    ScriptureQuote("Revelations 12:11", "And they overcame him by the blood of the Lamb, and by the word of their testimony.")
                )

                scriptures.forEach { script ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "\"${script.text}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "— ${script.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "When you feel tired or tempted, what negative identity lies do you tend to speak over yourself?",
            "Which identity verse above speaks directly to your heart? Write it custom as a personal declaration below."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_3_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your declaration...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_3_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonFourContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🔥 Lesson 4: Trigger Prevention & Management",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Avoid pitfalls and set healthy boundaries. An OverComer is NOT the same person they used to be!",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🚨 Proactive Focus: Avoid Triggers",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "We cannot control how others act/move or if they accept us as a New Creation. We can only control ourselves and influence others by our behavior and lifestyles.",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🏃 scriptures on fleeing temptation:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                val avoidVerses = listOf(
                    ScriptureQuote("1 Thessalonians 5:22", "Abstain from all appearance of evil."),
                    ScriptureQuote("1 Corinthians 6:18", "Flee from sexual immorality."),
                    ScriptureQuote("2 Timothy 2:22", "Flee the evil desires of youth and pursue righteousness, faith, love and peace..."),
                    ScriptureQuote("Matthew 26:41", "Watch and pray so that you will not fall into temptation. The spirit is willing, but the flesh is weak.")
                )

                avoidVerses.forEach { script ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "\"${script.text}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "— ${script.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🛡️ What to do when triggered:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val handleVerses = listOf(
                    ScriptureQuote("James 4:7", "Submit yourselves, then, to God. Resist the devil, and he will flee from you."),
                    ScriptureQuote("1 Corinthians 10:13", "No temptation has overtaken you except what is common to mankind. And God is faithful; he will not let you be tempted beyond what you can bear. But when you are tempted, he will also provide a way out..."),
                    ScriptureQuote("1 Peter 5:8", "Be alert and of sober mind. Your enemy the devil prowls around like a roaring lion looking for someone to devour. Resist him, standing firm in the faith...")
                )

                handleVerses.forEach { script ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "\"${script.text}\"",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "— ${script.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "What are your biggest 3 trigger triggers (e.g. certain friends, bars, stress and fatigue)?",
            "What is your immediate escape plan when faced with active temptations?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_4_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your defensive actions...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_4_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonFiveContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "⛪ Lesson 5: OverComer Fellowship & Community",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "You CAN Be Free • You DO Have Hope • You Are NOT Your Addiction!",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Weekly Gathering Details:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "📆 Every Thursday 7:00 PM - 9:00 PM\n" +
                           "⛪ The Refuge\n" +
                           "📍 290 Dunn Short Cut Rd, Conway, SC 29526",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📞 For Questions and Outrage Team Contacts:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "• Shane: 704-977-5735 / Outreach@therefugesc.org\n" +
                           "• Chatham Smith: 704-291-1540 / Chatham.smith@yahoo.com",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "Christ Desires To Set You Free completely from your bondages. There is absolutely nothing impossible for Him! Reach out, step into faith, and get connected to a loving team.",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Why is isolation the biggest trap of any addiction? How does team fellowship prevent a relapse?",
            "Write a brief message of prayer or encouragement for a fellow OverComer who might be struggling today."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_5_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_5_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonSixContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "👨‍Simple Parent Restoration",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Appreciate, honor, and restore family connections, remembering we only have one Mom and one Dad.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "👩 Mom",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                val momLines = listOf(
                    "When we’re 5 years old, we say: “Mommy, I love you.”",
                    "When we’re 13, we say: “Mom, whatever.”",
                    "When we’re 16, we say: “My Mom is SO annoying!”",
                    "When we’re 18, we say: “I want out of this house!”",
                    "When we’re 21, we say: “Mom, you were right.”",
                    "When we’re 30, we say: “I want to go to Mom’s house.”",
                    "When we’re 50, we say: “I don’t want to lose my Mom.”",
                    "When we’re 70, we say: “I’d give up everything to have my Mom here with me again.”"
                )
                
                momLines.forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "You only have one Mom. Appreciate her, whether she’s here on Earth or in Heaven.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "👨 Dad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val dadLines = listOf(
                    "When we’re 5 years old, we say: “Daddy, I love you.”",
                    "When we’re 13, we say: “Dad, whatever.”",
                    "When we’re 16, we say: “My Dad is SO annoying!”",
                    "When we’re 18, we say: “I want out of this house!”",
                    "When we’re 21, we say: “Dad, you were right.”",
                    "When we’re 30, we say: “I want to go to Dad’s house.”",
                    "When we’re 50, we say: “I don’t want to lose my Dad.”",
                    "When we’re 70, we say: “I’d give up everything to have my Dad here with me again.”"
                )

                dadLines.forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "You only have one Dad. Appreciate him, whether he’s here on Earth or in Heaven.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "What is one way you can show or express honor/appreciation to your parents (or honor their memory) today?",
            "How has God's grace helped heal any generational wounds or strained relationships in your family?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_6_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_6_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonSevenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌬️ Lesson 7: I Can't Catch My Breath",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Biblical and statistical insights on Anxiety, Depression, Trauma, and finding your breath in YHWH.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "📊 The Real Struggle (American Statistics):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• 32% (1 in 3) say drugs have been a problem in their family.\n" +
                           "• 33% (1 in 3) women and 25% (1 in 4) men have been sexually assaulted or raped.\n" +
                           "• 50% (1 in 2) marriages end in divorce.\n" +
                           "• Anxiety affects 40 million adults (18.1%), Major Depression affects 16 million (6.7%), Bipolar affects 5.7 million (2.6%), and PTSD affects 7.7 million (3.5%).\n" +
                           "• For every 1 person who commits suicide, 316 seriously contemplate it.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 Biblical Giants Faced Intense Mental Struggles:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Before any self-righteous warning, know that many of the greatest servants of God wrestled deeply:\n\n" +
                           "• David: \"My guilt has overwhelmed me like a burden too heavy to bear\" (Ps 38:4), \"Why are you downcast, O my soul? Why so disturbed within me?\" (Ps 42:11)\n\n" +
                           "• Elijah: Afraid, discouraged, tired. \"I have had enough Lord. Take my life...\" (1 Kings 19:4)\n\n" +
                           "• Jonah: Angry enough to die, seeking to run away. \"It is better for me to die than to live.\" (Jonah 4:3, 4:9)\n\n" +
                           "• Job: \"I loathe my very life, I have no peace, no quietness, I have no rest, but only turmoil.\" (Job 3:11, 3:26, 10:1, 30:15-17)\n\n" +
                           "• Moses: Broken by his followers' sins, asking: \"Blot me out of the book you have written.\" (Exodus 32:32)\n\n" +
                           "• Jeremiah: Wrestle with loneliness, defeat. \"Cursed be the day I was born! Why did I ever come out of the womb to see trouble and sorrow...\" (Jer 20:14-18)\n\n" +
                           "• Paul: \"When neither sun nor stars appeared... we finally gave up all hope of being saved.\" (Acts 27:20)\n\n" +
                           "• Jesus: \"My soul is deeply grieved to the point of death...\" (Mark 14:34-36). In extreme anguish, His sweat became like drops of blood in Gethsemane (Luke 22:44).",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "💨 YHWH - The True Source of Breath",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "God does not live in temples built by human hands. He Himself gives everyone life, breath, and everything else. (Acts 17:24-25)\n\n" +
                           "The name YHWH contains semi-consonants and semi-vowels. It is an aspirate word that linguists say is impossible to speak without breathing. The very first breath we take is breathing out YHWH.\n\n" +
                           "Therefore, when life knocks the breath out of you, stop running to different habits or distractions to get it back! Run to the CREATOR so He can breathe His supernatural LIFE back into you.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Which biblical figure's mental struggle helps you realize that mental battle does not mean you lack faith?",
            "When life knocks the breath out of you, what is your plan to run straight to YHWH instead of other coping mechanisms?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_7_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your reflection...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_7_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonEightContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🛡️ Lesson 8: Freedom from Fear (Nakia's Miracle)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Shane and LaToya's testimony of how God prepares us for tough situations and provides supernatural peace in crises.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🚨 The Wreck & The Miracle:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "During a Thursday night OverComer meeting, Shane felt a strong impression to teach on fear, and read 33 select scriptures on fear back-to-back to the class. Little did he know, he was being prepared for Friday.\n\n" +
                           "On Friday, Shane and LaToya received a panic call: their daughter NaKia had been in a devastating car wreck. When they arrived, the car was destroyed, Nakia's face was bloody, and she was crying in pain.\n\n" +
                           "At the hospital, scans showed Nakia had severe brain bleeding and several broken bones in her face. Triggered by PTSD from losing two infant daughters in the past, Shane began to struggle with panic. But he called his pastor, prayed, and stood firm.\n\n" +
                           "The next morning, scans showed a miracle: the brain bleeding had fully stopped, and her facial fractures were completely gone, leaving only a broken nose! She came home on Sunday. God is faithful! He prepares us in advance for the broken and fearful times if we pay attention to His Word.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 33 Verses of Victory Over Fear (Slay the Giants):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                val verses = listOf(
                    "Isaiah 41:10 — So do not fear, for I am with you; do not be dismayed, for I am your God.",
                    "Psalm 56:3 — When I am afraid, I put my trust in you.",
                    "Philippians 4:6-7 — Do not be anxious about anything, but present your requests to God...",
                    "John 14:27 — Peace I leave with you; my peace I give you. Do not let your hearts be troubled.",
                    "2 Timothy 1:7 — For God has not given us a spirit of fear, but of power, love, and a sound mind.",
                    "1 John 4:18 — There is no fear in love. But perfect love drives out fear...",
                    "Psalm 94:19 — When anxiety was great within me, your consolation brought joy to my soul.",
                    "Isaiah 43:1 — Fear not, for I have redeemed you; I have summoned you by name; you are mine.",
                    "Proverbs 12:25 — An anxious heart weighs a man down, but a kind word cheers him up.",
                    "Psalm 23:4 — Even though I walk through the valley of the shadow of death, I will fear no evil...",
                    "Joshua 1:9 — Be strong and courageous. Do not be terrified... the Lord is with you wherever you go."
                )

                verses.forEach { verse ->
                    Text(
                        text = "• $verse",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Access the full 33 verses at crosswalk.com or our shared OverComer document to shield your mind daily from sudden fear.",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "How does Shane's testimony of God preparing him on Thursday for Friday's storm change how you view consistent prayer and Bible study?",
            "Pick one of the verses on fear. Write it down here and speak it out loud. Why does this verse comfort you?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_8_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your declaration...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_8_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonNineContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "⛓️ Lesson 9: Overcoming Guilt & Shame",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Differentiating conduct (guilt) from identity (shame) using biblical wisdom and Brené Brown’s research.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚖️ Guilt vs. Shame — What's the Difference?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Guilt relates to our conduct. It’s an awareness of wrongdoing: \"I made a bad choice.\" Guilt can be healthy if it serves as a built-in warning system leading to genuine repentance. Guilt requires confession, mercy, and forgiveness.\n\n" +
                           "• Shame relates to our identity. It’s an unhealthy feeling that: \"I AM a bad person.\" Shame makes you feel dirty, exposed, humiliated, and unworthy. It causes you to lie, hide (just as Adam and Eve hid in the garden because they were ashamed), and isolate yourself.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🧼 The Antidote to Shame:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Shame researcher Brené Brown states that shame needs three things to grow exponentially in our lives: SECRECY, SILENCE, and JUDGMENT.\n\n" +
                           "The antidote to shame is speaking it aloud to an empathetic listener. Shame simply cannot survive connection and empathy. When we bring our darkest secrets into the light and share them with a trusted friend or spiritual companion, the hold of shame is instantly broken.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 Scriptural Foundations of Freedom:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Isaiah 43:25 — \"I, even I, am he who blots out your transgressions for my own sake, and remembers your sins no more.\"\n\n" +
                           "• Romans 8:1 — \"There is now no condemnation for those who are in Christ Jesus.\"\n\n" +
                           "• Psalm 32:5 — \"Then I acknowledged my sin to you and did not cover up my iniquity... and you forgave the guilt of my sin.\"\n\n" +
                           "• Psalm 103:12 — \"As far as the east is from the west, so far has he removed our transgressions from us.\"\n\n" +
                           "• 2 Corinthians 5:17 — \"So then, if anyone is in Christ, he is a new creation; the old has gone, the new is here!\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Shame tells you 'You are not good enough' or 'Who do you think you are?' triggering isolation. What is one area of shame you are ready to bring out of secrecy?",
            "Dwell on Romans 8:1 ('There is now no condemnation'). Write an honest declaration releasing your guilt and accepting Christ's full forgiveness."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_9_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_9_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonTenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🌈 Lesson 10: The Emotions of the Journey",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Navigate the 12 emotional milestones on the path to freedom, knowing God is perpetually with you.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "📖 Scriptural Foundation & Perpetual Presence",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Matthew 28:20 — \"...And lo, I am with you always [remaining with you perpetually—regardless of circumstance, and on every occasion], even to the end of the age.\"\n\n" +
                           "No matter which emotional storm you encounter, your Savior resides in the vehicle with you, sustaining your faith.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "👑 Case Study: The Rich Young Ruler (Matthew 19:16-26)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The rich young man wanted to obtain eternal life, claiming to keep all commandments. But when Jesus challenged him to part with what he valued most (possessions/status as comfort/security), he left grieving and distressed.\n\n" +
                           "\"With people it is impossible, but with God all things are possible.\" In recovery, what comfort are we holding on to? When we place our ultimate faith in anything other than Christ, we invite spiritual grief.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🧭 The 12 Emotional Milestones Encountered:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val stages = listOf(
                    "1. Fear" to "Scared to start / of potential failure.",
                    "2. Guilt/Shame" to "Feelings of inadequacy; not working hard enough.",
                    "3. Regret" to "Bitterly mourning where you could have been by now.",
                    "4. Courage" to "Gaining early wins and building solid spiritual confidence.",
                    "5. Desire" to "Seeing light at the end of the tunnel and pressing forward.",
                    "6. Excitement" to "Receiving multiple victories, driving fresh encouragement.",
                    "7. Pride" to "Celebrating your hard work and divine milestones.",
                    "8. Anger" to "A dip in performance or progress. The scale/setback adds tension; you need your Word (Your Mentor).",
                    "9. Willingness" to "Rediscovering resolve: 'I can truly do this through Christ.'",
                    "10. Acceptance" to "Learning from the past; recognizing that you cannot do it alone.",
                    "11. Freedom" to "The sweet, victorious relief of breaking all active chains.",
                    "12. Giving Back" to "Desiring and being fully equipped to reach back and lift others up!"
                )

                stages.forEach { (title, desc) ->
                    Column {
                        Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Which of the 12 emotional stages are you currently experiencing? Why?",
            "The Rich Young Ruler grieved because his security was in possessions rather than Christ. What security are you holding on to that you need to surrender to God?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_10_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your honest reflection...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_10_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonElevenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    var dollarState by remember { mutableStateOf(1) } // 1: New, 2: Old, 3: Dirty, 4: Stomped, 5: Torn

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "💸 Lesson 11: True Repentance & Your Infinite Value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Grasp the radical nature of turning to God, and realize your value block is defined by your Creator, never your condition.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚙️ What True Repentance Looks Like",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Repentance is turning from sin, unrighteous surrounding ways, and corrupt generations—it is active conversion. It means turning to God, listening entirely to Christ's teachings, and walking toward sincere, daily obedience.\n\n" +
                           "• Accepting Christ as Lord of one's life means shifting lordship from Satan (Eph 2:2) to the lordship of Christ and His Word (Acts 26:18).\n" +
                           "• Mere saving faith with no radical break from sin is incomplete. Faith that includes deep, sincere repentance is always the requirement for salvation.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📖 The Word of Repentance",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Acts 3:19 — \"Repent, then, and turn to God, so that your sins may be wiped out, that times of refreshing may come from the Lord.\"\n\n" +
                           "• Psalm 51:1-2 — \"Have mercy upon me, O God... blott out my transgressions. Wash me thoroughly from mine iniquity, and cleanse me from my sin.\"\n\n" +
                           "• Isaiah 1:18 — \"Come now, let us settle the matter... Though your sins are like scarlet, they shall be as white as snow; though they are red as crimson, they shall be like wool.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "💵 Interactive Illustration: The Dollar Bill's Worth",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Consider a dollar bill. If it gets old, is it worth less? If it gets dirty, is it worth less? If it is dropped, stepped on, stomped on, and torn, is it worthless? NO! Every store will take that dollar because its worth is NOT in its condition. Its worth is defined by who created it and stands behind it.\n\n" +
                           "Your life may feel dirty, stomped on by trauma, or torn apart by addiction. But your value to God is eternal! He sent His Son to put you back together, and His Holy Spirit to guide you.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                // Interactive selector demonstration of Value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val states = listOf("🆕 Crisp", "🗂️ Old", "💩 Dirty", "👟 Stomped", "🩹 Torn")
                    states.forEachIndexed { sIdx, name ->
                        val active = dollarState == (sIdx + 1)
                        FilterChip(
                            selected = active,
                            onClick = { dollarState = sIdx + 1 },
                            label = { Text(name, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = when (dollarState) {
                                1 -> "💵 BRAND NEW crisp dollar: VALUE IS 100%!"
                                2 -> "🗂️ Old, wrinkled dollar: VALUE IS STILL 100%!"
                                3 -> "💩 Covered in dirt and mud: VALUE IS STILL 100%!"
                                4 -> "👟 Stomped and crushed into the concrete: VALUE IS STILL 100%!"
                                else -> "🩹 Torn, taped together, and damaged: VALUE IS STILL 100%!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Value Verified", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Recognizing that saving faith requires a radical break with sin, what sinful pattern are you fully repenting of right now?",
            "Reflecting on the dollar bill illustration, how does knowing that your value depends entirely on your Creator (not your shape/dirt) help you forgive yourself?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_11_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_11_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonTwelveContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🎯 Lesson 12: Facing Your Faults & Confession",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Gather courage to dismantle secrecy and shame. Experience spiritual healing by bringing faults into God's light.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "🛠️ 3 Steps to Face Your Faults (Pastor Wayne Cordeiro)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "When we face our faults with courage, God performs a supernatural exchange:\n\n" +
                           "1. Recognize Your Faults: Drop the excuses, defensive shield, and blaming strategies.\n" +
                           "2. Emphasize His Lead: Settle your eyes on God's correction and guidelines.\n" +
                           "3. God Will Maximize Your Obedience: He converts your faults into assets, and your weakness into pure, unshakeable strength!",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🧼 Demystifying Acknowledgment & Confession",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Acknowledge is a legal term: admission, avowal, recognition of authority, and declaring your act to give it legal validity.\n\n" +
                           "• James 5:16 — \"Therefore confess your sins to each other and pray for each other so that you may be healed. The prayer of a righteous person is powerful and effective.\"\n\n" +
                           "• Proverbs 28:13 — \"Whoever conceals their sins does not prosper, but the one who confesses and renounces them finds mercy.\"\n\n" +
                           "• Psalm 32:3-5 — \"When I kept silent, my bones wasted away through my groaning all day long... Then I acknowledged my sin to you and did not cover up my iniquity... And you forgave the guilt of my sin.\"\n\n" +
                           "• Acts 19:18 — \"Many of those who believed now came and openly confessed what they had done.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "How has trying to keep silent or concealing your issues negatively impacted your physical and mental health (Psalm 32)?",
            "According to Cordeiro, God maximizes obedience by turning weaknesses into assets. What is a specific weakness you are committing to surrender to Him?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_12_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your confession/thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_12_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonThirteenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🔥 Lesson 13: Overcoming Temptation & Mentorship",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Study Christ's battles with temptational triggers, the sacrament of Communion, and the power of godly pastors.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⚔️ How Christ Defeated Temptation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• Matthew 4:1-11 (The Wilderness): Jesus defeated Satan's triggers by declaring: \"It is written!\" of God's Word directly back to the enemy. He relied entirely on written command.\n\n" +
                           "• Luke 22:39-46 (Gethsemane): Jesus faced the ultimate emotional battle. Grieved to death, sweating blood, He prayed: \"Not my will, but Yours be done.\" In Gethsemane, He teach us that prayer and submission is the shield against temptation.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "🍞 Communion & Fellowship (1 Corinthians 11:23-26)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "\"This is my body, which is for you; do this in remembrance of me... Whenever you eat this bread and drink this cup, you proclaim the Lord's death until He comes.\"\n\n" +
                           "Communion establishes the covenant family, reminding us daily of the broken body that bought our complete healing.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "👔 Tribute to Rev DJ Byrd (Holding Your Head Up)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "We are blessed to have pastors who will go to any length to reach the lost. Rev DJ Byrd (Senior Pastor of McColl PH Church) has preaching power, but is also known for cutting the grass, cleaning the church, and standing with people when no one else would give them a chance.\n\n" +
                           "When Shane wanted to quit, Pastor DJ wouldn't allow him. He encouraged, supported, and held Shane's head up through major dark storms. We must honor the leaders God commissions to pull us up.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "Jesus said 'It is written' in the wilderness, and 'not my will, but Yours' in Gethsemane. How can you combine scripture study and complete prayer to beat your triggers?",
            "Write a personal prayer or letter of appreciation for Pastor DJ Byrd (or a mentor who didn't let you quit during your dark days)."
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_13_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_13_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun LessonFourteenContent(prefs: android.content.SharedPreferences, onStatusChange: (Boolean) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "✈️ Lesson 14: Follow the Voice (The Anchorage Miracle)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "A thrilling testimony of survival, disorientation, and why trusting the Voice of your Instructor is the difference between life and death.",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "⛈️ Lost in the Alaskan Clouds",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "A traveler in the Aleutian Islands was offered a cheaper ride in a tiny personal plane instead of a commercial airline. Against his better judgment, he said yes. A few minutes into flight, they flew into blinding clouds. Suddenly, the pilot turned and said: \"I can't fly in clouds, they make me pass out,\" and passed out cold in the seat!\n\n" +
                           "Desperately shaking the pilot, with zero flight training, the passenger grabbed the radio microphone and screamed for help. A freight pilot flying to Tokyo heard him first, put his massive 747 in a loop, and connected him with Anchorage Emergency.",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "📻 'My Job is to Get You Home Safe. Promise Me You'll Obey My Voice.'",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The voice at Anchorage emergency gave a strict condition:\n" +
                           "\"My job is to get you home safe. If you want me to get you home safe, you gotta promise me you'll obey my voice. You can't see me, but I can see you on radar. If you don't obey my voice, you're gonna die.\"\n\n" +
                           "Disoriented in absolute darkness, the passenger wanted to steer by feelings. But the voice warned: \"You're 4 minutes from hitting a mountain/crashing. Don't look at anything outside. Don't watch the storm. Just listen to my voice, and I'll take you through.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = "✝️ The Cross is the Way Home",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Other night cargo pilots over Alaska chimed in: \"Trust the voice, that is the key.\"\n\n" +
                           "Under strict audio control, the instructor lined them up directly down the runway. He said: \"At the foot of the runway, there are lights in the shape of a CROSS. Don't you forget: THE CROSS IS THE WAY HOME.\"\n\n" +
                           "Guided by the Voice, they descended and landed safely (seven bumpy bounces!). The moment they stopped, the pilot woke up. The controller's final message was a weeping warning: \"I watch them crash and burn all the time because they won't follow my voice. They get other voices in their heads and they self-destruct. Thank you for listening.\"\n\n" +
                           "\"My sheep hear my voice, and they follow me.\"",
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }

        Text(
            text = "🙋 PERSONAL REFLECTION:",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        val questions = listOf(
            "The aircraft controller told the disoriented passenger, 'If you start watching the storm, you will die. Focus on my voice.' What storm in your life is currently distracting you from God’s Voice?",
            "At the foot of the Anchorage runway, a massive Cross of lights led them home. How does keeping your eyes permanently on the Cross of Christ prevent you from self-destructing?"
        )

        questions.forEachIndexed { idx, question ->
            val answerKey = "lesson_14_q_$idx"
            var answerText by remember(answerKey) {
                mutableStateOf(prefs.getString(answerKey, "") ?: "")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${idx + 1}. $question",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        prefs.edit().putString(answerKey, it).apply()
                    },
                    placeholder = { Text("Write your thoughts...", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.fillMaxWidth().testTag("recovery_input_lesson_14_q_$idx"),
                    textStyle = MaterialTheme.typography.bodySmall,
                    maxLines = 4
                )
            }
        }
    }
}

@Composable
fun TestimonyVictoryBoard(viewModel: OverComerViewModel, onNavigateToChat: () -> Unit) {
    val context = LocalContext.current
    var testimonyInput by remember { mutableStateOf("") }
    var activeQuoteIndex by remember { mutableStateOf(0) }

    val quotes = remember {
        listOf(
            "\"God is not just a God of survival; He is the God of absolute deliverance and victory.\"\n— David Wilkerson (Teen Challenge)",
            "\"The blood of the Lamb has paid for your freedom, and the word of your testimony confirms it.\"\n— OverComer Guide (Revelation 12:11)",
            "\"We don't fight FOR victory; we walk in the victory already secured by Jesus on the cross.\"\n— OverComer Creed",
            "\"Our trials are the canvas on which God paints His greatest testimonies of grace.\"\n— Paul David Tripp",
            "\"Genuine surrender to Jesus breaks every chain instantly. You are unconditionally free.\"\n— David Wilkerson (Teen Challenge)",
            "\"He who began a good work in you will carry it on to completion until the day of Christ Jesus.\"\n— Philippians 1:6"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("testimony_victory_board_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDF5), // Warm, slightly yellow/gold background
            contentColor = Color(0xFF5D4037) // Warm brown text color
        ),
        border = BorderStroke(1.5.dp, Color(0xFFFFA000).copy(alpha = 0.6f)) // Amber border
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Victory Icon",
                    tint = Color(0xFFFFA000), // Gold
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "VICTORY & TESTIMONY BOARD",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = "Celebrate His Faithfulness",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF795548)
                    )
                }
            }

            // Quotes Carousel Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFB300).copy(alpha = 0.08f))
                    .border(0.5.dp, Color(0xFFFFB300).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = quotes[activeQuoteIndex],
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 20.sp
                        ),
                        color = Color(0xFF4E342E),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quote cycle button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                activeQuoteIndex = (activeQuoteIndex + 1) % quotes.size
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE65100))
                        ) {
                            Text("Next Quote ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFFFB300).copy(alpha = 0.2f))

            // Action: Converse about Victorious Day
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Converse & Rejoice",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "Open up your OverComer Companion Chat to talk about your triumphs today, share how you overcame obstacles, and praise God together!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037)
                )

                Button(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testimony_converse_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Text("Share Victory with Companion ➔", fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFFFB300).copy(alpha = 0.2f))

            // Action: Log a Victory/Testimony entry to the Secure Journal
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Record Today's Victory",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "Record today's breakthroughs so you can look back and remember how God delivered you. Saved in your combined private journal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5D4037)
                )

                OutlinedTextField(
                    value = testimonyInput,
                    onValueChange = { testimonyInput = it },
                    placeholder = {
                        Text(
                            "What is your breakthrough or testimony today? E.g., 'Experienced complete peace and overcame temptation at work today! Glory to God!'",
                            fontSize = 13.sp,
                            color = Color(0xFF8D6E63)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testimony_input_field"),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFB300),
                        unfocusedBorderColor = Color(0xFFFFB300).copy(alpha = 0.5f)
                    )
                )

                Button(
                    onClick = {
                        if (testimonyInput.isNotBlank()) {
                            viewModel.addVictoryLog(
                                type = "JOURNAL_SECURE",
                                notes = testimonyInput
                            )
                            testimonyInput = ""
                            Toast.makeText(context, "Victory testimony recorded securely in your Private Journal! 🏆📖", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("testimony_save_btn"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = testimonyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Log in Secure Private Journal 🔐", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportGroupLocatorSection(viewModel: OverComerViewModel) {
    val context = LocalContext.current
    var locationInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Celebrate Recovery") } // "Celebrate Recovery", "Christian Support Groups", "Find a Church"
    var prioritizeAlignment by remember { mutableStateOf(true) }
    
    val searchResults by viewModel.localResources.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearchingResources.collectAsStateWithLifecycle()

    val categories = listOf("Celebrate Recovery", "Christian Support Groups", "Find a Church")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("support_locator_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Locator Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "SUPPORT & CHURCH LOCATOR",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Enter your Zip Code or City to find active Christ-centered support groups, Celebrate Recovery meetings, and local Bible-believing churches close to you.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Category Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val displayLabel = when (cat) {
                        "Celebrate Recovery" -> "Celebrate Recovery"
                        "Christian Support Groups" -> "Support Groups"
                        else -> "Find a Church ⛪"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { 
                            selectedCategory = cat
                            if (locationInput.isNotBlank()) {
                                val apiCategory = if (cat == "Find a Church") "Churches" else cat
                                viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment)
                            }
                        },
                        label = {
                            Text(
                                text = displayLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Alignment Option (Discreet checkbox/toggle for custom searches)
            if (selectedCategory == "Find a Church" || selectedCategory == "Christian Support Groups") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = prioritizeAlignment,
                        onCheckedChange = { 
                            prioritizeAlignment = it
                            if (locationInput.isNotBlank()) {
                                val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                                viewModel.searchLocalResources(locationInput, apiCategory, it)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Column {
                        Text(
                            text = "Prioritize fellowships aligned with OverComer's framework",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (prioritizeAlignment) 
                                "Focuses on ministries closely familiar with complete freedom & spirit-filled walk." 
                                else "Displays a broader list of all bible-believing denominations (Baptist, General, etc.).",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    placeholder = { Text("E.g. 29601 or Greenville, SC", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("locator_location_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (locationInput.isNotBlank()) {
                                val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                                viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment)
                            }
                        }
                    )
                )

                Button(
                    onClick = {
                        if (locationInput.isNotBlank()) {
                            val apiCategory = if (selectedCategory == "Find a Church") "Churches" else selectedCategory
                            viewModel.searchLocalResources(locationInput, apiCategory, prioritizeAlignment)
                        } else {
                            Toast.makeText(context, "Please enter a location first", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("locator_search_btn")
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Search Results Container
            if (searchResults.isNotEmpty()) {
                val displayCategoryName = if (selectedCategory == "Find a Church") "Christian Churches" else selectedCategory
                Text(
                    text = "Nearby matches for '$displayCategoryName' near '$locationInput':",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                searchResults.forEach { resource ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = resource.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = when (resource.type) {
                                                "Celebrate Recovery" -> "CR Group"
                                                "Christian Support Group" -> "Support Group"
                                                else -> "Church"
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // Address Row
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location Pin",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = resource.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Details Row
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Details",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = resource.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }

                            // Contact Row
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Contact Info",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = resource.contact,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Directions Action Button
                            Button(
                                onClick = {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(resource.directionUrl)
                                    )
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Directions,
                                        contentDescription = "Directions",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text("Get Directions & Details 🗺️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else if (isSearching) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Searching local Christian databases...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun PostIncarcerationSupportSection() {
    var expandedSection by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("post_incarceration_support_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Release Support Icon",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "REENTRY & TRANSITION FREEDOM",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "Transitioning back to society after long-term incarceration can feel overwhelming. If you feel like 'something is broken' inside or you are struggling to adjust, know that you are not alone, and you are NOT permanently broken. Here are powerful tools and guidance designed specifically to help you walk in full freedom.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Section 1: Understanding Post-Incarceration Challenges (PICS)
            PostIncarcerationSectionHeader(
                title = "1. Understanding Post-Incarceration Struggles",
                isExpanded = expandedSection == 1,
                onClick = { expandedSection = if (expandedSection == 1) null else 1 }
            )
            AnimatedVisibility(visible = expandedSection == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "After years in a hyper-controlled, high-vigilance prison environment, your mind and nervous system naturally adapted to survive. Returning to the fast-paced, choice-heavy outside world can make you feel:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        BulletPointItem("Hypervigilance: Constantly scanning rooms, sitting facing doors, or feeling suspicious of others' intentions.")
                        BulletPointItem("Sensory Overload: Overwhelmed by bright lights, loud/sudden noises, crowds, or traffic.")
                        BulletPointItem("Decision Fatigue: Struggling or feeling anxious when faced with everyday choices that others take for granted.")
                        BulletPointItem("Emotional Numbing: Difficulty connecting with loved ones or feeling flat, a shield used to survive inside.")
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 Spiritual Truth: This is not a biological disease that makes you permanently broken. It is a natural response to long-term survival mode. 2 Corinthians 5:17 promises that you are a new creation in Christ. God's grace can completely retrain your mind and restore your peace.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Section 2: Thought Reframing (Lie-to-Truth Transformation)
            PostIncarcerationSectionHeader(
                title = "2. Renewing Your Mind (Lie Transformation)",
                isExpanded = expandedSection == 2,
                onClick = { expandedSection = if (expandedSection == 2) null else 2 }
            )
            AnimatedVisibility(visible = expandedSection == 2) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "To walk in lasting freedom, we must identify internal 'prison rules' and replace them with Christ's truth:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LieToTruthItem(
                            lie = "I am institutionalized and will never fully adapt. I'm broken forever.",
                            truth = "I am a new creation (2 Corinthians 5:17). God is working in me, renewing my mind day by day, restoring everything the locusts have eaten."
                        )
                        LieToTruthItem(
                            lie = "I must keep my guard up and trust absolutely no one to stay safe.",
                            truth = "While wisdom is necessary, Christ is my ultimate protector. I can safely build healthy, Christ-centered boundaries with a trustworthy brotherhood."
                        )
                        LieToTruthItem(
                            lie = "I am a burden to my family and society because of my past.",
                            truth = "God has a custom, redemptive purpose for my life (Ephesians 2:10). My testimony of deliverance has immense power to help free others."
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Section 3: Calming Grounding (Handling Sensory Overload)
            PostIncarcerationSectionHeader(
                title = "3. Calming Grounding (Sensory Overload Tool)",
                isExpanded = expandedSection == 3,
                onClick = { expandedSection = if (expandedSection == 3) null else 3 }
            )
            AnimatedVisibility(visible = expandedSection == 3) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "When crowds, traffic, or loud environments trigger anxiety or panic, use these quick physical and spiritual grounding steps to tell your nervous system that you are safe in the present moment:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        BulletPointItem("1. Stop & Pause: Step away from the crowd or sit in a quiet spot if possible.")
                        BulletPointItem("2. The 3-3-3 Rule: Look around and name 3 things you can see, 3 things you can hear, and physically touch or move 3 parts of your body (e.g. tap your foot, roll your shoulders). This brings your mind out of survival-mode memory and back to current safety.")
                        BulletPointItem("3. Paced Breathing: Inhale slowly for 4 seconds, hold for 4, and exhale for 4. Tell your body: 'I am safe here, God is with me.'")
                        BulletPointItem("4. Anchor Scripture: Recite Psalm 46:10 in your mind: 'Be still, and know that I am God.'")
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Section 4: Reentry Community & Discipleship Support
            PostIncarcerationSectionHeader(
                title = "4. Reentry Mentorship & Supportive Networks",
                isExpanded = expandedSection == 4,
                onClick = { expandedSection = if (expandedSection == 4) null else 4 }
            )
            AnimatedVisibility(visible = expandedSection == 4) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Walking alone is the primary cause of struggles. Active redemptive community is essential:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPointItem("Establish a Stable Daily Routine: Structure your day with set times for prayer, bible reading, exercise, and fellowship. Routine restores a feeling of safety and control.")
                        BulletPointItem("Seek a Mature Mentor: Find a pastor, a spiritual leader, or a brother/sister who has walked a similar path of long-term freedom.")
                        BulletPointItem("Reentry Support Groups: Join specialized Christian reentry support networks like Prison Fellowship, local Teen Challenge outreach centers, or Celebrate Recovery groups.")
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "💡 Locator Hint:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Use the Support & Church Locator above to find active Celebrate Recovery groups, Christian support networks, and local bible-believing fellowships near your zipcode that will welcome you with open arms and walk with you without judgment.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostIncarcerationSectionHeader(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BulletPointItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun LieToTruthItem(lie: String, truth: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Lie",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Prison Lie: \"$lie\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    lineHeight = 16.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Truth",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Biblical Freedom: \"$truth\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}



