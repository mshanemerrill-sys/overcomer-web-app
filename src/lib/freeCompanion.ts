/** Free on-device, non-AI Companion guidance. No API call or publisher charge. */
export function freeCompanionResponse(message: string): string {
  const text = message.toLowerCase();
  if (/suicid|kill myself|end my life|self.harm|hurt myself|overdose/.test(text)) {
    return "Your safety matters. If you may hurt yourself, call or text 988 (U.S.) now, or contact local emergency services. Please reach out to a trusted person who can stay with you. You do not have to face this moment alone. Psalm 34:18 reminds us that the Lord is close to the brokenhearted.";
  }
  if (/withdrawal|seizure|can't breathe|chest pain|medical emergency/.test(text)) {
    return "A medical emergency or severe withdrawal needs immediate professional attention. Please contact local emergency services or an emergency department. Seeking medical care and trusting God can go hand in hand. Psalm 46:1 calls God our refuge and strength.";
  }
  if (/tempt|crav|relaps|addict|alcohol|drug|substance/.test(text)) {
    return "Thank you for speaking about this. A craving or difficult moment doesn't decide your future. Consider leaving the triggering situation, contacting a trusted support person, and choosing one healthy action for the next ten minutes. John 8:36 speaks of freedom in Christ. What is one safe choice you can make right now?";
  }
  if (/fear|anxi|panic|worr|stress|overwhelm/.test(text)) {
    return "I hear that this feels heavy. Try relaxing your shoulders, breathing gently, and naming five things you can see. Then consider one small action you can take today. Philippians 4:6-7 reminds us to bring our concerns before God. Would you like to describe what is worrying you most?";
  }
  if (/sad|lonely|depress|grief|loss|hopeless/.test(text)) {
    return "I'm glad you shared this. You matter, and you deserve support from people who care about you. If possible, reach out to someone trustworthy today. Psalm 34:18 reminds us that God is close to the brokenhearted. What has been the hardest part of today?";
  }
  return "Thank you for sharing that with OverComer. You can take one small, constructive step today, even when everything feels difficult. Romans 8:37 reminds us of our hope in Christ. What would meaningful progress look like for you right now?\n\nThis free response is a built-in encouragement, not a generated AI answer. To use Google Gemini AI, you can optionally add your own API key in Companion Settings.";
}
