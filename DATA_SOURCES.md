# MajorDNA Data Sources and Limitations

Last reviewed: 15 August 2026

## Personality questions

The ten personality prompts form a short Big Five profile derived from the public International Personality Item Pool approach. Positive items use responses 1 to 5. Reverse keyed items use `6 minus response` before normalization. The official IPIP scoring instructions and 50 item sample are the methodological sources:

- https://ipip.ori.org/newScoringInstructions.htm
- https://www.ipip.ori.org/New_IPIP-50-item-scale.htm

Because MajorDNA uses only two prompts per factor, the output must be labelled `Short Big Five Profile`. It is career exploration evidence, not a clinical assessment and not equivalent to the full IPIP 50 instrument.

## City University Malaysia programmes

The FOIT programme baseline comes from:

- https://city.edu.my/information-technology/

Programme names and requirements must be rechecked before a public release.

## Occupation definitions

Career names and role descriptions are aligned at a broad level with Malaysia's occupational classification resources. eMASCO is maintained under Malaysia's Ministry of Human Resources:

- https://emasco.mohr.gov.my/

## Salary data

The national salary context uses the Department of Statistics Malaysia Salaries and Wages Survey Report 2024, released 29 September 2025:

- https://www.statistics.gov.my/portal-main/release-content/salaries-and-wages-survey-report-20244
- https://storage.dosm.gov.my/labour/salaries_wages_2024.pdf

DOSM reports a 2024 median monthly salary and wage of RM2,793 and a mean of RM3,652 for Malaysian citizens. These are national benchmarks, not occupation specific salaries.

The annual ranges in `backend/data/careers.json` are editable scenario bands for dashboard comparison. They are not guarantees, offers, or official occupation medians. The interface must label them `indicative annual range`. Before external deployment, each occupation should receive a source URL, geography, observation date, experience level, sample method, and verified minimum and maximum.

## LLM

The hosted language model explains already computed results. It does not score questions or rank careers. Only anonymized scores, recommendations, gaps, and roadmap content are sent to the configured API. Names, age, student ID, country, and gender are excluded.
