---
schema_version: 1
id: 01M2VM61S50RCHCYM38ZSGSRB4
key: BC-6
type: feat
title: "The add/edit screen and the save packet: name field, confirm, remove, server validation against the held item"
created_by: kevin
created_at: 2026-09-19T01:23:38Z
---

## Scope

The edit menu and screen (`UI-UC-001`, `UI-REQ-006`, `-007`): name field of 32 characters, confirm sends one Fabric play packet (position, dimension, name) which the server applies only to the compass in the player's hand and only if a lodestone stands there or the entry exists (`COMPASS-REQ-007`, `UI-FAIL-003`); remove is a menu button; formatting codes stripped (`UI-FAIL-002`); saving plays vanilla's lodestone sound (`COMPASS-REQ-004`).

## Approach

A second menu and screen with a vanilla `EditBox`; confirm sends a `CustomPacketPayload` through Fabric's `ServerPlayNetworking`; the server handler re-reads the held item, checks the lodestone or the existing entry, strips formatting, writes the component and plays the sound.

## Acceptance criteria

- [ ] Game test: a save packet for a held compass adds the entry and chooses it; the same packet with another item in hand changes nothing; a rename of a lost entry is accepted; remove clears the choice when it was current.
- [ ] A name with formatting codes is stored plain (test).
- [ ] `just client`: right-click a lodestone, type a name, confirm, needle swings (Kevin's check).

## Constraints and prior findings

`ARCH-DEC-002`, Fabric API networking (custom payloads), `COMPASS-FAIL-001`.
