# MajorAI — Real Assessment & AI Implementation Plan

## 1. Product rule

The recommendation score must be calculated by deterministic Java code. The AI model may explain, compare, and generate a roadmap, but it must never invent or alter assessment scores. The result is career-exploration guidance, not a psychological diagnosis or admissions decision.

## 2. Assessment design

### A. Vocational interests — 50% of major match

Use the O*NET Mini Interest Profiler (30 items) or licensed O*NET Interest Profiler Short Form. It measures Holland's six RIASEC dimensions: Realistic, Investigative, Artistic, Social, Enterprising, and Conventional. Each item uses a 1–5 enjoyment scale. Calculate each dimension as its item mean, normalize it to 0–100, and retain the top three-letter Holland code.

Source: O*NET Interest Profiler and its published scoring/manual. Confirm and record the selected O*NET license before copying the actual items into production.

### B. Personality/work style — 20% of major match

Use a public-domain IPIP Big Five short scale after verifying its item set and scoring key. Do not copy the BFI-44 without permission: the University of Wisconsin page explicitly says it cannot grant use permission. Use 1–5 agreement responses, reverse-score keyed items (`6 - response`), then calculate the mean for Openness, Conscientiousness, Extraversion, Agreeableness, and Emotional Stability.

### C. Self-reported skill confidence — 15% of major match

Create clearly labeled, non-psychometric self-rating items for: quantitative reasoning, programming exposure, data interpretation, communication, visual creativity, attention to detail, and persistence. Use 3 behaviorally anchored items per skill. These scores describe confidence and must not be presented as measured ability.

### D. Work preferences and values — 15% of major match

Create paired or Likert items for collaboration, ambiguity tolerance, building vs. analyzing, people vs. systems, structured vs. exploratory work, and social impact. Values should refine explanations and ties, not override strong interest mismatch.

### Recommended length

- Core version: 30 O*NET interest items + 15 IPIP items + 12 skill-confidence items + 8 work-preference items = 65 items.
- Estimated completion: 12–15 minutes.
- Offer a save/resume facility. Do not shorten a validated scale by selecting arbitrary questions.

## 3. Scoring and matching

For every CityU program, maintain a reviewed target profile:

```text
ProgramProfile
- RIASEC target vector (6 dimensions)
- Big Five target vector (5 dimensions)
- skill target vector
- preference target vector
- curriculum evidence and source URL
- review date and reviewer
```

Calculate similarity within each vector using normalized weighted distance or cosine similarity, then:

```text
finalMatch = 0.50 * interestSimilarity
           + 0.20 * personalitySimilarity
           + 0.15 * skillConfidenceSimilarity
           + 0.15 * workPreferenceSimilarity
```

Rules:

- Display a score as a compatibility indicator, not a probability of success.
- Never show artificial precision: round to a whole percentage and show the four component scores.
- Add a confidence flag based on missing answers, straight-lining, completion time, and inconsistent reverse-keyed answers.
- A human academic reviewer must approve program target vectors before release.

## 4. CityU program catalog

The current official City University Malaysia Faculty of Information Technology catalog lists these full time bachelor programs, each with 120 credits and a duration of 36 months:

- Bachelor of Computer Science (Artificial Intelligence) (Honours).
- Bachelor of Computer Science (Cyber Security) with Honours.
- Bachelor in Software Engineering (Honours).
- Bachelor of Information Technology (Honours).

Before implementing the final catalog, recheck the official City University Malaysia FOIT pages and confirm the relevant campus options and current entry requirements. Store catalog records with `effectiveFrom`, `sourceUrl`, `lastVerifiedAt`, and `active` fields so outdated programs cannot silently remain visible.

## 5. AI analysis layer

### Recommended production model

Use OpenAI Responses API with `gpt-5.6-terra` as the quality/cost baseline. Official OpenAI documentation describes Terra as the balance between intelligence and cost, and current models support Structured Outputs. Use a JSON Schema response, temperature/default randomness appropriate to the Responses API, and a snapshot/model version when consistency is required.

The request contains only:

- deterministic component scores;
- top matched programs and their curriculum evidence;
- approved career/skill records;
- user-selected goals;
- explicit safety and output rules.

The model returns structured fields only:

```json
{
  "summary": "...",
  "evidence": [{"claim":"...","scoreDimension":"..."}],
  "programComparison": [],
  "skillGaps": [],
  "roadmap": [],
  "uncertainties": [],
  "advisorDisclaimer": "..."
}
```

Reject any AI response that cites a score or program not present in the supplied data. Fall back to template-based Java explanations when the API is unavailable.

## 6. Backend modules

```text
assessment/
  Instrument, Question, Scale, Answer, ScoringKey
  RiasecScorer, BigFiveScorer, ConfidenceScorer
catalog/
  CityUProgram, CurriculumOutcome, ProgramProfile
matching/
  SimilarityMetric, WeightedMatchEngine, MatchExplanation
ai/
  AIAdvisor, OpenAIResponsesAdvisor, TemplateAdvisor
repository/
  JsonQuestionRepository, JsonProgramRepository, ReportRepository
validation/
  ResponseQualityValidator, AIOutputValidator
```

This preserves OOP requirements through interfaces/abstraction, scorer polymorphism, encapsulated domain objects, collections for item banks and vectors, and JSON file handling. JavaFX remains a separate GUI client that consumes the same assessment and report services—not a disconnected OOP sample.

## 7. Data and validation work

1. Confirm the instrument licenses and download official item/scoring files.
2. Get written confirmation of the CityU program catalog and curriculum outcomes.
3. Have a career counselor/FOIT faculty member define and review each target vector.
4. Pilot with at least 30–50 students for usability and item clarity; do not call this psychometric validation.
5. For validation claims, run a larger study: internal consistency, test–retest reliability, factor structure, and convergent validity against the original instruments.
6. Build a gold evaluation set for AI explanations and test hallucination, score fidelity, safety, and repeatability.
7. Version every question bank, scoring key, program profile, prompt, and model.

## 8. Privacy and safety

- Obtain consent before assessment.
- Minimize stored personal data and allow report deletion.
- Never use results for admission, hiring, or mental-health diagnosis.
- Keep API keys server-side only.
- Do not send names, student IDs, or raw answers to the AI when aggregate scores are sufficient.
- Log model/version and prompt version, but redact personal data.

## 9. Delivery phases

1. **Evidence pack:** licenses, official questions, scoring keys, CityU catalog.
2. **Deterministic engine:** scoring, confidence checks, program profiles, tests.
3. **Real frontend flow:** 65-card assessment, save/resume, component charts.
4. **AI layer:** structured Responses API integration and offline fallback.
5. **JavaFX client:** same services and real stored reports.
6. **Pilot and review:** faculty/counselor review, student usability pilot.
7. **Evaluation:** scoring unit tests, end-to-end tests, AI gold-set evals, privacy review.
