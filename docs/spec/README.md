---
title: "create_brass_compass spec — index"
type: "spec"
category: "create_brass_compass"
repo: "create_brass_compass"
---

# Create Fly: Brass Compass — specification

A small Create Fly add-on for Minecraft 26.2 on Fabric: a **brass compass** that remembers any
number of lodestones and points at whichever one its holder chose. Two screens, one item, two
recipes. The project exists for a success feeling and to learn UI building on Create Fly's own
framework; it is nonetheless a distributed product with real users and is specified as one
(`decisions/DEC-001-classification.md`).

This spec is the distributed-product spec sheet in the chunked format. The sheet's sections map to
files as follows; a section marked *out of scope* says why in the file that would have held it.

| Sheet section | File |
|---|---|
| §1 Document control | this file: identifiers, state, decisions |
| §2 Executive summary and business context | `00-context.md` |
| §3 Product architecture and runtime topology | `04-architecture.md` |
| §4 Domain-driven functional specifications | `01-actors.md`, `02-journeys.md`, `03-glossary.md`, `domains/compass.md`, `domains/ui.md` |
| §5 Interface contracts and integration | `contracts/platform-matrix.md`, `contracts/public-surface.md`, `contracts/data-contract.md` |
| §6 Compliance, security and governance | `operations/compliance.md` |
| §7 Release engineering, distribution and support | `operations/release.md`, `operations/testing.md` |
| §8 Migration, compatibility and out of scope | `00-context.md` §What it will not do, `contracts/data-contract.md` |
| Appendix: technical blueprints | `04-architecture.md` §Data model, `contracts/data-contract.md` |

## Files and state

| File | Domain prefix | State |
|---|---|---|
| `00-context.md` | — | written |
| `01-actors.md` | `ACTORS` | written |
| `02-journeys.md` | `UC` | written |
| `03-glossary.md` | — | written |
| `04-architecture.md` | `ARCH` | written |
| `domains/compass.md` | `COMPASS` | written |
| `domains/ui.md` | `UI` | written |
| `contracts/platform-matrix.md` | `PLATFORM` | written |
| `contracts/public-surface.md` | `SURFACE` | written |
| `contracts/data-contract.md` | `DATA` | written |
| `operations/compliance.md` | `COMP` | written |
| `operations/release.md` | `REL` | written |
| `operations/testing.md` | `TEST` | written |

## Identifiers

`<DOMAIN>-<KIND>-<NNN>`: `COMPASS-REQ-004`, `UI-UC-002`, `COMPASS-FAIL-001`, `ARCH-DEC-001`.
Permanent; a withdrawn item keeps its number.

## Verifications

| # | Claim | Verified | Where |
|---|---|---|---|
| 1 | Create Fly 26.2 coordinate, mod id `create`, CC0, no Fabric API needed | 2026-09-18 | `vault/technical/minecraft/create-fly-26-2.md` |
| 2 | 26.2 toolchain: Java 25, Gradle 9.5.1, Loom 1.17, Loader 0.19.5, Fabric API 0.160.0 | 2026-09-18 | `vault/technical/minecraft/fabric-26-2-toolchain.md` |
| 3 | Vanilla's compass needle is an item-model `condition` on the `minecraft:lodestone_tracker` component with a `range_dispatch` over the `minecraft:compass` property; 32 frame models `compass_00..31` | 2026-09-19 | the 26.2 jar, `assets/minecraft/items/compass.json` |
| 4 | `LodestoneTracker(Optional<GlobalPos> target, boolean tracked)` with `tick(ServerLevel)`; `DataComponents.LODESTONE_TRACKER` | 2026-09-19 | the 26.2 jar |
| 5 | Create Fly's GUI framework: `foundation.gui.menu.MenuType` registered in `CreateRegistries.MENU_TYPE`, `MenuBase`, `MenuProvider.openHandledScreen`, client `AllMenuScreens.register`, `AbstractSimiContainerScreen`, `AllGuiTextures`, `IconButton`, `Label` | 2026-09-19 | `vault/technical/minecraft/fabric-26-2-toolchain.md`, `create_civilization` bank and job board |
| 6 | Create Fly items `create:brass_ingot`, `create:precision_mechanism` exist (`AllItems`) | 2026-09-19 | the Create Fly jar |
| 7 | Vanilla `Item.useOn(UseOnContext)` and `use(Level, Player, InteractionHand)` are the two interaction hooks; `inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)` ticks a held item | 2026-09-19 | the 26.2 jar |

## Divergences from heimathafen standards

| Standard | Divergence | Recorded in |
|---|---|---|
| `default-license-apache-2-cla` | MIT, no CLA | `decisions/DEC-003-licence.md` |
| `naming-theme` | Descriptive English under the Create add-on convention | `decisions/DEC-002-name.md` |

## Decisions

| ID | Decision | State |
|---|---|---|
| `DEC-001` | Distributed product, full spec sheet | written |
| `DEC-002` | `create_brass_compass`, mod id `brass_compass`, "Create Fly: Brass Compass" | written |
| `DEC-003` | MIT | written |
| `DEC-004` | Toolchain as `create_civilization`: Java 25, Gradle 9.5.1, Loom 1.17, Kotlin DSL | written |
| `DEC-005` | Two recipes: compass ringed by brass; compass plus precision mechanism | written |
| `DEC-006` | Destinations live on the item as a data component; screens on Create Fly's framework | written |
| `COMPASS-DEC-003..006`, `UI-DEC-002` | Sneak passes through; current entry per dimension; Create's base tab; spin with nothing chosen; per-dimension list | written |

## Open questions gathered

All five questions of the first draft were ruled on 2026-09-19 and folded into the domain
decisions (`COMPASS-DEC-003..006`, `UI-DEC-002`). Nothing is open before the backlog.
