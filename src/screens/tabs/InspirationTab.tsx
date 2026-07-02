import { useState } from 'react'
import { inspirationQuotes } from '../../lib/data'
import { Quote, Heart, RefreshCw, Moon, Shield, Sparkles, Crown, FileText, Download } from 'lucide-react'
import type { InspirationQuote } from '../../lib/types'

type Category = 'OVERCOMING_CRAVINGS' | 'PEACE_ANXIETY' | 'STRENGTH_FAITH' | 'GRACE_FORGIVENESS' | 'IAM_DECLARATIONS'

const categoryInfo: Record<Category, { label: string; icon: React.ReactNode; color: string }> = {
  IAM_DECLARATIONS: { label: 'I AM Declarations', icon: <Crown className="w-5 h-5" />, color: 'bg-accent-gold' },
  OVERCOMING_CRAVINGS: { label: 'Fight From Victory', icon: <Shield className="w-5 h-5" />, color: 'bg-accent-teal' },
  PEACE_ANXIETY: { label: 'Peace & Anxiety', icon: <Moon className="w-5 h-5" />, color: 'bg-primary-400' },
  STRENGTH_FAITH: { label: 'Strength & Faith', icon: <Sparkles className="w-5 h-5" />, color: 'bg-secondary-500' },
  GRACE_FORGIVENESS: { label: 'Grace & Forgiveness', icon: <Heart className="w-5 h-5" />, color: 'bg-accent-coral' }
}

export default function InspirationTab() {
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null)

  const filteredQuotes = selectedCategory
    ? inspirationQuotes.filter(q => q.category === selectedCategory)
    : inspirationQuotes

  const categories = Object.keys(categoryInfo) as Category[]

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
          <QuoteCard key={quote.id} quote={quote} />
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
    </div>
  )
}

function QuoteCard({ quote }: { quote: InspirationQuote }) {
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

      <button className="mt-4 w-full flex items-center justify-center gap-2 text-primary-500 font-semibold text-sm hover:bg-primary-50 py-2 rounded-xl transition-colors">
        <RefreshCw className="w-4 h-4" />
        Renew My Mind
      </button>
    </div>
  )
}

