---
title: "create_brass_compass spec — COMPASS: the item and its entries"
type: "spec"
category: "create_brass_compass"
---

# `COMPASS` — the brass compass and its destinations

## 1. Purpose

The item: what it remembers, how it points, how it is made. Not the screens (`domains/ui.md`).

## 2. Dimensions

| Dimension | Answer |
|---|---|
| **Actors** | The holder (`ACTORS-001`) does everything; another player (`ACTORS-002`) becomes the holder by receiving the item; the server (`ACTORS-003`) applies every change; Create Fly (`ACTORS-005`) supplies brass and the mechanism; a datapack author (`ACTORS-006`) may change recipes. Nobody edits a compass they do not hold. |
| **Over time** | Crafted with no entries and a spinning needle. Entries are added, renamed, chosen and removed; a lodestone breaking marks its entry lost on the next tick that observes it; placing one back revives it. The item stack records the entries and the current choice; the change itself is not logged (no history). |
| **Multiplicity** | One compass holds 0..n entries with no cap; at zero the needle spins and the switch screen says so; at hundreds the screen scrolls and nothing else changes. Exactly 0..1 entry is current. A position is saved at most once per compass. Two compasses may save the same lodestone. |
| **Unwanted** | A client asks to edit an item it does not hold, or an entry that does not exist; a name longer than the field; a lodestone in another dimension; the lodestone gone. Each is refused or shown, never silently applied or invented. |
| **Not-you** | A player who never opens the screen still gets vanilla behaviour: right-clicking a lodestone with the compass saves and points. A server player who receives the item from someone else sees names they did not write. A modpack author expects the recipes to be data. |

## 3. Enumerations

### Entry lifecycle

| from ↓ / to → | `present` | `lost` | `removed` |
|---|---|---|---|
| **(none)** | ✅ saved at a lodestone | ❌ | ❌ |
| **`present`** | — | ✅ the block is gone when the item ticks | ✅ removed in the screen |
| **`lost`** | ✅ a lodestone stands there again when the item ticks | — | ✅ removed in the screen |
| **`removed`** | ❌ (saving again creates a new entry) | ❌ | — |

`removed` is terminal: the entry leaves the component. Being current is orthogonal: any `present`
or `lost` entry may be current; removing the current entry leaves no current entry.

### Cardinality

| Relation | Count | At zero | At the top |
|---|---|---|---|
| compass → entries | 0..n | needle spins, screen says "no lodestones saved" | scrolls; no cap |
| compass → current entry per dimension | 0..1 each | needle spins in that dimension | N/A |
| entry → position | exactly 1, unique per compass | illegal | N/A |
| entry → name | exactly 1, at most 32 characters | default name generated from the position | truncated at entry |
| lodestone → compasses that saved it | 0..n | nothing | nothing; the block knows no compass |

## 4. Use cases

`UC-001`, `UC-002`, `UC-003`, `UC-004`, `UC-005`, `UC-006` in `02-journeys.md`.

## 5. Requirements

| ID | Requirement | Priority | From |
|---|---|---|---|
| `COMPASS-REQ-001` | The system shall provide the item `brass_compass:brass_compass`, stackable to one, craftable by `DEC-005`. | Must | `UC-006` |
| `COMPASS-REQ-002` | **When** the holder, not sneaking, uses the item on a lodestone block, the system shall open the add/edit screen for that position on that item (`UI-UC-001`). | Must | `UC-001` |
| `COMPASS-REQ-003` | **When** the holder, not sneaking, uses the item on anything else, the system shall open the switch screen for that item (`UI-UC-002`). | Must | `UC-002` |
| `COMPASS-REQ-014` | **While** the holder sneaks, the system shall pass the use through unchanged, as if the hand held nothing of this mod. | Must | `DEC-003` |
| `COMPASS-REQ-015` | The system shall keep the chosen entry per dimension and point at the chosen entry of the dimension the holder is in. | Must | `DEC-004` |
| `COMPASS-REQ-016` | **While** the needle has a target, the item shall show the enchantment glint, as a vanilla compass bound to a lodestone does. | Should | Kevin, 2026-09-19 |
| `COMPASS-REQ-004` | **When** an entry is saved, the system shall store position, dimension, name and `present` on the item, make it current, and play vanilla's lodestone-bind sound. | Must | `UC-001` |
| `COMPASS-REQ-005` | **While** an entry is current, the system shall set the item's `lodestone_tracker` to that position on every server inventory tick, so the vanilla needle points at it. | Must | Over time |
| `COMPASS-REQ-006` | **While** no entry is current, the system shall leave the tracker untracked, so the needle spins. | Must | Multiplicity |
| `COMPASS-REQ-007` | **If** a client requests a change for an item that is not in the player's hand, or an entry that does not exist, **then** the system shall refuse it and change nothing. | Must | Unwanted |
| `COMPASS-REQ-008` | **When** the held item ticks and the current entry's block is not a lodestone, the system shall mark the entry `lost` and leave it current. | Must | `UC-004` |
| `COMPASS-REQ-009` | **When** a `lost` entry's position holds a lodestone again on a tick, the system shall mark it `present`. | Should | `UC-004` |
| `COMPASS-REQ-010` | **When** the holder saves a position already on the item, the system shall open its existing entry instead of adding a second. | Must | Cardinality |
| `COMPASS-REQ-011` | The system shall store an entry in another dimension and point at it exactly as vanilla does (spinning when the holder is elsewhere). | Must | Not-you |
| `COMPASS-REQ-012` | The system shall show the current entry's name in the item's tooltip, or "no destination". | Should | Not-you |
| `COMPASS-REQ-013` | **Where** a datapack replaces the recipes, the system shall use the datapack's. | Must | `ACTORS-006` |

## 6. Failure modes

| ID | Failure | Response |
|---|---|---|
| `COMPASS-FAIL-001` | Edit for an item not in hand | Refused; the screen closes; nothing written. |
| `COMPASS-FAIL-002` | Name empty or over 32 characters | Empty falls back to the default name; longer is truncated by the field before sending. |
| `COMPASS-FAIL-003` | Position saved twice | The second save opens the first entry (`REQ-010`). |
| `COMPASS-FAIL-004` | Component from a newer mod version | Read-only list, no edits, a tooltip line says so (`contracts/data-contract.md`). |

## 7. Open questions

| Question | Blocks | Decided by |
|---|---|---|
| (none open; the three questions of the first draft were ruled on 2026-09-19, see §8) | | |

## 8. Decisions

- `COMPASS-DEC-001` — **Entries live on the item, no cap** (Kevin, 2026-09-19): the brass compass
  "shouldn't have a limit". **Cost if wrong:** a compass with thousands of entries is a large item
  stack; bounded in practice by the screen's usability, not by code.
- `COMPASS-DEC-003` — **Sneaking disables the compass** (Kevin, 2026-09-19): a sneak-right-click with
  the compass is a normal right-click by the player (the lodestone, block or item behaves as if the
  hand were empty of the compass); no quick bind. **Cost if wrong:** none; the screen is one click.
- `COMPASS-DEC-004` — **Current destination is per dimension** (Kevin, 2026-09-19, "per dimension
  state"): the item keeps one chosen entry per dimension; crossing a portal makes that dimension's
  choice the needle's target, and none means spinning. **Cost if wrong:** the component holds a
  small map instead of one index.
- `COMPASS-DEC-005` — **Creative tab: Create's base tab, beside the brass items** (Kevin).
- `COMPASS-DEC-006` — **With no entry chosen the needle spins** (Kevin); the tooltip says so.
- `COMPASS-DEC-002` — **The needle is vanilla's**: the tracker is derived, never a second pointer.
  **Cost if wrong:** the tracker refreshes on inventory ticks only, so a switch shows after the next
  tick, within a game tick of the click.
