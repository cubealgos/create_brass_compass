---
title: "create_brass_compass spec — UI: the switch screen and the add/edit screen"
type: "spec"
category: "create_brass_compass"
---

# `UI` — two screens on Create Fly's framework

## 1. Purpose

The two screens and the messages behind them. Not the item's rules (`domains/compass.md`).

## 2. Dimensions

| Dimension | Answer |
|---|---|
| **Actors** | The holder sees and clicks; the server opens, validates and applies; Create Fly supplies the frame, textures, icon buttons and labels. |
| **Over time** | A screen is opened for one item stack and one moment; it shows the entries as sent at opening; every action goes to the server and the screen closes or refreshes from the server's answer. Nothing is remembered between openings. |
| **Multiplicity** | One screen open at a time (vanilla rule). The switch screen lists 0..n entries, scrolling past the frame's rows. The edit screen shows exactly one entry. |
| **Unwanted** | A click after the item changed hands; a name with formatting codes; a client that sends a button index out of range. Refused server-side; the client only ever asks. |
| **Not-you** | A player with a controller or narration: every widget is a vanilla `AbstractWidget` with a narration string. A player in another language: every string is a translation key. |

## 3. Enumerations

### Screen states

| from ↓ / to → | `closed` | `switch` | `edit` |
|---|---|---|---|
| **`closed`** | — | ✅ use on non-lodestone | ✅ use on lodestone |
| **`switch`** | ✅ choose, escape, item changed | — | ✅ edit button on an entry |
| **`edit`** | ✅ confirm, remove, escape, item changed | ✅ back | — |

### Messages

| Message | Direction | Carries | Validated against |
|---|---|---|---|
| open | server → client | the entries and the current index at opening | — |
| choose | client → server (menu button) | the row index | the held item's entries |
| edit | client → server (menu button) | the row index | the same |
| save | client → server (custom packet) | position, dimension, name | the held item, the lodestone at the position |
| remove | client → server (menu button) | the row index | the held item's entries |

## 4. Use cases

### `UI-UC-001` — the add/edit screen
Actor: holder. 1. The screen shows the name field with the current or default name, the position,
a confirm button and, for an existing entry, a remove button. 2. Confirm sends save. 3. Remove
sends remove. 4. The server applies and closes.

### `UI-UC-002` — the switch screen
Actor: holder. 1. The screen shows one row per entry: name, dimension, distance when in the same
dimension, a marker on the current one, a lost marker. 2. Clicking the row chooses. 3. The edit
icon opens `UI-UC-001`. 4. With no entries the body says so.

## 5. Requirements

| ID | Requirement | Priority | From |
|---|---|---|---|
| `UI-REQ-001` | The system shall draw both screens with Create Fly's `AbstractSimiContainerScreen` and `AllGuiTextures`, so they look like Create's own. | Must | `DEC-006` |
| `UI-REQ-002` | The switch screen shall list only the entries of the dimension the holder is in, in the order they were saved, scrolling when they exceed the frame, with an indicator naming that dimension. | Must | `UI-DEC-002` |
| `UI-REQ-010` | Each row shall show the entry's name and its distance from the holder in blocks. | Should | `UI-DEC-002` |
| `UI-REQ-003` | The switch screen shall mark the current entry and every lost entry distinctly. | Must | `UC-004` |
| `UI-REQ-004` | **When** a row is clicked, the system shall choose it through a menu button click and close the screen. | Must | `UC-002` |
| `UI-REQ-005` | **While** the compass has no entries, the switch screen shall say so and show no rows. | Must | Multiplicity |
| `UI-REQ-006` | The edit screen shall offer a name field of at most 32 characters, confirm, and remove for an existing entry. | Must | `UC-003` |
| `UI-REQ-007` | **When** confirm is pressed, the client shall send one packet with the entry, and the server shall apply it only if the item in hand matches (`COMPASS-REQ-007`). | Must | Unwanted |
| `UI-REQ-008` | Every string shall be a translation key with an `en_us` entry. | Must | Not-you |
| `UI-REQ-009` | **If** the item in hand changes while a screen is open, **then** the server shall close it and apply nothing. | Must | Unwanted |

## 6. Failure modes

| ID | Failure | Response |
|---|---|---|
| `UI-FAIL-001` | Row index out of range | Ignored; the menu returns false. |
| `UI-FAIL-002` | Formatting codes in a name | Stripped server-side; stored plain. |
| `UI-FAIL-003` | Packet for a position with no lodestone | Refused unless the entry already exists (a rename of a lost entry is fine). |

## 7. Open questions

| Question | Blocks | Decided by |
|---|---|---|
| (none open; ruled 2026-09-19, see §8) | | |

## 8. Decisions

- `UI-DEC-002` — **The list is per dimension** (Kevin, 2026-09-19): only the current dimension's
  entries show, under an indicator of the dimension being viewed; entries elsewhere are kept and
  reappear there. Rows show name and distance (the author's call, since every listed entry shares
  the holder's dimension). Remove is final, no confirmation (the author's call: a removed entry is
  one right-click from being saved again). **Cost if wrong:** a player looking for a nether entry
  from the overworld does not see it and may save it twice.
- `UI-DEC-001` — **Stock-keeper list layout for the switch screen** (as decided for
  `create_civilization`'s job board, `DEC-014` there): header, scrolling rows, footer, icon buttons.
  **Cost if wrong:** the textures are Create Fly's; a resource pack changing them changes us.
