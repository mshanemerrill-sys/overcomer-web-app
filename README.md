# OverComer Web App

This responsive web/PWA build mirrors the Android `OverComer-main` source at GitHub commit `c5e998ddc6faaa12d94d90d3a23cc0bf6540ab09` as closely as browser APIs allow.

## Included experience

- Android-matched welcome screen, five-tab navigation, focus paths, colors, and mobile spacing
- Freedom counter, declarations, Bible affirmations, OverComer lessons, support locator, veteran transition, re-entry, vetted resources, and The Faith Connection
- Testimony & Victory Board and tracker logs
- Four-digit-PIN private journal with AES-GCM encryption on the user's device
- AI-assisted Thought Renewal analysis
- Saved Companion sessions and browser-based hands-free voice conversations
- SOS grounding, support contacts, and helplines
- Installable PWA behavior for supported mobile and desktop browsers

## Browser-specific behavior

- Voice recognition depends on browser support and microphone permission. Chrome and Edge generally provide the most complete experience.
- Private-journal encryption stays on the current browser/device. Losing the PIN or clearing browser storage makes those encrypted entries unrecoverable.
- The web app retains the existing Supabase authentication configuration. Android and web accounts/data will not automatically share records unless both versions are connected to the same backend and data model.

## Run locally

```bash
npm install
npm run dev
```

## Production build

```bash
npm run lint
npm run build
```

The deployable output is generated in `dist/`.

## Environment variables

The web build expects these Vite variables when cloud sign-in is enabled:

```text
VITE_SUPABASE_URL=
VITE_SUPABASE_ANON_KEY=
```

Gemini keys are entered by individual users in the app and stored locally in their browser.
