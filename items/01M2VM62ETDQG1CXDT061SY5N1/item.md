---
schema_version: 1
id: 01M2VM62ETDQG1CXDT061SY5N1
key: BC-8
type: chore
title: "Release 1.0.0+26.2: tag, jar and checksum, Modrinth and CurseForge pages, SUPPORT.md"
created_by: kevin
created_at: 2026-09-19T01:23:39Z
---

## Scope

`REL-REQ-001..002`: `just release` from a clean checkout at the tag, checksum in the notes, platform pages stating Create Fly as required, `SUPPORT.md`, the disclosure note in `README.md` (`operations/compliance.md`).

## Approach

`just release` builds from a tag on `production`; the checksum goes into the release notes; the platform pages are Kevin's; `SUPPORT.md` says issues only.

## Acceptance criteria

- [ ] Tag `v1.0.0+26.2` on `production`, jar and checksum attached.
- [ ] Modrinth and CurseForge listings live (Kevin's accounts).
- [ ] `SUPPORT.md` and the disclosure note exist.

## Constraints and prior findings

`docs/spec/operations/release.md`.
