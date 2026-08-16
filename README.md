# MajorDNA AI

Major and career discovery platform built with React, Spring Boot, and a JavaFX/OOP companion module.

## Included

- Login-first Student and Admin roles
- Admin-created student accounts with forced first-login password change
- 46-item main assessment and a separate Track Test
- Big Five, intelligence profile, work style, interests, and skills scoring
- General career explorer with 30 career records
- City University Malaysia FOIT matching and Apply Now links
- Live Tech DNA, latest result, career and major rankings, skill gaps, roadmap, and report download
- Groq-hosted open-weight LLM advisor through `GROQ_API_KEY`
- Friendly Admin tools for metrics, questions, careers, and student accounts
- JSON file handling plus OOP abstraction, inheritance, polymorphism, encapsulation, and collections
- Explicit validated getters and setters in the `User` inheritance hierarchy

No student result is preloaded. A dashboard result is created only after a student completes an assessment.

## Run

Put the Groq key in `backend/.env`, then use two terminals:

```powershell
cd backend
mvn spring-boot:run
```

```powershell
cd frontend
npm install
npm run dev -- --host 127.0.0.1 --port 5188 --strictPort
```

Open `http://127.0.0.1:5188`. Initial administrator login: `admin` / `Admin123!`. Change this password before deployment.

JavaFX can be started separately with `cd javafx-module` followed by `mvn javafx:run`.

See `DATA_SOURCES.md` for assessment and program sources. The platform supports career exploration and is not a psychological diagnosis or an admission guarantee.
