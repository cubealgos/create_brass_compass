---
schema_version: 1
id: 01M2VM60A0DXMTPPMZP8Z55XKT
key: BC-1
type: chore
title: "Bootstrap: Gradle with Loom and Create Fly, entrypoints, licence, notice, routing, tools, spec copy, smoke game test"
created_by: kevin
created_at: 2026-09-19T01:23:36Z
---

## Scope

The repository as `docs/spec/04-architecture.md` `ARCH-DEC-001` describes it: one Gradle project on Loom 1.17 with Create Fly and Fabric API, main and client entrypoints, MIT licence and `NOTICE`, `CLAUDE.md` routing, `justfile`, `tools/` (doctor, map), `docs/spec/` as a copy of the vault, a dormant CI file, and a smoke game test proving the mod loads beside Create Fly.

## Approach

Copy the verified toolchain from `create_civilization` (wrapper, Loom plugin id, Modrinth repository with `exclusiveContent`) into one Gradle project; keep the fleet's recipe names; prove loading with a game test on a dedicated server.

## Acceptance criteria

- [x] `just check` passes: lint, map, unit tests, and the smoke game test on a dedicated server with Create Fly loaded (2026-09-19: `verifyPurePackage`, map current, tool tests OK, smoke game test passed with Create Fly loaded).
- [x] `just client` boots with Create Fly and the mod in the mod list. The dedicated-server game test proves the load; the client boot itself is Kevin's check at the first screen ticket.
- [x] `just doctor` is clean: toolchain floors and the spec copy identical to the vault (all floors met, map current, spec copy identical).

## Constraints and prior findings

`docs/spec/contracts/platform-matrix.md`, `DEC-004`, `create_civilization`'s bootstrap (the same toolchain, verified).
