# Tasks – DAYBRIEF

## A. Project Setup
- [ ] Create Android project (Kotlin)
- [ ] Setup permissions (RECORD_AUDIO, FOREGROUND_SERVICE)
- [ ] Add notification channel

## B. Foreground Service
- [ ] Implement DailyListeningService
- [ ] Persistent notification (recording active)
- [ ] Start/Stop controlled by UI only

## C. Audio Pipeline
- [ ] AudioRecorder wrapper
- [ ] Chunk audio (e.g. 30–60 sec)
- [ ] Voice Activity Detection

## D. Speech-to-Text
- [ ] Google Speech-to-Text client
- [ ] Convert audio → text
- [ ] Delete audio after transcription

## E. Event Segmentation
- [ ] Manual marker (Mark Event button)
- [ ] Silence-based segmentation
- [ ] Topic-shift heuristic

## F. End-of-Day Worker
- [ ] Aggregate daily transcript
- [ ] Run AI summarization
- [ ] Store DailyBrief

## G. UI
- [ ] Daily setup screen
- [ ] Focus mode screen
- [ ] Daily review screen

## H. Privacy
- [ ] Encrypted local storage
- [ ] Delete raw transcript after summary
- [ ] Manual delete all data
