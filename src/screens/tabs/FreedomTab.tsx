import { useState, useEffect } from 'react'
import { useAppStore } from '../../store/useAppStore'
import { generateVerseOfTheDay } from '../../lib/geminiClient'
import { academyLessons } from '../../lib/data'
import {
  Target, Star, BookOpen, ChevronDown, ChevronUp, Check,
  Wind, MapPin, Phone, ExternalLink, MessageCircle, RefreshCw, Download
} from 'lucide-react'
import type { FocusPath, VerseOfTheDay } from '../../lib/types'

interface FreedomTabProps {
  onNavigateToCompanion: () => void
}

export default function FreedomTab({ onNavigateToCompanion }: FreedomTabProps) {
  const { userPath, freedomGoal, setFreedomGoal, verseOfTheDay, setVerseOfTheDay } = useAppStore()
  const [showSettings, setShowSettings] = useState(false)
  const [showAcademy, setShowAcademy] = useState(false)
  const [isLoadingVerse, setIsLoadingVerse] = useState(false)

  const path = userPath || 'SUBSTANCE_RECOVERY'

  const defaultDeclaration = {
    SUBSTANCE_RECOVERY: 'An OverComer has submitted their life wholly to Christ and no longer fights FOR victory over addiction — rather FROM a position of victory. I AM Loved By God. I AM NOT Who Others Say I Am. I AM NOT Who I Used To Be. I AM Who God Says I Am.',
    MENTAL_HEALTH: 'My mind belongs to Christ. God has not given me a spirit of fear, but of power, love, and a sound mind. I cast every anxious thought on Him, for He cares for me. I AM NOT Who I Used To Be — I AM a New Creation.',
    TOUGH_DAY: 'This day does not define me. Greater is He that is in me than he that is in the world. I choose to seek God first today. His grace is sufficient for me.',
    TESTIMONY_VICTORY: 'I am more than a conqueror through Christ who loves me! They overcame him by the blood of the Lamb and by the word of their testimony. My story is not over — God is still writing it!'
  }

  useEffect(() => {
    if (!verseOfTheDay) {
      refreshVerse()
    }
  }, [])

  const refreshVerse = async () => {
    setIsLoadingVerse(true)
    try {
      const verse = await generateVerseOfTheDay()
      setVerseOfTheDay(verse)
    } catch {
      // Fallback is handled in the client
    } finally {
      setIsLoadingVerse(false)
    }
  }

  const daysCount = freedomGoal?.startDate
    ? Math.floor((Date.now() - freedomGoal.startDate) / (1000 * 60 * 60 * 24))
    : 0

  return (
    <div className="p-4 pb-8 space-y-4">
      {/* Verse of the Day */}
      <VerseCard
        verse={verseOfTheDay}
        isLoading={isLoadingVerse}
        onRefresh={refreshVerse}
      />

      {/* Freedom Day Counter */}
      <FreedomCounterCard
        daysCount={daysCount}
        struggleType={freedomGoal?.struggleType || getDefaultStruggle(path)}
        path={path}
        onSettingsClick={() => setShowSettings(true)}
      />

      {/* Personal Declaration / Creed */}
      <DeclarationCard
        declaration={freedomGoal?.customDeclaration || defaultDeclaration[path]}
        path={path}
      />

      {/* Bible Affirmations */}
      <AffirmationsCard path={path} />

      {/* OverComer Obedience Academy */}
      <AcademyCard
        isExpanded={showAcademy}
        onToggle={() => setShowAcademy(!showAcademy)}
      />

      {/* Support & Church Locator */}
      <SupportLocatorCard />

      {/* Calming Breathing Support */}
      <BreathingCard />

      {/* SOS Support Network */}
      <SOSNetworkCard />

      {/* Open Companion Button */}
      <button
        onClick={onNavigateToCompanion}
        className="w-full bg-primary-400 hover:bg-primary-500 text-white font-bold py-4 px-6 rounded-2xl shadow-lg transition-all hover:shadow-xl active:scale-98 flex items-center justify-center gap-3"
      >
        <MessageCircle className="w-5 h-5" />
        Talk to OverComer Companion
      </button>

      {/* Settings Modal */}
      {showSettings && (
        <SettingsModal
          path={path}
          startDate={freedomGoal?.startDate || Date.now() - (3 * 24 * 60 * 60 * 1000)}
          struggleType={freedomGoal?.struggleType || getDefaultStruggle(path)}
          declaration={freedomGoal?.customDeclaration || defaultDeclaration[path]}
          onSave={(startDate, struggleType, declaration) => {
            setFreedomGoal({ startDate, struggleType, customDeclaration: declaration })
            setShowSettings(false)
          }}
          onClose={() => setShowSettings(false)}
        />
      )}
    </div>
  )
}

function getDefaultStruggle(path: FocusPath): string {
  switch (path) {
    case 'MENTAL_HEALTH': return 'Anxiety & Depression'
    case 'TESTIMONY_VICTORY': return 'Victorious Breakthrough'
    case 'TOUGH_DAY': return 'Daily Stress'
    default: return 'Substance Use'
  }
}

function VerseCard({
  verse,
  isLoading,
  onRefresh
}: {
  verse: VerseOfTheDay | null
  isLoading: boolean
  onRefresh: () => void
}) {
  return (
    <div className="bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl p-5 text-white shadow-lg">
      <div className="flex items-center justify-between mb-3">
        <h3 className="font-bold text-sm uppercase tracking-wide text-white/80">Verse of the Day</h3>
        <button
          onClick={onRefresh}
          disabled={isLoading}
          className="p-1.5 rounded-full bg-white/20 hover:bg-white/30 transition-colors disabled:opacity-50"
        >
          <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
        </button>
      </div>

      {verse ? (
        <>
          <p className="text-lg font-medium leading-relaxed italic">
            "{verse.text}"
          </p>
          <p className="text-sm font-semibold mt-2 text-white/90">
            — {verse.reference}
          </p>
          {verse.reflection && (
            <p className="mt-3 text-sm text-white/80 leading-relaxed">
              {verse.reflection}
            </p>
          )}
        </>
      ) : (
        <p className="text-white/70">Loading...</p>
      )}
    </div>
  )
}

function FreedomCounterCard({
  daysCount,
  struggleType,
  path,
  onSettingsClick
}: {
  daysCount: number
  struggleType: string
  path: FocusPath
  onSettingsClick: () => void
}) {
  const counterLabel = {
    SUBSTANCE_RECOVERY: 'Days of Freedom',
    MENTAL_HEALTH: 'Days of Peace',
    TOUGH_DAY: 'Days of Strength',
    TESTIMONY_VICTORY: 'Days of Victory'
  }

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <div className="flex items-start justify-between">
        <div>
          <h3 className="font-bold text-gray-900">{counterLabel[path]}</h3>
          <p className="text-sm text-gray-500">{struggleType}</p>
        </div>
        <button
          onClick={onSettingsClick}
          className="p-2 rounded-lg hover:bg-gray-100 transition-colors text-gray-400 hover:text-gray-600"
        >
          <Target className="w-5 h-5" />
        </button>
      </div>

      <div className="mt-4 text-center py-4 bg-gradient-to-br from-primary-50 to-secondary-50 rounded-xl">
        <span className="text-5xl font-black text-primary-500">{daysCount}</span>
        <p className="text-sm text-gray-600 mt-1">days</p>
      </div>
    </div>
  )
}

function DeclarationCard({
  declaration,
  path
}: {
  declaration: string
  path: FocusPath
}) {
  const title = {
    SUBSTANCE_RECOVERY: 'My OverComer Creed',
    MENTAL_HEALTH: 'My Mental Peace Covenant',
    TOUGH_DAY: "Today's Declaration",
    TESTIMONY_VICTORY: 'My Victory Declaration'
  }

  return (
    <div className="bg-gradient-to-br from-secondary-50 to-primary-50 rounded-2xl p-5 shadow-md border border-primary-100">
      <h3 className="font-bold text-primary-600 mb-3">{title[path]}</h3>
      <p className="text-gray-700 leading-relaxed italic">{declaration}</p>
    </div>
  )
}

function AffirmationsCard({ path: _path }: { path: FocusPath }) {
  const affirmations = [
    { text: 'I AM Loved By God — I AM NOT Who Others Say I Am', reference: 'OverComer Declaration' },
    { text: 'I AM a New Creation — the old is gone, the new has come', reference: '2 Corinthians 5:17' },
    { text: 'I AM unquestionably free — the Son has set me free', reference: 'John 8:36 AMP' },
    { text: 'I AM more than a conqueror through Him who loved me', reference: 'Romans 8:37' },
    { text: 'I AM victorious — God gives me the victory through Christ', reference: '1 Corinthians 15:57' },
    { text: 'I AM chosen, a royal priesthood, God\'s special possession', reference: '1 Peter 2:9' },
    { text: 'I AM held together by Christ — the Cross is in my very cells', reference: 'Colossians 1:17' }
  ]

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
        <Star className="w-5 h-5 text-accent-gold" />
        Bible Affirmations
      </h3>
      <div className="space-y-2">
        {affirmations.map((aff, i) => (
          <div key={i} className="flex items-center gap-2 text-sm">
            <Check className="w-4 h-4 text-accent-teal flex-shrink-0" />
            <span className="text-gray-700">{aff.text}</span>
            <span className="text-xs text-gray-400">({aff.reference})</span>
          </div>
        ))}
      </div>
    </div>
  )
}

const stepWorksheets: Record<number, { pdf: string; notes?: string }> = {
  1: { pdf: '/philosophy/Step_1_Worksheet.pdf' },
  2: { pdf: '/philosophy/Step_2_Worksheet.pdf', notes: '/philosophy/Step_2_Notes.pdf' },
  3: { pdf: '/philosophy/Step_3_Worksheet.pdf' },
  4: { pdf: '/philosophy/Step_4_Worksheet.pdf' },
  5: { pdf: '/philosophy/Step_5_worksheet.pdf' },
  6: { pdf: '/philosophy/Step_6_Worksheet.pdf' },
  7: { pdf: '/philosophy/Step_7_Worksheet.pdf' }
}

function AcademyCard({
  isExpanded,
  onToggle
}: {
  isExpanded: boolean
  onToggle: () => void
}) {
  const { completedLessons } = useAppStore()
  const completed = completedLessons.length
  const total = academyLessons.length

  const groupedLessons = academyLessons.reduce((acc, lesson) => {
    const key = lesson.stepNumber
    if (!acc[key]) acc[key] = []
    acc[key].push(lesson)
    return acc
  }, {} as Record<number, typeof academyLessons>)

  return (
    <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      <button
        onClick={onToggle}
        className="w-full flex items-center justify-between p-5 hover:bg-gray-50 transition-colors"
      >
        <div className="flex items-center gap-3">
          <BookOpen className="w-6 h-6 text-primary-500" />
          <div className="text-left">
            <h3 className="font-bold text-gray-900">OverComer Obedience Academy</h3>
            <p className="text-sm text-gray-500">{completed} / {total} Completed</p>
          </div>
        </div>
        {isExpanded ? (
          <ChevronUp className="w-5 h-5 text-gray-400" />
        ) : (
          <ChevronDown className="w-5 h-5 text-gray-400" />
        )}
      </button>

      {isExpanded && (
        <div className="border-t border-gray-100 p-4 space-y-4 max-h-[32rem] overflow-y-auto">
          {Object.entries(groupedLessons).map(([stepNum, lessons]) => {
            const stepN = Number(stepNum)
            const worksheet = stepWorksheets[stepN]
            return (
              <div key={stepNum}>
                <div className="flex items-center justify-between mb-2">
                  <h4 className="font-bold text-sm text-primary-600">
                    Step {stepNum}: {lessons[0].stepTitle}
                  </h4>
                  {worksheet && (
                    <div className="flex items-center gap-1.5">
                      <a
                        href={worksheet.pdf}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-1 text-xs bg-primary-50 hover:bg-primary-100 text-primary-600 font-semibold px-2 py-1 rounded-lg transition-colors"
                        onClick={e => e.stopPropagation()}
                      >
                        <Download className="w-3 h-3" />
                        Worksheet
                      </a>
                      {worksheet.notes && (
                        <a
                          href={worksheet.notes}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="flex items-center gap-1 text-xs bg-accent-teal/10 hover:bg-accent-teal/20 text-accent-teal font-semibold px-2 py-1 rounded-lg transition-colors"
                          onClick={e => e.stopPropagation()}
                        >
                          <Download className="w-3 h-3" />
                          Notes
                        </a>
                      )}
                    </div>
                  )}
                </div>
                <div className="space-y-2">
                  {lessons.map(lesson => (
                    <button
                      key={lesson.id}
                      onClick={() => {}}
                      className={`w-full flex items-center gap-3 p-3 rounded-xl text-left transition-colors ${
                        completedLessons.includes(lesson.id)
                          ? 'bg-accent-teal/10 border border-accent-teal/20'
                          : 'bg-gray-50 hover:bg-gray-100'
                      }`}
                    >
                      <div className={`w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 ${
                        completedLessons.includes(lesson.id)
                          ? 'bg-accent-teal text-white'
                          : 'bg-gray-200 text-gray-400'
                      }`}>
                        {completedLessons.includes(lesson.id) ? (
                          <Check className="w-4 h-4" />
                        ) : (
                          <span className="text-xs font-bold">{lesson.id}</span>
                        )}
                      </div>
                      <span className="text-sm font-medium text-gray-700">
                        {lesson.title}
                      </span>
                    </button>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

function SupportLocatorCard() {
  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <h3 className="font-bold text-gray-900 mb-3 flex items-center gap-2">
        <MapPin className="w-5 h-5 text-primary-500" />
        Support & Church Locator
      </h3>
      <p className="text-sm text-gray-600 mb-4">Find Celebrate Recovery, support groups, and churches near you.</p>

      <a
        href="https://www.thefaithconnection.org"
        target="_blank"
        rel="noopener noreferrer"
        className="flex items-center justify-between p-3 bg-primary-50 rounded-xl hover:bg-primary-100 transition-colors"
      >
        <div>
          <p className="font-semibold text-primary-700">The Faith Connection</p>
          <p className="text-xs text-primary-600">Our Ministry Home</p>
        </div>
        <ExternalLink className="w-4 h-4 text-primary-500" />
      </a>
    </div>
  )
}

function BreathingCard() {
  return (
    <div className="bg-gradient-to-br from-accent-teal/10 to-primary-50 rounded-2xl p-5 shadow-md border border-accent-teal/20">
      <h3 className="font-bold text-gray-900 mb-2 flex items-center gap-2">
        <Wind className="w-5 h-5 text-accent-teal" />
        Calming Breathing Support
      </h3>
      <p className="text-sm text-gray-600 mb-3">
        Practice paced breathing to reduce stress and anxiety.
      </p>
      <button className="w-full bg-accent-teal hover:bg-teal-700 text-white font-semibold py-3 px-4 rounded-xl transition-colors">
        Start Breathing Exercise
      </button>
    </div>
  )
}

function SOSNetworkCard() {
  return (
    <div className="bg-red-50 rounded-2xl p-5 shadow-md border border-red-100">
      <h3 className="font-bold text-red-700 mb-2 flex items-center gap-2">
        <Phone className="w-5 h-5" />
        SOS Support Network
      </h3>
      <p className="text-sm text-red-600">
        If you're in crisis, reach out to your support network or call emergency services immediately.
      </p>
    </div>
  )
}

function SettingsModal({
  path: _path,
  startDate,
  struggleType,
  declaration,
  onSave,
  onClose
}: {
  path: FocusPath
  startDate: number
  struggleType: string
  declaration: string
  onSave: (startDate: number, struggleType: string, declaration: string) => void
  onClose: () => void
}) {
  const [dateStr, setDateStr] = useState(new Date(startDate).toISOString().split('T')[0])
  const [struggle, setStruggle] = useState(struggleType)
  const [dec, setDec] = useState(declaration)

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl w-full max-w-md max-h-[90vh] overflow-y-auto animate-slide-up">
        <div className="p-5 border-b border-gray-100">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-gray-900">Freedom Settings</h2>
            <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg">
              ✕
            </button>
          </div>
        </div>

        <div className="p-5 space-y-4">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Start Date</label>
            <input
              type="date"
              value={dateStr}
              onChange={(e) => setDateStr(e.target.value)}
              className="input-field"
              placeholder="YYYY"  // Removed unused label prop
            />
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Struggle Type</label>
            <select
              value={struggle}
              onChange={(e) => setStruggle(e.target.value)}
              className="input-field"
            >
              <option value="Substance Use">Substance Use</option>
              <option value="Anxiety & Depression">Anxiety & Depression</option>
              <option value="Fear">Fear</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">My Declaration</label>
            <textarea
              value={dec}
              onChange={(e) => setDec(e.target.value)}
              rows={4}
              className="input-field resize-none"
              placeholder="Write your personal declaration..."
            />
          </div>
        </div>

        <div className="p-5 border-t border-gray-100 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 btn-outline"
          >
            Cancel
          </button>
          <button
            onClick={() => {
              onSave(
                new Date(dateStr).getTime(),
                struggle,
                dec
              )
            }}
            className="flex-1 btn-primary"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  )
}

