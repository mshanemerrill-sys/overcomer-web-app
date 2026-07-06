import { type FocusPath } from '../lib/types'
import { Cloud, Shield, Heart, Star, ChevronRight, ExternalLink } from 'lucide-react'
import { useAppStore } from '../store/useAppStore'
import { useEffect } from 'react'

interface FocusSelectionScreenProps {
  onSelect: (path: FocusPath) => void
}

export default function FocusSelectionScreen({ onSelect }: FocusSelectionScreenProps) {
  const user = useAppStore(state => state.user)
  const greeting = user?.displayName ? `Welcome back, ${user.displayName}` : 'Welcome to OverComer — where we live from Christ\'s position of Victory'

  useEffect(() => {
    document.title = 'OverComer - Welcome'
  }, [])

  return (
    <div className="min-h-screen bg-gradient-to-b from-primary-800 via-primary-600 to-accent-coral p-4 flex flex-col items-center justify-center">
      <div className="w-full max-w-lg animate-fade-in">
        <BannerHeader />

        <div className="mt-6 mb-8 text-center">
          <h1 className="text-2xl font-extrabold text-white text-shadow mb-2">
            {greeting}
          </h1>
          <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-4 mt-4">
            <p className="text-white/90 font-semibold leading-relaxed">
              This app is built to lift up those fighting addiction or mental health struggles, assist veterans processing service, or support individuals overcoming the weight of past incarceration. It is equally a refuge for anyone who doesn't face these specific battles but is simply having a rough day and needs a lift. Out of every struggle comes a story.
            </p>
            <p className="text-white/80 text-sm mt-3 leading-relaxed">
              Step into your focus path, claim your peace, or simply log in to share your testimony and Victory Day.
            </p>
          </div>
          <p className="text-white/70 text-sm mt-4">
            Select a focus path below to customize your experience:
          </p>
        </div>

        <div className="space-y-4">
          <FocusOptionCard
            title="All Around Tough Day"
            description="God is your present help in trouble. Direct encouragement, calming breath exercises, and a compassionate space to bring your burdens to Christ."
            icon={<Cloud className="w-6 h-6" />}
            color="bg-red-500"
            onClick={() => onSelect('TOUGH_DAY')}
          />

          <FocusOptionCard
            title="Substance Recovery Freedom"
            description="Complete deliverance is possible through Christ. Sobriety tracker, I AM identity declarations, thought reframing, and the OverComer 7-Step obedience program."
            icon={<Shield className="w-6 h-6" />}
            color="bg-primary-400"
            onClick={() => onSelect('SUBSTANCE_RECOVERY')}
          />

          <FocusOptionCard
            title="Mental Health Wellness"
            description="God has not given you a spirit of fear, but of power, love, and a sound mind. Peace scriptures, anxiety logs, grounding tools, and breath exercises."
            icon={<Heart className="w-6 h-6" />}
            color="bg-accent-teal"
            onClick={() => onSelect('MENTAL_HEALTH')}
          />

          <FocusOptionCard
            title="Today is a Testimony/Victory Day"
            description="Celebrate what God has done! Enjoy victorious scriptures, battle-winning quotes, and share your triumphs with your OverComer companion."
            icon={<Star className="w-6 h-6" />}
            color="bg-accent-amber"
            onClick={() => onSelect('TESTIMONY_VICTORY')}
          />
        </div>

        {/* The Faith Connection Link */}
        <div className="mt-8 text-center">
          <a
            href="https://www.thefaithconnection.org"
            target="_blank"
            rel="noopener noreferrer"
            className="inline-flex items-center gap-2 text-white/80 hover:text-white text-sm font-medium transition-colors"
          >
            A Ministry of The Faith Connection
            <ExternalLink className="w-4 h-4" />
          </a>
        </div>
      </div>
    </div>
  )
}

function FocusOptionCard({
  title,
  description,
  icon,
  color,
  onClick
}: {
  title: string
  description: string
  icon: React.ReactNode
  color: string
  onClick: () => void
}) {
  return (
    <button
      onClick={onClick}
      className="w-full bg-white rounded-2xl p-4 shadow-lg hover:shadow-xl transition-all duration-200 flex items-center gap-4 group hover:scale-[1.02] active:scale-[0.98]"
    >
      <div className={`w-12 h-12 ${color} rounded-xl flex items-center justify-center text-white`}>
        {icon}
      </div>
      <div className="flex-1 text-left">
        <h3 className="font-bold text-gray-900">{title}</h3>
        <p className="text-sm text-gray-600 mt-1">{description}</p>
      </div>
      <ChevronRight className="w-5 h-5 text-gray-400 group-hover:text-gray-600 transition-colors" />
    </button>
  )
}

function BannerHeader() {
  return (
    <div className="relative bg-gradient-to-b from-primary-800 via-primary-600 to-primary-400 rounded-3xl overflow-hidden shadow-2xl h-48">
      {/* Sunset sky gradient background */}
      <div className="absolute inset-0 bg-gradient-to-b from-[#2E1C47] via-[#6B2D5C] via-60% to-[#D84A5A] to-[#FF9E79]" />

      {/* Sun */}
      <div className="absolute bottom-12 left-1/2 -translate-x-1/2 w-24 h-24 rounded-full bg-yellow-100/80 shadow-lg shadow-yellow-200/50" />
      <div className="absolute bottom-10 left-1/2 -translate-x-1/2 w-32 h-32 rounded-full bg-yellow-400/20 blur-sm" />

      {/* Ground silhouette */}
      <div className="absolute bottom-0 left-0 right-0 h-14 bg-[#160E22]" style={{ borderRadius: '50% 50% 0 0' }} />

      {/* Person silhouette - standing in victory pose */}
      <svg className="absolute bottom-14 left-1/2 -translate-x-1/2" width="80" height="120" viewBox="0 0 80 120">
        {/* Head */}
        <circle cx="40" cy="15" r="10" fill="#160E22" />
        <circle cx="40" cy="15" r="12" fill="none" stroke="#160E22" strokeWidth="3" />

        {/* Body */}
        <path d="M28 30 L52 30 L48 65 L32 65 Z" fill="#160E22" />

        {/* Arms spread wide */}
        <path d="M28 35 L5 25 L3 30 L25 40 Z" fill="#160E22" />
        <path d="M52 35 L75 25 L77 30 L55 40 Z" fill="#160E22" />

        {/* Legs */}
        <path d="M32 65 L38 65 L36 100 L30 100 Z" fill="#160E22" />
        <path d="M42 65 L48 65 L50 100 L44 100 Z" fill="#160E22" />
      </svg>

      {/* Text overlay */}
      <div className="absolute top-6 left-0 right-0 text-center">
        <h2 className="text-3xl font-black text-white text-shadow-lg">I AM a OverComer</h2>
        <p className="text-sm font-semibold italic text-white/90 mt-1 text-shadow-sm">Revelation 12:11</p>
      </div>
    </div>
  )
}
