---
schema_version: 1
id: 01M2VWR8WHBZ7P8SNXN3TM1JFN
key: BC-12
type: docs
title: Modrinth icon on Create's blueprint badge
created_by: kevin
created_at: 2026-09-19T03:53:24Z
---

## Scope

Kevin (2026-09-19): Create add-ons share an icon theme; find examples and redo the icon on it. Sixteen Modrinth icons compared (Create, Create Fly, Steam 'n' Rails, Crafts & Additions, Copycats+, Enchantment Industry, New Age, Bells & Whistles, Interiors, Jetpack, Goggles, Deco, Slice & Dice, Big Cannons, Structures): a round badge, a blueprint-blue disc with a faint white grid, a pale ring, a darker blue outer band, the subject large and centred with a white outline. Create Fly uses the same badge in grey.

## Approach

Rendered with the palette sampled from Create's own icon (outer band 60,118,168; ring 190,214,235; blueprint 82,150,209; grid white at half alpha): the brass compass sprite scaled without smoothing, a one-pixel white outline and a soft shadow, at 512x512. `tools/icon.py` makes it reproducible.

## Acceptance criteria

- [x] `docs/modrinth/icon.png` is the badge; `tools/icon.py` (`just icon`) regenerates it; the finding is in `vault/technical/minecraft/create-fly-26-2.md` (2026-09-19).

## Constraints and prior findings

Modrinth serves icons as 96 px webp on the search API; the palette was sampled from that size.
