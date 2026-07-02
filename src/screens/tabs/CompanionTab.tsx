import { useState, useEffect, useRef } from 'react'
import { useAppStore } from '../../store/useAppStore'
import { generateSupportResponse, checkDailyLimit, getTodayUsageCount, isCustomKeyActive } from '../../lib/geminiClient'
import { Send, Save, Trash2, History, Loader2 } from 'lucide-react'

export default function CompanionTab() {
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const {
    chatMessages,
    addChatMessage,
    clearChatMessages,
    saveCurrentChat,
    userPath
  } = useAppStore()

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [chatMessages])

  const handleSend = async () => {
    if (!input.trim() || isLoading) return

    const message = input.trim()
    setInput('')

    // Add user message
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

      addChatMessage({
        text: response,
        isUser: false,
        timestamp: Date.now()
      })
    } catch (error) {
      addChatMessage({
        text: 'I am here with you. Please hold on while I reconnect.',
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
      {/* Header with usage info */}
      <div className="bg-white border-b border-gray-100 px-4 py-3 flex items-center justify-between">
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
            onClick={() => {/* Show saved chats modal */}}
            className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
            title="Saved Chats"
          >
            <History className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Daily limit warning */}
      {dailyLimitReached && !customKeyActive && (
        <div className="bg-amber-50 border-b border-amber-100 px-4 py-3">
          <p className="text-sm text-amber-700">
            You've reached today's free limit. Add your own Gemini API key for unlimited access.
          </p>
        </div>
      )}

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {chatMessages.map((msg, index) => (
          <MessageBubble key={index} message={msg} />
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
        <div className="flex items-center gap-2">
          <input
            ref={inputRef}
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Talk to your Companion..."
            className="flex-1 input-field"
            disabled={isLoading || (dailyLimitReached && !customKeyActive)}
          />
          <button
            onClick={handleSend}
            disabled={!input.trim() || isLoading || (dailyLimitReached && !customKeyActive)}
            className="bg-primary-400 hover:bg-primary-500 disabled:bg-gray-200 text-white p-3 rounded-xl transition-colors"
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
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
