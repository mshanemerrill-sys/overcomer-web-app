import { useState } from 'react'
import { inspirationQuotes } from '../../lib/data'
import { Quote, Heart, RefreshCw, Moon, Shield, Sparkles, Crown } from 'lucide-react'
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

