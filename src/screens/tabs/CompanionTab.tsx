import { useState, useEffect, useRef } from 'react'
import { useAppStore } from '../../store/useAppStore'
import { generateSupportResponse, checkDailyLimit, getTodayUsageCount, isCustomKeyActive, getApiKey } from '../../lib/geminiClient'
import { Send, Save, Trash2, History, Loader as Loader2, Key, ExternalLink, ChevronDown, ChevronUp } from 'lucide-react'
import ApiSettingsDialog from '../../components/ApiSettingsDialog'

export default function CompanionTab() {
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [showApiDialog, setShowApiDialog] = useState(false)
  const [showInstructions, setShowInstructions] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const {
    chatMessages,
    addChatMessage,
    clearChatMessages,
    saveCurrentChat,
    userPath,
    pendingCompanionMessage,
    setPendingCompanionMessage,
  } = useAppStore()

  const hasApiKey = Boolean(getApiKey())

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [chatMessages])

  // Auto-send a message injected by another tab (e.g. "Renew My Mind" from Inspiration)
  useEffect(() => {
    if (!pendingCompanionMessage) return
    const message = pendingCompanionMessage
    setPendingCompanionMessage(null)

    addChatMessage({ text: message, isUser: true, timestamp: Date.now() })
    setIsLoading(true)
    generateSupportResponse(message, chatMessages, userPath || 'SUBSTANCE_RECOVERY')
      .then(response => {
        if (response === 'NO_API_KEY_SETUP') {
          addChatMessage({ text: '__API_KEY_NEEDED__', isUser: false, timestamp: Date.now() })
        } else {
          addChatMessage({ text: response, isUser: false, timestamp: Date.now() })
        }
      })
      .catch(() => {
        addChatMessage({ text: 'I am here with you. Please try again in a moment.', isUser: false, timestamp: Date.now() })
      })
      .finally(() => setIsLoading(false))
  // runs once on mount to consume the pending message
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleSend = async () => {
    if (!input.trim() || isLoading) return

    const message = input.trim()
    setInput('')

    addChatMessage({
      text: message,
      isUser: true,
      timestamp: Date.now()
    })

    setIsLoading(true)
    try {
      const response = await generateSupportResponse(
        message,
        chatMessages,
        userPath || 'SUBSTANCE_RECOVERY'
      )

      if (response === 'NO_API_KEY_SETUP') {
        // Don't add a broken message — just show the setup card prominently
        // (the card is always visible when no key is set, so nothing to add)
        addChatMessage({
          text: '__API_KEY_NEEDED__',
          isUser: false,
          timestamp: Date.now()
        })
      } else {
        addChatMessage({
          text: response,
          isUser: false,
          timestamp: Date.now()
        })
      }
    } catch {
      addChatMessage({
        text: 'I am here with you. Please try again in a moment.',
        isUser: false,
        timestamp: Date.now()
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  const dailyLimitReached = checkDailyLimit()
  const dailyCount = getTodayUsageCount()
  const customKeyActive = isCustomKeyActive()

  return (
    <div className="flex flex-col h-full bg-background-light">
      {/* Header */}
      <div className="bg-white border-b border-gray-100 px-4 py-3">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="font-bold text-gray-900">OverComer's Companion</h2>
            <p className="text-xs text-gray-500">
              {customKeyActive
                ? 'Unlimited AI Support'
                : `${30 - dailyCount}/30 requests today`
              }
            </p>
          </div>
          <div className="flex items-center gap-2">
          <button
            onClick={() => setShowApiDialog(true)}
            className={`p-2 rounded-lg transition-colors ${hasApiKey ? 'hover:bg-gray-100 text-gray-500' : 'bg-primary-100 text-primary-600 hover:bg-primary-200'}`}
            title="API Settings"
          >
            <Key className="w-5 h-5" />
          </button>
          <button
            onClick={() => {
              const title = prompt('Save chat as:', `Session ${new Date().toLocaleDateString()}`)
              if (title) saveCurrentChat(title)
            }}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Save Chat"
          >
            <Save className="w-5 h-5" />
          </button>
          <button
            onClick={clearChatMessages}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Clear Chat"
          >
            <Trash2 className="w-5 h-5" />
          </button>
          <button
            onClick={() => {}}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Saved Chats"
          >
            <History className="w-5 h-5" />
          </button>
          </div>
        </div>
      </div>

      {/* Legal micro-copy */}
      <div className="bg-amber-50 border-b border-amber-100 px-4 py-2">
        <p className="text-xs text-amber-700 leading-relaxed">
          Your AI OverComer's Companion for biblical encouragement. For professional medical or clinical crisis support, please see the <strong>Re-entry</strong> resource tab.
        </p>
      </div>

      {/* Daily limit warning */}
      {dailyLimitReached && !customKeyActive && (
        <div className="bg-amber-50 border-b border-amber-100 px-4 py-3">
          <p className="text-sm text-amber-700">
            You've reached today's free limit.{' '}
            <button onClick={() => setShowApiDialog(true)} className="font-semibold underline">
              Add your free API key
            </button>{' '}
            for unlimited access.
          </p>
        </div>
      )}

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">

        {/* API Key Setup Card — always shown at top when no key, or after a NO_API_KEY response */}
        {!hasApiKey && (
          <ApiKeySetupCard
            showInstructions={showInstructions}
            onToggleInstructions={() => setShowInstructions(v => !v)}
            onOpenDialog={() => setShowApiDialog(true)}
          />
        )}

        {chatMessages.map((msg, index) => (
          msg.text === '__API_KEY_NEEDED__'
            ? <ApiKeyInlinePrompt key={index} onOpenDialog={() => setShowApiDialog(true)} />
            : <MessageBubble key={index} message={msg} />
        ))}

        {isLoading && (
          <div className="flex justify-start">
            <div className="bg-primary-100 rounded-2xl rounded-bl-md px-4 py-3">
              <Loader2 className="w-5 h-5 animate-spin text-primary-500" />
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="bg-white border-t border-gray-100 p-4 safe-bottom">
        {!hasApiKey && (
          <p className="text-xs text-center text-gray-400 mb-2">
            A free API key is required to chat.{' '}
            <button onClick={() => setShowApiDialog(true)} className="text-primary-500 font-semibold">
              Set up in 2 minutes
            </button>
          </p>
        )}
        <div className="flex items-center gap-2">
          <input
            ref={inputRef}
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={hasApiKey ? 'Talk to your Companion...' : 'Add a free API key to begin...'}
            className="flex-1 input-field"
            disabled={isLoading || (dailyLimitReached && !customKeyActive)}
          />
          <button
            onClick={hasApiKey ? handleSend : () => setShowApiDialog(true)}
            disabled={hasApiKey ? (!input.trim() || isLoading || (dailyLimitReached && !customKeyActive)) : false}
            className="bg-primary-400 hover:bg-primary-500 disabled:bg-gray-200 text-white p-3 rounded-xl transition-colors"
          >
            {hasApiKey ? <Send className="w-5 h-5" /> : <Key className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {showApiDialog && <ApiSettingsDialog onClose={() => setShowApiDialog(false)} />}
    </div>
  )
}

function ApiKeySetupCard({
  showInstructions,
  onToggleInstructions,
  onOpenDialog
}: {
  showInstructions: boolean
  onToggleInstructions: () => void
  onOpenDialog: () => void
}) {
  return (
    <div className="bg-gradient-to-br from-primary-50 to-secondary-50 rounded-2xl border border-primary-100 p-4 shadow-sm">
      <div className="flex items-start gap-3 mb-3">
        <div className="w-10 h-10 bg-primary-500 rounded-xl flex items-center justify-center flex-shrink-0">
          <Key className="w-5 h-5 text-white" />
        </div>
        <div>
          <h3 className="font-bold text-gray-900 text-sm">Unlock Your Free AI Companion</h3>
          <p className="text-xs text-gray-600 mt-0.5">
            A free Google Gemini API key is required. No credit card. Takes 2 minutes.
          </p>
        </div>
      </div>

      <div className="flex gap-2 mb-3">
        <a
          href="https://aistudio.google.com/apikey"
          target="_blank"
          rel="noopener noreferrer"
          className="flex-1 flex items-center justify-center gap-2 bg-white border border-primary-200 text-primary-600 font-semibold text-xs py-2.5 px-3 rounded-xl hover:bg-primary-50 transition-colors"
        >
          <ExternalLink className="w-3.5 h-3.5" />
          Get Free Key
        </a>
        <button
          onClick={onOpenDialog}
          className="flex-1 bg-primary-500 hover:bg-primary-600 text-white font-semibold text-xs py-2.5 px-3 rounded-xl transition-colors"
        >
          Enter Key
        </button>
      </div>

      <button
        onClick={onToggleInstructions}
        className="w-full flex items-center justify-between text-xs text-gray-500 hover:text-gray-700 transition-colors py-1"
      >
        <span className="font-medium">Step-by-step instructions</span>
        {showInstructions ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
      </button>

      {showInstructions && (
        <div className="mt-3 space-y-2.5 border-t border-primary-100 pt-3">
          {[
            { step: '1', text: 'Tap "Get Free Key" above — this opens Google AI Studio in your browser.' },
            { step: '2', text: 'Sign in with any Google account (Gmail, etc.). It\'s free.' },
            { step: '3', text: 'Click "Create API Key" — Google generates your personal key instantly.' },
            { step: '4', text: 'Copy the key (starts with "AIzaSy...").' },
            { step: '5', text: 'Tap "Enter Key" above, paste your key, and tap Save. You\'re done!' }
          ].map(({ step, text }) => (
            <div key={step} className="flex items-start gap-2.5">
              <span className="w-5 h-5 bg-primary-500 text-white rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
                {step}
              </span>
              <p className="text-xs text-gray-700 leading-relaxed">{text}</p>
            </div>
          ))}
          <div className="bg-green-50 border border-green-200 rounded-xl p-3 mt-2">
            <p className="text-xs text-green-800 font-medium">
              Your key is stored only on this device — it never leaves your phone. Google's free tier is more than enough for daily use.
            </p>
          </div>
        </div>
      )}
    </div>
  )
}

function ApiKeyInlinePrompt({ onOpenDialog }: { onOpenDialog: () => void }) {
  return (
    <div className="flex justify-start">
      <div className="max-w-[85%] bg-white shadow-md border border-primary-100 rounded-2xl rounded-bl-md px-4 py-3">
        <p className="text-sm text-gray-800 leading-relaxed">
          To respond, I need a free Google Gemini API key configured on your device. It takes about 2 minutes and is completely free.
        </p>
        <button
          onClick={onOpenDialog}
          className="mt-3 w-full flex items-center justify-center gap-2 bg-primary-500 hover:bg-primary-600 text-white font-semibold text-sm py-2.5 px-4 rounded-xl transition-colors"
        >
          <Key className="w-4 h-4" />
          Set Up Free API Key
        </button>
      </div>
    </div>
  )
}

function MessageBubble({ message }: { message: { text: string; isUser: boolean; timestamp: number } }) {
  const formattedTime = new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

  return (
    <div className={`flex ${message.isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`max-w-[85%] ${
        message.isUser
          ? 'bg-primary-400 text-white rounded-2xl rounded-br-md'
          : 'bg-white shadow-md border border-gray-100 rounded-2xl rounded-bl-md'
      }`}>
        <div className="px-4 py-3">
          <p className={`text-sm leading-relaxed whitespace-pre-wrap ${
            message.isUser ? 'text-white' : 'text-gray-800'
          }`}>
            {message.text}
          </p>
          <p className={`text-xs mt-1 ${
            message.isUser ? 'text-white/70' : 'text-gray-400'
          }`}>
            {formattedTime}
          </p>
        </div>
      </div>
    </div>
  )
}
