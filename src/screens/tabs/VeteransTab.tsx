import { useState, useEffect, useRef } from 'react'
import { Medal, Brain, RefreshCw, Shield, Users, MapPin, ChevronDown, Check, ExternalLink, Phone, CircleAlert as AlertCircle, Search, BookOpen, Heart, Star, Eye, Ear, Hand, Leaf, Wind, Waves, Lightbulb, Clock, CirclePlay as PlayCircle, CirclePause as PauseCircle, X } from 'lucide-react'

type SectionId = 'honor' | 'understanding' | 'scripture' | 'transition' | 'grounding' | 'resources'

export default function VeteransTab() {
  const [activeSection, setActiveSection] = useState<SectionId | null>('honor')

  const sections: { id: SectionId; title: string; subtitle: string; icon: React.ReactNode; color: string }[] = [
    {
      id: 'honor',
      title: 'You Are Seen & Honored',
      subtitle: 'God sees every sacrifice you made',
      icon: <Medal className="w-5 h-5" />,
      color: 'bg-green-700'
    },
    {
      id: 'understanding',
      title: 'Understanding Your Wounds',
      subtitle: 'PTSD, moral injury & invisible scars',
      icon: <Brain className="w-5 h-5" />,
      color: 'bg-blue-600'
    },
    {
      id: 'scripture',
      title: 'Scripture for Warriors',
      subtitle: 'God\'s Word for your specific battles',
      icon: <BookOpen className="w-5 h-5" />,
      color: 'bg-amber-600'
    },
    {
      id: 'transition',
      title: 'Civilian Life Transition',
      subtitle: 'Rebuilding identity, mission & belonging',
      icon: <RefreshCw className="w-5 h-5" />,
      color: 'bg-teal-600'
    },
    {
      id: 'grounding',
      title: 'Calming & Grounding',
      subtitle: 'Tools for when your nervous system fires',
      icon: <Shield className="w-5 h-5" />,
      color: 'bg-purple-600'
    },
    {
      id: 'resources',
      title: 'Veteran Resources',
      subtitle: 'Local, state, federal & faith-based help',
      icon: <MapPin className="w-5 h-5" />,
      color: 'bg-rose-600'
    }
  ]

  return (
    <div className="pb-8">
      {/* Hero Banner */}
      <div className="bg-gradient-to-br from-green-800 via-green-700 to-teal-600 px-5 pt-6 pb-8">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-12 h-12 bg-white/20 rounded-2xl flex items-center justify-center">
            <Medal className="w-7 h-7 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-extrabold text-white leading-tight">Veteran Support</h2>
            <p className="text-white/80 text-sm">You served with honor. God sees it all.</p>
          </div>
        </div>
        <div className="bg-white/15 rounded-2xl p-4 mt-2">
          <p className="text-white leading-relaxed text-sm">
            Whether you served in combat or simply wore the uniform and transitioned to civilian life —
            the invisible wounds of service are real. <strong className="text-white">You are not broken.
            You are not alone. And it is not weakness to ask for help.</strong> The bravest warriors know
            when to call for reinforcements.
          </p>
        </div>
        <div className="mt-3 bg-white/10 rounded-xl p-3 text-center">
          <p className="text-white/90 text-xs font-semibold">
            Veterans Crisis Line: Call or text <span className="font-black text-white">988, press 1</span>
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
            {section.id === 'honor' && <HonorContent />}
            {section.id === 'understanding' && <UnderstandingContent />}
            {section.id === 'scripture' && <ScriptureContent />}
            {section.id === 'transition' && <TransitionContent />}
            {section.id === 'grounding' && <GroundingContent />}
            {section.id === 'resources' && <ResourcesContent />}
          </SectionCard>
        ))}
      </div>

      {/* Bottom Encouragement */}
      <div className="mx-4 mt-4 bg-gradient-to-r from-green-100 to-teal-50 rounded-2xl p-5 text-center">
        <p className="text-green-800 font-bold italic text-base leading-snug">
          "Even though I walk through the darkest valley, I will fear no evil, for you are with me;
          your rod and your staff, they comfort me."
        </p>
        <p className="text-green-700 text-sm font-semibold mt-1">— Psalm 23:4</p>
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
    <div className={`bg-white rounded-2xl shadow-sm border transition-all ${isActive ? 'border-green-200 shadow-md' : 'border-gray-100'}`}>
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

/* ─── Honor Content ─────────────────────────────────────────────── */
function HonorContent() {
  return (
    <>
      <div className="bg-green-50 rounded-xl p-4">
        <h4 className="font-bold text-green-800 mb-2 text-sm flex items-center gap-2">
          <Medal className="w-4 h-4" />
          To Every Veteran Reading This
        </h4>
        <p className="text-green-700 text-sm leading-relaxed">
          You raised your hand. You left behind everything familiar — family, friends, comfort, safety —
          to serve something bigger than yourself. God does not take that lightly, and neither do we.
        </p>
      </div>

      {[
        {
          icon: <Star className="w-5 h-5 text-amber-500" />,
          title: 'Your Service Was Seen by God',
          description: 'Every deployment, every sleepless watch, every moral weight you carried — your Heavenly Father watched every moment. He does not forget your faithfulness.',
          bg: 'bg-amber-50'
        },
        {
          icon: <Heart className="w-5 h-5 text-rose-500" />,
          title: 'Struggle is Not Failure',
          description: 'Coming home is its own kind of battle. Asking for help is not weakness — it is the same tactical wisdom that kept your unit alive. You are not less of a warrior for being human.',
          bg: 'bg-rose-50'
        },
        {
          icon: <Shield className="w-5 h-5 text-blue-500" />,
          title: 'It is OK to Not be OK',
          description: 'Military culture builds incredible strength but can also build walls against receiving care. God created us to carry burdens together. "Bear one another\'s burdens." (Galatians 6:2)',
          bg: 'bg-blue-50'
        },
        {
          icon: <Medal className="w-5 h-5 text-green-700" />,
          title: 'Your Mission is Not Over',
          description: 'Leaving the military is not the end of your mission — it is a new assignment from the same Commander. "For we are God\'s handiwork, created in Christ Jesus to do good works, which God prepared in advance for us to do." (Ephesians 2:10)',
          bg: 'bg-green-50'
        }
      ].map((item, i) => (
        <div key={i} className={`${item.bg} rounded-xl p-3 flex gap-3`}>
          <div className="flex-shrink-0 mt-0.5">{item.icon}</div>
          <div>
            <p className="font-bold text-gray-800 text-sm">{item.title}</p>
            <p className="text-gray-600 text-xs leading-relaxed mt-0.5">{item.description}</p>
          </div>
        </div>
      ))}

      <div className="bg-gradient-to-r from-green-100 to-teal-100 rounded-xl p-4 text-center">
        <p className="text-green-800 font-bold italic text-sm">
          "The Lord is my strength and my shield; my heart trusts in him, and he helps me."
        </p>
        <p className="text-green-600 text-xs font-semibold mt-1">— Psalm 28:7</p>
      </div>
    </>
  )
}

/* ─── Understanding Content ─────────────────────────────────────── */
function UnderstandingContent() {
  const [activeTab, setActiveTab] = useState<'ptsd' | 'moral'>('ptsd')

  const ptsdSigns = [
    { icon: <Shield className="w-5 h-5 text-blue-500" />, title: 'Hypervigilance', description: 'Constant scanning for threats, inability to relax, startling easily. Your brain was trained to keep your unit alive — that wiring doesn\'t turn off when you come home.', bg: 'bg-blue-50' },
    { icon: <Waves className="w-5 h-5 text-cyan-500" />, title: 'Flashbacks & Intrusions', description: 'Unwanted memories, nightmares, or feeling transported back to a specific moment. These are not signs of weakness — they are signs your mind is trying to process extreme experiences.', bg: 'bg-cyan-50' },
    { icon: <Wind className="w-5 h-5 text-gray-500" />, title: 'Emotional Numbing', description: 'Feeling detached, unable to feel joy or connect with loved ones. Emotional armor that protected you in-theater can become a wall against the people who love you most.', bg: 'bg-gray-50' },
    { icon: <Lightbulb className="w-5 h-5 text-amber-500" />, title: 'Avoidance', description: 'Staying away from crowds, sounds, situations, or conversations that could trigger memories. This makes sense short-term but shrinks your world over time.', bg: 'bg-amber-50' },
    { icon: <Clock className="w-5 h-5 text-rose-500" />, title: 'Sleep Disruption', description: 'Insomnia, nightmares, or sleeping too much. Sleep is when the brain processes trauma — and when processing is blocked, the body resists rest.', bg: 'bg-rose-50' }
  ]

  const moralInjuries = [
    { title: 'What You Did', description: 'Actions taken under orders or survival pressure that violated your own moral code — even when those actions were necessary, legal, or in defense of others.' },
    { title: 'What You Witnessed', description: 'Seeing things happen — to civilians, to your unit, to the enemy — that your conscience cannot file away cleanly. The soul was not made to be neutral to suffering.' },
    { title: 'What You Failed to Prevent', description: 'Loss of a fellow service member, a mission gone wrong, civilian casualties — the weight of "I should have done more" or "I should have been there."' },
    { title: 'Betrayal by Leadership', description: 'When orders, policies, or systems violated what you were told you were fighting for. Betrayal by those in authority is its own kind of wound.' }
  ]

  return (
    <>
      <div className="bg-blue-50 rounded-xl p-4">
        <h4 className="font-bold text-blue-800 mb-1 text-sm">Two Distinct Wounds</h4>
        <p className="text-blue-700 text-xs leading-relaxed">
          Veterans often carry two different types of wounds that require different kinds of healing.
          Understanding the difference helps you find the right path forward.
        </p>
      </div>

      <div className="flex rounded-xl overflow-hidden border border-gray-200">
        <button
          onClick={() => setActiveTab('ptsd')}
          className={`flex-1 py-2.5 text-sm font-bold transition-colors ${activeTab === 'ptsd' ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-50'}`}
        >
          PTSD
        </button>
        <button
          onClick={() => setActiveTab('moral')}
          className={`flex-1 py-2.5 text-sm font-bold transition-colors ${activeTab === 'moral' ? 'bg-amber-600 text-white' : 'bg-white text-gray-600 hover:bg-gray-50'}`}
        >
          Moral Injury
        </button>
      </div>

      {activeTab === 'ptsd' && (
        <>
          <div className="bg-blue-50 rounded-xl p-3">
            <p className="text-blue-800 font-bold text-sm mb-1">Post-Traumatic Stress — A Nervous System Wound</p>
            <p className="text-blue-700 text-xs leading-relaxed">
              PTSD develops when the brain's threat-response system becomes locked in a state of high alert
              after extreme danger. It is not a character flaw, a spiritual failure, or a sign that something
              is permanently wrong with you. <strong>It is your brain doing exactly what it was trained to do — and
              needing help to reset.</strong>
            </p>
          </div>
          {ptsdSigns.map((item, i) => (
            <div key={i} className={`${item.bg} rounded-xl p-3 flex gap-3`}>
              <div className="flex-shrink-0 mt-0.5">{item.icon}</div>
              <div>
                <p className="font-bold text-gray-800 text-sm">{item.title}</p>
                <p className="text-gray-600 text-xs leading-relaxed mt-0.5">{item.description}</p>
              </div>
            </div>
          ))}
        </>
      )}

      {activeTab === 'moral' && (
        <>
          <div className="bg-amber-50 rounded-xl p-3">
            <p className="text-amber-800 font-bold text-sm mb-1">Moral Injury — A Soul Wound</p>
            <p className="text-amber-700 text-xs leading-relaxed">
              Moral injury is different from PTSD. It is a deep wound to your sense of right and wrong —
              a fracture between who you believed yourself to be and what you experienced in service.
              It carries <strong>guilt, shame, grief, and spiritual disconnection.</strong>
            </p>
            <p className="text-amber-700 text-xs leading-relaxed mt-2">
              The good news: this is exactly the wound Christ specializes in healing. He carried our shame,
              our failures, our worst moments — to Calvary. "For God did not send his Son into the world to
              condemn the world, but to save the world through him." (John 3:17)
            </p>
          </div>
          {moralInjuries.map((item, i) => (
            <div key={i} className="bg-white border border-amber-100 rounded-xl p-3">
              <p className="font-bold text-amber-800 text-sm">{item.title}</p>
              <p className="text-gray-600 text-xs leading-relaxed mt-1">{item.description}</p>
            </div>
          ))}
          <div className="bg-gradient-to-r from-amber-50 to-orange-50 rounded-xl p-4 text-center">
            <p className="text-amber-800 font-bold italic text-sm">
              "If we confess our sins, he is faithful and just and will forgive us our sins
              and purify us from all unrighteousness."
            </p>
            <p className="text-amber-600 text-xs font-semibold mt-1">— 1 John 1:9</p>
          </div>
        </>
      )}
    </>
  )
}

/* ─── Scripture Content ─────────────────────────────────────────── */
function ScriptureContent() {
  const verses = [
    {
      ref: 'Psalm 23:4',
      text: 'Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.',
      context: 'For those who have walked through dark places and cannot unsee what they have seen.'
    },
    {
      ref: 'Isaiah 43:2',
      text: 'When you pass through the waters, I will be with you; and when you pass through the rivers, they will not sweep over you. When you walk through the fire, you will not be burned.',
      context: 'For those who feel they are still in the fire — drowning in memories, overwhelmed by pain.'
    },
    {
      ref: 'Psalm 34:18',
      text: 'The Lord is close to the brokenhearted and saves those who are crushed in spirit.',
      context: 'God does not stand at a distance from your pain. He moves toward it.'
    },
    {
      ref: '2 Timothy 1:7',
      text: 'For the Spirit God gave us does not make us timid, but gives us power, love and self-discipline.',
      context: 'For the hypervigilance, the panic, the fear that won\'t go away — God\'s Spirit counteracts fear with power.'
    },
    {
      ref: 'Romans 8:38-39',
      text: 'For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers, neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord.',
      context: 'Not war. Not what you did. Not what was done to you. Nothing separates you from His love.'
    },
    {
      ref: 'Jeremiah 1:5',
      text: 'Before I formed you in the womb I knew you, before you were born I set you apart.',
      context: 'Your identity was established before your service record. You are His, first and always.'
    },
    {
      ref: 'Lamentations 3:22-23',
      text: 'Because of the Lord\'s great love we are not consumed, for his compassions never fail. They are new every morning; great is your faithfulness.',
      context: 'For the mornings after the nightmares, after the flashbacks — His mercies are fresh today.'
    },
    {
      ref: 'Galatians 6:2',
      text: 'Carry each other\'s burdens, and in this way you will fulfill the law of Christ.',
      context: 'You were never meant to carry the weight of service alone. Asking for help is obedience to this verse.'
    },
    {
      ref: 'Psalm 91:1-2',
      text: 'Whoever dwells in the shelter of the Most High will rest in the shadow of the Almighty. I will say of the Lord, "He is my refuge and my fortress, my God, in whom I trust."',
      context: 'The warrior\'s psalm — written for those who know what it is to need cover.'
    }
  ]

  return (
    <>
      <div className="bg-amber-50 rounded-xl p-4">
        <h4 className="font-bold text-amber-800 mb-1 text-sm flex items-center gap-2">
          <BookOpen className="w-4 h-4" />
          God's Word for Warriors
        </h4>
        <p className="text-amber-700 text-xs leading-relaxed">
          These are not comfort verses chosen to minimize what you experienced. These are battle-tested
          promises from a God who has always been present in the worst of human conflict.
        </p>
      </div>

      {verses.map((v, i) => (
        <ScriptureCard key={i} verse={v} index={i + 1} />
      ))}
    </>
  )
}

function ScriptureCard({ verse, index }: { verse: { ref: string; text: string; context: string }; index: number }) {
  const [open, setOpen] = useState(false)

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <button
        onClick={() => setOpen(!open)}
        className="w-full p-3 text-left flex items-start gap-3 hover:bg-gray-50 transition-colors"
      >
        <span className="bg-green-100 text-green-700 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
          {index}
        </span>
        <div className="flex-1">
          <p className="text-xs font-bold text-amber-700">{verse.ref}</p>
          <p className="text-gray-700 italic text-sm leading-snug mt-0.5 line-clamp-2">"{verse.text}"</p>
        </div>
        <ChevronDown className={`w-4 h-4 text-gray-400 flex-shrink-0 mt-1 transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>
      {open && (
        <div className="px-3 pb-3">
          <div className="bg-green-50 border border-green-100 rounded-xl p-3">
            <p className="text-gray-800 text-sm italic leading-relaxed mb-2">"{verse.text}"</p>
            <p className="text-amber-700 font-bold text-xs mb-2">— {verse.ref}</p>
            <div className="border-t border-green-100 pt-2">
              <p className="text-xs text-green-700 font-semibold uppercase tracking-wide mb-1">Why This Matters for You</p>
              <p className="text-gray-700 text-xs leading-relaxed">{verse.context}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

/* ─── Transition Content ─────────────────────────────────────────── */
function TransitionContent() {
  const lies = [
    {
      lie: 'I lost my identity when I took off the uniform. I don\'t know who I am anymore.',
      truth: 'Your identity was never your rank, your unit, or your MOS. Before you ever raised your right hand, God knew you by name and set you apart for a purpose that outlasts your service. You are His child first.',
      scripture: 'Jeremiah 1:5'
    },
    {
      lie: 'No one here understands what I went through. I\'m completely alone.',
      truth: 'You have a High Priest in Jesus Christ who "sympathizes with our weaknesses" and was "tempted in every way, just as we are." (Hebrews 4:15) He understands. And He has placed people in your path who can come alongside you.',
      scripture: 'Hebrews 4:14-16'
    },
    {
      lie: 'Civilian life is small and pointless compared to what I did in service.',
      truth: 'Every season has a God-ordained mission. The skills, discipline, and character built in uniform are not wasted — they are the foundation for your next assignment. "For we are God\'s handiwork, created in Christ Jesus to do good works." (Ephesians 2:10)',
      scripture: 'Ephesians 2:10'
    },
    {
      lie: 'I saw and did things I can never tell anyone. God couldn\'t forgive what I know about myself.',
      truth: 'Christ bore every single moment to Calvary — including what you\'ve never been able to say out loud. There is no sin, no act, no memory that is beyond the reach of His blood. 1 John 1:9 is unconditional.',
      scripture: '1 John 1:9'
    },
    {
      lie: 'I\'m too angry, too broken, too damaged for my family. They\'d be better off without me.',
      truth: 'This is the enemy\'s most dangerous lie. You are worth fighting for. Your family needs you — healed, not absent. Psalm 34:18: "The Lord is close to the brokenhearted." He is close to you right now.',
      scripture: 'Psalm 34:18'
    }
  ]

  const routineItems = [
    { time: 'Morning', activity: 'Rise at a consistent time — your body needs structure it can trust again', icon: '🌅' },
    { time: 'Mission', activity: 'Set one purpose for the day — even small. Mission gives meaning.', icon: '🎯' },
    { time: 'Movement', activity: 'Physical exercise is one of the most evidence-supported PTSD interventions available', icon: '🏃' },
    { time: 'Connect', activity: 'One meaningful human contact per day — in person is best', icon: '🤝' },
    { time: 'Quiet', activity: 'Ten minutes of Scripture and prayer — before the noise of the day begins', icon: '📖' },
    { time: 'Night', activity: 'Write one thing that went right. Rest without guilt.', icon: '🌙' }
  ]

  return (
    <>
      <div className="bg-teal-50 rounded-xl p-4">
        <p className="text-teal-800 font-bold text-sm mb-1">The Hardest Battle Is Coming Home</p>
        <p className="text-teal-700 text-xs leading-relaxed">
          Transition from military to civilian life is one of the most profound identity shifts a person
          can experience. The structure, purpose, brotherhood, and mission that organized your world are
          suddenly gone — replaced by a world that does not understand what you carry.
          <strong className="text-teal-800"> That is real, and it is hard. Here are truths that help.</strong>
        </p>
      </div>

      {lies.map((item, i) => (
        <TruthCard key={i} item={item} index={i + 1} />
      ))}

      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <p className="font-bold text-gray-800 text-sm mb-1 flex items-center gap-2">
          <Clock className="w-4 h-4 text-teal-500" />
          Rebuild With Structure
        </p>
        <p className="text-gray-500 text-xs mb-3">
          Military life was structured. Civilian life often isn't. Creating your own structure restores
          the sense of safety and control that transition strips away.
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

/* ─── Grounding Content ─────────────────────────────────────────── */
function GroundingContent() {
  const [breathPhase, setBreathPhase] = useState<'idle' | 'inhale' | 'hold' | 'exhale'>('idle')
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const clearTimers = () => { if (timerRef.current) clearTimeout(timerRef.current) }
  const startBreathing = () => {
    clearTimers()
    setBreathPhase('inhale')
    timerRef.current = setTimeout(() => {
      setBreathPhase('hold')
      timerRef.current = setTimeout(() => {
        setBreathPhase('exhale')
        timerRef.current = setTimeout(() => setBreathPhase('idle'), 6000)
      }, 4000)
    }, 4000)
  }
  const stop = () => { clearTimers(); setBreathPhase('idle') }
  useEffect(() => () => clearTimers(), [])

  const phaseConfig = {
    idle: { label: 'Tap to Begin', sub: '"Be still, and know that I am God." — Ps 46:10', color: 'bg-white/20' },
    inhale: { label: 'Breathe IN', sub: '4 seconds — fill your lungs slowly', color: 'bg-blue-400/30' },
    hold: { label: 'HOLD', sub: '4 seconds — rest in stillness', color: 'bg-teal-400/30' },
    exhale: { label: 'Breathe OUT', sub: '6 seconds — release it all', color: 'bg-green-400/30' }
  }
  const cfg = phaseConfig[breathPhase]

  return (
    <>
      <div className="bg-purple-50 rounded-xl p-4">
        <p className="text-purple-800 font-bold text-sm mb-1">When Your Nervous System Fires</p>
        <p className="text-purple-700 text-xs leading-relaxed">
          Hypervigilance, flashbacks, and panic responses are involuntary. Your trained brain is doing what
          it was built to do. These tools help interrupt the threat-response loop and return you to the present.
        </p>
      </div>

      {/* 5-4-3-2-1 Grounding */}
      <div className="bg-white rounded-xl border border-gray-200 p-4">
        <p className="font-bold text-gray-800 text-sm mb-1">5-4-3-2-1 Sensory Grounding</p>
        <p className="text-gray-500 text-xs mb-3">Use this when a trigger hits. Ground yourself in the present moment:</p>
        <div className="space-y-2">
          {[
            { icon: <Eye className="w-5 h-5 text-blue-600" />, count: 5, sense: 'SEE', prompt: 'Name 5 things you can see right now. Describe color, shape, distance.', bg: 'bg-blue-50' },
            { icon: <Hand className="w-5 h-5 text-green-600" />, count: 4, sense: 'TOUCH', prompt: 'Name 4 things you can physically feel — ground under your feet, air on your skin.', bg: 'bg-green-50' },
            { icon: <Ear className="w-5 h-5 text-amber-600" />, count: 3, sense: 'HEAR', prompt: 'Name 3 distinct sounds around you — close and distant.', bg: 'bg-amber-50' },
            { icon: <Leaf className="w-5 h-5 text-rose-600" />, count: 2, sense: 'SMELL', prompt: 'Name 2 things you can smell. If nothing, breathe slowly and notice.', bg: 'bg-rose-50' },
            { icon: <Lightbulb className="w-5 h-5 text-purple-600" />, count: 1, sense: 'TASTE', prompt: 'Name 1 thing you can taste or notice in your mouth right now.', bg: 'bg-purple-50' }
          ].map((item, i) => (
            <div key={i} className={`flex items-center gap-3 p-3 ${item.bg} rounded-xl`}>
              <div className="w-10 h-10 bg-white rounded-xl flex items-center justify-center flex-shrink-0 shadow-sm">
                {item.icon}
              </div>
              <div>
                <p className="font-bold text-gray-800 text-sm">{item.count} things you can {item.sense}</p>
                <p className="text-xs text-gray-600">{item.prompt}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Breathing */}
      <div className="bg-gradient-to-br from-green-700 to-teal-700 rounded-2xl p-5 text-white">
        <p className="font-bold text-sm mb-1">Tactical Breathing</p>
        <p className="text-white/70 text-xs mb-4">Used by Special Forces to regulate under fire — now use it to regulate at home</p>
        <div className={`rounded-2xl ${cfg.color} p-5 text-center cursor-pointer transition-all`} onClick={breathPhase === 'idle' ? startBreathing : undefined}>
          <p className="text-2xl font-extrabold mb-1">{cfg.label}</p>
          <p className="text-white/80 text-xs">{cfg.sub}</p>
        </div>
        <div className="flex gap-2 mt-3">
          {breathPhase === 'idle' ? (
            <button onClick={startBreathing} className="flex-1 flex items-center justify-center gap-2 bg-white/20 hover:bg-white/30 py-2.5 rounded-xl font-semibold text-sm transition-colors">
              <PlayCircle className="w-4 h-4" />
              Start
            </button>
          ) : (
            <button onClick={stop} className="flex-1 flex items-center justify-center gap-2 bg-white/20 hover:bg-white/30 py-2.5 rounded-xl font-semibold text-sm transition-colors">
              <PauseCircle className="w-4 h-4" />
              Stop
            </button>
          )}
        </div>
      </div>

      <div className="bg-gray-50 rounded-xl p-4">
        <p className="font-bold text-gray-800 text-sm mb-2 flex items-center gap-2">
          <Leaf className="w-4 h-4 text-green-500" />
          Rapid Reset Tools
        </p>
        <ul className="space-y-2">
          {[
            { tip: 'Cold water on face/wrists', detail: 'Activates the mammalian dive reflex — slows heart rate in under 30 seconds.' },
            { tip: 'Plant your feet, press hard', detail: 'Stand or sit. Press both feet into the floor. You are here. You are safe. Not there.' },
            { tip: 'Name your location aloud', detail: '"I am at [location]. It is [year]. I am safe right now." Saying it out loud uses a different brain pathway.' },
            { tip: 'Slow your walk deliberately', detail: 'Your mind follows your body. Slowing physical movement signals the nervous system to downshift.' },
            { tip: 'Simple prayer', detail: '"Lord, I am here. You are here. That is enough." Say it until you believe it.' }
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

/* ─── Resources Content ─────────────────────────────────────────── */
function ResourcesContent() {
  const [zipcode, setZipcode] = useState('')
  const [submittedZip, setSubmittedZip] = useState('')

  const handleZipSearch = () => {
    const clean = zipcode.replace(/\D/g, '').slice(0, 5)
    if (clean.length === 5) setSubmittedZip(clean)
  }

  const localLinks = submittedZip
    ? [
        {
          name: 'VA Medical Centers & Clinics Near You',
          desc: `Find VA health facilities near ${submittedZip}`,
          url: `https://www.va.gov/find-locations/?location=${submittedZip}&facilityType=health`,
          emoji: '🏥'
        },
        {
          name: 'Vet Centers Near You',
          desc: `Readjustment counseling, peer support, free — near ${submittedZip}`,
          url: `https://www.va.gov/find-locations/?location=${submittedZip}&facilityType=vet_center`,
          emoji: '🤝'
        },
        {
          name: 'SAMHSA Treatment Locator',
          desc: `Mental health and substance treatment near ${submittedZip}`,
          url: `https://findtreatment.gov/locator?location=${submittedZip}`,
          emoji: '💙'
        },
        {
          name: '211 Local Resources',
          desc: `Local social services, housing, crisis support near ${submittedZip}`,
          url: `https://www.211.org/find-local-resources?location=${submittedZip}`,
          emoji: '📞'
        },
        {
          name: 'American Legion Posts Near You',
          desc: `Veteran community, support, and advocacy near ${submittedZip}`,
          url: `https://www.legion.org/posts/find-a-post?zip=${submittedZip}`,
          emoji: '🇺🇸'
        },
        {
          name: 'VFW Posts Near You',
          desc: `Veterans of Foreign Wars community near ${submittedZip}`,
          url: `https://www.vfw.org/find-a-post?zip=${submittedZip}`,
          emoji: '⭐'
        }
      ]
    : []

  const crisisResources = [
    { name: 'Veterans Crisis Line', desc: 'Call or text 988, press 1 — 24/7, confidential', url: 'https://www.veteranscrisisline.net', phone: '988 (press 1)', emoji: '🆘' },
    { name: 'Veterans Crisis Chat', desc: 'VeteransCrisisLine.net — online chat available now', url: 'https://www.veteranscrisisline.net/get-help-now/chat/', emoji: '💬' },
    { name: 'Crisis Text Line', desc: 'Text HOME to 741741 — free, 24/7', url: 'https://www.crisistextline.org', emoji: '📱' }
  ]

  const federalResources = [
    { name: 'VA Mental Health Services', desc: 'Free mental health care for eligible veterans — no disability rating required for many services', url: 'https://www.mentalhealth.va.gov', emoji: '🏛️' },
    { name: 'VA PTSD Treatment', desc: 'VA PTSD program — evidence-based treatments, including CPT and PE therapy', url: 'https://www.ptsd.va.gov/gethelp/find_treatment.asp', emoji: '🔬' },
    { name: 'My HealtheVet (VA Portal)', desc: 'Manage VA health care, refills, appointments online', url: 'https://www.myhealth.va.gov', emoji: '💊' },
    { name: 'VA Benefits & Claims', desc: 'File or check the status of disability claims, education, housing benefits', url: 'https://www.va.gov/decision-reviews/', emoji: '📋' },
    { name: 'HUD-VASH (Homeless Vets)', desc: 'Housing vouchers specifically for homeless veterans', url: 'https://www.hud.gov/program_offices/public_indian_housing/programs/hcv/vash', emoji: '🏠' },
    { name: 'Veterans Employment Center', desc: 'Hire a vet, resume tools, federal hiring preferences', url: 'https://www.careeronestop.org/Veterans/default.aspx', emoji: '💼' }
  ]

  const faithResources = [
    { name: 'Mighty Oaks Foundation', desc: 'Christ-centered warrior programs for veterans and first responders — peer-based healing retreats', url: 'https://www.mightyoaksprograms.org', emoji: '🌳' },
    { name: 'Save A Warrior', desc: 'Evidence-based moral injury healing programs for veterans and active duty', url: 'https://www.saveawarrior.org', emoji: '⚔️' },
    { name: 'Team Red White & Blue', desc: 'Physical and social activity — reconnect with community and purpose', url: 'https://www.teamrwb.org', emoji: '🏃' },
    { name: 'Give An Hour', desc: 'Free mental health care for military and veterans from volunteer providers', url: 'https://giveanhour.org', emoji: '⏱️' },
    { name: 'Dave Roever Ministries', desc: 'Vietnam veteran and evangelist — speaks directly to warriors about God\'s healing and restoration', url: 'https://daveroever.org', emoji: '✝️' },
    { name: 'Celebrate Recovery (Military)', desc: 'Find a CR group near you — Christ-centered recovery community that welcomes veterans', url: 'https://www.celebraterecovery.com/crgroups', emoji: '🙏' },
    { name: 'The Faith Connection', desc: 'Our ministry home — safe, non-judgmental community that welcomes veterans', url: 'https://www.thefaithconnection.org', emoji: '🏠' }
  ]

  return (
    <>
      <div className="bg-rose-50 rounded-xl p-4">
        <p className="text-rose-800 font-bold text-sm mb-1 flex items-center gap-2">
          <AlertCircle className="w-4 h-4" />
          Veteran Resources
        </p>
        <p className="text-rose-700 text-xs leading-relaxed">
          Resources are organized into: Crisis Lines, Local (zip code), Federal, and Faith-Based.
          You deserve help. Finding it is not weakness — it is mission planning.
        </p>
      </div>

      {/* Crisis — always first */}
      <div className="bg-red-50 rounded-xl p-4">
        <p className="font-bold text-red-800 text-sm mb-2 flex items-center gap-2">
          <span>🆘</span> Crisis Lines — Use These Now If Needed
        </p>
        <div className="space-y-2">
          {crisisResources.map((item, i) => (
            <a key={i} href={item.url} target="_blank" rel="noopener noreferrer"
              className="flex items-start justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors gap-2">
              <div className="flex-1">
                <p className="font-semibold text-gray-800 text-xs flex items-center gap-1.5">
                  <span>{item.emoji}</span>{item.name}
                </p>
                <p className="text-gray-500 text-xs mt-0.5">{item.desc}</p>
                {'phone' in item && item.phone && (
                  <p className="text-red-600 text-xs font-bold mt-1 flex items-center gap-1">
                    <Phone className="w-3 h-3" />{item.phone}
                  </p>
                )}
              </div>
              <ExternalLink className="w-3.5 h-3.5 text-gray-400 flex-shrink-0 mt-0.5" />
            </a>
          ))}
        </div>
      </div>

      {/* Local Zip Finder */}
      <div className="bg-blue-50 rounded-xl p-4">
        <p className="font-bold text-blue-800 text-sm mb-2 flex items-center gap-2">
          <MapPin className="w-4 h-4" />
          Find Local Resources
        </p>
        <p className="text-blue-700 text-xs mb-3">
          Enter your zip code to find VA facilities, Vet Centers, local counseling, and community support near you.
        </p>
        <div className="flex gap-2">
          <input
            type="text"
            inputMode="numeric"
            maxLength={5}
            value={zipcode}
            onChange={e => setZipcode(e.target.value.replace(/\D/g, '').slice(0, 5))}
            onKeyDown={e => e.key === 'Enter' && handleZipSearch()}
            placeholder="Enter zip code"
            className="flex-1 border border-blue-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white"
          />
          <button
            onClick={handleZipSearch}
            disabled={zipcode.length !== 5}
            className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white px-4 rounded-xl text-sm font-semibold transition-colors flex items-center gap-1.5"
          >
            <Search className="w-4 h-4" />
            Find
          </button>
          {submittedZip && (
            <button onClick={() => { setSubmittedZip(''); setZipcode('') }}
              className="p-2.5 rounded-xl bg-gray-200 hover:bg-gray-300 transition-colors">
              <X className="w-4 h-4 text-gray-600" />
            </button>
          )}
        </div>

        {submittedZip && (
          <div className="mt-3 space-y-2">
            <p className="text-xs font-bold text-blue-700 uppercase tracking-wide">Local Results for {submittedZip}</p>
            {localLinks.map((item, i) => (
              <a key={i} href={item.url} target="_blank" rel="noopener noreferrer"
                className="flex items-start justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors gap-2">
                <div className="flex-1">
                  <p className="font-semibold text-gray-800 text-xs flex items-center gap-1.5">
                    <span>{item.emoji}</span>{item.name}
                  </p>
                  <p className="text-gray-500 text-xs mt-0.5">{item.desc}</p>
                </div>
                <ExternalLink className="w-3.5 h-3.5 text-gray-400 flex-shrink-0 mt-0.5" />
              </a>
            ))}
          </div>
        )}
      </div>

      {/* Federal */}
      <div className="bg-indigo-50 rounded-xl p-4">
        <p className="font-bold text-indigo-800 text-sm mb-2 flex items-center gap-2">
          <span>🏛️</span> Federal VA Resources
        </p>
        <div className="space-y-2">
          {federalResources.map((item, i) => (
            <a key={i} href={item.url} target="_blank" rel="noopener noreferrer"
              className="flex items-start justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors gap-2">
              <div className="flex-1">
                <p className="font-semibold text-gray-800 text-xs flex items-center gap-1.5">
                  <span>{item.emoji}</span>{item.name}
                </p>
                <p className="text-gray-500 text-xs mt-0.5">{item.desc}</p>
              </div>
              <ExternalLink className="w-3.5 h-3.5 text-gray-400 flex-shrink-0 mt-0.5" />
            </a>
          ))}
        </div>
      </div>

      {/* Faith & Community */}
      <div className="bg-teal-50 rounded-xl p-4">
        <p className="font-bold text-teal-800 text-sm mb-2 flex items-center gap-2">
          <span>✝️</span> Faith-Based & Community Programs
        </p>
        <div className="space-y-2">
          {faithResources.map((item, i) => (
            <a key={i} href={item.url} target="_blank" rel="noopener noreferrer"
              className="flex items-start justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors gap-2">
              <div className="flex-1">
                <p className="font-semibold text-gray-800 text-xs flex items-center gap-1.5">
                  <span>{item.emoji}</span>{item.name}
                </p>
                <p className="text-gray-500 text-xs mt-0.5">{item.desc}</p>
              </div>
              <ExternalLink className="w-3.5 h-3.5 text-gray-400 flex-shrink-0 mt-0.5" />
            </a>
          ))}
        </div>
      </div>

      {/* State Resources Note */}
      <div className="bg-gray-50 rounded-xl p-4">
        <p className="font-bold text-gray-700 text-sm mb-1 flex items-center gap-2">
          <Users className="w-4 h-4 text-gray-500" />
          State Veterans Services
        </p>
        <p className="text-gray-600 text-xs leading-relaxed mb-2">
          Every state has a Veterans Affairs department offering benefits counseling, claims assistance,
          employment help, and crisis services — often free of charge.
        </p>
        <a href="https://www.va.gov/statedva.htm" target="_blank" rel="noopener noreferrer"
          className="flex items-center justify-between p-3 bg-white rounded-xl hover:bg-gray-50 transition-colors border border-gray-200">
          <div>
            <p className="font-semibold text-gray-800 text-xs">Find Your State Veterans Affairs Office</p>
            <p className="text-gray-500 text-xs mt-0.5">VA.gov — all 50 states + territories listed</p>
          </div>
          <ExternalLink className="w-3.5 h-3.5 text-gray-400 flex-shrink-0" />
        </a>
      </div>
    </>
  )
}
