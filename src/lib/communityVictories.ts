import { isSupabaseConfigured, supabase } from './supabase'

/** Only explicitly community-shared entries use this table. Personal logs stay local. */
export type CommunityVictoryPost = {
  id: string
  user_id: string
  display_name: string
  message: string
  focus_path: string | null
  created_at: string
}
const message = (error: unknown) => error instanceof Error ? error.message : String(error)
const ensureConfigured = () => {
  if (!isSupabaseConfigured) throw new Error('Shared Victory Board is unavailable: cloud configuration is missing.')
}

export async function loadCommunityVictories(): Promise<CommunityVictoryPost[]> {
  ensureConfigured()
  const { data: { user }, error: authError } = await supabase.auth.getUser()
  if (authError || !user) throw new Error('Sign in to see the Community Victory Board.')
  const { data, error } = await supabase.from('community_victories')
    .select('id,user_id,display_name,message,focus_path,created_at')
    .eq('is_visible', true)
    .order('created_at', { ascending: false }).limit(100)
  if (error) throw new Error('Unable to load community testimonies: ' + message(error))
  return (data ?? []) as CommunityVictoryPost[]
}

export async function publishCommunityVictory(content: string, focusPath: string | null): Promise<CommunityVictoryPost> {
  ensureConfigured()
  const { data: { user }, error: authError } = await supabase.auth.getUser()
  if (authError || !user) throw new Error('Sign in to share a testimony with the community.')
  const trimmed = content.trim()
  if (!trimmed || trimmed.length > 10000) throw new Error('Please enter a testimony of up to 10,000 characters.')
  const displayName = String(user.user_metadata?.display_name || 'OverComer').slice(0, 60)
  const { data, error } = await supabase.from('community_victories').insert({
    user_id: user.id,
    display_name: displayName,
    message: trimmed,
    focus_path: focusPath,
    is_visible: true,
  }).select('id,user_id,display_name,message,focus_path,created_at').single()
  if (error) throw new Error('Community sharing failed: ' + message(error))
  return data as CommunityVictoryPost
}

export async function hideOwnCommunityVictory(id: string): Promise<void> {
  ensureConfigured()
  const { data: { user } } = await supabase.auth.getUser()
  if (!user) throw new Error('Sign in to remove your community testimony.')
  const { error } = await supabase.from('community_victories').delete()
    .eq('id', id).eq('user_id', user.id)
  if (error) throw new Error('Could not remove your testimony: ' + message(error))
}
