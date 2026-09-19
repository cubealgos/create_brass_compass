---
title: "create_brass_compass spec — context: why, for whom, and what it will not do"
type: "spec"
category: "create_brass_compass"
---

# 00 — Context

## Why this exists

A vanilla compass binds to exactly one lodestone and forgets it when bound to another. A player
with a base, a mine, a farm and a friend's house wants one compass that knows them all. The brass
compass, made with Create's brass, keeps every lodestone it has ever been shown and points at the
one chosen in a small screen.

It is also, deliberately, a first complete product on Create Fly's GUI framework: a screen that
lists things, a screen that edits one thing, and the packets between client and server. What is
learned here is reused in `create_civilization`'s screens.

## Who it is for

- Players on Minecraft 26.2 with Fabric and Create Fly, single player or on a server.
- Server operators who install it alongside Create Fly and expect nothing to configure.
- Modpack authors who want a compass upgrade that fits Create's material progression.

## Business context

No business model, no revenue, no telemetry. Published on Modrinth and CurseForge under MIT
(`decisions/DEC-003-licence.md`). Support is a public issue tracker and nothing more.

## What it will not do

- No waypoints in the world, no HUD arrows, no map: the needle is the only display.
- No sharing of destinations between compasses or players except by handing over the item.
- No teleportation, no distance cheating: it points, as a compass does.
- No Create kinetic behaviour; brass is the material, not a machine.
- No cross-dimension pointing beyond what vanilla does (a lodestone in another dimension spins the
  needle); the screen still lists it.
- No configuration file at 1.0; behaviour is fixed and small.

## Success

Kevin binds three lodestones in a creative world, switches between them from the screen, renames
one, breaks one and sees the entry marked lost, and the whole thing took an evening to build and
looks like it belongs to Create.
