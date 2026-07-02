import { useState } from 'react'
import {
  DoorOpen, Brain, Heart, Users, ChevronDown, ChevronUp, Shield,
  Sun, Eye, Ear, Hand, RefreshCw, BookOpen, MapPin, ExternalLink, Check
} from 'lucide-react'

export default function ReentrySection() {
  const [isExpanded, setIsExpanded] = useState(false)
  const [activeSection, setActiveSection] = useState<string | null>(null)

  const sections = [
    { id: 'understanding', title: 'Understanding Post-Incarceration Struggles', icon: <Brain className="w-5 h-5" /> },
    { id: 'renewing', title: 'Renewing Your Mind', icon: <RefreshCw className="w-5 h-5" /> },
    { id: 'grounding', title: 'Calming Grounding Tools', icon: <Sun className="w-5 h-5" /> },
    { id: 'mentorship', title: 'Mentorship & Support Networks', icon: <Users className="w-5 h-5" /> }
  ]

  return (
    <div className="bg-white rounded-2xl shadow-md border border-gray-100 overflow-hidden">
      {/* Header */}
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="w-full flex items-center justify-between p-5 hover:bg-gray-50 transition-colors"
      >
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-gradient-to-br from-accent-teal to-accent-teal/70 rounded-xl flex items-center justify-center text-white">
            <DoorOpen className="w-6 h-6" />
          </div>
          <div className="text-left">
            <h3 className="font-bold text-gray-900">Reentry & Transition Freedom</h3>
            <p className="text-sm text-gray-500">Compassionate help for post-incarceration adjustment</p>
          </div>
        </div>
        {isExpanded ? (
          <ChevronUp className="w-5 h-5 text-gray-400" />
        ) : (
          <ChevronDown className="w-5 h-5 text-gray-400" />
        )}
      </button>

      {isExpanded && (
        <div className="border-t border-gray-100">
          {/* Introduction */}
          <div className="p-5 bg-gradient-to-r from-accent-teal/5 to-primary-50/50">
            <p className="text-gray-700 leading-relaxed text-sm">
              If you've experienced long-term incarceration, you may feel like something is fundamentally "broken" inside.
              We want you to know: <strong className="text-primary-600">you are not broken.</strong> What you're experiencing
              is a natural response to surviving in a high-vigilance environment. Through Christ's grace, your mind can fully
              heal and renew. This section is here to walk with you through that process.
            </p>
          </div>

          {/* Section Navigation */}
          <div className="grid grid-cols-2 gap-2 p-4">
            {sections.map(section => (
              <button
                key={section.id}
                onClick={() => setActiveSection(activeSection === section.id ? null : section.id)}
                className={`flex items-center gap-2 p-3 rounded-xl text-left transition-all ${
                  activeSection === section.id
                    ? 'bg-accent-teal/10 border-2 border-accent-teal'
                    : 'bg-gray-50 hover:bg-gray-100 border-2 border-transparent'
                }`}
              >
                <span className={activeSection === section.id ? 'text-accent-teal' : 'text-gray-400'}>
                  {section.icon}
                </span>
                <span className={`text-xs font-medium ${activeSection === section.id ? 'text-accent-teal' : 'text-gray-600'}`}>
                  {section.title}
                </span>
              </button>
            ))}
          </div>

          {/* Section Content */}
          <div className="p-4 pt-0">
            {activeSection === 'understanding' && <UnderstandingSection />}
            {activeSection === 'renewing' && <RenewingSection />}
            {activeSection === 'grounding' && <GroundingSection />}
            {activeSection === 'mentorship' && <MentorshipSection />}
          </div>
        </div>
      )}
    </div>
  )
}

function UnderstandingSection() {
  return (
    <div className="space-y-4 animate-fade-in">
      <div className="bg-blue-50 rounded-xl p-4">
        <h4 className="font-bold text-blue-800 mb-3 flex items-center gap-2">
          <Heart className="w-5 h-5" />
          You Are Not Broken
        </h4>
        <p className="text-sm text-blue-700 leading-relaxed mb-3">
          Long-term incarceration requires your mind and nervous system to adapt to constant alertness and hyper-control.
          This survival state helped you get through. Now, transitioning to a high-choice, fast-paced society can feel
          overwhelming — but this doesn't mean something is wrong with you.
        </p>
        <p className="text-sm text-blue-700 leading-relaxed">
          What you're experiencing is called <strong>Post-Incarceration Syndrome (PICS)</strong> — a natural response
          to prolonged survival mode. It can be completely healed through Christ's grace and mental realignment.
        </p>
      </div>

      <div className="bg-white rounded-xl p-4 border border-gray-200">
        <h4 className="font-bold text-gray-800 mb-3">Common Experiences After Release:</h4>
        <ul className="space-y-3">
          <ExperienceItem
            title="Hypervigilance"
            description="Feeling constantly on guard, scanning for threats, difficulty relaxing"
          />
          <ExperienceItem
            title="Sensory Overload"
            description="Crowds, traffic, bright lights, or loud sounds feeling overwhelming"
          />
          <ExperienceItem
            title="Decision Fatigue"
            description="Feeling exhausted by too many daily choices after years of structured routine"
          />
          <ExperienceItem
            title="Emotional Numbing"
            description="Difficulty feeling joy or connection, feeling 'flat' or disconnected"
          />
          <ExperienceItem
            title="Identity Confusion"
            description="Struggling with who you are outside of the prison system"
          />
        </ul>
      </div>

      <div className="bg-gradient-to-r from-primary-100 to-secondary-100 rounded-xl p-4">
        <p className="text-primary-800 font-semibold text-center italic">
          "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here!"
        </p>
        <p className="text-center text-primary-600 text-sm mt-1">— 2 Corinthians 5:17</p>
      </div>
    </div>
  )
}

function ExperienceItem({ title, description }: { title: string; description: string }) {
  return (
    <li className="flex items-start gap-3">
      <div className="w-6 h-6 bg-gray-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
        <Check className="w-4 h-4 text-gray-400" />
      </div>
      <div>
        <span className="font-semibold text-gray-800">{title}:</span>
        <span className="text-gray-600 text-sm ml-1">{description}</span>
      </div>
    </li>
  )
}

function RenewingSection() {
  const truths = [
    {
      lie: "I am institutionalized and will never fully adapt. I'm broken forever.",
      truth: "I am a new creation in Christ. God is working in me, renewing my mind day by day.",
      scripture: "2 Corinthians 5:17"
    },
    {
      lie: "I don't belong in this world anymore. Everyone can see I'm different.",
      truth: "I belong to God's family. He has prepared good works for me to walk in.",
      scripture: "Ephesians 2:10"
    },
    {
      lie: "I'll always feel anxious and can never relax like normal people.",
      truth: "God gives me peace that transcends understanding. My nervous system can heal.",
      scripture: "Philippians 4:7"
    },
    {
      lie: "I have too many choices now. I can't handle this freedom.",
      truth: "God's Spirit guides me. I can take one decision at a time, trusting His lead.",
      scripture: "Proverbs 3:5-6"
    },
    {
      lie: "My past defines me. I'll always be seen as an ex-con.",
      truth: "My identity is in Christ alone. I am forgiven, redeemed, and called by name.",
      scripture: "Isaiah 43:1"
    },
    {
      lie: "I can't connect with people. I've lost the ability to feel.",
      truth: "God is restoring my soul. He softens my heart and gives me capacity to love again.",
      scripture: "Psalm 23:3"
    },
    {
      lie: "The world moved on without me. I'm too far behind.",
      truth: "God's timing is perfect. He was preparing this moment for me before I was born.",
      scripture: "Psalm 139:16"
    }
  ]

  return (
    <div className="space-y-4 animate-fade-in">
      <div className="bg-amber-50 rounded-xl p-4">
        <h4 className="font-bold text-amber-800 mb-2 flex items-center gap-2">
          <RefreshCw className="w-5 h-5" />
          Transforming Lies Into Truth
        </h4>
        <p className="text-sm text-amber-700 leading-relaxed">
          The enemy whispers that you're permanently damaged. God declares you a new creation.
          Below are common post-prison lies — and the Biblical truths that replace them.
          <strong> Speak these truths over yourself daily.</strong>
        </p>
      </div>

      <div className="space-y-3">
        {truths.map((item, index) => (
          <TruthCard key={index} item={item} index={index + 1} />
        ))}
      </div>
    </div>
  )
}

function TruthCard({ item, index }: { item: { lie: string; truth: string; scripture: string }; index: number }) {
  const [expanded, setExpanded] = useState(false)

  return (
    <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <button
        onClick={() => setExpanded(!expanded)}
        className="w-full p-4 text-left flex items-start gap-3 hover:bg-gray-50 transition-colors"
      >
        <span className="bg-red-100 text-red-600 w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0">
          {index}
        </span>
        <div className="flex-1">
          <p className="text-sm text-gray-500 font-medium">The Lie:</p>
          <p className="text-gray-700 italic text-sm">"{item.lie}"</p>
        </div>
        <ChevronDown className={`w-5 h-5 text-gray-400 flex-shrink-0 transition-transform ${expanded ? 'rotate-180' : ''}`} />
      </button>

      {expanded && (
        <div className="px-4 pb-4 animate-fade-in">
          <div className="bg-accent-teal/10 rounded-xl p-4">
            <p className="text-accent-teal font-semibold text-sm mb-1">✝️ Biblical Freedom:</p>
            <p className="text-gray-800 text-sm leading-relaxed mb-2">"{item.truth}"</p>
            <p className="text-primary-500 font-semibold text-sm">— {item.scripture}</p>
          </div>
        </div>
      )}
    </div>
  )
}

function GroundingSection() {
  const [breathingStep, setBreathingStep] = useState(0)
  const [isActive, setIsActive] = useState(false)

  const startGrounding = () => {
    setIsActive(true)
    setBreathingStep(1)
    setTimeout(() => setBreathingStep(2), 4000)
    setTimeout(() => setBreathingStep(3), 8000)
    setTimeout(() => {
      setBreathingStep(0)
      setIsActive(false)
    }, 14000)
  }

  return (
    <div className="space-y-4 animate-fade-in">
      <div className="bg-purple-50 rounded-xl p-4">
        <h4 className="font-bold text-purple-800 mb-2 flex items-center gap-2">
          <Sun className="w-5 h-5" />
          When the World Feels Too Much
        </h4>
        <p className="text-sm text-purple-700 leading-relaxed">
          After incarceration, normal environments (malls, busy streets, family gatherings) can trigger
          sensory overload. Your nervous system learned to stay hyper-alert. These tools help you calm
          and ground yourself when everything feels overwhelming.
        </p>
      </div>

      {/* 3-3-3 Rule */}
      <div className="bg-white rounded-xl p-4 border border-gray-200">
        <h5 className="font-bold text-gray-800 mb-3 flex items-center gap-2">
          <Eye className="w-4 h-4 text-primary-500" />
          The 3-3-3 Sensory Rule
        </h5>
        <p className="text-sm text-gray-600 mb-3">
          When anxiety hits, pause and do this simple grounding exercise:
        </p>
        <div className="space-y-3">
          <div className="flex items-center gap-3 p-3 bg-blue-50 rounded-lg">
            <Eye className="w-6 h-6 text-blue-500" />
            <div>
              <p className="font-semibold text-blue-800">Name 3 things you can SEE</p>
              <p className="text-xs text-blue-600">Look around. Describe the colors, shapes, textures.</p>
            </div>
          </div>
          <div className="flex items-center gap-3 p-3 bg-green-50 rounded-lg">
            <Ear className="w-6 h-6 text-green-500" />
            <div>
              <p className="font-semibold text-green-800">Name 3 things you can HEAR</p>
              <p className="text-xs text-green-600">Focus on sounds — traffic, birds, voices, distant hums.</p>
            </div>
          </div>
          <div className="flex items-center gap-3 p-3 bg-amber-50 rounded-lg">
            <Hand className="w-6 h-6 text-amber-500" />
            <div>
              <p className="font-semibold text-amber-800">Name 3 things you can TOUCH</p>
              <p className="text-xs text-amber-600">Feel your clothing, a chair, the ground beneath your feet.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Paced Breathing */}
      <div className="bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl p-4 text-white">
        <h5 className="font-bold mb-3 flex items-center gap-2">
          <RefreshCw className="w-4 h-4" />
          Paced Breathing with Psalm 46:10
        </h5>
        <p className="text-white/80 text-sm mb-4">
          Combine deep breathing with God's Word to restore calm.
        </p>

        <div className="text-center py-4">
          {isActive ? (
            <div className="space-y-2">
              <div className="w-24 h-24 mx-auto rounded-full bg-white/20 flex items-center justify-center">
                <span className="text-2xl font-bold">
                  {breathingStep === 1 ? 'IN' : breathingStep === 2 ? 'HOLD' : breathingStep === 3 ? 'OUT' : ''}
                </span>
              </div>
              <p className="text-sm text-white/70">
                {breathingStep === 1 ? 'Inhale 4 sec' : breathingStep === 2 ? 'Hold 4 sec' : breathingStep === 3 ? 'Exhale 6 sec' : ''}
              </p>
            </div>
          ) : (
            <p className="italic text-white/90">"Be still, and know that I am God."</p>
          )}
        </div>

        <button
          onClick={startGrounding}
          disabled={isActive}
          className="w-full bg-white/20 hover:bg-white/30 text-white font-semibold py-2 rounded-lg transition-colors disabled:opacity-50"
        >
          {isActive ? 'Breathing...' : 'Start Breathing Exercise'}
        </button>
      </div>

      {/* Quick Grounding Tips */}
      <div className="bg-gray-50 rounded-xl p-4">
        <h5 className="font-bold text-gray-800 mb-3">Quick Grounding Tips</h5>
        <ul className="space-y-2 text-sm text-gray-600">
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span><strong>Cold water:</strong> Splash cold water on your face or hold an ice cube to reset your nervous system</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span><strong>Plant your feet:</strong> Feel the ground. You’re here, now, safe.</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span><strong>Slow your pace:</strong> Walk slower. Breathe slower. You don't need to rush.</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span><strong>Pray a simple prayer:</strong> "Jesus, be my peace right now."</span>
          </li>
        </ul>
      </div>
    </div>
  )
}

function MentorshipSection() {
  return (
    <div className="space-y-4 animate-fade-in">
      <div className="bg-green-50 rounded-xl p-4">
        <h4 className="font-bold text-green-800 mb-2 flex items-center gap-2">
          <Users className="w-5 h-5" />
          Building Your Support System
        </h4>
        <p className="text-sm text-green-700 leading-relaxed">
          You were not meant to do this alone. Transitional freedom works best when shared with trusted
          mentors, a supportive church, and a stable daily routine. Here's how to rebuild your foundation.
        </p>
      </div>

      {/* Daily Routine */}
      <div className="bg-white rounded-xl p-4 border border-gray-200">
        <h5 className="font-bold text-gray-800 mb-3 flex items-center gap-2">
          <Shield className="w-4 h-4 text-primary-500" />
          Create a Stable Daily Routine
        </h5>
        <p className="text-sm text-gray-600 mb-3">
          Predictability restores a sense of safety and control that incarceration disrupted.
        </p>
        <div className="space-y-2">
          <RoutineItem time="Morning" activity="Wake at the same time daily, pray, read Scripture" />
          <RoutineItem time="Midday" activity="One meaningful task or goal — even small wins count" />
          <RoutineItem time="Afternoon" activity="Physical activity: walk, stretch, or exercise" />
          <RoutineItem time="Evening" activity="Connect with someone supportive (call, text, or visit)" />
          <RoutineItem time="Night" activity="Reflect on the day, journal victories, rest well" />
        </div>
      </div>

      {/* Find a Mentor */}
      <div className="bg-white rounded-xl p-4 border border-gray-200">
        <h5 className="font-bold text-gray-800 mb-3 flex items-center gap-2">
          <BookOpen className="w-4 h-4 text-primary-500" />
          Find a Spiritual Mentor
        </h5>
        <p className="text-sm text-gray-600 mb-3">
          A mentor who walks with Christ can help guide you through this transition with grace, wisdom, and accountability.
        </p>
        <ul className="space-y-2 text-sm text-gray-600">
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span>Ask your pastor to connect you with a mature believer</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span>Look for someone who listens well and lives by Scripture</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-accent-teal mt-0.5 flex-shrink-0" />
            <span>Commit to weekly check-ins — in person or by phone</span>
          </li>
        </ul>
      </div>

      {/* Support Network Links */}
      <div className="bg-primary-50 rounded-xl p-4">
        <h5 className="font-bold text-primary-800 mb-3 flex items-center gap-2">
          <MapPin className="w-4 h-4" />
          Find Grace-Filled Support Networks
        </h5>
        <p className="text-sm text-primary-700 mb-3">
          Use the Support & Church Locator above to find ministries that welcome you without judgment:
        </p>
        <div className="space-y-2">
          <a
            href="https://www.celebraterecovery.com/crgroups"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-white rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div>
              <p className="font-semibold text-gray-800">Celebrate Recovery</p>
              <p className="text-xs text-gray-500">Christ-centered 12-step program nationwide</p>
            </div>
            <ExternalLink className="w-4 h-4 text-primary-500" />
          </a>
          <a
            href="https://www.thefaithconnection.org"
            target="_blank"
            rel="noopener noreferrer"
            className="flex items-center justify-between p-3 bg-white rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div>
              <p className="font-semibold text-gray-800">The Faith Connection</p>
              <p className="text-xs text-gray-500">Our ministry home — you belong here</p>
            </div>
            <ExternalLink className="w-4 h-4 text-primary-500" />
          </a>
        </div>
      </div>

      {/* Reentry Resources */}
      <div className="bg-amber-50 rounded-xl p-4">
        <h5 className="font-bold text-amber-800 mb-3">Additional Reentry Resources</h5>
        <ul className="space-y-2 text-sm text-amber-700">
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-amber-500 mt-0.5 flex-shrink-0" />
            <span><strong>Employment assistance:</strong> Check with local reentry programs for job training and placement</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-amber-500 mt-0.5 flex-shrink-0" />
            <span><strong>Housing support:</strong> Many churches offer transitional housing referrals</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-amber-500 mt-0.5 flex-shrink-0" />
            <span><strong>Document restoration:</strong> Ask case workers about ID, license, and benefits restoration</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="w-4 h-4 text-amber-500 mt-0.5 flex-shrink-0" />
            <span><strong>Counseling services:</strong> Christian counselors can help process PICS with grace and truth</span>
          </li>
        </ul>
      </div>

      {/* Encouragement */}
      <div className="bg-gradient-to-r from-accent-teal/10 to-primary-100/50 rounded-xl p-4 text-center">
        <p className="text-primary-800 font-bold italic">
          "For I know the plans I have for you," declares the LORD, "plans to prosper you and not to harm you, plans to give you hope and a future."
        </p>
        <p className="text-primary-600 text-sm mt-1">— Jeremiah 29:11</p>
      </div>
    </div>
  )
}

function RoutineItem({ time, activity }: { time: string; activity: string }) {
  return (
    <div className="flex items-center gap-3 p-2 bg-gray-50 rounded-lg">
      <span className="bg-primary-100 text-primary-700 text-xs font-bold px-2 py-1 rounded min-w-16 text-center">
        {time}
      </span>
      <span className="text-sm text-gray-700">{activity}</span>
    </div>
  )
}
