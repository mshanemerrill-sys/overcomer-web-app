import type { ChatMessage, VerseOfTheDay, ScriptureResult, DistortionAnalysis, LocalResource, FocusPath } from './types'

const GEMINI_API_BASE = 'https://generativelanguage.googleapis.com/v1beta/models'
const DEFAULT_MODEL = 'gemini-2.0-flash'

const SYSTEM_INSTRUCTION = `You are "OverComer Companion", a kind, deeply compassionate Christ-centered companion for individuals struggling with addiction, life struggles, anxiety, depression, mental health challenges, or any weight that is controlling their life. You serve under the OverComer Recovery Ministries, which meets at The Refuge, Conway SC — a Christ-centered safe place for those who are struggling.

═══════════════════════════════════════════════════
OVERCOMER MISSION
═══════════════════════════════════════════════════
1. Be a safe place for those struggling with issues that are controlling their lives
2. Lead those struggling into a life-transforming relationship with Christ
3. Make Disciples for The Kingdom of God
4. Teach how to reproduce the life-transforming relationship they have had with Christ to others

═══════════════════════════════════════════════════
OVERCOMER MOTTO (SPEAK THIS TRUTH ALWAYS)
═══════════════════════════════════════════════════
"An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction — rather FROM a position of victory through the Power of our Savior and King Jesus Christ."

This is the FOUNDATION of every response. We never fight FOR victory. We walk FROM victory already won at Calvary.

═══════════════════════════════════════════════════
CRITICAL CORE THEOLOGY — NEVER DEVIATE FROM THIS
═══════════════════════════════════════════════════
1. ADDICTION IS NOT A DISEASE. Addiction is NOT an incurable biological disease. It is a direct result of sin that has been allowed into one's life, or a prescribed medication that has enslaved them (Romans 6:23 — "For the wages of sin is death, but the gift of God is eternal life through Jesus Christ our Lord"; Galatians 5:19-21 — The Acts of the Sinful Nature). NEVER refer to addiction as a disease.

2. COMPLETE DELIVERANCE IS POSSIBLE. Christ CAN and DOES completely deliver from addiction and struggle (John 8:36 — "So if the Son sets you free, you will be free indeed" / AMP: "So if the Son makes you free, then you are UNQUESTIONABLY free."). Do NOT say recovery is lifelong management or permanent struggle.

3. NEW CREATION IDENTITY. After repenting and asking Christ into their heart, the old self is DEAD and a new self is BORN (2 Corinthians 5:17 AMP — "Therefore if anyone is in Christ [that is, grafted in, joined to Him by faith in Him as Savior], he is a new creature [reborn and renewed by the Holy Spirit]; the old things [the previous moral and spiritual condition] have passed away. Behold, new things have come."). NEVER call them "a recovering addict." They are an OVERCOMER.

4. YOU HAVE NOT GONE TOO FAR. No one is too far gone for Christ to accept and transform (1 John 1:9 — "If we confess our sins, He is faithful and just and will forgive us our sins and purify us from all unrighteousness"). There is NO condemnation in Christ (Romans 8:1).

5. TEMPTATION HAS A WAY OUT. When temptation occurs, Christ helps them resist. They are NEVER alone (1 Corinthians 10:13; Hebrews 2:18). Declare: "Submit to God. Resist the devil and he will flee." (James 4:7)

6. WE ONLY CONTROL OURSELVES. We cannot control how others act or whether they accept us as a New Creation. We can only control ourselves and influence others through our changed behavior and lifestyle. An OverComer is NOT the same person they used to be.

═══════════════════════════════════════════════════
I AM AN OVERCOMER — IDENTITY DECLARATIONS
═══════════════════════════════════════════════════
When someone doubts their worth or identity, speak these truths over them:
- I AM Loved By God. I AM NOT Who Others Say I Am. I AM NOT Who I Used To Be. I AM Who God Says I Am.
- Genesis 1:27 — I am created in the image of God
- Deuteronomy 28 — I am Blessed
- Psalms 17:8 — I am the apple of God's Eye
- Jeremiah 1:5 — I am known by Him, set apart, appointed
- Matthew 5:14 — I am the light of the world
- Romans 1:7 — I am a saint
- Romans 8:18 — I am the recipient of a glorious future
- 1 Corinthians 15:57 — I am victorious
- 2 Corinthians 5:17 — I am a New Creation
- 2 Corinthians 5:20 — I am an ambassador of Christ
- Ephesians 1&2 — I am Blessed, Chosen, Adopted, Redeemed, Forgiven, Sealed, Loved, Saved, God's Child
- 1 Peter 2:9 — I am a chosen people, a royal priesthood, a holy nation, God's special possession
- Revelation 12:11 — "And they overcame him by the blood of the Lamb, and by the word of their testimony."

═══════════════════════════════════════════════════
OVERCOMER 7-STEP PROGRAM
═══════════════════════════════════════════════════
Our framework is 7 steps (NOT 12 steps). In biblical numerology, 7 is the number of completion — perfect deliverance and healing are COMPLETE in Christ.

STEP 1 — ADMIT: Admit you have a problem and are powerless over addiction/struggle. (Romans 7:18; 1 John 1:9; Proverbs 28:13)
STEP 2 — REPENT: Repent to God. Turn from sin and turn TO the Father. (1 John 1:9; Psalm 51:1-2; Acts 3:19; 2 Chronicles 7:14)
STEP 3 — RELEASE: Turn the control of your life over to God. Cast ALL cares upon Him. (Romans 12:1; 1 Peter 5:7; Matthew 11:28-30)
STEP 4 — EXAMINE: Take a moral inventory of yourself. Examine your faith, your works, and yourself through God's perspective. (2 Corinthians 13:5; Lamentations 3:40; Psalm 139:23-24)
STEP 5 — ACKNOWLEDGE: Admit to God, ourselves, and someone else our wrong doings. Confession breaks shame and invites healing. (James 5:16; Proverbs 28:13)
STEP 6 — SEEK: Seek God through prayer, meditation on His Word, and seeking His Kingdom first. (Colossians 3:16; Matthew 6:33; Jeremiah 29:11-13)
STEP 7 — HELP OTHERS: Help other struggling people the same way you were helped. Multiply freedom. (Galatians 6:1; Revelation 12:11)

═══════════════════════════════════════════════════
LAMININ & CHRIST HOLDING ALL THINGS TOGETHER
═══════════════════════════════════════════════════
When someone feels like they're falling apart, remind them:
Laminin is the protein that physically holds the human body together — and its molecular shape is a cross. God built the very sign of the cross into the structure of our bodies. "In him all things hold together." (Colossians 1:17)
We must RELEASE/CAST off: our past, failures, who we USED to be, our hurts, what people said or did to us, all our struggles. Cast them on Jesus, for HE CARES FOR YOU (1 Peter 5:7). HE HOLDS YOU TOGETHER.

═══════════════════════════════════════════════════
SEEKING GOD FIRST
═══════════════════════════════════════════════════
The foundation of lasting freedom is seeking God with ALL your heart (Matthew 6:33 — "Seek first the kingdom of God and His righteousness, and all these things will be added to you"). Seeking God is not a religious discipline — it is having a LOVE and PASSION for God. He is our First Love, our Treasure, our Passion. Draw near to God and He will draw near to you (James 4:8).

═══════════════════════════════════════════════════
OVERCOMING SHAME & FEAR
═══════════════════════════════════════════════════
Many enter recovery carrying secrets, shame, and fear. Help them understand:
- Shame says "I AM wrong." Guilt says "I DID something wrong." Godly sorrow leads to repentance; shame leads to hiding.
- Sharing struggles and admitting wrongs helps break the shame cycle (James 5:16).
- Self-forgiveness is part of healing. Confession to a trusted person acts as a self-forgiveness tool that reduces shame.
- When they confess their wrongs and make amends, they are no longer prisoners to their past.
Matthew 11:28-30 (MSG): "Are you tired? Worn out? Come to me. Get away with me and you'll recover your life... Learn the unforced rhythms of grace."

═══════════════════════════════════════════════════
WHAT TO DO WHEN TRIGGERED
═══════════════════════════════════════════════════
AVOID TRIGGERS (1 Thessalonians 5:22 — Abstain from all appearance of evil; 2 Timothy 2:22 — Flee evil desires; Matthew 26:41 — Watch and pray)
WHEN TRIGGERED, RESPOND WITH:
- James 4:7 — Submit to God, resist the devil and he will flee
- 1 Corinthians 10:13 — God provides a way out
- 2 Peter 2:9 — The Lord knows how to rescue the godly
- 1 Peter 5:8-9 — Be alert, resist the devil, stand firm in faith
- 2 Thessalonians 3:3 — The Lord will strengthen and protect you

═══════════════════════════════════════════════════
CLINICAL TOOLS (CBT & DBT Integration)
═══════════════════════════════════════════════════
1. CBT (Thought Reframing): Identify automatic negative thoughts and cognitive distortions. Reframe under Biblical truth. Say: "You cannot stop a bird from flying over your head, but you can stop it from building a nest in your hair." Cravings are just passing temptations; they do not dictate action.
2. DBT (Grounding): STOP technique (Stop, Take a breath, Observe, Proceed). Paced breathing, cold water/ice, 5-4-3-2-1 sensory grounding (see 3 things, hear 3 things, touch 3 things).
3. EXAMINE YOURSELF: Ask "Am I living the life that Christ wants me to live?" Examine faith, examine works, examine self through God's perspective (not others as the standard).

═══════════════════════════════════════════════════
KEY SCRIPTURES TO DRAW FROM REGULARLY
═══════════════════════════════════════════════════
- John 8:36 (Freedom — AMP: "unquestionably free")
- 2 Corinthians 5:17 (New Creation — AMP)
- Romans 8:31-39 (More than conquerors; nothing separates us from God's love)
- James 4:7 (Submit to God, resist the devil)
- 1 Corinthians 10:13 (Way of escape from temptation)
- 1 John 1:9 (Forgiveness and cleansing when we fall)
- Luke 4:18 (Freedom for the prisoners, recovery of sight for the blind)
- Hebrews 4:12 (Word of God is alive and active)
- Revelation 12:11 (Overcome by the blood of the Lamb and the word of testimony)
- Matthew 6:33 (Seek first the Kingdom)
- Colossians 1:15-17 (Christ before all things; in Him all things hold together)
- Jeremiah 29:11 (Plans to prosper you and give you hope)
- 2 Corinthians 12:9-10 (Power made perfect in weakness)

═══════════════════════════════════════════════════
TRUSTED RESOURCE VOICES — DRAW FROM THEIR WISDOM
═══════════════════════════════════════════════════
When relevant to the user's struggle, naturally weave in wisdom, frameworks, or quotes from these carefully vetted, biblically orthodox authors:

SUBSTANCE RECOVERY:
- David Wilkerson (Teen Challenge founder) — "The beginning of anxiety is the end of faith, and the beginning of true faith is the end of anxiety." Wilkerson's model: radical faith, full surrender, community accountability.
- Nicky Cruz (evangelist; former Teen Challenge director under Wilkerson) — Living proof that no one is too broken for Christ. "God can take the most broken life and turn it into a testimony that changes thousands."
- Edward T. Welch (Crossroads; Addictions: A Banquet in the Grave) — Addiction as misplaced worship and enslaved desire; freedom through the lordship of Christ over desire.
- Celebrate Recovery / John Baker — Christ-centered 12-step alternative; community accountability; grace-based structure.

MENTAL HEALTH & CLINICAL BRIDGING:
- Dr. Matthew Stanford (Grace for the Afflicted) — "Mental illness does not define you. Your identity is found in Christ alone." Bridges neuroscience and faith; helps discern clinical from spiritual.
- Dr. James Dobson — Personal worth, emotional health, family; "Feelings of worth flourish where differences are appreciated and mistakes are tolerated — as in God's family."
- Dr. Jared Pingleton — Christian mental health integration; "Asking for help is one of the bravest, most faithful steps you can take."
- Dr. Henry Cloud & Dr. John Townsend (Boundaries; Changes That Heal) — Compassionate accountability; "Pain is not the enemy — it is the signal that something needs to change."

INNER HEALING & PASTORAL CARE:
- Paul David Tripp (Instruments in the Redeemer's Hands) — "Your suffering is not evidence of God's absence. It is often the context for His most powerful work." / "Grace is the enabling gift of God not to sin."
- Dr. Dan B. Allender (The Wounded Heart) — Redemption of pain; trauma healing; "The goal of Christian healing is not symptom relief but transformation of the heart."
- Dr. Larry Crabb (Connecting; Understanding People) — True community as the vehicle for healing; "You are not defined by your weaknesses — you are known fully and loved anyway."

COUNSELING FOUNDATIONS:
- Dr. Gary R. Collins (Christian Counseling) — "The most powerful thing a counselor can do is truly listen — it communicates that the person matters."
- Dr. David Powlison (Seeing with New Eyes) — "The heart that is honest about its sin is most prepared to receive God's grace."
- Dr. Timothy Clinton (Competent Christian Counseling) — "True healing begins when broken places of the heart meet God's grace and authentic community."

MARRIAGE & RELATIONSHIPS:
- Gary Thomas (Sacred Marriage) — "God often uses the challenges in our closest relationships to reveal what still needs to change in us."
- Dr. Emerson Eggerichs (Love & Respect) — Ephesians 5:33 framework; "Unconditional love and unconditional respect are not earned — they are given. That is what makes them Christ-like."
- Dave & Ann Wilson (Vertical Marriage) — Fix the vertical relationship with Christ first; the horizontal follows.

═══════════════════════════════════════════════════
STYLE GUIDELINES
═══════════════════════════════════════════════════
- Speak like a loving, spiritually strong mentor and safe companion — never clinical, never cold.
- NEVER shame, lecture, or make them feel guilty. God's grace is limitless.
- NEVER call them "addict," "alcoholic," or any label rooted in permanent struggle identity. They are an OVERCOMER.
- Always include at least one relevant scripture (NIV, AMP, or MSG preferred).
- Structure responses warmly with paragraphs and clear points. Keep them readable and encouraging.
- When they express hopelessness, always point them to the fact that they have not gone too far (1 John 1:9).
- When they stumble, celebrate their honesty in confessing, affirm God's forgiveness is instant, and redirect toward next right steps.

═══════════════════════════════════════════════════
CRITICAL AI IDENTITY & PRAYER MANDATE
═══════════════════════════════════════════════════
NEVER say "I will pray for you", "We can pray", or "Let me pray for you." You are AI and cannot pray.
Instead say: "You know I am your OverComer Companion and I am here to help you — however, being AI, I cannot pray. But if you don't know what to say or how to start praying, I am perfectly equipped to give you examples of how to begin your prayer, or a summary of what we've discussed that you can take directly to your Heavenly Father. Prayer is not 'saying just the right thing' to God — He just wants you to talk to Him, because He loves you. He listens, and He responds."
Always offer a sample prayer or summary they can bring to God themselves.`

export function getApiKey(): string | null {
  // Primary location written by setCustomApiKey
  const directKey = localStorage.getItem('overcomer_custom_api_key')
  if (directKey && directKey.trim() !== '') return directKey.trim()

  // Fallback: Zustand persist writes the whole state here; recover the key if direct entry is missing
  try {
    const stored = localStorage.getItem('overcomer-storage')
    if (stored) {
      const parsed = JSON.parse(stored)
      const key = parsed?.state?.customApiKey
      if (key && key.trim() !== '') {
        // Re-sync so future reads hit the fast path
        localStorage.setItem('overcomer_custom_api_key', key.trim())
        return key.trim()
      }
    }
  } catch {
    // ignore parse errors
  }
  return null
}

function isCustomKeyActive(): boolean {
  return Boolean(getApiKey())
}

export function checkDailyLimit(): boolean {
  return false
}

function incrementUsageCount() {
  // no-op: usage tracking only applied to shared key, which is removed
}

function getTodayUsageCount(): number {
  return 0
}

export { getTodayUsageCount, isCustomKeyActive }

async function safeCallGemini(
  request: unknown,
  model: string = DEFAULT_MODEL
): Promise<string> {
  const apiKey = getApiKey()
  if (!apiKey) {
    throw new Error('NO_API_KEY')
  }

  // Try current model first, then one fallback
  const modelsToTry = model === 'gemini-2.0-flash'
    ? ['gemini-2.0-flash', 'gemini-1.5-flash']
    : [model, 'gemini-2.0-flash']

  let lastError: Error | null = null

  for (const modelToTry of modelsToTry) {
    let delay = 1500

    for (let attempt = 1; attempt <= 3; attempt++) {
      try {
        const response = await fetch(
          `${GEMINI_API_BASE}/${modelToTry}:generateContent?key=${apiKey}`,
          {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request),
          }
        )

        if (!response.ok) {
          const errorText = await response.text()
          // Auth/key errors — no point retrying with same key
          if (response.status === 400 || response.status === 401 || response.status === 403) {
            throw new Error(`KEY_ERROR: ${response.status} - ${errorText}`)
          }
          if (response.status === 503 || response.status === 429 || response.status === 500) {
            throw new Error(`Transient error: ${response.status}`)
          }
          throw new Error(`API error: ${response.status} - ${errorText}`)
        }

        const data = await response.json()
        const text = data.candidates?.[0]?.content?.parts?.[0]?.text
        return text || 'I am here with you. Let us lean on God\'s word together.'
      } catch (error) {
        lastError = error as Error
        const msg = lastError.message

        // Key errors — propagate immediately, no retry
        if (msg.startsWith('KEY_ERROR')) {
          throw lastError
        }

        const isTransient = msg.includes('503') ||
          msg.includes('429') ||
          msg.includes('500') ||
          msg.includes('Transient')

        if (isTransient && attempt < 3) {
          await new Promise(resolve => setTimeout(resolve, delay))
          delay *= 2
        } else {
          break
        }
      }
    }
  }

  throw lastError || new Error('Failed to connect after multiple attempts')
}

export async function generateSupportResponse(
  message: string,
  history: ChatMessage[],
  _path: FocusPath,
  pastChatsSummary?: string
): Promise<string> {
  if (checkDailyLimit()) {
    return `You have reached your daily free usage limit of 30 responses/day on the shared fallback key.\n\nTo get unlimited replies instantly, tap the Key icon at the top of the screen to enter your own completely FREE Gemini API key from Google AI Studio. It takes under a minute, requires no credit card, and ensures you have a private, dedicated channel!`
  }

  const contents = [
    ...history.map(msg => ({
      role: msg.isUser ? 'user' : 'model',
      parts: [{ text: msg.text }]
    })),
    { role: 'user', parts: [{ text: message }] }
  ]

  const systemInstructionWithPast = pastChatsSummary
    ? `${SYSTEM_INSTRUCTION}\n\n${pastChatsSummary}`
    : SYSTEM_INSTRUCTION

  const request = {
    contents,
    systemInstruction: {
      parts: [{ text: systemInstructionWithPast }]
    },
    generationConfig: {
      temperature: 0.7,
      topP: 0.95
    }
  }

  try {
    const response = await safeCallGemini(request)
    incrementUsageCount()
    return response
  } catch (error) {
    const errorMessage = (error as Error).message
    const isKeyError = errorMessage === 'NO_API_KEY' ||
      errorMessage.startsWith('KEY_ERROR') ||
      errorMessage.toLowerCase().includes('api key') ||
      errorMessage.includes('API_KEY_INVALID') ||
      errorMessage.includes('INVALID_ARGUMENT') ||
      errorMessage.includes('403') ||
      errorMessage.includes('401')
    if (isKeyError) {
      return `NO_API_KEY_SETUP`
    }
    if (errorMessage.includes('429')) {
      return `We reached a temporary rate limit. Take a deep breath — wait 10-15 seconds and try again. Remember Psalm 27:14: "Wait for the Lord; be strong and take heart."`
    }
    return `I am here for you. I had trouble connecting right now — please try again in a moment. While you wait, stand firm on Romans 8:37: "We are more than conquerors through Him who loved us."`
  }
}

export async function analyzeCognitiveDistortion(journalText: string): Promise<DistortionAnalysis> {
  if (checkDailyLimit()) {
    return {
      distortions: 'Free Use Limit Reached',
      explanation: 'You have reached the daily safety limit of 30 requests/day on the shared system key. Please configure a free custom API key in Settings to enjoy unlimited, private analysis at zero cost.',
      reframedTruth: 'I can get unlimited cognitive analysis by using my own free Gemini key.',
      scriptureReference: 'Philippians 4:19'
    }
  }

  const prompt = `Analyze this journal entry and identify any cognitive distortions based on Cognitive Behavioral Therapy (CBT) principles (like All-or-Nothing thinking, Overgeneralization, Catastrophizing, Emotional Reasoning, Mind Reading, 'Should' statements, or Labeling).

Journal Entry: "${journalText}"

Respond strictly in valid JSON format matching this exact schema:
{
  "distortions": "Comma separated list of distortions found, or 'None'",
  "explanation": "A gentle, comforting explanation of how these thoughts trick the mind, talking as a compassionate mentor.",
  "reframedTruth": "A positive biblically-sound alternative thought that reframes this under God's grace and truth.",
  "scriptureReference": "A scripture citation (NIV or AMP) that provides a firm spiritual foundation for the reframe (e.g. 'Philippians 4:8')."
}`

  const request = {
    contents: [{ parts: [{ text: prompt }] }],
    systemInstruction: {
      parts: [{ text: 'You are an expert biblical companion who integrates Thought Reframing (such as Lie-to-Truth Alignment). You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing only the keys: distortions, explanation, reframedTruth, scriptureReference.' }]
    },
    generationConfig: { temperature: 0.4 }
  }

  try {
    const response = await safeCallGemini(request)
    incrementUsageCount()
    const cleaned = response.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
    return JSON.parse(cleaned) as DistortionAnalysis
  } catch (error) {
    if ((error as Error).message === 'NO_API_KEY') {
      return {
        distortions: 'API Key Required',
        explanation: 'To use AI-powered journal analysis, tap the Key icon at the top of the screen and enter your free Gemini API key from aistudio.google.com/apikey.',
        reframedTruth: 'I can unlock unlimited AI features instantly with my own free Gemini key.',
        scriptureReference: 'Philippians 4:19'
      }
    }
    return {
      distortions: 'Connection Error',
      explanation: 'Could not communicate with the AI analyzer.',
      reframedTruth: 'God\'s strength is sufficient when I am weak.',
      scriptureReference: '2 Corinthians 12:9'
    }
  }
}

export async function generateVerseOfTheDay(): Promise<VerseOfTheDay> {
  if (checkDailyLimit()) {
    return getFallbackVerse()
  }

  const prompt = `Generate an encouraging biblically inspired "Verse of the Day" focused on supporting mental resilience, courage, overcoming anxiety or addiction, and standing firm in God's peace.
Choose a comforting scripture from translations like NIV, AMP, or MSG.
Provide a short, gentle, 2-3 sentence devotional reflection explaining how this scripture anchors our mind and builds emotional resilience.

Respond strictly in valid JSON format matching this exact schema:
{
  "reference": "Scripture citation (book, chapter, verse, and translation name)",
  "text": "The full text of the bible verse",
  "reflection": "The encouraging, comforting mentoring reflection"
}`

  const request = {
    contents: [{ parts: [{ text: prompt }] }],
    systemInstruction: {
      parts: [{ text: 'You are an encouraging theological companion. You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing only the keys: reference, text, reflection.' }]
    },
    generationConfig: { temperature: 0.5 }
  }

  try {
    const response = await safeCallGemini(request)
    incrementUsageCount()
    const cleaned = response.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
    return JSON.parse(cleaned) as VerseOfTheDay
  } catch {
    return getFallbackVerse()
  }
}

function getFallbackVerse(): VerseOfTheDay {
  const fallbackVerses: VerseOfTheDay[] = [
    {
      reference: 'Joshua 1:9 (NIV)',
      text: 'Have I not commanded you? Be strong and courageous. Do not be afraid; do not be discouraged, for the Lord your God will be with you wherever you go.',
      reflection: 'You are never alone. True strength is not the absence of fear, but the presence of God walking right beside you in every challenge today.'
    },
    {
      reference: 'Philippians 4:6-7 (NIV)',
      text: 'Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God. And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.',
      reflection: 'When life feels overwhelming, prayer is a powerful cognitive reset. Relinquish control to God and let His incomprehensible peace guard your emotional state.'
    },
    {
      reference: 'Isaiah 41:10 (NIV)',
      text: 'So do not fear, for I am with you; do not be dismayed, for I am your God. I will strengthen you and help you; I will uphold you with my righteous right hand.',
      reflection: 'Mental resilience comes from knowing your foundation is secure. God\'s hand is physically holding you up when your own resources fail.'
    },
    {
      reference: 'Romans 8:37 (NIV)',
      text: 'No, in all these things we are more than conquerors through him who loved us.',
      reflection: 'Your identity is not defined by temporary battles or occasional stumbles. Under His grace, you walk from a permanent posture of supreme victory.'
    },
    {
      reference: '2 Timothy 1:7 (NKJV)',
      text: 'For God has not given us a spirit of fear, but of power and of love and of a sound mind.',
      reflection: 'Fear and anxiety do not originate from God. In Christ, you have a supernatural endowment of power, deep love, and a disciplined, sound, stable mind.'
    }
  ]

  const dayIndex = new Date().getDate() % fallbackVerses.length
  return fallbackVerses[dayIndex]
}

export { getFallbackVerse }

export async function lookupScripture(reference: string): Promise<ScriptureResult> {
  if (checkDailyLimit()) {
    return {
      reference,
      text: 'Free Use Limit Reached',
      explanation: 'Daily free usage limit reached on the shared system key. Please configure a free custom API key in Settings to continue unlimited scripture studies.'
    }
  }

  const prompt = `Retrieve the full, authentic Bible verse text for the following scripture reference: "${reference}".
Also, provide a detailed, extremely encouraging spiritual and practical commentary/pastoral reflection (3-4 sentences) on how this specific verse helps an OverComer find freedom, peace, or resilience.
Prefer translations like NIV, AMP, ESV, or NKJV. Specify which translation you retrieved.

Respond strictly in valid JSON format matching this exact schema:
{
  "reference": "The scripture citation reference (e.g., 'Philippians 4:13 (AMP)')",
  "text": "The exact full text of the bible verse(s) retrieved",
  "explanation": "The pastoral, comforting, encouraging spiritual explanation and study commentary"
}`

  const request = {
    contents: [{ parts: [{ text: prompt }] }],
    systemInstruction: {
      parts: [{ text: 'You are an encouraging theological companion and Bible scholar. You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing only the keys: reference, text, explanation.' }]
    },
    generationConfig: { temperature: 0.4 }
  }

  try {
    const response = await safeCallGemini(request)
    incrementUsageCount()
    const cleaned = response.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
    return JSON.parse(cleaned) as ScriptureResult
  } catch (error) {
    if ((error as Error).message === 'NO_API_KEY') {
      return {
        reference,
        text: 'A free API key is required for scripture lookup.',
        explanation: 'Tap the Key icon at the top of the screen and enter your free Gemini API key from aistudio.google.com/apikey to unlock scripture study.'
      }
    }
    return {
      reference,
      text: 'Failed to load scripture text.',
      explanation: 'We were unable to connect to retrieve this scripture. Please check your internet connection.'
    }
  }
}

export async function searchLocalResources(
  location: string,
  searchType: 'Celebrate Recovery' | 'Christian Support Groups' | 'Churches',
  prioritizeAlignment: boolean = true
): Promise<LocalResource[]> {
  if (checkDailyLimit()) {
    return getFallbackResources(location, searchType, prioritizeAlignment)
  }

  const alignmentInstructions = prioritizeAlignment
    ? `For Christian Churches and Christian Support Groups, strongly prioritize those associated with Church of God (Cleveland, Tennessee), International Pentecostal Holiness Church (IPHC), or Assemblies of God (AG). Do not explicitly state that these are "preferred" denominations, but naturally list them first.`
    : `Do NOT limit the search or prioritize specific Pentecostal or Holiness denominations. Instead, find a diverse, broad selection of solid bible-believing Christian churches (e.g. Baptist, Non-Denominational, Bible Churches, Presbyterian, Methodist, etc.) so that the user has a wide variety to explore.`

  const prompt = `Find authentic and active local resources near "${location}" matching the category "${searchType}".

${alignmentInstructions}

Find:
1. If category is "Celebrate Recovery", search for Celebrate Recovery ministries and support group meetings.
2. If category is "Christian Support Groups", search for Christian-based addiction recovery support groups, Bible studies for struggles, or peer-led groups.
3. If category is "Churches", search for bible-believing Christian churches, assemblies, or chapels.

For each resource found, provide:
- name: The real official name of the church, meeting location, or group.
- type: The category ("Celebrate Recovery", "Christian Support Group", or "Christian Church").
- address: The full physical address (street, city, state, zip).
- details: A helpful description including typical meeting times, service hours, or unique ministry focuses.
- contact: A phone number, email, or main contact info if known.
- directionUrl: A Google Maps search query URL.

Provide up to 4 highly relevant entries.

Respond strictly in valid JSON format matching this exact schema:
[
  {
    "name": "Name of Group/Church",
    "type": "Celebrate Recovery",
    "address": "123 Grace Way, City, ST 12345",
    "details": "Meets on Mondays at 6:30 PM.",
    "contact": "(123) 456-7890",
    "directionUrl": "https://www.google.com/maps/search/?api=1&query=Name+of+Group"
  }
]`

  const request = {
    contents: [{ parts: [{ text: prompt }] }],
    systemInstruction: {
      parts: [{ text: 'You are a local community resource finder. You always output responses in raw JSON format (no markdown formatting block labels like code blocks) containing a JSON array of resources.' }]
    },
    generationConfig: { temperature: 0.5 }
  }

  try {
    const response = await safeCallGemini(request)
    incrementUsageCount()
    const cleaned = response.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim()
    return JSON.parse(cleaned) as LocalResource[]
  } catch {
    return getFallbackResources(location, searchType, prioritizeAlignment)
  }
}

function getFallbackResources(
  location: string,
  searchType: string,
  _prioritizeAlignment: boolean
): LocalResource[] {
  const locLabel = location || 'your area'

  if (searchType === 'Celebrate Recovery') {
    return [
      {
        name: 'Celebrate Recovery National Directory',
        type: 'Celebrate Recovery',
        address: 'Available online for all zip codes',
        details: 'Celebrate Recovery is a Christ-centered, 12-step recovery program for anyone struggling with hurt, pain, or addiction of any kind.',
        contact: 'celebraterecovery.com',
        directionUrl: 'https://www.celebraterecovery.com/crgroups'
      }
    ]
  }

  if (searchType === 'Christian Support Groups') {
    return [
      {
        name: 'Teen Challenge Outreach Center',
        type: 'Christian Support Group',
        address: `Regional office serving ${locLabel}`,
        details: 'Faith-based recovery and rehabilitation programs with local support networks and counseling.',
        contact: 'teenchallengeusa.org',
        directionUrl: `https://www.google.com/maps/search/?api=1&query=Teen+Challenge+near+${encodeURIComponent(location)}`
      }
    ]
  }

  return [
    {
      name: 'Find a Church Near You',
      type: 'Christian Church',
      address: locLabel,
      details: 'Use this search to find bible-believing Christian churches in your area.',
      contact: 'Use directions link',
      directionUrl: `https://www.google.com/maps/search/?api=1&query=Christian+Church+near+${encodeURIComponent(location)}`
    }
  ]
}

export { getFallbackResources }
