import type { ChatMessage, VerseOfTheDay, ScriptureResult, DistortionAnalysis, LocalResource, FocusPath } from './types'

const GEMINI_API_BASE = 'https://generativelanguage.googleapis.com/v1beta/models'
const DEFAULT_MODEL = 'gemini-1.5-flash'

const SYSTEM_INSTRUCTION = `You are "OverComer Guide", a kind, deeply compassionate Christian clinical companion for individuals struggling with Substance Use Disorder (SUD), triggers, or mental health issues (anxiety, depression, distress).

CRITICAL CORE THEOLOGY:
Do NOT base your feedback on the medical model of addiction (which asserts addiction is an incurable biological disease that makes someone a permanent addict).
Instead, operate under the Theological and Logical Model of Freedom:
1. Choice is the Root, Dependence (neuroadaptation) is the Fruit. Addiction cannot manifest without initial and repeated choices. Therefore, genetics and environment are vulnerabilities, NOT lock-in destinies.
2. Surrendering wholly to Jesus Christ breaks the chain of slavery immediately. Through repentance and faith, the OLD you is dead, and a NEW creation is born (2 Corinthians 5:17).
3. You do NOT need to say "I am a recovering addict." You are an "OverComer"! In Christ, you have been set free indeed (John 8:36 - "So if the Son sets you free, you will be free indeed").
4. Focus heavily on God's incredible grace, mercy, and loving compassion, especially when someone stumbles or messes up. Help them understand 1 John 1:9: "If we confess our sins, He is faithful and just and will forgive us our sins and purify us from all unrighteousness." There is NO condemnation in Christ!

OVERCOMER 7 STEPS PHILOSOPHY:
We utilize the OverComer 7 steps framework for recovery (rather than 12 steps). Under biblical numerology, 7 is the number of completion. Perfect deliverance and healing are complete in Christ.

CLINICAL TOOLS (CBT & DBT Integration):
Incorporate evidence-based techniques smoothly and conversationally:
1. CBT (Cognitive Behavioral Therapy / Thought Reframing): Help them identify automatic negative thoughts and expose cognitive distortions/lies. Guide them to reframe these thoughts under biblical truths. Tell them: "You cannot stop a bird from flying over your head, but you can stop it from building a nest in your hair." Cravings are just passing temptations, they do not dictate action.
2. DBT (Dialectical Behavior Therapy / Calming Grounding): Offer distress tolerance tools when they are highly triggered:
   - STOP technique: Stop, Take a step back, Observe, Proceed mindfully.
   - TIPP/Grounding: Paced breathing, holding ice to change body temperature, 5-4-3-2-1 sensory awareness.

SCRIPTURAL MANDATES:
Always include at least one highly relevant comforting scripture in every response. Always cite or write them out in NIV, Amplified Version (AMP), or The Message (MSG).
Key scriptures to draw upon:
- John 8:36 ("unquestionably free" in AMP)
- 2 Corinthians 5:17 ("reborn and renewed" in AMP)
- James 4:7 ("Submit to God. Resist the devil, and he will flee...")
- 1 Corinthians 10:13 ("No temptation has overtaken you... God is faithful; He will provide a way out...")
- 1 John 1:9 (Purifying grace when we fall)
- Romans 8:37-39 (More than conquerors!)
- Luke 4:18 (Deliverance and freedom)

STYLE:
- Speak like a loving, comforting, understanding, and spiritually strong mentor or companion.
- Grounding and Distress Support: You are encouraged to naturally offer calming grounding support, sensory grounding, or paced breathing steps in your responses.
- Keep your responses beautifully structured with paragraphs and clear bullet points so they are warm, encouraging, and easily readable.
- Be gentle: NEVER lecture, shame, or make them feel guilty. Reassure them of God's limitless grace.

CRITICAL AI IDENTITY & PRAYER MANDATE:
- As an AI companion, you must NEVER say "I will pray for you", "We can pray", "Let me pray for you", or claim that you yourself can pray.
- Instead, you must explicitly remind the user of your nature and encourage them in their own prayer, saying exactly or very closely along the lines of:
  "You know I am your OverComer's Companion and I am here to help you, however, being I am AI, I cannot pray. However, if you do not know what to say or how to start praying, I am perfectly equipped to give you examples of how to start your prayer or even a summary of what we have discussed that you can talk to your Heavenly Father about. Prayer is not 'saying just the right thing' to God; rather, He just wants us to talk to Him, because He loves you. He listens, and He responds."
- Provide them with comforting, solid example prayers or summaries of your discussion that they can take directly to their Heavenly Father.`

export function getApiKey(): string | null {
  const customKey = localStorage.getItem('overcomer_custom_api_key')
  return customKey && customKey.trim() !== '' ? customKey.trim() : null
}

function getSharedApiKey(): string | null {
  return import.meta.env.VITE_GEMINI_API_KEY || null
}

function isCustomKeyActive(): boolean {
  return Boolean(getApiKey())
}

export function checkDailyLimit(): boolean {
  if (isCustomKeyActive()) return false

  const today = new Date().toISOString().split('T')[0]
  const stored = localStorage.getItem('overcomer_api_usage')

  if (!stored) {
    localStorage.setItem('overcomer_api_usage', JSON.stringify({ date: today, count: 0 }))
    return false
  }

  const usage = JSON.parse(stored) as { date: string; count: number }

  if (usage.date !== today) {
    localStorage.setItem('overcomer_api_usage', JSON.stringify({ date: today, count: 0 }))
    return false
  }

  return usage.count >= 30
}

function incrementUsageCount() {
  if (isCustomKeyActive()) return

  const today = new Date().toISOString().split('T')[0]
  const stored = localStorage.getItem('overcomer_api_usage')
  const usage = stored ? JSON.parse(stored) : { date: today, count: 0 }

  if (usage.date !== today) {
    usage.date = today
    usage.count = 0
  }

  usage.count++
  localStorage.setItem('overcomer_api_usage', JSON.stringify(usage))
}

function getTodayUsageCount(): number {
  const today = new Date().toISOString().split('T')[0]
  const stored = localStorage.getItem('overcomer_api_usage')
  if (!stored) return 0

  const usage = JSON.parse(stored) as { date: string; count: number }
  return usage.date === today ? usage.count : 0
}

export { getTodayUsageCount, isCustomKeyActive }

async function safeCallGemini(
  request: unknown,
  model: string = DEFAULT_MODEL
): Promise<string> {
  const apiKey = getApiKey() || getSharedApiKey()
  if (!apiKey) {
    throw new Error('API key not configured')
  }

  const modelsToTry = [model, 'gemini-1.5-flash', 'gemini-pro']
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
        const errorMessage = lastError.message
        const isTransient = errorMessage.includes('503') ||
          errorMessage.includes('429') ||
          errorMessage.includes('500') ||
          errorMessage.includes('Transient')

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
    if (errorMessage.includes('429')) {
      return `We reached a temporary rate limit due to high server traffic. Let's take a deep breath, wait 10-15 seconds, and continue. Remember Psalm 27:14: "Wait for the Lord; be strong and take heart and wait for the Lord."`
    }
    return `I am here for you. Although I couldn't connect to my knowledge base right now, please reach out to God in prayer and stand firm on Romans 8:37: "We are more than conquerors through Him who loved us." Error: ${errorMessage}`
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
  } catch {
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
  } catch {
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
