---
title: "create_brass_compass spec — public surface: what a datapack, resource pack or add-on may rely on"
type: "spec"
category: "create_brass_compass"
---

# Public surface (`SURFACE`)

| Surface | Stable from | What it is |
|---|---|---|
| Item id `brass_compass:brass_compass` | 1.0 | The item |
| Component `brass_compass:destinations` | 1.0, versioned | `contracts/data-contract.md` |
| Recipes `brass_compass:brass_compass` (shaped) and `brass_compass:brass_compass_from_mechanism` (shapeless) | 1.0 | Overridable by datapack |
| Item model `assets/brass_compass/items/brass_compass.json` and 32 frame models and textures | 1.0 | Replaceable by resource pack; frames follow vanilla's naming `brass_compass_00..31` |
| Translation keys `item.brass_compass.brass_compass`, `screen.brass_compass.*`, `tooltip.brass_compass.*` | 1.0 | Language packs |
| Creative tab placement | 1.0 | Open question in `domains/compass.md` |

Not public: the menu types, the packet, the screen classes. Versioned by SemVer over the surface
above (`operations/release.md`).

`SURFACE-REQ-001`: a change to a stable surface is a major version.
