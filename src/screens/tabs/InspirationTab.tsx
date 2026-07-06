import { useState } from 'react'
import { inspirationQuotes } from '../../lib/data'
import { Quote, Heart, RefreshCw, Moon, Shield, Sparkles, Crown, FileText, Download, BookOpen, ChevronDown, ChevronUp, Users } from 'lucide-react'
import { useAppStore } from '../../store/useAppStore'
import type { InspirationQuote } from '../../lib/types'

type Category = 'OVERCOMING_CRAVINGS' | 'PEACE_ANXIETY' | 'STRENGTH_FAITH' | 'GRACE_FORGIVENESS' | 'IAM_DECLARATIONS'

const categoryInfo: Record<Category, { label: string; icon: React.ReactNode; color: string }> = {
  IAM_DECLARATIONS: { label: 'I AM Declarations', icon: <Crown className="w-5 h-5" />, color: 'bg-accent-gold' },
  OVERCOMING_CRAVINGS: { label: 'Fight From Victory', icon: <Shield className="w-5 h-5" />, color: 'bg-accent-teal' },
  PEACE_ANXIETY: { label: 'Peace & Anxiety', icon: <Moon className="w-5 h-5" />, color: 'bg-primary-400' },
  STRENGTH_FAITH: { label: 'Strength & Faith', icon: <Sparkles className="w-5 h-5" />, color: 'bg-secondary-500' },
  GRACE_FORGIVENESS: { label: 'Grace & Forgiveness', icon: <Heart className="w-5 h-5" />, color: 'bg-accent-coral' }
}

export default function InspirationTab({ onNavigateToCompanion }: { onNavigateToCompanion: () => void }) {
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null)
  const { setPendingCompanionMessage } = useAppStore()

  const filteredQuotes = selectedCategory
    ? inspirationQuotes.filter(q => q.category === selectedCategory)
    : inspirationQuotes

  const categories = Object.keys(categoryInfo) as Category[]

  const handleRenewMind = (quote: InspirationQuote) => {
    setPendingCompanionMessage(
      `I want to renew my mind on this scripture: "${quote.text}" — ${quote.reference}. Please help me by: (1) giving me a personal declaration I can speak aloud based on this truth, (2) a short reflection on how this applies to my life right now, and (3) a prayer I can pray to seal this truth in my heart.`
    )
    onNavigateToCompanion()
  }

  return (
    <div className="p-4 pb-8">
      {/* Header */}
      <div className="text-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Inspiration</h1>
        <p className="text-sm text-gray-600 mt-1">Declare who God says you are. Renew your mind in His truth.</p>
      </div>

      {/* Category Filters */}
      <div className="flex gap-2 overflow-x-auto pb-4 scrollbar-hide">
        <button
          onClick={() => setSelectedCategory(null)}
          className={`flex-shrink-0 px-4 py-2 rounded-full font-medium text-sm transition-colors ${
            !selectedCategory
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          All
        </button>
        {categories.map(cat => (
          <button
            key={cat}
            onClick={() => setSelectedCategory(cat)}
            className={`flex-shrink-0 flex items-center gap-2 px-4 py-2 rounded-full font-medium text-sm transition-colors ${
              selectedCategory === cat
                ? 'bg-gray-900 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {categoryInfo[cat].icon}
            {categoryInfo[cat].label}
          </button>
        ))}
      </div>

      {/* I AM Banner (shown when that category is selected or at top of All) */}
      {(selectedCategory === 'IAM_DECLARATIONS' || !selectedCategory) && (
        <div className="bg-gradient-to-br from-accent-gold/20 to-primary-50 rounded-2xl p-5 mb-4 border border-accent-gold/30">
          <p className="text-center font-black text-lg text-gray-900 leading-snug">
            I AM Loved By God.
          </p>
          <p className="text-center font-bold text-gray-700 text-sm mt-1">
            I AM NOT Who Others Say I Am.
          </p>
          <p className="text-center font-bold text-gray-700 text-sm">
            I AM NOT Who I Used To Be.
          </p>
          <p className="text-center font-black text-primary-600 text-sm mt-1">
            I AM Who God Says I Am.
          </p>
          <p className="text-center text-xs text-gray-500 mt-2">OverComer Identity Declaration</p>
        </div>
      )}

      {/* Quotes Grid */}
      <div className="space-y-4">
        {filteredQuotes.map(quote => (
          <QuoteCard key={quote.id} quote={quote} onRenewMind={handleRenewMind} />
        ))}
      </div>

      {/* Devotional Resources */}
      {(!selectedCategory || selectedCategory === 'STRENGTH_FAITH' || selectedCategory === 'IAM_DECLARATIONS') && (
        <div className="mt-6 space-y-3">
          <h3 className="font-bold text-gray-900 flex items-center gap-2">
            <FileText className="w-5 h-5 text-primary-500" />
            Devotional Reading
          </h3>
          {[
            {
              title: 'Seek God With All Your Heart',
              description: 'A deep devotional on pursuing God wholeheartedly in your recovery walk.',
              pdf: '/philosophy/Seek_God_With_All_Your_Heart.pdf'
            },
            {
              title: 'Seek First the Kingdom of God',
              description: 'Study guide on Matthew 6:33 — making God\'s Kingdom your daily priority.',
              pdf: '/philosophy/Seek_1st_The_Kingdom_Of_God.pdf'
            },
            {
              title: 'Tell Your Story: 10 Tips for Sharing Your Testimony',
              description: 'How to share what God has done in your life to help and inspire others.',
              pdf: '/philosophy/Tell_Your_Story__10_Tips_for_Sharing_Your_Testimony_With_Others.pdf'
            }
          ].map((doc, i) => (
            <div key={i} className="bg-white rounded-2xl p-4 shadow-md border border-gray-100 flex items-start gap-3">
              <div className="w-10 h-10 bg-primary-100 rounded-xl flex items-center justify-center flex-shrink-0">
                <FileText className="w-5 h-5 text-primary-500" />
              </div>
              <div className="flex-1">
                <h4 className="font-bold text-gray-900 text-sm leading-snug">{doc.title}</h4>
                <p className="text-xs text-gray-500 mt-1 leading-relaxed">{doc.description}</p>
                <a
                  href={doc.pdf}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="mt-2 inline-flex items-center gap-1.5 text-xs text-primary-600 font-semibold hover:text-primary-700 transition-colors"
                >
                  <Download className="w-3.5 h-3.5" />
                  Open PDF
                </a>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Trusted Voices Library */}
      {(!selectedCategory) && <TrustedVoicesLibrary />}
    </div>
  )
}

function QuoteCard({ quote, onRenewMind }: { quote: InspirationQuote; onRenewMind: (quote: InspirationQuote) => void }) {
  const catInfo = categoryInfo[quote.category]

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <div className="flex items-start gap-3">
        <div className={`w-10 h-10 ${catInfo.color} rounded-xl flex items-center justify-center text-white flex-shrink-0`}>
          <Quote className="w-5 h-5" />
        </div>
        <div className="flex-1">
          <p className="text-gray-800 font-medium leading-relaxed italic">
            "{quote.text}"
          </p>
          <p className="text-sm text-gray-500 mt-2 font-semibold">
            — {quote.reference}
          </p>
        </div>
      </div>

      <button
        onClick={() => onRenewMind(quote)}
        className="mt-4 w-full flex items-center justify-center gap-2 text-primary-500 font-semibold text-sm hover:bg-primary-50 py-2 rounded-xl transition-colors"
      >
        <RefreshCw className="w-4 h-4" />
        Renew My Mind
      </button>
    </div>
  )
}

interface TrustedBook {
  title: string
  author: string
  description: string
  tag: string
  tagColor: string
}

const trustedVoicesData: Array<{ topic: string; color: string; books: TrustedBook[] }> = [
  {
    topic: 'Substance Recovery',
    color: 'bg-accent-teal',
    books: [
      {
        title: 'The Cross and the Switchblade',
        author: 'David Wilkerson',
        description: 'The story that launched Teen Challenge — the gold standard in faith-based recovery ministry.',
        tag: 'Recovery',
        tagColor: 'bg-accent-teal/10 text-accent-teal'
      },
      {
        title: 'Run Baby Run',
        author: 'Nicky Cruz',
        description: 'From gang life to Christ — a raw, powerful testimony that addiction and violence have no hold on a life surrendered to Jesus.',
        tag: 'Testimony',
        tagColor: 'bg-accent-teal/10 text-accent-teal'
      },
      {
        title: 'Crossroads: A Step-by-Step Guide Away from Addiction',
        author: 'Edward T. Welch',
        description: 'A strictly biblical workbook for recovery — addresses behavioral choices and personal accountability head-on.',
        tag: 'Workbook',
        tagColor: 'bg-accent-teal/10 text-accent-teal'
      },
    ],
  },
  {
    topic: 'Mental Health & Faith',
    color: 'bg-primary-400',
    books: [
      {
        title: 'Grace for the Afflicted',
        author: 'Dr. Matthew Stanford',
        description: 'A Christian neuroscientist bridges faith and clinical care — the definitive guide for ministry leaders navigating mental illness.',
        tag: 'Clinical + Faith',
        tagColor: 'bg-primary-100 text-primary-600'
      },
      {
        title: 'Blame It on the Brain?',
        author: 'Edward T. Welch',
        description: 'Helps leaders discern when a struggle is spiritual, behavioral, or requires professional clinical care.',
        tag: 'Discernment',
        tagColor: 'bg-primary-100 text-primary-600'
      },
      {
        title: 'Boundaries: When to Say Yes, How to Say No',
        author: 'Dr. Henry Cloud & Dr. John Townsend',
        description: 'A theological and psychological framework for deep compassion paired with uncompromising accountability.',
        tag: 'Relationships',
        tagColor: 'bg-primary-100 text-primary-600'
      },
    ],
  },
  {
    topic: 'Inner Healing & Transformation',
    color: 'bg-secondary-500',
    books: [
      {
        title: 'Instruments in the Redeemer\'s Hands',
        author: 'Paul David Tripp',
        description: 'How ordinary believers can engage in personal, transformative ministry — speaking truth into each other\'s lives.',
        tag: 'Ministry',
        tagColor: 'bg-secondary-100 text-secondary-600'
      },
      {
        title: 'The Wounded Heart',
        author: 'Dr. Dan B. Allender',
        description: 'A landmark text in Christian trauma care — addresses the deep damage of abuse with immense empathy and theological depth.',
        tag: 'Trauma',
        tagColor: 'bg-secondary-100 text-secondary-600'
      },
      {
        title: 'Connecting',
        author: 'Dr. Larry Crabb',
        description: 'True community as the vehicle for healing — where we are fully known and fully loved, mirroring God\'s heart.',
        tag: 'Community',
        tagColor: 'bg-secondary-100 text-secondary-600'
      },
    ],
  },
  {
    topic: 'Marriage & Relationships',
    color: 'bg-accent-coral',
    books: [
      {
        title: 'Sacred Marriage',
        author: 'Gary Thomas',
        description: 'What if God designed marriage to make us holy more than to make us happy? A cornerstone shift in perspective.',
        tag: 'Marriage',
        tagColor: 'bg-red-50 text-red-500'
      },
      {
        title: 'Love & Respect',
        author: 'Dr. Emerson Eggerichs',
        description: 'Grounded in Ephesians 5:33 — breaks down the communication cycles that destroy marriages with practical, actionable steps.',
        tag: 'Communication',
        tagColor: 'bg-red-50 text-red-500'
      },
      {
        title: 'Vertical Marriage',
        author: 'Dave & Ann Wilson',
        description: 'Your horizontal relationship with your spouse can only be transformed by first fixing your vertical relationship with Christ.',
        tag: 'Marriage',
        tagColor: 'bg-red-50 text-red-500'
      },
    ],
  },
  {
    topic: 'Pastoral & Christian Counseling',
    color: 'bg-accent-gold',
    books: [
      {
        title: 'Christian Counseling: A Comprehensive Guide',
        author: 'Dr. Gary R. Collins',
        description: 'The standard textbook for Christian counselors — covers a vast range of counseling scenarios with proven frameworks.',
        tag: 'Counseling',
        tagColor: 'bg-yellow-50 text-yellow-600'
      },
      {
        title: 'Seeing with New Eyes',
        author: 'Dr. David Powlison',
        description: 'How Scripture diagnoses human motives and brings practical, grace-centered change to daily struggles.',
        tag: 'Scripture',
        tagColor: 'bg-yellow-50 text-yellow-600'
      },
      {
        title: 'Dare to Discipline / The Strong-Willed Child',
        author: 'Dr. James Dobson',
        description: 'The foundational framework for loving, firm parenting rooted in biblical principles — essential for families in recovery.',
        tag: 'Family',
        tagColor: 'bg-yellow-50 text-yellow-600'
      },
    ],
  },
]

function TrustedVoicesLibrary() {
  const [expandedTopic, setExpandedTopic] = useState<string | null>(null)

  return (
    <div className="mt-8">
      <div className="flex items-center gap-2 mb-4">
        <Users className="w-5 h-5 text-primary-500" />
        <h3 className="font-bold text-gray-900">Trusted Voices Library</h3>
      </div>
      <p className="text-sm text-gray-500 mb-4 leading-relaxed">
        Biblically vetted authors and resources — carefully selected to align with the OverComer worldview.
      </p>

      <div className="space-y-3">
        {trustedVoicesData.map(section => (
          <div key={section.topic} className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <button
              onClick={() => setExpandedTopic(expandedTopic === section.topic ? null : section.topic)}
              className="w-full flex items-center justify-between p-4 text-left"
            >
              <div className="flex items-center gap-3">
                <div className={`w-8 h-8 ${section.color} rounded-lg flex items-center justify-center`}>
                  <BookOpen className="w-4 h-4 text-white" />
                </div>
                <span className="font-bold text-gray-900 text-sm">{section.topic}</span>
                <span className="text-xs text-gray-400">{section.books.length} resources</span>
              </div>
              {expandedTopic === section.topic
                ? <ChevronUp className="w-4 h-4 text-gray-400 flex-shrink-0" />
                : <ChevronDown className="w-4 h-4 text-gray-400 flex-shrink-0" />
              }
            </button>

            {expandedTopic === section.topic && (
              <div className="border-t border-gray-100 divide-y divide-gray-50">
                {section.books.map((book, i) => (
                  <div key={i} className="p-4">
                    <div className="flex items-start justify-between gap-2 mb-1">
                      <h4 className="font-bold text-gray-900 text-sm leading-snug">{book.title}</h4>
                      <span className={`flex-shrink-0 text-xs font-semibold px-2 py-0.5 rounded-full ${book.tagColor}`}>
                        {book.tag}
                      </span>
                    </div>
                    <p className="text-xs font-semibold text-primary-500 mb-1">{book.author}</p>
                    <p className="text-xs text-gray-500 leading-relaxed">{book.description}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}

