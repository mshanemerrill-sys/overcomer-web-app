import { useState } from 'react'
import { useAppStore } from '../../store/useAppStore'
import { Plus, Trash2, Edit, Brain, Heart, AlertCircle } from 'lucide-react'
import type { VictoryLog } from '../../lib/types'

type LogType = 'REFLECT' | 'TRIGGER' | 'CBT'

export default function JournalTab() {
  const { victoryLogs, addVictoryLog, deleteVictoryLog, userPath } = useAppStore()
  const [showAddModal, setShowAddModal] = useState(false)
  const [filterType, setFilterType] = useState<LogType | 'ALL'>('ALL')

  const filteredLogs = filterType === 'ALL'
    ? victoryLogs
    : victoryLogs.filter(log => log.type === filterType)

  return (
    <div className="p-4 pb-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Victory Logs</h1>
          <p className="text-sm text-gray-600">Your journal of victories and reflections</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="bg-primary-400 hover:bg-primary-500 text-white p-3 rounded-xl transition-colors"
        >
          <Plus className="w-5 h-5" />
        </button>
      </div>

      {/* Filter */}
      <div className="flex gap-2 mb-6 overflow-x-auto pb-2 scrollbar-hide">
        {['ALL', 'REFLECT', 'TRIGGER', 'CBT'].map(type => (
          <button
            key={type}
            onClick={() => setFilterType(type as LogType | 'ALL')}
            className={`flex-shrink-0 px-4 py-2 rounded-full font-medium text-sm transition-colors ${
              filterType === type
                ? 'bg-gray-900 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {type === 'ALL' ? 'All' : type === 'REFLECT' ? 'Reflections' : type === 'TRIGGER' ? 'Triggers' : 'Thought Reframes'}
          </button>
        ))}
      </div>

      {/* Logs List */}
      {filteredLogs.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-2xl shadow-sm">
          <Edit className="w-12 h-12 text-gray-300 mx-auto mb-4" />
          <h3 className="font-semibold text-gray-600">No entries yet</h3>
          <p className="text-sm text-gray-400 mt-1">Start journaling your victories</p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredLogs.map(log => (
            <LogCard key={log.id} log={log} onDelete={() => deleteVictoryLog(log.id)} />
          ))}
        </div>
      )}

      {/* Add Log Modal */}
      {showAddModal && (
        <AddLogModal
          onClose={() => setShowAddModal(false)}
          onAdd={(log) => {
            addVictoryLog(log)
            setShowAddModal(false)
          }}
          userPath={userPath || 'SUBSTANCE_RECOVERY'}
        />
      )}
    </div>
  )
}

function LogCard({
  log,
  onDelete
}: {
  log: VictoryLog
  onDelete: () => void
}) {
  const typeInfo = {
    REFLECT: { icon: <Heart className="w-4 h-4" />, color: 'bg-accent-teal', label: 'Reflection' },
    TRIGGER: { icon: <AlertCircle className="w-4 h-4" />, color: 'bg-accent-coral', label: 'Trigger' },
    CBT: { icon: <Brain className="w-4 h-4" />, color: 'bg-primary-400', label: 'Thought Reframe' }
  }

  const info = typeInfo[log.type]
  const formattedDate = new Date(log.timestamp).toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })

  return (
    <div className="bg-white rounded-2xl p-5 shadow-md border border-gray-100">
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className={`${info.color} text-white p-2 rounded-xl`}>
            {info.icon}
          </div>
          <div>
            <span className="font-semibold text-gray-900">{info.label}</span>
            <p className="text-xs text-gray-400">{formattedDate}</p>
          </div>
        </div>
        <button
          onClick={onDelete}
          className="p-2 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-red-500 transition-colors"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>

      {log.notes && (
        <p className="text-gray-700 mt-3 text-sm leading-relaxed">{log.notes}</p>
      )}

      {log.type === 'CBT' && (
        <div className="mt-4 space-y-2 text-sm">
          {log.triggerContext && (
            <div>
              <span className="font-semibold text-gray-600">Trigger: </span>
              <span className="text-gray-700">{log.triggerContext}</span>
            </div>
          )}
          {log.automaticThought && (
            <div>
              <span className="font-semibold text-gray-600">Automatic Thought: </span>
              <span className="text-gray-700 italic">{log.automaticThought}</span>
            </div>
          )}
          {log.identifiedDistortion && (
            <div>
              <span className="font-semibold text-gray-600">Distortion: </span>
              <span className="text-amber-600">{log.identifiedDistortion}</span>
            </div>
          )}
          {log.reframedTruth && (
            <div className="bg-accent-teal/10 p-3 rounded-lg">
              <span className="font-semibold text-accent-teal">Reframed Truth: </span>
              <span className="text-gray-700">{log.reframedTruth}</span>
            </div>
          )}
          {log.scriptureReference && (
            <div>
              <span className="font-semibold text-primary-500">{log.scriptureReference}</span>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

function AddLogModal({
  onClose,
  onAdd,
  userPath
}: {
  onClose: () => void
  onAdd: (log: Omit<VictoryLog, 'id' | 'timestamp' | 'userId'>) => void
  userPath: string
}) {
  const [type, setType] = useState<LogType>('REFLECT')
  const [notes, setNotes] = useState('')
  const [triggerContext, setTriggerContext] = useState('')
  const [automaticThought, setAutomaticThought] = useState('')
  const [reframedTruth, setReframedTruth] = useState('')
  const [scriptureReference, setScriptureReference] = useState('')

  const handleAdd = () => {
    onAdd({
      type,
      notes,
      triggerContext: type === 'CBT' || type === 'TRIGGER' ? triggerContext : undefined,
      automaticThought: type === 'CBT' ? automaticThought : undefined,
      identifiedDistortion: undefined,
      reframedTruth: type === 'CBT' ? reframedTruth : undefined,
      scriptureReference: type === 'CBT' ? scriptureReference : undefined,
      userPath: userPath as any
    })
  }

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl w-full max-w-md max-h-[90vh] overflow-y-auto animate-slide-up">
        <div className="p-5 border-b border-gray-100">
          <h2 className="text-lg font-bold text-gray-900">New Log Entry</h2>
        </div>

        <div className="p-5 space-y-4">
          {/* Type Selection */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Type</label>
            <div className="grid grid-cols-3 gap-2">
              {[
                { type: 'REFLECT', label: 'Reflection', icon: <Heart className="w-4 h-4" /> },
                { type: 'TRIGGER', label: 'Trigger', icon: <AlertCircle className="w-4 h-4" /> },
                { type: 'CBT', label: 'Thought Reframe', icon: <Brain className="w-4 h-4" /> }
              ].map(option => (
                <button
                  key={option.type}
                  onClick={() => setType(option.type as LogType)}
                  className={`flex flex-col items-center gap-1 p-3 rounded-xl font-medium text-sm transition-colors ${
                    type === option.type
                      ? 'bg-primary-100 text-primary-600 border-2 border-primary-400'
                      : 'bg-gray-50 text-gray-600 border-2 border-transparent hover:bg-gray-100'
                  }`}
                >
                  {option.icon}
                  {option.label}
                </button>
              ))}
            </div>
          </div>

          {/* Notes - Always shown */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">Notes</label>
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={3}
              className="input-field resize-none"
              placeholder="Write your thoughts..."
            />
          </div>

          {/* CBT-specific fields */}
          {type === 'CBT' && (
            <>
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Trigger Context</label>
                <input
                  type="text"
                  value={triggerContext}
                  onChange={(e) => setTriggerContext(e.target.value)}
                  className="input-field"
                  placeholder="What triggered this thought?"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Automatic Thought</label>
                <textarea
                  value={automaticThought}
                  onChange={(e) => setAutomaticThought(e.target.value)}
                  rows={2}
                  className="input-field resize-none"
                  placeholder="What thought came to mind?"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Reframed Truth</label>
                <textarea
                  value={reframedTruth}
                  onChange={(e) => setReframedTruth(e.target.value)}
                  rows={2}
                  className="input-field resize-none"
                  placeholder="What's the biblical truth here?"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Scripture Reference</label>
                <input
                  type="text"
                  value={scriptureReference}
                  onChange={(e) => setScriptureReference(e.target.value)}
                  className="input-field"
                  placeholder="e.g., Philippians 4:13"
                />
              </div>
            </>
          )}

          {/* Trigger-specific fields */}
          {type === 'TRIGGER' && (
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">Trigger Context</label>
              <input
                type="text"
                value={triggerContext}
                onChange={(e) => setTriggerContext(e.target.value)}
                className="input-field"
                placeholder="What triggered you?"
              />
            </div>
          )}
        </div>

        <div className="p-5 border-t border-gray-100 flex gap-3">
          <button onClick={onClose} className="flex-1 btn-outline">Cancel</button>
          <button onClick={handleAdd} className="flex-1 btn-primary">Save Entry</button>
        </div>
      </div>
    </div>
  )
}
