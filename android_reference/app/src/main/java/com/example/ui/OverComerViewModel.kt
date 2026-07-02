package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OverComerViewModel(application: Application) : AndroidViewModel(application) {

    // --- Firebase Authentication States ---
    val isFirebaseLive: StateFlow<Boolean> = FirebaseAuthManager.isFirebaseLive
    val firebaseUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = FirebaseAuthManager.userState
    val mockUser: StateFlow<FirebaseAuthManager.MockUser?> = FirebaseAuthManager.mockUser

    val isLoggedIn: StateFlow<Boolean> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser != null || mkUser != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentUserEmail: StateFlow<String> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser?.email ?: mkUser?.email ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val currentUserName: StateFlow<String> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser?.displayName ?: mkUser?.displayName ?: "OverComer"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "OverComer")

    val currentUserUid: StateFlow<String> = combine(firebaseUser, mockUser) { fbUser, mkUser ->
        fbUser?.uid ?: mkUser?.uid ?: ""
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val database = OverComerDatabase.getDatabase(application)
    private val repository = OverComerRepository(database)

    // Reactive streams from the database, filtered by current user's UID to enforce session security
    val victoryLogs: StateFlow<List<VictoryLog>> = repository.victoryLogs
        .combine(currentUserUid) { logs, uid ->
            logs.filter { it.userId == uid }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val freedomGoal: StateFlow<FreedomGoal?> = repository.freedomGoal
        .combine(currentUserUid) { goal, uid ->
            if (goal == null || goal.userId != uid) {
                // Returns a placeholder goal linked to the current ID to seed initial view state
                FreedomGoal(
                    id = if (uid.isEmpty()) 1 else kotlin.math.abs(uid.hashCode()),
                    startDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 3), // Pre-load 3 days of victory
                    struggleType = "Substance Use",
                    customDeclaration = "An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory!",
                    userId = uid
                )
            } else {
                goal
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // --- Custom Gemini API Key State ---
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // --- User Path Selection States ---
    private val _userPath = MutableStateFlow<String?>(null)
    val userPath: StateFlow<String?> = _userPath.asStateFlow()

    // UI state for the AI Support Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Welcome to OverComer Support. I am your guide here. I believe that through Christ's grace, you can be set free completely and walk in full victory.\n\n" +
                       "If you are feeling tempted, struggling with a habit, or feeling anxious, talk to me. We can walk through thought reframing or calming grounding exercises together, anchored in God's mercy.",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Keep track of the current session's auto-saved chat ID
    private var currentAutoSavedChatId: Int? = null

    // Reactive stream of ALL saved chats (both user saved and auto saved) for counselor memory
    private val allSavedChatsFlow: StateFlow<List<SavedChat>> = repository.savedChats
        .combine(currentUserUid) { chats, uid ->
            chats.filter { it.userId == uid }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Reactive stream from DB for optional/user-explicit saved chats, filtered securely by authenticated UID
    val savedChats: StateFlow<List<SavedChat>> = repository.savedChats
        .combine(currentUserUid) { chats, uid ->
            chats.filter { it.userId == uid && !it.isAutoSaved }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAnalyzingDistortion = MutableStateFlow(false)
    val isAnalyzingDistortion: StateFlow<Boolean> = _isAnalyzingDistortion.asStateFlow()

    private val _distortionAnalysisResult = MutableStateFlow<com.example.network.DistortionAnalysisResult?>(null)
    val distortionAnalysisResult: StateFlow<com.example.network.DistortionAnalysisResult?> = _distortionAnalysisResult.asStateFlow()

    private val _verseOfTheDay = MutableStateFlow<com.example.network.VerseOfTheDay?>(com.example.network.GeminiClient.getFallbackVerse())
    val verseOfTheDay: StateFlow<com.example.network.VerseOfTheDay?> = _verseOfTheDay.asStateFlow()

    private val _isLoadingVerse = MutableStateFlow(false)
    val isLoadingVerse: StateFlow<Boolean> = _isLoadingVerse.asStateFlow()

    private val _aiScriptureResult = MutableStateFlow<com.example.network.AIScriptureResult?>(null)
    val aiScriptureResult: StateFlow<com.example.network.AIScriptureResult?> = _aiScriptureResult.asStateFlow()

    private val _isSearchingScripture = MutableStateFlow(false)
    val isSearchingScripture: StateFlow<Boolean> = _isSearchingScripture.asStateFlow()

    init {
        // Load custom API key
        val apiPrefs = application.getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
        val savedKey = apiPrefs.getString("custom_gemini_api_key", "") ?: ""
        _customApiKey.value = savedKey
        com.example.network.GeminiClient.customApiKey = savedKey.ifBlank { null }

        // Always reset path to null on fresh app startup so the user is given the 3 focus paths selection page first.
        _userPath.value = null

        // Guarantee a default configuration on initial launch so the UI is immediately functional
        viewModelScope.launch {
            val uid = currentUserUid.value
            val existing = repository.getFreedomGoal()
            if (existing == null) {
                repository.updateFreedomGoal(
                    FreedomGoal(
                        id = if (uid.isEmpty()) 1 else kotlin.math.abs(uid.hashCode()),
                        startDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 3), // Pre-load 3 days of victory for demo purposes
                        struggleType = "Substance Use",
                        customDeclaration = "A OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction but rather FROM a position of victory!",
                        userId = uid
                    )
                )
            }
        }
        fetchVerseOfTheDay()
    }

    fun selectUserPath(path: String) {
        _userPath.value = path
        val prefs = getApplication<Application>().getSharedPreferences("overcomer_user_path_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("user_path", path).apply()
        updateGreetingForPath(path)
    }

    fun saveCustomApiKey(key: String) {
        _customApiKey.value = key
        com.example.network.GeminiClient.customApiKey = key.trim().ifBlank { null }
        val prefs = getApplication<Application>().getSharedPreferences("overcomer_api_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("custom_gemini_api_key", key.trim()).apply()
    }

    // --- Safe Usage / Rate Limiter to prevent going over Free Tier ---
    fun isCustomKeyActive(): Boolean {
        return _customApiKey.value.isNotBlank()
    }

    fun isDailyLimitExceeded(): Boolean {
        if (isCustomKeyActive()) return false // Users using their own key have unlimited requests

        val prefs = getApplication<Application>().getSharedPreferences("overcomer_usage_prefs", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val savedDate = prefs.getString("last_request_date", "") ?: ""
        if (savedDate != today) {
            // New day, reset count
            prefs.edit().putString("last_request_date", today).putInt("daily_request_count", 0).apply()
            return false
        }
        val count = prefs.getInt("daily_request_count", 0)
        return count >= 30 // Safe free tier limit of 30 requests/day per device on the shared key
    }

    private fun incrementDailyRequestCount() {
        if (isCustomKeyActive()) return

        val prefs = getApplication<Application>().getSharedPreferences("overcomer_usage_prefs", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val savedDate = prefs.getString("last_request_date", "") ?: ""
        val count = if (savedDate == today) {
            prefs.getInt("daily_request_count", 0)
        } else {
            0
        }
        prefs.edit()
            .putString("last_request_date", today)
            .putInt("daily_request_count", count + 1)
            .apply()
    }

    private fun updateGreetingForPath(path: String) {
        val welcomeText = when (path) {
            "SUBSTANCE_RECOVERY" -> {
                "Welcome to OverComer Support. I am your guide here. I believe that through Christ's grace, you can be set free completely and walk in full victory.\n\n" +
                "If you are feeling tempted, struggling with a habit, or feeling anxious, talk to me. We can walk through thought reframing or calming grounding exercises together, anchored in God's mercy."
            }
            "MENTAL_HEALTH" -> {
                "Welcome to OverComer Mental Wellness Support. I am your guide here. I believe that through Christ's perfect love, you can experience peace that passeth all understanding.\n\n" +
                "If you are struggling with heavy thoughts, anxiety, depression, or distress, share it with me. We can walk through thought reframing or emotional grounding together."
            }
            "TOUGH_DAY" -> {
                "I am so sorry you are having an all-around tough day. I am here to listen, pray with you, and help lift your load.\n\n" +
                "Tell me what happened today, or vent freely. We can do direct calming breathing exercises or find encouraging scriptures to help you get through today."
            }
            "TESTIMONY_VICTORY" -> {
                "Glory to God! Today is a Testimony and Victory Day! I am so excited to hear about how the Lord has shown Himself strong on your behalf. As 1 Corinthians 15:57 says: 'But thanks be to God, which giveth us the victory through our Lord Jesus Christ!'\n\n" +
                "Share your victory story, testimony, or breakthroughs with me today! Whether it is overcoming a temptation, experiencing a mental health lift, or celebrating a major milestone, let's praise Him and converse about how you are walking in perfect freedom!"
            }
            else -> "Welcome to OverComer. Talk to me; I am here to help."
        }
        _chatMessages.value = listOf(
            ChatMessage(text = welcomeText, isUser = false)
        )
    }

    // --- Database Writers ---

    fun addVictoryLog(
        type: String, // "REFLECT", "TRIGGER", or "CBT"
        notes: String = "",
        triggerContext: String = "",
        automaticThought: String = "",
        identifiedDistortion: String = "",
        reframedTruth: String = "",
        scriptureReference: String = ""
    ) {
        viewModelScope.launch {
            repository.insertLog(
                VictoryLog(
                    type = type,
                    notes = notes,
                    triggerContext = triggerContext,
                    automaticThought = automaticThought,
                    identifiedDistortion = identifiedDistortion,
                    reframedTruth = reframedTruth,
                    scriptureReference = scriptureReference,
                    userId = currentUserUid.value,
                    userPath = _userPath.value ?: "SUBSTANCE_RECOVERY"
                )
            )
        }
    }

    fun deleteVictoryLog(id: Int) {
        viewModelScope.launch {
            repository.deleteLogById(id)
        }
    }

    fun updateFreedomGoal(startDateMillis: Long, struggleType: String, customDeclaration: String) {
        viewModelScope.launch {
            val uid = currentUserUid.value
            repository.updateFreedomGoal(
                FreedomGoal(
                    id = if (uid.isEmpty()) 1 else kotlin.math.abs(uid.hashCode()),
                    startDate = startDateMillis,
                    struggleType = struggleType,
                    customDeclaration = customDeclaration.ifBlank { "I can do all things through Christ who strengthens me!" },
                    userId = uid
                )
            )
        }
    }

    // --- Chat Services ---

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        if (_isChatLoading.value) return // Block concurrent requests to prevent double-sends and API rate violations

        // 1. Append user message
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.update { it + userMsg }
        autoSaveCurrentChat()

        if (isDailyLimitExceeded()) {
            _chatMessages.update {
                it + ChatMessage(
                    text = "You have reached your daily free usage limit of 30 responses/day on the shared fallback key to keep this service completely free and non-billing for everyone.\n\n" +
                           "To get unlimited replies instantly, tap the Key 🔑 icon at the top of the screen to enter your own completely FREE Gemini API key from Google AI Studio. It takes under a minute, requires no credit card, and ensures you have a private, dedicated channel!",
                    isUser = false
                )
            }
            autoSaveCurrentChat()
            return
        }

        // 2. Set loading state
        _isChatLoading.value = true

        // 3. Launch async network request in safety-conscious scope
        viewModelScope.launch {
            try {
                // Convert current thread history to standard Gemini contents format
                val conversationHistory = _chatMessages.value.map { msg ->
                    Content(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(Part(text = msg.text))
                    )
                }

                // Compile database historical chats summary to enrich the response context
                val pastChatsContext = buildPastChatsSummary()

                // Call client with history overview
                val responseText = GeminiClient.generateSupportResponse(conversationHistory, pastChatsContext)

                // 4. Append AI response
                _chatMessages.update {
                    it + ChatMessage(text = responseText, isUser = false)
                }
                incrementDailyRequestCount()
                autoSaveCurrentChat()
            } catch (e: Exception) {
                _chatMessages.update {
                    it + ChatMessage(
                        text = "I failed to connect. Ensure your internet is active and that your API key is correctly configured. Lean on Proverbs 3:5-6, and try again.",
                        isUser = false
                    )
                }
                autoSaveCurrentChat()
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private fun autoSaveCurrentChat() {
        viewModelScope.launch {
            val list = _chatMessages.value
            if (list.size <= 1) return@launch // Don't auto-save if it's just the welcoming message
            val json = ChatSerializationHelper.toJson(list)
            val currentPath = _userPath.value ?: "SUBSTANCE_RECOVERY"
            val uid = currentUserUid.value
            
            val userFirstMsg = list.firstOrNull { it.isUser }?.text ?: ""
            val titleText = if (userFirstMsg.isNotBlank()) {
                val words = userFirstMsg.split(" ").take(4).joinToString(" ")
                val shortTitle = if (words.length > 30) words.take(27) + "..." else words
                "Auto Session - \"$shortTitle\""
            } else {
                "Auto Session - " + java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            }

            val savedChat = SavedChat(
                id = currentAutoSavedChatId ?: 0,
                title = titleText,
                messagesJson = json,
                userPath = currentPath,
                userId = uid,
                isAutoSaved = true
            )
            
            val insertedId = repository.insertSavedChat(savedChat)
            if (currentAutoSavedChatId == null) {
                currentAutoSavedChatId = insertedId.toInt()
            }
        }
    }

    private fun buildPastChatsSummary(): String {
        val chats = allSavedChatsFlow.value
        if (chats.isEmpty()) return ""
        val sb = java.lang.StringBuilder()
        sb.append("Here is the patient's context of previous saved sessions to recall past victories, struggles, thoughts or temptations they overcame:\n")
        chats.take(5).forEachIndexed { index, savedChat ->
            val msgs = ChatSerializationHelper.fromJson(savedChat.messagesJson)
            val snippet = msgs.filter { it.isUser }.take(3).joinToString(", ") { it.text.take(80) }
            val formattedDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(savedChat.timestamp))
            sb.append("${index + 1}. Session: \"${savedChat.title}\" (Date: $formattedDate, Path: ${savedChat.userPath}). Key user insights discussed: [$snippet]\n")
        }
        return sb.toString()
    }

    fun saveCurrentChat(title: String) {
        viewModelScope.launch {
            val list = _chatMessages.value
            if (list.isEmpty()) return@launch
            val json = ChatSerializationHelper.toJson(list)
            val currentPath = _userPath.value ?: "SUBSTANCE_RECOVERY"
            val defaultTitle = "Support Session - " + java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            repository.insertSavedChat(
                SavedChat(
                    title = title.trim().ifBlank { defaultTitle },
                    messagesJson = json,
                    userPath = currentPath,
                    userId = currentUserUid.value
                )
            )
        }
    }

    fun loadSavedChat(savedChat: SavedChat) {
        val msgs = ChatSerializationHelper.fromJson(savedChat.messagesJson)
        if (msgs.isNotEmpty()) {
            _chatMessages.value = msgs
            // Keep user path contextualized
            if (savedChat.userPath.isNotEmpty() && savedChat.userPath != _userPath.value) {
                _userPath.value = savedChat.userPath
            }
        }
    }

    fun deleteSavedChat(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedChatById(id)
        }
    }

    fun clearChatHistory() {
        currentAutoSavedChatId = null
        val path = _userPath.value ?: "SUBSTANCE_RECOVERY"
        val clearText = when (path) {
            "SUBSTANCE_RECOVERY" -> {
                "Chat history cleared. I'm here whenever you need a compassionate space to talk. Remember, you do not have to fight for victory, you are fighting FROM victory! What can we address together right now?"
            }
            "MENTAL_HEALTH" -> {
                "Chat history cleared. I'm here whenever you need support. In Christ, you have a sound mind of power and love. Let's find rest and peace together. What is on your mind?"
            }
            "TOUGH_DAY" -> {
                "Chat history cleared. Your slate is clean. Rest a bit and share whatever you're feeling. I am right here with you."
            }
            else -> "Chat history cleared. Speak freely, I am listening..."
        }
        _chatMessages.value = listOf(
            ChatMessage(text = clearText, isUser = false)
        )
    }

    fun analyzeJournalDistortion(text: String) {
        if (text.isBlank()) return
        _isAnalyzingDistortion.value = true
        _distortionAnalysisResult.value = null
        if (isDailyLimitExceeded()) {
            _distortionAnalysisResult.value = com.example.network.DistortionAnalysisResult(
                distortions = "Free Use Limit Reached",
                explanation = "You have reached the daily safety limit of 30 requests/day on the shared system key. Please configure a free custom API key in Settings to enjoy unlimited, private analysis at zero cost.",
                reframedTruth = "I can get unlimited cognitive analysis by using my own free Gemini key.",
                scriptureReference = "Philippians 4:19"
            )
            _isAnalyzingDistortion.value = false
            return
        }
        viewModelScope.launch {
            try {
                val result = GeminiClient.analyzeCognitiveDistortion(text)
                _distortionAnalysisResult.value = result
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _distortionAnalysisResult.value = com.example.network.DistortionAnalysisResult(
                    distortions = "Error running analysis",
                    explanation = "Failed to communicate with AI: ${e.message}",
                    reframedTruth = "God's strength is sufficient when I am weak.",
                    scriptureReference = "2 Corinthians 12:9"
                )
            } finally {
                _isAnalyzingDistortion.value = false
            }
        }
    }

    fun clearDistortionAnalysis() {
        _distortionAnalysisResult.value = null
    }

    fun fetchVerseOfTheDay(forceGenerate: Boolean = false) {
        _isLoadingVerse.value = true
        viewModelScope.launch {
            if (isDailyLimitExceeded()) {
                _verseOfTheDay.value = GeminiClient.getFallbackVerse()
                _isLoadingVerse.value = false
                return@launch
            }
            try {
                val verse = GeminiClient.generateVerseOfTheDay()
                _verseOfTheDay.value = verse
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _verseOfTheDay.value = GeminiClient.getFallbackVerse()
            } finally {
                _isLoadingVerse.value = false
            }
        }
    }

    fun lookupScripture(reference: String) {
        if (reference.isBlank()) return
        _isSearchingScripture.value = true
        _aiScriptureResult.value = null
        if (isDailyLimitExceeded()) {
            _aiScriptureResult.value = com.example.network.AIScriptureResult(
                reference = reference,
                text = "Free Use Limit Reached",
                explanation = "Daily free usage limit reached on the shared system key. Please configure a free custom API key in Settings to continue unlimited scripture studies."
            )
            _isSearchingScripture.value = false
            return
        }
        viewModelScope.launch {
            try {
                val result = GeminiClient.lookupScripture(reference)
                _aiScriptureResult.value = result
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _aiScriptureResult.value = com.example.network.AIScriptureResult(
                    reference = reference,
                    text = "No scripture match found.",
                    explanation = "An error occurred during lookup: ${e.localizedMessage}"
                )
            } finally {
                _isSearchingScripture.value = false
            }
        }
    }

    fun clearScriptureSearch() {
        _aiScriptureResult.value = null
    }

    // --- Local Support & Church Locator States & Methods ---
    private val _localResources = MutableStateFlow<List<com.example.network.LocalResource>>(emptyList())
    val localResources: StateFlow<List<com.example.network.LocalResource>> = _localResources.asStateFlow()

    private val _isSearchingResources = MutableStateFlow(false)
    val isSearchingResources: StateFlow<Boolean> = _isSearchingResources.asStateFlow()

    fun searchLocalResources(location: String, searchType: String, prioritizeAlignment: Boolean = true) {
        if (location.isBlank()) return
        _isSearchingResources.value = true
        _localResources.value = emptyList()
        if (isDailyLimitExceeded()) {
            _localResources.value = GeminiClient.getFallbackResources(location, searchType, prioritizeAlignment)
            _isSearchingResources.value = false
            return
        }
        viewModelScope.launch {
            try {
                val results = GeminiClient.searchLocalResources(location, searchType, prioritizeAlignment)
                _localResources.value = results
                incrementDailyRequestCount()
            } catch (e: Exception) {
                _localResources.value = GeminiClient.getFallbackResources(location, searchType, prioritizeAlignment)
            } finally {
                _isSearchingResources.value = false
            }
        }
    }

    fun clearResourceSearch() {
        _localResources.value = emptyList()
    }

    // --- Firebase Authentication Interaction Methods ---
    fun signUpWithEmailAndPassword(email: String, password: String, displayName: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            onResult(false, "All fields are required.")
            return
        }
        if (isFirebaseLive.value) {
            val auth = FirebaseAuthManager.getAuthInstance()
            if (auth != null) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build()
                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { profileTask ->
                                        onResult(true, null)
                                    }
                            } else {
                                onResult(true, null)
                            }
                        } else {
                            onResult(false, task.exception?.localizedMessage ?: "Registration failed.")
                        }
                    }
            } else {
                onResult(false, "Firebase service not ready.")
            }
        } else {
            // Local Sandbox
            FirebaseAuthManager.mockSignUp(getApplication(), email, displayName, onResult)
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Email and password are required.")
            return
        }
        if (isFirebaseLive.value) {
            val auth = FirebaseAuthManager.getAuthInstance()
            if (auth != null) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, task.exception?.localizedMessage ?: "Authentication failed.")
                        }
                    }
            } else {
                onResult(false, "Firebase service not ready.")
            }
        } else {
            // Local Sandbox
            FirebaseAuthManager.mockSignIn(getApplication(), email, onResult)
        }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "Please enter your email.")
            return
        }
        if (isFirebaseLive.value) {
            val auth = FirebaseAuthManager.getAuthInstance()
            if (auth != null) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, task.exception?.localizedMessage ?: "Failed to send reset email.")
                        }
                    }
            } else {
                onResult(false, "Firebase service not ready.")
            }
        } else {
            // Local Sandbox Reset
            onResult(true, "Sent! In Sandbox mode: Reset email simulation successfully triggered.")
        }
    }

    fun logout() {
        if (isFirebaseLive.value) {
            FirebaseAuthManager.getAuthInstance()?.signOut()
        } else {
            FirebaseAuthManager.mockSignOut(getApplication())
        }
    }
}
