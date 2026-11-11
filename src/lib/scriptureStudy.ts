import type { BibleVersion } from './bibleData'
import type { ScriptureResult } from './types'

/**
 * Bible study works without Gemini. Verse text is loaded from an actual
 * public-domain Bible source (KJV) or from bundled KJV Quick Study passages.
 * NIV, AMP, NKJV and MSG text is not reproduced without publisher rights.
 */
const QUICK_STUDY_KJV: Record<string, string> = {
  'john 8:36': 'If the Son therefore shall make you free, ye shall be free indeed.',
  'romans 8:37': 'Nay, in all these things we are more than conquerors through him that loved us.',
  'james 4:7': 'Submit yourselves therefore to God. Resist the devil, and he will flee from you.',
  'proverbs 3:5-6': 'Trust in the LORD with all thine heart; and lean not unto thine own understanding. In all thy ways acknowledge him, and he shall direct thy paths.',
  '1 corinthians 10:13': 'There hath no temptation taken you but such as is common to man: but God is faithful, who will not suffer you to be tempted above that ye are able; but will with the temptation also make a way to escape, that ye may be able to bear it.',
}

const QUICK_STUDY_REFLECTIONS: Record<string, string> = {
  'john 8:36': 'Jesus describes real freedom as a gift from the Son. Consider one way you can respond to His invitation to live in freedom today.',
  'romans 8:37': "God's love is stronger than the difficulties we face. Identify a challenge and one wise, faithful step you can take through it.",
  'james 4:7': 'Submitting to God includes practical choices. Name a temptation or pressure, and decide what boundary or trusted support could help you resist it.',
  'proverbs 3:5-6': 'Trust is lived out in ordinary decisions. Bring an uncertainty before God, seek wise counsel, and take a thoughtful next step.',
  '1 corinthians 10:13': "Temptation is a common human experience, and this passage points to God's faithfulness. Think ahead about a safe person, place, or choice you can turn to when pressure increases.",
}

export function normalizeScriptureReference(reference: string): string {
  return reference.trim().toLowerCase().replace(/\s+/g, ' ')
}

export function getQuickStudyVerse(reference: string): string | undefined {
  return QUICK_STUDY_KJV[normalizeScriptureReference(reference)]
}

export function licensedBibleLink(reference: string, selectedVersion: BibleVersion): string {
  return 'https://www.biblegateway.com/passage/?search=' +
    encodeURIComponent(reference.trim()) + '&version=' + encodeURIComponent(selectedVersion)
}

function reflection(reference: string): string {
  return QUICK_STUDY_REFLECTIONS[normalizeScriptureReference(reference)] ??
    "Read the surrounding verses for context. What does this passage reveal about God's character, and what faithful step can you take today? Add your own Gemini API key in Settings for optional AI commentary."
}

function result(
  reference: string,
  requestedVersion: BibleVersion,
  text: string,
  status: string,
  available: boolean,
): ScriptureResult {
  return {
    reference,
    text,
    explanation: reflection(reference),
    displayedVersion: available ? 'KJV' : undefined,
    sourceNote: status,
    selectedVersionLink: licensedBibleLink(reference, requestedVersion),
    scriptureAvailable: available,
    commentaryIsAi: false,
  }
}

export async function retrieveScripture(
  reference: string,
  selectedVersion: BibleVersion = 'NIV',
): Promise<ScriptureResult> {
  const clean = reference.trim()
  if (clean.length < 3 || clean.length > 90 || !/^[a-z0-9\s:,.\-]+$/i.test(clean)) {
    return result(clean, selectedVersion,
      'Please enter a valid Scripture reference.',
      'Try John 8:36 or 1 Corinthians 10:13. Scripture lookup is free and does not require an AI key.',
      false)
  }

  const modernTranslation = selectedVersion !== 'KJV'
  const cached = getQuickStudyVerse(clean)
  const translationNote = modernTranslation
    ? 'You selected ' + selectedVersion +
      '. Showing public-domain KJV text here, not ' + selectedVersion +
      '. Use the link below to read ' + selectedVersion + ' in a licensed Bible reader.'
    : 'Public-domain King James Version (KJV).'

  // Never make the five Quick Study examples depend on network connectivity.
  if (cached) {
    return result(clean, selectedVersion, cached,
      'Available offline. ' + translationNote, true)
  }

  try {
    const controller = new AbortController()
    const timeout = setTimeout(() => controller.abort(), 12000)
    let response: Response
    try {
      response = await fetch(
        'https://bible-api.com/' + encodeURIComponent(clean) + '?translation=kjv',
        { headers: { Accept: 'application/json' }, signal: controller.signal },
      )
    } finally {
      clearTimeout(timeout)
    }

    if (response.status === 404) {
      return result(clean, selectedVersion,
        'No passage found for that reference.',
        'Check the Bible book, chapter and verse. You can also open the selected translation using the reader link below.',
        false)
    }
    if (!response.ok) {
      return result(clean, selectedVersion,
        'Scripture text is temporarily unavailable.',
        'The Bible provider returned an error (HTTP ' + response.status + '). Your device may still be online. Try one of the offline Quick Study passages or open the reader below.',
        false)
    }
    const data: unknown = await response.json()
    if (!data || typeof data !== 'object') throw new Error('Invalid Bible service response')
    const payload = data as { text?: unknown; translation_id?: unknown }
    if (String(payload.translation_id ?? '').toLowerCase() !== 'kjv') {
      throw new Error('Bible service returned an unexpected translation')
    }
    const passageText = typeof payload.text === 'string' ? payload.text.trim() : ''
    if (!passageText) throw new Error('Bible service returned an empty passage')
    return result(clean, selectedVersion, passageText,
      'Provided by bible-api.com. ' + translationNote, true)
  } catch {
    return result(clean, selectedVersion,
      'Scripture text is temporarily unavailable.',
      'The Bible service could not be reached for this reference. This does not necessarily mean your device is offline. The five Quick Study passages remain available without a connection.',
      false)
  }
}
