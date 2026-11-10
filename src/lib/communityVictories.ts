import { isSupabaseConfigured, supabase } from './supabase'

/** The canonical public-to-members feed is managed by authenticated Supabase RPCs. */
export type CommunityVictoryPost = {
  id: string
  display_name: string
  message: string
  focus_path: string | null
  created_at: string
  is_own: boolean
}

const errorText = (error: unknown) =>
  error instanceof Error ? error.message : String(error)

const ensureConfigured = () => {
  if (!isSupabaseConfigured) {
    throw new Error('Community Victory Board is unavailable because cloud configuration is missing.')
  }
}

const requireSignedIn = async () => {
  ensureConfigured()
  const { data: { user }, error } = await supabase.auth.getUser()
  if (error || !user) throw new Error('Please sign in to view or share community testimonies.')
  return user
}

export async function loadCommunityVictories(): Promise<CommunityVictoryPost[]> {
  await requireSignedIn()
  const { data, error } = await supabase.rpc('get_community_victory_feed', { p_limit: 100 })
  if (error) throw new Error('Unable to load shared testimonies: ' + errorText(error))
  return (data ?? []) as CommunityVictoryPost[]
}

export async function publishCommunityVictory(content: string, focusPath: string | null): Promise<string> {
  await requireSignedIn()
  const trimmed = content.trim()
  if (!trimmed || trimmed.length > 1000) {
    throw new Error('Please enter a testimony of 1 to 1,000 characters.')
  }
  // The existing server function verifies the author and does not rely on
  // a user-controlled user_id. Personal journal entries are never uploaded.
  const { data, error } = await supabase.rpc('publish_community_victory', {
    p_message: trimmed,
    p_focus_path: focusPath,
    p_source_victory_id: null,
    p_display_name: null,
  })
  if (error) throw new Error('Community sharing failed: ' + errorText(error))
  if (typeof data !== 'string' || !data) throw new Error('The server did not confirm publication.')
  return data
}

export async function hideOwnCommunityVictory(id: string): Promise<void> {
  await requireSignedIn()
  const { data, error } = await supabase.rpc('remove_community_victory', { p_id: id })
  if (error) throw new Error('Could not remove the testimony: ' + errorText(error))
  if (data !== true) throw new Error('This testimony was not removed. Only its author can remove it.')
}
