# MajorDNA

MajorDNA is a GUI-based major and career discovery platform for students at City University Malaysia. It combines a React frontend, a Java Spring Boot REST backend, a hosted LLM advisor, JSON file persistence, and a JavaFX desktop report dashboard.

The system is designed as an OOP course project and simulates a real student guidance workflow rather than a simple CRUD application:

1. A student or administrator signs in.
2. A student completes an assessment.
3. Java scoring logic calculates the profile and category results.
4. Careers and majors are ranked using explainable weighted matching.
5. City University Malaysia FOIT programmes are matched to the result.
6. The report stores strengths, skill gaps, roadmap steps, salary ranges, and advisor insight.
7. The same saved report can be opened in the React dashboard or JavaFX desktop module.

## Main features

- Student and Admin login with role protection
- Admin-created student accounts and first-login password change
- 46-question main assessment with Personality, Intelligence Profile, Work Style, Interests, and Skills categories
- Separate 9-question Track Test for Artificial Intelligence, Data Science and Analytics, and Cyber Security
- Short Big Five profile, intelligence profile, work-style profile, and Tech DNA
- 30-career library across Technology, Business, Healthcare, Engineering, Education, and Creative and Media
- Explainable career ranking, major ranking, strengths, gaps, and roadmap
- City University Malaysia Faculty of Information Technology programme matching
- Groq-compatible LLM advisor with a deterministic Java fallback when the API is unavailable
- Admin management for questions, careers, and student accounts
- Saved reports and assessment completion analytics in JSON files
- JavaFX charts for the saved assessment report
- OOP implementation using encapsulation, inheritance, polymorphism, abstraction, collections, and file handling

## Project structure

```text
MajorDNA_FINAL/
├── backend/                 Spring Boot REST API and Java business logic
│   ├── src/main/java/       Controllers, models, and services
│   ├── src/main/resources/  Seed question and career data
│   ├── data/                Runtime JSON data and saved reports
│   └── src/test/            JUnit tests
├── frontend/                React and Vite web application
├── javafx-module/           JavaFX desktop report dashboard
├── data/                    Supporting data copy
├── DATA_SOURCES.md          Assessment, CityU, occupation, salary, and LLM sources
└── README.md                Project setup and usage guide
```

## Requirements

- Java 17
- Maven 3.9 or newer
- Node.js 18 or newer and npm
- Internet access for Maven/npm dependencies and the optional Groq advisor

## Configuration

Create `backend/.env` and add the API key without committing it to GitHub:

```env
GROQ_API_KEY=your_groq_api_key_here
```

The backend reads the key from the environment or this `.env` file. The LLM is optional. If no key is configured, the built-in deterministic Java advisor remains available.

## Run the application

Open three VS Code terminals from the `MajorDNA_FINAL` folder.

### 1. Start the Java backend

```powershell
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

Health check:

```text
http://localhost:8080/api/health
```

Expected response:

```json
{
  "status": "ready",
  "service": "MajorDNA AI"
}
```

### 2. Start the React frontend

```powershell
cd frontend
npm install
npm run dev -- --host 127.0.0.1 --port 5188 --strictPort
```

Open `http://127.0.0.1:5188` in the browser.

### 3. Start the JavaFX dashboard

Complete at least one assessment first so that `backend/data/reports.json` contains a report.

```powershell
cd javafx-module
mvn javafx:run
```

The JavaFX module loads the latest saved report and displays radar and bar charts.

## Local administrator account

For local testing only:

```text
Username: admin
Password: Admin123!
```

Change this password before sharing or deploying the application.

## Useful API endpoints

| Endpoint | Purpose |
|---|---|
| `GET /api/health` | Backend health check |
| `GET /api/questions?mode=CAREER_EXPLORER` | Main assessment questions |
| `GET /api/questions?mode=SUB_TRACK` | Track Test questions |
| `POST /api/assessment/start` | Start an assessment session |
| `POST /api/assessment` | Score an assessment and save the report |
| `GET /api/reports` | List saved reports |
| `GET /api/reports/{id}` | Load one report |
| `POST /api/advisor` | Ask the career advisor about a report |
| `GET /api/ai/status` | Check LLM configuration and fallback status |
| `GET /api/majors` | List supported majors |
| `GET /api/careers` | List the career library |
| `POST /api/careers/rank` | Rank careers for a profile |
| `GET /api/admin/metrics` | Admin-only completion metrics |

Admin endpoints require the `X-Auth-Token` header returned by login.

## OOP implementation

- **Encapsulation:** private fields and validated getters/setters in `User`, `CityUStudent`, and `CareerExplorer`.
- **Inheritance:** `CityUStudent` and `CareerExplorer` extend the abstract `User` class.
- **Polymorphism:** both subclasses override `getUserType()`, and the method is called through a `User` reference.
- **Abstraction:** `RecommendationService` and `AIAnalysisService` define service contracts; `User` is abstract.
- **Collections:** `List`, `Map`, `Set`, `ArrayList`, `LinkedHashMap`, and stream operations store and rank data.
- **File handling:** Jackson and `java.nio.file.Files` / `Path` read and write JSON persistence files.

## Testing

The backend test class is:

```text
backend/src/test/java/com/majordna/service/AssessmentServiceTest.java
```

It checks the main question distribution, reverse scoring, Track Test coverage, getters and setters, runtime polymorphism, and invalid state validation.

Run the checks locally:

```powershell
cd backend
mvn test

cd ..\frontend
npm run build
```
## UML Design
<img width="2960" height="2464" alt="mermaid-diagram" src="https://github.com/user-attachments/assets/c4dffe16-8c2c-42a5-ac19-d7391bc445cb" />

## Data and academic limitations

- The personality section is labelled a **Short Big Five Profile**. It is not equivalent to the validated IPIP-50 instrument and is not a clinical diagnosis.
- Career matching is transparent rules-based guidance, not a probability of admission, academic success, employment, or salary.
- Salary values are indicative annual MYR scenario bands. They are not official occupation-specific salary guarantees.
- JSON persistence is appropriate for this OOP MVP but should be replaced by a transactional database for production use.
- CityU programmes and entry requirements should be rechecked against the official university source before public deployment.

See [`DATA_SOURCES.md`](DATA_SOURCES.md) for the assessment, CityU, occupation, salary, and LLM sources.
