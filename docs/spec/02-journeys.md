---
title: "create_brass_compass spec — journeys: the use cases end to end"
type: "spec"
category: "create_brass_compass"
---

# 02 — Journeys

Every step names who acts. `UC` ids are flat across the project; domain files reference them.

### `UC-001` — Save a lodestone

Actor: player (`ACTORS-001`) · Goal: make the compass remember a lodestone

| Step | Actor | Action |
|---|---|---|
| 1 | player | Right-clicks a lodestone block while holding a brass compass. |
| 2 | server | Opens the add/edit screen for this compass and this lodestone's position; if the position is already saved, the screen shows its entry. |
| 3 | player | Types a name, or keeps the default "Lodestone at x, y, z", and confirms. |
| 4 | server | Saves the entry on the held item, makes it the current destination, plays the lodestone sound. |
| 5 | player | Sees the needle swing to it. |

Fails when: the item in hand is not the one the screen was opened for → `COMPASS-FAIL-001`.

### `UC-002` — Switch destination

Actor: player · Goal: point the compass at another saved lodestone

| Step | Actor | Action |
|---|---|---|
| 1 | player | Right-clicks with the compass at air or any block that is not a lodestone. |
| 2 | server | Opens the switch screen listing every entry, the current one marked, lost ones marked. |
| 3 | player | Clicks an entry. |
| 4 | server | Sets the tracker to that entry; the screen closes; the needle swings. |

Fails when: the compass has no entries → the screen says so and offers nothing (`UI-REQ-005`).

### `UC-003` — Rename or remove an entry

Actor: player · Goal: keep the list tidy

| Step | Actor | Action |
|---|---|---|
| 1 | player | In the switch screen, presses the edit button on an entry. |
| 2 | server | Opens the add/edit screen for that entry. |
| 3 | player | Changes the name and confirms, or presses remove. |
| 4 | server | Applies it to the held item; if the current destination was removed, the needle spins until another is chosen. |

### `UC-004` — A saved lodestone is gone

Actor: another player (`ACTORS-002`) breaks a lodestone · Goal of the holder: understand the needle

| Step | Actor | Action |
|---|---|---|
| 1 | another player | Breaks the lodestone. |
| 2 | server | On the next tick of the held compass, vanilla's tracker finds no lodestone: the needle spins. |
| 3 | player | Opens the switch screen; the entry is marked lost. |
| 4 | player | Removes it or keeps it; placing a lodestone at the same position revives it (`COMPASS-REQ-009`). |

### `UC-005` — Hand the compass to a friend

Actor: player · Goal: share the destinations

| Step | Actor | Action |
|---|---|---|
| 1 | player | Drops or trades the item. |
| 2 | another player | Picks it up and opens the switch screen: every entry is there. |

### `UC-006` — Craft it

Actor: player · Goal: obtain a brass compass

| Step | Actor | Action |
|---|---|---|
| 1 | player | Crafts a compass ringed by eight brass ingots, or a compass with a precision mechanism (`DEC-005`). |
| 2 | game | Yields one brass compass with no entries. |
