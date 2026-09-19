---
schema_version: 1
id: 01M2VM615AAQQAHW1HM53RQ7QM
key: BC-4
type: feat
title: "Recipes and creative tab: brass ring and precision mechanism, listed in Create's base tab"
created_by: kevin
created_at: 2026-09-19T01:23:37Z
---

## Scope

Two data recipes (`DEC-005`): shaped compass ringed by eight `create:brass_ingot`; shapeless compass plus `create:precision_mechanism`. The item joins Create Fly's base creative tab beside the brass items (`COMPASS-DEC-005`).

## Approach

Two recipe JSON files under `data/brass_compass/recipe/`; the creative tab entry through Fabric's `ItemGroupEvents.modifyEntriesEvent` on Create Fly's base tab key, found by reading its jar.

## Acceptance criteria

- [ ] Game test: both recipes craft one brass compass with no entries.
- [ ] A datapack override of the shaped recipe takes effect (`COMPASS-REQ-013`, game test through the recipe manager).
- [ ] The item appears in Create's base tab in `just client` (Kevin's check).

## Constraints and prior findings

`docs/spec/contracts/public-surface.md`; Create Fly's creative tab class found by reading its jar.
