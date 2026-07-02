import { useState, useEffect, useRef } from 'react'
import { DoorOpen, Brain, RefreshCw, Sun, Users, Shield, BookOpen, Eye, Ear, Hand, ChevronDown, Check, ExternalLink, MapPin, Star, Clock, Phone, Leaf, Waves, Wind, CircleAlert as AlertCircle, Lightbulb, CirclePlay as PlayCircle, CirclePause as PauseCircle } from 'lucide-react'

type SectionId = 'understanding' | 'renewing' | 'grounding' | 'mentorship' | 'resources'

export default function ReentryTab() {
  const [activeSection, setActiveSection] = useState<SectionId | null>('understanding')

  const sections: { id: SectionId; title: string; subtitle: string; icon: React.ReactNode; color: string }[] = [
    {
      id: 'understanding',
      title: 'You Are Not Broken',
      subtitle: 'Understanding post-incarceration struggles',
      icon: <Brain className="w-5 h-5" />,
      color: 'bg-blue-500'
    },
    {
      id: 'renewing',
      title: 'Renewing Your Mind',
      subtitle: 'Replace lies with biblical truth',
      icon: <RefreshCw className="w-5 h-5" />,
      color: 'bg-amber-500'
    },
    {
      id: 'grounding',
      title: 'Calming & Grounding',
      subtitle: 'Tools for sensory overload and panic',
      icon: <Sun className="w-5 h-5" />,
      color: 'bg-green-500'
    },
    {
      id: 'mentorship',
      title: 'Building Your Foundation',
      subtitle: 'Routine, mentorship, and support',
      icon: <Users className="w-5 h-5" />,
      color: 'bg-purple-500'
    },
    {
      id: 'resources',
      title: 'Curated Resources',
      subtitle: 'Agencies, programs, and hotlines',
      icon: <MapPin className="w-5 h-5" />,
      color: 'bg-rose-500'
    }
  ]

  return (
    <div className="pb-8">
      {/* Hero Banner */}
      <div className="bg-gradient-to-br from-teal-600 via-teal-500 to-cyan-500 px-5 pt-6 pb-8">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-12 h-12 bg-white/20 rounded-2xl flex items-center justify-center">
            <DoorOpen className="w-7 h-7 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-extrabold text-white leading-tight">Re-entry Support</h2>
            <p className="text-white/80 text-sm">Your path back — one step at a time</p>
          </div>
        </div>
        <div className="bg-white/15 rounded-2xl p-4 mt-2">
          <p className="text-white leading-relaxed text-sm">
            If you've experienced long-term incarceration, you may feel like something inside
            you is permanently broken. <strong className="text-white">That feeling is a lie.</strong> What
            you're experiencing is a natural response to surviving in an extreme environment — and through
            Christ's grace, you can fully heal and thrive.
          </p>
        </div>
      </div>

      {/* Section Navigation */}
      <div className="px-4 -mt-4 space-y-2">
        {sections.map(section => (
          <SectionCard
            key={section.id}
            section={section}
            isActive={activeSection === section.id}
            onToggle={() => setActiveSection(activeSection === section.id ? null : section.id)}
          >
            {section.id === 'understanding' && <UnderstandingContent />}
            {section.id === 'renewing' && <RenewingContent />}
            {section.id === 'grounding' && <GroundingContent />}
            {section.id === 'mentorship' && <MentorshipContent />}
            {section.id === 'resources' && <ResourcesContent />}
          </SectionCard>
        ))}
      </div>

      {/* Bottom Encouragement */}
      <div className="mx-4 mt-4 bg-gradient-to-r from-primary-100 to-teal-50 rounded-2xl p-5 text-center">
        <p className="text-primary-800 font-bold italic text-base leading-snug">
          "For I know the plans I have for you," declares the LORD, "plans to prosper you and not to harm you, plans to give you hope and a future."
        </p>
        <p className="text-primary-600 text-sm font-semibold mt-1">— Jeremiah 29:11</p>
      </div>
    </div>
  )
}

function SectionCard({
  section,
  isActive,
  onToggle,
  children
}: {
  section: { id: SectionId; title: string; subtitle: string; icon: React.ReactNode; color: string }
  isActive: boolean
  onToggle: () => void
  children: React.ReactNode
}) {
  return (
    <div className={`bg-white rounded-2xl shadow-sm border transition-all ${isActive ? 'border-teal-200 shadow-md' : 'border-gray-100'}`}>
      <button
        onClick={onToggle}
        className="w-full flex items-center gap-3 p-4 text-left"
      >
        <div className={`w-10 h-10 ${section.color} rounded-xl flex items-center justify-center text-white flex-shrink-0`}>
          {section.icon}
        </div>
        <div className="flex-1 min-w-0">
          <p className="font-bold text-gray-900 text-sm">{section.title}</p>
          <p className="text-xs text-gray-500 truncate">{section.subtitle}</p>
        </div>
        <ChevronDown className={`w-5 h-5 text-gray-400 flex-shrink-0 transition-transform duration-200 ${isActive ? 'rotate-180' : ''}`} />
      </button>

      {isActive && (
        <div className="px-4 pb-4 border-t border-gray-100 pt-3 space-y-3">
          {children}
        </div>
      )}
    </div>
  )
}

/* ─── Understanding Section ─────────────────────────────────────── */
function UnderstandingContent() {
  const experiences = [
    {
      icon: <Shield className="w-5 h-5 text-blue-500" />,
      title: 'Hypervigilance',
      description: 'Constantly scanning for danger, unable to relax, startling easily. Your brain learned that staying on high alert was essential to survive. That wiring takes time to reset.',
      bg: 'bg-blue-50'
    },
    {
      icon: <Waves className="w-5 h-5 text-cyan-500" />,
      title: 'Sensory Overload',
      description: 'Crowds, traffic, bright stores, and loud conversations can feel physically overwhelming. After years of controlled, stripped-down environments, the outside world is a sensory flood.',
      bg: 'bg-cyan-50'
    },
    {
      icon: <Lightbulb className="w-5 h-5 text-amber-500" />,
      title: 'Decision Fatigue',
      description: 'Too many choices — what to eat, where to go, what to say — can feel paralyzing. You had almost no choices for years, and now you have thousands. Give yourself time.',
      bg: 'bg-amber-50'
    },
    {
      icon: <Wind className="w-5 h-5 text-gray-500" />,
      title: 'Emotional Numbing',
      description: 'Feeling flat, disconnected, or unable to feel joy or love like you used to. Emotional protection was a survival mechanism inside. It fades as safety is rebuilt.',
      bg: 'bg-gray-50'
    },
    {
      icon: <Star className="w-5 h-5 text-purple-500" />,
      title: 'Identity Confusion',
      description: 'Struggling to know who you are outside the prison system. For years, your identity was your number. Now God calls you by name — His child, His workmanship.',
      bg: 'bg-purple-50'
    },
    {
      icon: <Clock className="w-5 h-5 text-rose-500" />,
      title: 'Time Disorientation',
      description: 'The world moved fast while you were inside. Technology, prices, relationships, norms — all shifted. Feeling behind is normal. You\'ll catch up at your own God-ordained pace.',
      bg: 'bg-rose-50'
    }
  ]

  return (
    <>
      <div className="bg-blue-50 rounded-xl p-4">
        <h4 className="font-bold text-blue-800 mb-2 text-sm">What is Post-Incarceration Syndrome (PICS)?</h4>
        <p className="text-blue-700 text-sm leading-relaxed">
          PICS describes the cluster of responses that develop after surviving long-term incarceration.
          Prison environments require constant high alertness to survive — your mind and nervous system
          adapted brilliantly. Once released, those adaptations clash with the outside world.
        </p>
        <p className="text-blue-700 text-sm leading-relaxed mt-2">
          <strong>This is not a disease. It is not permanent. It is not who you are.</strong> It is
          a trained response that can be fully retrained through Christ and intentional healing.
        </p>
      </div>

      <p className="text-xs font-bold text-gray-500 uppercase tracking-wide">Common Experiences</p>

      {experiences.map((exp, i) => (
        <div key={i} className={`${exp.bg} rounded-xl p-3 flex gap-3`}>
          <div className="flex-shrink-0 mt-0.5">{exp.icon}</div>
          <div>
            <p className="font-bold text-gray-800 text-sm">{exp.title}</p>
            <p className="text-gray-600 text-xs leading-relaxed mt-0.5">{exp.description}</p>
          </div>
        </div>
      ))}

      <div className="bg-gradient-to-r from-teal-100 to-blue-100 rounded-xl p-4 text-center">
        <p className="text-teal-800 font-bold italic text-sm">
          "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here!"
        </p>
        <p className="text-teal-600 text-xs font-semibold mt-1">— 2 Corinthians 5:17</p>
      </div>
    </>
  )
}

/* ─── Renewing Section ──────────────────────────────────────────── */
function RenewingContent() {
  const truths = [
    {
      lie: 'I am institutionalized and will never fully adapt. I\'m broken forever.',
      truth: 'I am a new creation in Christ. God is actively renewing my mind day by day. Nothing is broken that His grace cannot restore.',
      scripture: '2 Corinthians 5:17'
    },
    {
      lie: 'I don\'t belong out here. Everyone can see I\'m different — damaged goods.',
      truth: 'I was created with purpose before I was born. God has prepared good works specifically for me to walk in. I belong.',
      scripture: 'Ephesians 2:10'
    },
    {
      lie: 'I\'ll always be anxious. Normal people don\'t feel this way.',
      truth: 'God\'s peace guards my heart and mind. My nervous system can and will heal. Anxiety is not my permanent address.',
      scripture: 'Philippians 4:7'
    },
    {
      lie: 'I can\'t handle all these choices. I\'ve lost the ability to function.',
      truth: 'God\'s Spirit guides me in wisdom. I can take one decision at a time, trusting His lead. I am capable.',
      scripture: 'Proverbs 3:5-6'
    },
    {
      lie: 'My past defines me. I\'ll always be seen as an ex-con — nothing more.',
      truth: 'My identity is in Christ alone. I am forgiven, redeemed, chosen, and called. My past is part of my story, not its ending.',
      scripture: 'Isaiah 43:1'
    },
    {
      lie: 'I\'ve lost the ability to connect. I don\'t know how to love or be loved anymore.',
      truth: 'God is restoring my heart. He softens what hardship hardened. I have full capacity to give and receive love in Him.',
      scripture: 'Psalm 23:3'
    },
    {
      lie: 'The world left me behind. I\'ll never catch up.',
      truth: 'God\'s timing is flawless. He was preparing this exact moment before I was born. I haven\'t missed my purpose.',
      scripture: 'Psalm 139:16'
    },
    {
      lie: 'I don\'t deserve a fresh start. Not after everything I\'ve done.',
      truth: 'Christ paid the full price for my restoration. His mercies are new every morning. I am worthy of a fresh start because He says so.',
      scripture: 'Lamentations 3:22-23'
    }
  ]

  return (
    <>
      <div className="bg-amber-50 rounded-xl p-4">
        <h4 className="font-bold text-amber-800 mb-1 text-sm flex items-center gap-2">
          <RefreshCw className="w-4 h-4" />
          Replacing Prison Lies With God\'s Truth
        </h4>
        <p className="text-amber-700 text-xs leading-relaxed">
          Long incarceration plants deep lies about who you are. Every lie has a specific biblical
          counter-truth. Tap each card and speak these truths over yourself daily — they rewire the
          narrative your mind has accepted.
        </p>
      </div>

      {truths.map((item, i) => (
        <TruthCard key={i} item={item} index={i + 1} />
      ))}
    </>
  )
}

function TruthCard({ item, index }: { item: { lie: string; truth: string; scripture: string }; index: number }) {
  const [open, setOpen] = useState(false)

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <button
        onClick={() => setOpen(!open)}
        className="w-full p-3 text-left flex items-start gap-3 hover:bg-gray-50 transition-colors"
      >
        <span className="bg-red-100 text-red-600 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
          {index}
        </span>
        <div className="flex-1">
          <p className="text-xs text-gray-400 font-semibold uppercase tracking-wide mb-0.5">The Lie</p>
          <p className="text-gray-700 italic text-sm leading-snug">"{item.lie}"</p>
        </div>
        <ChevronDown className={`w-4 h-4 text-gray-400 flex-shrink-0 mt-1 transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>
      {open && (
        <div className="px-3 pb-3">
          <div className="bg-teal-50 border border-teal-100 rounded-xl p-3">
            <p className="text-xs text-teal-600 font-bold uppercase tracking-wide mb-1">God's Truth</p>
            <p className="text-gray-800 text-sm leading-relaxed mb-2">{item.truth}</p>
            <p className="text-teal-600 font-bold text-xs">— {item.scripture}</p>
          </div>
        </div>
      )}
    </div>
  )
}

/* ─── Grounding Section ─────────────────────────────────────────── */
function GroundingContent() {
  const [breathPhase, setBreathPhase] = useState<'idle' | 'inhale' | 'hold' | 'exhale'>('idle')
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const clearTimers = () => {
    if (timerRef.current) clearTimeout(timerRef.current)
  }

  const startBreathing = () => {
    clearTimers()
    setBreathPhase('inhale')
    timerRef.current = setTimeout(() => {
      setBreathPhase('hold')
      timerRef.current = setTimeout(() => {
        setBreathPhase('exhale')
        timerRef.current = setTimeout(() => {
          setBreathPhase('idle')
        }, 6000)
      }, 4000)
    }, 4000)
  }

  const stop = () => {
    clearTimers()
    setBreathPhase('idle')
  }

  useEffect(() => () => clearTimers(), [])

  const phaseConfig = {
    idle: { label: 'Tap to Begin', sub: '"Be still, and know that I am God." — Ps 46:10', color: 'bg-white/20' },
    inhale: { label: 'Breathe IN', sub: '4 seconds — fill your lungs slowly', color: 'bg-blue-400/30' },
    hold: { label: 'HOLD', sub: '4 seconds — rest in stillness', color: 'bg-teal-400/30' },
    exhale: { label: 'Breathe OUT', sub: '6 seconds — release all tension', color: 'bg-green-400/30' }
  }

  const cfg = phaseConfig[breathPhase]

  return (
    <>
      <div className="bg-green-50 rounded-xl p-4">
        <p className="text-green-800 font-bold text-sm mb-1">When the World Feels Too Much</p>
        <p className="text-green-700 text-xs leading-relaxed">
          Crowded stores, busy streets, family gatherings — everyday environments can trigger your nervous system.
          These tools are fast, effective, and always available.
        </p>
      </div>

      {/* 3-3-3 Rule */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <p className="font-bold text-gray-800 text-sm mb-1">The 3-3-3 Sensory Rule</p>
        <p className="text-gray-500 text-xs mb-3">Pause wherever you are and do this right now:</p>
        <div className="space-y-2">
          <div className="flex items-center gap-3 p-3 bg-blue-50 rounded-xl">
            <div className="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center flex-shrink-0">
              <Eye className="w-5 h-5 text-blue-600" />
            </div>
            <div>
              <p className="font-bold text-blue-800 text-sm">Name 3 things you can SEE</p>
              <p className="text-xs text-blue-600">Look around. Describe their color, shape, texture.</p>
            </div>
          </div>
          <div className="flex items-center gap-3 p-3 bg-green-50 rounded-xl">
            <div className="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center flex-shrink-0">
              <Ear className="w-5 h-5 text-green-600" />
            </div>
            <div>
              <p className="font-bold text-green-800 text-sm">Name 3 things you can HEAR</p>
              <p className="text-xs text-green-600">Focus on specific sounds — close, then distant.</p>
            </div>
          </div>
          <div className="flex items-center gap-3 p-3 bg-amber-50 rounded-xl">
            <div className="w-10 h-10 bg-amber-100 rounded-xl flex items-center justify-center flex-shrink-0">
              <Hand className="w-5 h-5 text-amber-600" />
            </div>
            <div>
              <p className="font-bold text-amber-800 text-sm">Name 3 things you can TOUCH</p>
              <p className="text-xs text-amber-600">Feel the ground, your clothes, a chair surface.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Breathing Exercise */}
      <div className="bg-gradient-to-br from-teal-600 to-teal-700 rounded-2xl p-5 text-white">
        <p className="font-bold text-sm mb-1">Paced Breathing with Scripture</p>
        <p className="text-white/70 text-xs mb-4">Breathe in rhythm with God's Word</p>

        <div
          className={`rounded-2xl ${cfg.color} p-5 text-center cursor-pointer transition-all`}
          onClick={breathPhase === 'idle' ? startBreathing : undefined}
        >
          <p className="text-2xl font-extrabold mb-1">{cfg.label}</p>
          <p className="text-white/80 text-xs">{cfg.sub}</p>
        </div>

        <div className="flex gap-2 mt-3">
          {breathPhase === 'idle' ? (
            <button
              onClick={startBreathing}
              className="flex-1 flex items-center justify-center gap-2 bg-white/20 hover:bg-white/30 py-2.5 rounded-xl font-semibold text-sm transition-colors"
            >
              <PlayCircle className="w-4 h-4" />
              Start Exercise
            </button>
          ) : (
            <button
              onClick={stop}
              className="flex-1 flex items-center justify-center gap-2 bg-white/20 hover:bg-white/30 py-2.5 rounded-xl font-semibold text-sm transition-colors"
            >
              <PauseCircle className="w-4 h-4" />
              Stop
            </button>
          )}
        </div>
      </div>

      {/* Quick Grounding Tips */}
      <div className="bg-gray-50 rounded-xl p-4">
        <p className="font-bold text-gray-800 text-sm mb-2 flex items-center gap-2">
          <Leaf className="w-4 h-4 text-green-500" />
          Instant Grounding Tips
        </p>
        <ul className="space-y-2">
          {[
            { tip: 'Cold water', detail: 'Splash your face or hold ice — this activates the dive reflex and slows your heart rate immediately.' },
            { tip: 'Plant your feet', detail: 'Press both feet flat to the floor. Feel solid ground. You are here, now, and safe.' },
            { tip: 'Slow your pace', detail: 'Walk and breathe slower deliberately. Your mind takes cues from your body.' },
            { tip: 'Step outside or find quiet', detail: 'Remove yourself from overwhelming stimuli. Give your nervous system space to reset.' },
            { tip: 'Simple prayer', detail: '"Jesus, be my peace right now." Six words. Always works.' }
          ].map((item, i) => (
            <li key={i} className="flex items-start gap-2 text-sm">
              <Check className="w-4 h-4 text-teal-500 mt-0.5 flex-shrink-0" />
              <span><strong className="text-gray-800">{item.tip}:</strong> <span className="text-gray-600">{item.detail}</span></span>
            </li>
          ))}
        </ul>
      </div>
    </>
  )
}

/* ─── Mentorship Section ────────────────────────────────────────── */
function MentorshipContent() {
  const routineItems = [
    { time: 'Morning', activity: 'Wake at the same time — pray, read one verse, drink water', icon: '🌅' },
    { time: 'Midday', activity: 'One meaningful task — even small victories count and compound', icon: '⚡' },
    { time: 'Afternoon', activity: 'Physical movement: walk, stretch, or any mild exercise', icon: '🚶' },
    { time: 'Evening', activity: 'Connect with someone safe — call, text, or meet in person', icon: '🤝' },
    { time: 'Night', activity: 'Write one thing God did today. Rest without guilt.', icon: '🌙' }
  ]

  return (
    <>
      <div className="bg-purple-50 rounded-xl p-4">
        <p className="text-purple-800 font-bold text-sm mb-1">You Were Not Meant to Do This Alone</p>
        <p className="text-purple-700 text-xs leading-relaxed">
          Healthy reentry is built on three pillars: a stable daily routine, a trusted spiritual mentor,
          and a grace-filled community that accepts you without condition.
        </p>
      </div>

      {/* Daily Routine */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <p className="font-bold text-gray-800 text-sm mb-1 flex items-center gap-2">
          <Clock className="w-4 h-4 text-purple-500" />
          Build a Stable Daily Routine
        </p>
        <p className="text-gray-500 text-xs mb-3">
          Predictability restores the sense of safety and control that incarceration stripped away. Start simple.
        </p>
        <div className="space-y-2">
          {routineItems.map((item, i) => (
            <div key={i} className="flex items-center gap-3 p-2.5 bg-gray-50 rounded-xl">
              <span className="text-lg w-8 text-center flex-shrink-0">{item.icon}</span>
              <div>
                <p className="text-xs font-bold text-gray-500 uppercase">{item.time}</p>
                <p className="text-sm text-gray-700">{item.activity}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Find a Mentor */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <p className="font-bold text-gray-800 text-sm mb-1 flex items-center gap-2">
          <BookOpen className="w-4 h-4 text-purple-500" />
          Find a Spiritual Mentor
        </p>
        <p className="text-gray-500 text-xs mb-3">
          A Christ-following mentor can walk you through reentry with wisdom, accountability, and grace.
        </p>
        <ul className="space-y-2">
          {[
            'Ask your pastor to connect you with a mature, patient believer',
            'Look for someone who listens more than they talk',
            'Commit to a weekly check-in — in person is best, phone works too',
            'Be honest about your struggles — secrecy feeds shame',
            'A good mentor has their own accountability; they\'re not perfect either'
          ].map((tip, i) => (
            <li key={i} className="flex items-start gap-2 text-sm">
              <Check className="w-4 h-4 text-teal-500 mt-0.5 flex-shrink-0" />
              <span className="text-gray-700">{tip}</span>
            </li>
          ))}
        </ul>
      </div>

      {/* Community */}
      <div className="bg-gradient-to-r from-teal-50 to-purple-50 rounded-xl p-4">
        <p className="font-bold text-gray-800 text-sm mb-2 flex items-center gap-2">
          <Users className="w-4 h-4 text-teal-600" />
          Grace-Filled Communities
        </p>
        <p className="text-gray-600 text-xs mb-3">These ministries welcome you without judgment:</p>
        <div className="space-y-2">
          <a
            href="https://www.celebraterecovery.com/crgroups"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors"
          >
            <div>
              <p className="font-bold text-gray-800 text-sm">Celebrate Recovery</p>
              <p className="text-xs text-gray-500">Christ-centered recovery groups nationwide</p>
            </div>
            <ExternalLink className="w-4 h-4 text-teal-500 flex-shrink-0" />
          </a>
          <a
            href="https://www.thefaithconnection.org"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors"
          >
            <div>
              <p className="font-bold text-gray-800 text-sm">The Faith Connection</p>
              <p className="text-xs text-gray-500">Our ministry home — you belong here</p>
            </div>
            <ExternalLink className="w-4 h-4 text-teal-500 flex-shrink-0" />
          </a>
        </div>
      </div>
    </>
  )
}

/* ─── Resources Section ─────────────────────────────────────────── */
function ResourcesContent() {
  const categories = [
    {
      title: 'Housing & Shelter',
      icon: '🏠',
      color: 'bg-blue-50',
      titleColor: 'text-blue-800',
      items: [
        { name: 'Volunteers of America Reentry', desc: 'Transitional housing and reentry services nationwide', url: 'https://www.voa.org/reentry' },
        { name: 'Salvation Army Housing', desc: 'Emergency and transitional housing programs', url: 'https://www.salvationarmyusa.org' },
        { name: 'HUD Housing Resources', desc: 'Find federal housing assistance near you', url: 'https://www.hud.gov' }
      ]
    },
    {
      title: 'Employment & Skills',
      icon: '💼',
      color: 'bg-green-50',
      titleColor: 'text-green-800',
      items: [
        { name: 'American Job Centers', desc: 'Free career help, resume support, and job training', url: 'https://www.careeronestop.org' },
        { name: 'Honest Jobs', desc: 'Job board specifically for people with records', url: 'https://www.honestjobs.com' },
        { name: 'Defy Ventures', desc: 'Entrepreneurship training for formerly incarcerated individuals', url: 'https://defyventures.org' }
      ]
    },
    {
      title: 'Legal & Documents',
      icon: '📋',
      color: 'bg-amber-50',
      titleColor: 'text-amber-800',
      items: [
        { name: 'Reentry Council Resources', desc: 'Federal reentry policies and legal rights overview', url: 'https://csgjusticecenter.org' },
        { name: 'Getting ID After Incarceration', desc: 'Step-by-step guide to restoring vital documents', url: 'https://www.benefits.gov' },
        { name: 'Legal Aid Services', desc: 'Find free or low-cost legal help in your area', url: 'https://www.lawhelp.org' }
      ]
    },
    {
      title: 'Mental Health & Counseling',
      icon: '💙',
      color: 'bg-purple-50',
      titleColor: 'text-purple-800',
      items: [
        { name: 'SAMHSA National Helpline', desc: '1-800-662-4357 — Free, confidential 24/7 support', url: 'https://www.samhsa.gov/find-help/national-helpline', phone: '1-800-662-4357' },
        { name: 'Christian Counselors Network', desc: 'Find a biblical counselor near you', url: 'https://www.aacc.net' },
        { name: 'Crisis Text Line', desc: 'Text HOME to 741741 — free crisis counseling', url: 'https://www.crisistextline.org' }
      ]
    },
    {
      title: 'Faith & Spiritual Support',
      icon: '✝️',
      color: 'bg-teal-50',
      titleColor: 'text-teal-800',
      items: [
        { name: 'Prison Fellowship', desc: 'Ongoing support and community for those post-release', url: 'https://www.prisonfellowship.org' },
        { name: 'Celebrate Recovery Locator', desc: 'Find a local CR group — safe, grace-filled community', url: 'https://www.celebraterecovery.com/crgroups' },
        { name: 'The Faith Connection', desc: 'Our home ministry — welcoming, non-judgmental', url: 'https://www.thefaithconnection.org' }
      ]
    },
    {
      title: 'Crisis Hotlines',
      icon: '📞',
      color: 'bg-rose-50',
      titleColor: 'text-rose-800',
      items: [
        { name: 'National Suicide & Crisis Lifeline', desc: 'Call or text 988 — available 24/7', url: 'https://988lifeline.org', phone: '988' },
        { name: 'Crisis Text Line', desc: 'Text HOME to 741741 anytime', url: 'https://www.crisistextline.org' },
        { name: 'SAMHSA Helpline', desc: 'Mental health and substance use — free, confidential', url: 'https://www.samhsa.gov/find-help/national-helpline', phone: '1-800-662-4357' }
      ]
    }
  ]

  return (
    <>
      <div className="bg-rose-50 rounded-xl p-4">
        <p className="text-rose-800 font-bold text-sm mb-1 flex items-center gap-2">
          <AlertCircle className="w-4 h-4" />
          Curated Support Resources
        </p>
        <p className="text-rose-700 text-xs leading-relaxed">
          These are vetted organizations that serve people in reentry with dignity, grace, and practical help.
        </p>
      </div>

      {categories.map((cat, i) => (
        <div key={i} className={`${cat.color} rounded-xl p-4`}>
          <p className={`font-bold text-sm ${cat.titleColor} mb-2 flex items-center gap-2`}>
            <span className="text-base">{cat.icon}</span>
            {cat.title}
          </p>
          <div className="space-y-2">
            {cat.items.map((item, j) => (
              <a
                key={j}
                href={item.url}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-start justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors gap-2"
              >
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-800 text-xs">{item.name}</p>
                  <p className="text-gray-500 text-xs mt-0.5">{item.desc}</p>
                  {item.phone && (
                    <p className="text-teal-600 text-xs font-bold mt-1 flex items-center gap-1">
                      <Phone className="w-3 h-3" />
                      {item.phone}
                    </p>
                  )}
                </div>
                <ExternalLink className="w-3.5 h-3.5 text-gray-400 flex-shrink-0 mt-0.5" />
              </a>
            ))}
          </div>
        </div>
      ))}
    </>
  )
}
