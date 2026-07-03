import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { User, FocusPath, VictoryLog, FreedomGoal, SavedChat, ChatMessage, VerseOfTheDay, SupportContact } from '../lib/types'
import { getFallbackVerse } from '../lib/geminiClient'

interface AppState {
  // Auth state
  user: User | null
  setUser: (user: User | null) => void

  // Focus path
  userPath: FocusPath | null
  setUserPath: (path: FocusPath | null) => void

  // Initialization
  isLoading: boolean
  initializeFromStorage: () => void

  // Victory logs
  victoryLogs: VictoryLog[]
  addVictoryLog: (log: Omit<VictoryLog, 'id' | 'timestamp' | 'userId'>) => void
  deleteVictoryLog: (id: string) => void

  // Freedom goal
  freedomGoal: FreedomGoal | null
  setFreedomGoal: (goal: Omit<FreedomGoal, 'id' | 'userId'>) => void

  // Saved chats
  savedChats: SavedChat[]
  chatMessages: ChatMessage[]
  addChatMessage: (message: ChatMessage) => void
  clearChatMessages: () => void
  saveCurrentChat: (title: string) => void
  deleteSavedChat: (id: string) => void
  loadSavedChat: (chat: SavedChat) => void
  currentAutoSavedChatId: string | null

  // Verse of the day
  verseOfTheDay: VerseOfTheDay | null
  setVerseOfTheDay: (verse: VerseOfTheDay | null) => void

  // Custom API key
  customApiKey: string | null
  setCustomApiKey: (key: string | null) => void

  // Support contacts
  supportContacts: SupportContact[]
  addSupportContact: (contact: Omit<SupportContact, 'id'>) => void
  updateSupportContact: (id: string, contact: Partial<SupportContact>) => void
  deleteSupportContact: (id: string) => void

  // Academy lesson progress
  completedLessons: number[]
  toggleLessonComplete: (lessonId: number) => void
  setLessonAnswer: (lessonId: number, questionIndex: number, answer: string) => void
  lessonAnswers: Record<number, Record<number, string>>
}

export const useAppStore = create<AppState>()(
  persist(
    (set, get) => ({
      // Auth state
      user: null,
      setUser: (user) => set({ user }),

      // Focus path
      userPath: null,
      setUserPath: (path) => {
        set({ userPath: path })
        // Reset chat messages when path changes
        if (path) {
          set({
            chatMessages: [getPathGreeting(path)],
            currentAutoSavedChatId: null
          })
        }
      },

      // Initialization
      isLoading: true,
      initializeFromStorage: () => {
        const state = get()
        if (!state.verseOfTheDay) {
          set({ verseOfTheDay: getFallbackVerse() })
        }
        set({ isLoading: false })
      },

      // Victory logs
      victoryLogs: [],
      addVictoryLog: (log) => {
        const newLog: VictoryLog = {
          ...log,
          id: `log_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
          timestamp: Date.now(),
          userId: get().user?.id || 'local_user',
        }
        set({ victoryLogs: [newLog, ...get().victoryLogs] })
      },
      deleteVictoryLog: (id) => {
        set({ victoryLogs: get().victoryLogs.filter(l => l.id !== id) })
      },

      // Freedom goal
      freedomGoal: null,
      setFreedomGoal: (goal) => {
        const newGoal: FreedomGoal = {
          ...goal,
          id: `goal_${get().user?.id || 'local'}`,
          userId: get().user?.id || 'local_user',
        }
        set({ freedomGoal: newGoal })
      },

      // Saved chats
      savedChats: [],
      chatMessages: [getPathGreeting('SUBSTANCE_RECOVERY' as FocusPath)],
      addChatMessage: (message) => {
        set({ chatMessages: [...get().chatMessages, message] })
      },
      clearChatMessages: () => {
        const path = get().userPath || 'SUBSTANCE_RECOVERY' as FocusPath
        set({
          chatMessages: [getPathGreeting(path)],
          currentAutoSavedChatId: null
        })
      },
      saveCurrentChat: (title) => {
        const messages = get().chatMessages
        if (messages.length <= 1) return

        const newChat: SavedChat = {
          id: `chat_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
          timestamp: Date.now(),
          title: title || `Support Session - ${new Date().toLocaleDateString()}`,
          messages,
          userPath: get().userPath || 'SUBSTANCE_RECOVERY' as FocusPath,
          userId: get().user?.id || 'local_user',
          isAutoSaved: false,
        }
        set({ savedChats: [newChat, ...get().savedChats] })
      },
      deleteSavedChat: (id) => {
        set({ savedChats: get().savedChats.filter(c => c.id !== id) })
      },
      loadSavedChat: (chat) => {
        set({
          chatMessages: chat.messages,
          userPath: chat.userPath
        })
      },
      currentAutoSavedChatId: null,

      // Verse of the day
      verseOfTheDay: null,
      setVerseOfTheDay: (verse) => set({ verseOfTheDay: verse }),

      // Custom API key
      customApiKey: null,
      setCustomApiKey: (key) => {
        if (key) {
          localStorage.setItem('overcomer_custom_api_key', key)
        } else {
          localStorage.removeItem('overcomer_custom_api_key')
        }
        set({ customApiKey: key })
      },

      // Support contacts
      supportContacts: [],
      addSupportContact: (contact) => {
        const newContact: SupportContact = {
          ...contact,
          id: `contact_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
        }
        set({ supportContacts: [...get().supportContacts, newContact] })
      },
      updateSupportContact: (id, contact) => {
        set({
          supportContacts: get().supportContacts.map(c => c.id === id ? { ...c, ...contact } : c)
        })
      },
      deleteSupportContact: (id) => {
        set({ supportContacts: get().supportContacts.filter(c => c.id !== id) })
      },

      // Academy lessons
      completedLessons: [],
      toggleLessonComplete: (lessonId) => {
        const completed = get().completedLessons
        if (completed.includes(lessonId)) {
          set({ completedLessons: completed.filter(id => id !== lessonId) })
        } else {
          set({ completedLessons: [...completed, lessonId] })
        }
      },
      lessonAnswers: {},
      setLessonAnswer: (lessonId, questionIndex, answer) => {
        const currentAnswers = get().lessonAnswers
        const lessonAnswers = currentAnswers[lessonId] || {}
        set({
          lessonAnswers: {
            ...currentAnswers,
            [lessonId]: {
              ...lessonAnswers,
              [questionIndex]: answer
            }
          }
        })
      }
    }),
    {
      name: 'overcomer-storage',
      partialize: (state) => ({
        user: state.user,
        victoryLogs: state.victoryLogs,
        freedomGoal: state.freedomGoal,
        savedChats: state.savedChats,
        verseOfTheDay: state.verseOfTheDay,
        customApiKey: state.customApiKey,
        supportContacts: state.supportContacts,
        completedLessons: state.completedLessons,
        lessonAnswers: state.lessonAnswers,
      }),
    }
  )
)

function getPathGreeting(path: FocusPath): ChatMessage {
  const greetings: Record<FocusPath, string> = {
    TOUGH_DAY: `I am so sorry you are having an all-around tough day. I am here to listen, pray with you, and help lift your load.\n\nTell me what happened today, or vent freely. We can do direct calming breathing exercises or find encouraging scriptures to help you get through today.`,
    SUBSTANCE_RECOVERY: `Welcome to OverComer Support. I am your guide here. I believe that through Christ's grace, you can be set free completely and walk in full victory.\n\nIf you are feeling tempted, struggling with a habit, or feeling anxious, talk to me. We can walk through thought reframing or calming grounding exercises together, anchored in God's mercy.`,
    MENTAL_HEALTH: `Welcome to OverComer Mental Wellness Support. I am your guide here. I believe that through Christ's perfect love, you can experience peace that passeth all understanding.\n\nIf you are struggling with heavy thoughts, anxiety, depression, or distress, share it with me. We can walk through thought reframing or emotional grounding together.`,
    TESTIMONY_VICTORY: `Glory to God! Today is a Testimony and Victory Day! I am so excited to hear about how the Lord has shown Himself strong on your behalf. As 1 Corinthians 15:57 says: "But thanks be to God, which giveth us the victory through our Lord Jesus Christ!"\n\nShare your victory story, testimony, or breakthroughs with me today! Whether it is overcoming a temptation, experiencing a mental health lift, or celebrating a major milestone, let's praise Him and converse about how you are walking in perfect freedom!`
  }

  return {
    text: greetings[path],
    isUser: false,
    timestamp: Date.now()
  }
}
