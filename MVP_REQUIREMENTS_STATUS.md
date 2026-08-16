# MajorDNA MVP Completion Status

Status date: 15 August 2026

## Implemented MVP scope

- File based 46 item main assessment with the required 10 personality, 10 intelligence, 8 work style, 8 interests, and 10 skills distribution.
- Short Big Five profile with positive and reverse keyed items using `6 minus response` scoring.
- Separate nine item Track Test for Cyber Security, Artificial Intelligence, and Data Science and Analytics.
- Five category career formula: Personality 30%, Intelligence Profile 25%, Interests 20%, Skills 15%, Work Style 10%.
- Thirty local career records across Technology, Business, Healthcare, Engineering, Education, and Creative and Media.
- Top five career ranking, top major aggregation, explainable strengths and gaps, growth suggestions, and Careers to Explore Carefully.
- Required profile fields and validation plus optional current major, country, gender, and student ID.
- Saved reports in local JSON with unique IDs, saved report list, and report loading.
- React dashboard for five categories, Big Five, intelligence, work style, major ranking, careers, and salary ranges.
- JavaFX dashboard that loads the same JSON report and shows radar, Big Five, intelligence, work style, career, and salary charts.
- Local Student and Admin accounts, registration, token sessions, role authorization, and protected Admin APIs.
- Admin editing for local question and career JSON with validation.
- GroqCloud LLM explanation using anonymized report context, with deterministic Java fallback.
- Source and limitation documentation in `DATA_SOURCES.md`.

## Important academic limitations

- The ten personality items are a short exploration profile and are not equivalent to the validated IPIP 50 instrument.
- Career compatibility is transparent rules based guidance, not a probability of academic or career success.
- Salary ranges are labelled indicative annual MYR scenario bands. DOSM provides the national baseline, but each occupation still needs a verified occupation specific source before public deployment.
- Demo account passwords must be changed before public use.
- Local JSON persistence is appropriate for the OOP MVP, not a multi user production deployment.

## Verification

- React production build passes.
- Backend main Java sources compile successfully with Java 17 and the locally available dependencies.
- JavaFX main sources compile successfully with Java 17 and JavaFX 21 dependencies.
- Full Maven execution from the Codex sandbox was blocked by access permissions to the local Maven repository; run `mvn test` normally in VS Code for the final machine level check.
