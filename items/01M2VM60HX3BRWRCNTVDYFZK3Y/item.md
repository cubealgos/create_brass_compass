---
schema_version: 1
id: 01M2VM60HX3BRWRCNTVDYFZK3Y
key: BC-2
type: feat
title: "The destinations component: pure entries model, versioned codec, per-dimension choice, unique positions"
created_by: kevin
created_at: 2026-09-19T01:23:37Z
---

## Scope

`brass_compass.destinations`: `Entry` (dimension, position, name, present), `Destinations` (entries in order, chosen index per dimension, version), rules `COMPASS-REQ-010` (unique position), `COMPASS-FAIL-002` (name limits), `DATA-REQ-001..003` (version, forward migrations, malformed read as empty). The Fabric side registers the data component `brass_compass:destinations` with a codec over the pure model.

## Acceptance criteria

- [ ] Unit tests: add, rename, remove, choose per dimension, duplicate position returns the existing entry, name truncation, codec round trip, newer version read-only (`DATA-REQ-001`).
- [ ] `verifyPurePackage` fails on a deliberate Minecraft import and passes without (proof in the ticket).
- [ ] The component survives a save round trip in a game test.

## Constraints and prior findings

`docs/spec/contracts/data-contract.md`, `COMPASS-DEC-001`, `-004`.
