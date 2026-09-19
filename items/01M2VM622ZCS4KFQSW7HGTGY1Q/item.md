---
schema_version: 1
id: 01M2VM622ZCS4KFQSW7HGTGY1Q
key: BC-7
type: test
title: "Game-test sweep and the playable check: every COMPASS and UI requirement named by a test, hand-over between players, no network calls"
created_by: kevin
created_at: 2026-09-19T01:23:38Z
---

## Scope

`TEST-REQ-001`: a table in the ticket mapping every `COMPASS-REQ` and `UI-REQ` to its test; hand-over between two mock players (`UC-005`); `COMP-REQ-001` no network call (code review line recorded). The client checklist run once by Kevin.

## Approach

One table in the ticket, one test class per domain, two mock server players for the hand-over; the network claim is a code-review line because the mod opens no socket anywhere.

## Acceptance criteria

- [ ] Every requirement has a named test or a recorded reason it cannot be tested headless.
- [ ] `just check` green three runs in a row.
- [ ] Kevin's client checklist done and noted here.

## Constraints and prior findings

`docs/spec/operations/testing.md`.
