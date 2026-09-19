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

## Acceptance criteria

- [ ] `just check` passes: lint, map, unit tests, and the smoke game test on a dedicated server with Create Fly loaded.
- [ ] `just client` boots with Create Fly and the mod in the mod list.
- [ ] `just doctor` is clean: toolchain floors and the spec copy identical to the vault.

## Constraints and prior findings

`docs/spec/contracts/platform-matrix.md`, `DEC-004`, `create_civilization`'s bootstrap (the same toolchain, verified).
