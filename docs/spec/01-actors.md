---
title: "create_brass_compass spec — actors: who touches the compass and what each may do"
type: "spec"
category: "create_brass_compass"
---

# 01 — Actors

| ID | Actor | May | May not |
|---|---|---|---|
| `ACTORS-001` | **Player (holder)** | Craft the compass; show it a lodestone to save it; open the switch screen; choose, rename and remove entries; hand the item to another player | See or edit another compass's entries |
| `ACTORS-002` | **Another player** | Receive the item and, holding it, do everything the holder may; break a lodestone someone else saved | Edit an entry on a compass they do not hold |
| `ACTORS-003` | **Server** | Own every entry's truth; validate every edit; tick the needle; refuse edits from a client that does not hold the item | Trust a client-sent position or name without checking the held item |
| `ACTORS-004` | **Server operator** | Install and remove the mod; nothing to configure | Read players' destinations from the outside except through the item's data in the save |
| `ACTORS-005` | **Create Fly** (dependency) | Supply brass, the precision mechanism, the GUI framework, the creative tab | Be replaced by upstream Create: the mod targets Create Fly's ids and classes |
| `ACTORS-006` | **Modpack or datapack author** | Change the recipes through a datapack; change item textures through a resource pack | Change the screens or the entry format |
| `ACTORS-007` | **Contributor** | Build, test and change the mod under MIT | Add telemetry or network calls (`operations/compliance.md`) |

## Findings from writing this

- **`FINDING-1`** The compass changes hands. Every rule about entries belongs to the *item*, not the
  player: a handed-over compass carries its entries, and the receiver may do everything the giver
  could. There is no owner.
- **`FINDING-2`** Another player breaking a saved lodestone is the normal way an entry becomes lost;
  the holder learns it from the screen and the needle, never from a message at the time.
- **`FINDING-3`** The server is an actor because the screens run on the client: every edit is a
  request the server checks against the item actually in the player's hand.
