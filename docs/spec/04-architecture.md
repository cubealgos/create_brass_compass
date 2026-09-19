---
title: "create_brass_compass spec — architecture: one item, one component, two Create screens"
type: "spec"
category: "create_brass_compass"
---

# 04 — Architecture

Sheet §3. Small on purpose: everything below is what a two-screen item mod needs and nothing it does
not.

## Shape

```
 client                                   server
 ┌──────────────────────────┐             ┌──────────────────────────────┐
 │ SwitchScreen  (Create    │ menu open   │ BrassCompassItem             │
 │   AbstractSimiContainer  │◄────────────│   use()  → open switch menu  │
 │   Screen)                │ button click│   useOn(lodestone) → open    │
 │ EditScreen  (name field, │────────────►│      add/edit menu           │
 │   confirm, remove)       │ edit packet │ SwitchMenu / EditMenu        │
 └──────────────────────────┘────────────►│   (MenuBase) validate + apply│
                                          │ Destinations component on    │
                                          │   the ItemStack (truth)      │
                                          │ inventoryTick → tracker      │
                                          └──────────────────────────────┘
```

**The item stack is the truth.** The `brass_compass:destinations` data component holds every
entry and which one is current. The server writes it; the client only draws it. Vanilla's
`lodestone_tracker` component is derived from the current entry on every server inventory tick,
so the vanilla needle model works unchanged.

## `ARCH-DEC-001` — a Fabric mod on Create Fly, one jar, Java 25

Same toolchain and layout as `create_civilization` (`decisions/DEC-004-toolchain.md`): Loom 1.17,
Gradle 9.5.1, Kotlin DSL with a version catalog, one Gradle project (no sim split: there is no
sim), `fabric.mod.json` depending on `fabricloader`, `minecraft ~26.2`, `java >= 25`, `create`
(Create Fly's id) and `fabric-api` (for the networking and item APIs).

**Cost if wrong:** ports track Create Fly's release cadence; the GUI classes are Create Fly's and
not upstream Create's.

## `ARCH-DEC-002` — screens on Create Fly's framework, edits as packets the server validates

Both screens are `AbstractSimiContainerScreen`s over `MenuBase` menus registered in Create Fly's
menu registry, opened server-side with `MenuProvider.openHandledScreen`. Choosing an entry is a
vanilla menu button click (`clickMenuButton`), which needs no packet. Renaming carries text, which
a button click cannot, so the edit screen sends one custom Fabric play packet; the server applies
it only to the compass in the player's hand and only if the entry exists.

**Alternatives:** vanilla `Screen`s with `MenuScreens` (private in 26.2) · a pure client-side
edit that writes the component and trusts the client (rejected: a client could write any
position into any item).

**Cost if wrong:** the framework is Create Fly's internal API; a Create Fly release can move it.
Bounded by keeping every framework touch in two classes.

## `ARCH-DEC-003` — the component is versioned from the first commit

`destinations` carries a `version` field; a newer version read by an older build keeps the data
untouched and shows the list read-only (`contracts/data-contract.md`).

## Runtime topology (sheet §3.1)

Out of scope beyond the diagram above: the mod runs inside the Minecraft client and server
processes; there is no process of its own, no daemon, no file it writes. Item data lives in the
world save as any item component does.

## Local state and on-disk layout (sheet §3.5)

Nothing of the mod's own. Entries are inside item stacks inside the world save; a resource pack may
replace textures; a datapack may replace recipes.

## Failure modes with no single owner (sheet §3.6)

| ID | Failure | Response |
|---|---|---|
| `ARCH-FAIL-001` | A screen is open and the player drops or swaps the compass | Every server-side apply re-reads the held item; a mismatch closes the screen with nothing changed (`COMPASS-FAIL-001`). |
| `ARCH-FAIL-002` | Create Fly is missing or an incompatible version | Fabric Loader refuses to start with its dependency message; the mod adds nothing. |
| `ARCH-FAIL-003` | A resource pack removes the needle frames | Vanilla's missing-model purple; the item still works. |
