---
schema_version: 1
id: 01M2VM61FACDVVKYQVGC6AGKT8
key: BC-5
type: feat
title: "Interactions and the switch screen: use opens the per-dimension list on Create's framework; sneak passes through"
created_by: kevin
created_at: 2026-09-19T01:23:38Z
---

## Scope

`use` and `useOn` (`COMPASS-REQ-002`, `-003`, `-014`): not sneaking, a lodestone opens the add/edit menu (BC-6), anything else the switch menu; sneaking passes through. The switch screen: Create Fly `MenuType` in `CreateRegistries.MENU_TYPE`, `MenuBase`, `AbstractSimiContainerScreen` with the stock-keeper list textures (`UI-DEC-001`), rows for the holder's dimension only with a dimension indicator and distance (`UI-REQ-002`, `-003`, `-010`), choose by menu button click (`UI-REQ-004`), empty state (`UI-REQ-005`), item-changed guard (`UI-REQ-009`).

## Acceptance criteria

- [ ] Game test: the menu built for a mock player lists only entries of that dimension, choosing a row sets the choice and the tracker, an out-of-range row is ignored, sneaking does not open anything.
- [ ] Screen strings are translation keys with `en_us` entries.
- [ ] `just client`: the screen opens, scrolls and looks like Create's request window (Kevin's check).

## Constraints and prior findings

`docs/spec/domains/ui.md`, `ARCH-DEC-002`, the vault's Create Fly GUI findings, `create_civilization`'s `JobBoardScreen` as the starting point.
