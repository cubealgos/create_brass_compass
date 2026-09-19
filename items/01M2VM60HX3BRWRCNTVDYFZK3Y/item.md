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

## Approach

A record-based model in a package with no Minecraft imports, checked by `verifyPurePackage`; a Fabric-side `DataComponentType` whose codec maps to and from the model, with `version` read first so a newer version short-circuits into a read-only wrapper.

## Acceptance criteria

- [x] Unit tests: add, rename, remove, choose per dimension, duplicate position returns the existing entry, name truncation, codec round trip, newer version read-only (`DATA-REQ-001`). `DestinationsTest`, six tests; the codec round trip is the game test below.
- [x] `verifyPurePackage` fails on a deliberate Minecraft import and passes without (proof 2026-09-19: `import net.minecraft.core.BlockPos;` prepended to `Names.java` gave `pure package imports the game: Names.java: import net.minecraft.core.BlockPos;`; removed, the task passes).
- [x] The component survives a save round trip in a game test (`ComponentGameTest`: two entries, one marked lost, encoded and parsed through `ItemStack.CODEC` with registry ops, equal).

## Constraints and prior findings

`docs/spec/contracts/data-contract.md`, `COMPASS-DEC-001`, `-004`.
