# MajorDNA Backend Master Plan

Version: 1.0 planning baseline  
Status: Ready for academic and CityU review before implementation

## 1. Decisions we are locking now

1. The assessment result is computed by deterministic Java code.
2. The AI never creates, changes, or guesses a score.
3. Core scored questions come from established public or licensed sources.
4. CityU recommendations include only programs verified from current official university material.
5. The result is career exploration guidance, not admission advice, hiring screening, or psychological diagnosis.
6. JavaFX uses the same real domain and scoring services as the web application.

## 2. Real assessment structure

### Part A: Vocational interests

Source: O*NET Mini Interest Profiler Version 2.0.

- 30 official questions.
- Five questions for each RIASEC dimension.
- Dimensions: Realistic, Investigative, Artistic, Social, Enterprising, Conventional.
- Response scale:
  - 1 Strongly Dislike
  - 2 Dislike
  - 3 Unsure
  - 4 Like
  - 5 Strongly Like
- No reverse scoring.
- Weight in final program compatibility: 70 percent.

O*NET recommends the full 60 question form when time permits. We choose the official 30 question Mini IP because the frontend uses one card at a time and targets new students. This choice must be recorded in the report metadata.

#### Interest scoring

Each RIASEC dimension contains five items:

```text
rawDimension = sum of its five answers
minimum = 5
maximum = 25
normalizedDimension = ((rawDimension - 5) / 20) * 100
```

Nominal weight per O*NET question:

```text
70 percent / 30 questions = 2.333 percent
```

This is the question's nominal share of the interest evidence. Program matching uses the completed six dimensional vector, so the UI must not claim that changing one answer always changes the final match by exactly 2.333 points.

### Part B: Personality and work style

Source: Goldberg's 50 item IPIP Big Five Factor Markers from the official IPIP site.

- 50 public domain items.
- Ten questions per factor.
- Factors: Extraversion, Agreeableness, Conscientiousness, Emotional Stability, Intellect or Imagination.
- Response scale:
  - 1 Very Inaccurate
  - 2 Moderately Inaccurate
  - 3 Neither Inaccurate nor Accurate
  - 4 Moderately Accurate
  - 5 Very Accurate
- Weight in final program compatibility: 30 percent.

The exact official item order and positive or negative scoring key must be stored in a versioned JSON file. Do not use the copyrighted BFI 44 questionnaire.

#### Reverse scoring

```text
positive item score = response
negative item score = 6 - response
factorRaw = sum of ten keyed item scores
minimum = 10
maximum = 50
factorNormalized = ((factorRaw - 10) / 40) * 100
```

Nominal weight per IPIP question:

```text
30 percent / 50 questions = 0.6 percent
```

### Part C: Skills and prior exposure

These are not part of the compatibility percentage because self confidence is not measured ability.

Collect 12 short behaviorally anchored questions covering:

- programming exposure;
- mathematics and statistics exposure;
- working with data;
- written and spoken communication;
- visual design and creativity;
- persistence and study habits.

Use these answers only for:

- skill gap analysis;
- roadmap starting level;
- course recommendations;
- questions the advisor should ask next.

Every result must label these values as self reported experience.

### Total assessment length

- 80 scored official questions.
- 12 unscored readiness questions.
- 92 cards total.
- Expected completion time: approximately 18 to 25 minutes.
- Save and resume is required.
- Show progress separately for Interests, Work Style, and Readiness.

## 3. Response quality checks

The engine calculates a response quality flag but never secretly changes answers.

Checks:

- missing answers;
- completing implausibly quickly;
- choosing the same option for almost every item;
- strong contradictions across reverse keyed IPIP pairs;
- assessment interrupted across different instrument versions.

Output:

```text
HIGH: complete and internally usable
MEDIUM: one warning, interpret cautiously
LOW: incomplete or multiple consistency warnings, request retake
```

## 4. Verified CityU catalog baseline

The official City University Malaysia Faculty of Information Technology page currently confirms these full time bachelor programs. Each is listed as 120 credit hours over 36 months, with intakes in Jan/Feb, May/June, and Sept/Oct:

### Bachelor of Computer Science (Artificial Intelligence) (Honours)

- Faculty: Information Technology.
- Duration: 36 months.
- Credits: 120.
- Campus shown on the programme page: Cyberjaya.
- Catalog status: verified baseline.

### Bachelor of Computer Science (Cyber Security) with Honours

- Faculty: Information Technology.
- Duration: 36 months.
- Credits: 120.
- Catalog status: verified baseline.

### Bachelor in Software Engineering (Honours)

- Faculty: Information Technology.
- Duration: 36 months.
- Credits: 120.
- Campuses shown on the programme page: Petaling Jaya and Cyberjaya.
- Catalog status: verified baseline.

### Bachelor of Information Technology (Honours)

- Faculty: Information Technology.
- Duration: 36 months.
- Credits: 120.
- Campuses shown on the programme page: Petaling Jaya and Cyberjaya.
- Catalog status: verified baseline.

No other bachelor program should appear in FOIT matching until a current official City University Malaysia source confirms it. The catalog must be rechecked before each release because programme names, campuses, intakes, and requirements may change.

Each catalog record stores:

```json
{
  "programId": "cityu-my-bcs-ai-hons",
  "officialName": "Bachelor of Computer Science (Artificial Intelligence) (Honours)",
  "university": "City University Malaysia",
  "faculty": "Faculty of Information Technology",
  "durationMonths": 36,
  "creditHours": 120,
  "campuses": ["Cyberjaya"],
  "sourceUrl": "https://city.edu.my/bachelor-in-computer-science-artificial-intelligence-honours/",
  "effectiveFrom": "current official catalog",
  "lastVerifiedAt": "date",
  "active": true
}
```

Before coding target profiles, extract the published modules and learning outcomes from City University Malaysia and have a Faculty of Information Technology subject matter expert approve every target vector.

## 5. Program target profiles

Each program requires two reviewed target vectors:

```text
RIASEC target: R, I, A, S, E, C
Big Five target: E, A, C, ES, I
```

Do not invent these values from the program name. Build them from:

1. official curriculum and learning outcomes;
2. O*NET profiles of relevant occupations;
3. review by at least one CityU subject matter expert;
4. a version and review date.

Initial example values may be used only in tests and must be marked fixtures, never production data.

## 6. Deterministic Java matching logic

### Step 1: Calculate user vectors

- `RiasecScorer` returns six values from 0 to 100.
- `BigFiveScorer` returns five values from 0 to 100.
- `ResponseQualityService` returns the quality flag and warnings.

### Step 2: Compare with each approved program profile

Use weighted normalized Euclidean distance initially because it is transparent and easy to test:

```text
dimensionDistance = sqrt(sum(weight[d] * (user[d] - target[d])^2) / sum(weight[d]))
similarity = clamp(100 - dimensionDistance, 0, 100)
```

### Step 3: Combine components

```text
programCompatibility = round(
    0.70 * riasecSimilarity
  + 0.30 * bigFiveSimilarity
)
```

Display:

- final compatibility;
- interest compatibility;
- personality compatibility;
- top supporting dimensions;
- largest mismatches;
- response quality;
- program profile version.

Do not call the result a probability of success. Use the label `Compatibility indicator`.

## 7. AI model selection

### Chosen baseline

OpenAI `gpt-5.6-terra` through the Responses API.

Reason:

- official OpenAI documentation positions Terra as the balance of intelligence and cost;
- it supports Structured Outputs;
- it is suitable for explanation, comparison, and roadmap generation;
- the deterministic Java engine remains the source of truth.

Start with `reasoning.effort: low` for routine explanations. Evaluate `medium` only if the quality test set shows a meaningful improvement.

### AI responsibilities

Allowed:

- explain why the deterministic score was produced;
- compare verified CityU programs;
- summarize strengths and tensions;
- generate a roadmap using an approved resource catalog;
- answer advisor questions from the saved result;
- explicitly state uncertainty.

Not allowed:

- score raw answers;
- change or override Java results;
- invent programs, courses, requirements, salaries, or labor statistics;
- diagnose personality or mental health;
- promise admission or career success.

### Data sent to AI

Send only:

- normalized dimension scores;
- deterministic program matches;
- approved program facts;
- approved skill resources;
- response quality warnings;
- the user's selected goal.

Do not send student ID, full name, email, or raw questionnaire answers when aggregate scores are sufficient.

### Structured AI output

```json
{
  "summary": "string",
  "evidence": [
    {
      "statement": "string",
      "dimension": "Investigative",
      "score": 84
    }
  ],
  "programComparison": [
    {
      "programId": "cityu-bsc-ai",
      "advantages": ["string"],
      "challenges": ["string"]
    }
  ],
  "skillGaps": [
    {
      "skillId": "python-foundations",
      "evidenceType": "self_reported",
      "recommendation": "string"
    }
  ],
  "roadmap": [
    {
      "weekRange": "1 to 2",
      "resourceId": "approved-resource-id",
      "outcome": "string"
    }
  ],
  "uncertainties": ["string"],
  "disclaimer": "string"
}
```

The backend validates every returned program ID, dimension, score, and resource ID against the request. Invalid output is rejected and replaced by a Java template explanation.

## 8. Domain and OOP design

```text
domain
  User (abstract)
    CityUStudent
    CareerExplorer
  Instrument
  Question
  ScoringKey
  AssessmentSession
  Answer
  ScoreVector
  Program
  ProgramProfile
  ProgramMatch
  AnalysisReport

scoring
  Scorer (interface)
    RiasecScorer
    BigFiveScorer
  SimilarityMetric (interface)
    WeightedEuclideanSimilarity

ai
  AnalysisProvider (interface)
    OpenAIAnalysisProvider
    TemplateAnalysisProvider

repository
  QuestionRepository (interface)
    JsonQuestionRepository
  ProgramRepository (interface)
    JsonProgramRepository
  ReportRepository (interface)
    JsonReportRepository
```

OOP mapping:

- Encapsulation: immutable domain fields with validated constructors.
- Inheritance: user modes share abstract `User` behavior.
- Abstraction: scorer, similarity, AI, and repository interfaces.
- Polymorphism: services operate on interfaces and select implementations at runtime.
- Collections: question banks, answers, vectors, programs, reports.
- File handling: versioned JSON instruments, catalogs, and saved reports.

## 9. JavaFX requirement

JavaFX must depend on a shared `majordna-core` Maven module. It will:

- load the same real question bank;
- display the same card based assessment;
- call the same scoring services;
- display the same result data;
- load and save real JSON reports.

It must not contain duplicate scoring logic or sample results.

Recommended Maven structure:

```text
majordna-parent
  majordna-core
  majordna-api
  majordna-javafx
  frontend
```

## 10. REST API

```text
GET  /api/v1/instruments/current
POST /api/v1/assessments
PUT  /api/v1/assessments/{id}/answers
POST /api/v1/assessments/{id}/complete
GET  /api/v1/reports/{id}
POST /api/v1/reports/{id}/analysis
POST /api/v1/reports/{id}/advisor
GET  /api/v1/programs
GET  /api/v1/programs/{id}
DELETE /api/v1/users/{id}/data
```

Assessment submission must include the instrument version. The backend rejects answers for unknown or mixed versions.

## 11. Storage

For the course project, use JSON repositories first to demonstrate file handling:

```text
data/instruments/onet-mini-ip-v2.json
data/instruments/ipip-big-five-50-v1.json
data/programs/cityu-programs-2025.json
data/programs/program-profiles-v1.json
data/resources/learning-resources-v1.json
data/reports/{reportId}.json
```

Never commit real student reports to Git. Add them to `.gitignore`.

## 12. Tests required before calling it real

### Unit tests

- all 1 answers produce the expected minimum;
- all 5 answers produce the expected maximum;
- negative IPIP items use `6 - response`;
- every instrument has the correct item count and dimensions;
- program compatibility is always 0 to 100;
- unchanged answers always produce unchanged results;
- mixed instrument versions are rejected.

### Golden scoring tests

Create manually calculated answer sets and expected RIASEC and Big Five results. These are reviewed independently before coding.

### AI evaluation set

At least 30 fixed reports covering:

- clear AI match;
- clear Cyber Security match;
- clear Software Engineering match;
- clear Information Technology match;
- close tie;
- low response quality;
- missing readiness data;
- adversarial advisor questions;
- request for a nonexistent CityU major.

Pass criteria:

- 100 percent score fidelity;
- zero invented program IDs;
- zero invented numeric scores;
- valid JSON schema;
- required uncertainty and disclaimer present;
- roadmap contains only approved resource IDs.

### Human review

- CityU faculty reviews program facts and profiles.
- Career counselor reviews wording and interpretation.
- Students test usability and comprehension.
- Do not call a small usability pilot psychometric validation.

## 13. Implementation phases

### Phase 1: Evidence and approvals

- Register or confirm permitted O*NET use.
- Export the official Mini IP questions and metadata.
- Export the official IPIP 50 items and scoring key.
- Obtain current CityU curriculum and learning outcomes.
- Approve disclaimers and consent wording.

### Phase 2: Shared Java core

- Build immutable domain models.
- Build JSON repositories.
- Implement RIASEC and IPIP scoring.
- Add quality checks.
- Add golden unit tests.

### Phase 3: Program matching

- Create faculty reviewed target profiles.
- Implement similarity and weighted combination.
- Build transparent match explanations.

### Phase 4: API and persistence

- Implement versioned REST endpoints.
- Add save and resume.
- Add report deletion and privacy controls.

### Phase 5: AI analysis

- Integrate Responses API and structured schema.
- Add output validation.
- Add Java offline fallback.
- Run the AI evaluation set.

### Phase 6: React and JavaFX integration

- Replace frontend fixture questions with API data.
- Connect card progress and save resume.
- Connect result charts to real report data.
- Rebuild JavaFX on the same `majordna-core` module.

### Phase 7: Review and release

- Security and privacy review.
- Faculty and counselor approval.
- End to end test on a clean VS Code setup.
- Freeze instrument, program profile, prompt, and model versions for the demo.

## 14. Items requiring a CityU decision

1. Use `City University Malaysia` as the university name and `Faculty of Information Technology (FOIT)` as the faculty name throughout the product.
2. Confirm which campus choices should be shown to the target student group.
3. Obtain or confirm the current curriculum and learning outcomes for Artificial Intelligence, Cyber Security, Software Engineering, and Information Technology.
4. Nominate the FOIT faculty member who approves program target profiles.
5. Decide whether the final assessment is English only or bilingual English and Arabic.
6. Approve whether OpenAI API may process anonymized aggregate scores.
