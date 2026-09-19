---
schema_version: 1
id: 01M2VWH4B0W80H0K3QQPXNENX0
key: BC-11
type: docs
title: "Modrinth listing assets: icon, body, gallery shot list"
created_by: kevin
created_at: 2026-09-19T03:49:30Z
---

## Scope

What the Modrinth project page needs beyond the jar (Kevin, 2026-09-19): a square icon, the summary, the markdown body, the categories and side settings, and gallery images with captions. The icon and body are produced here under `docs/modrinth/`; the gallery is Kevin's in-game screenshots of the 1.0.1 build, taken to a shot list recorded here.

## Approach

The icon is the item's own needle sprite (frame 00, brass-tinted) scaled up without smoothing to 512 px on a transparent ground, so it matches the in-game look. The body is the listing text already in `docs/modrinth.md`, moved to `docs/modrinth/body.md` and completed with headings, crafting and the version line.

## Acceptance criteria

- [x] `docs/modrinth/icon.png` (512x512, transparent, 2 KiB) and `docs/modrinth/body.md` exist; `docs/modrinth.md` folded into them (2026-09-19).
- [x] The shot list with captions is in `docs/modrinth/gallery.md` (five shots).

## Constraints and prior findings

Modrinth icons: PNG, GIF or WebP, up to 256 KiB, square. Gallery images up to 5 MiB each.
