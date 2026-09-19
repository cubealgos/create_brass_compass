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

- [x] Release preparation merged: version 1.0.0+26.2 in one place, CHANGELOG.md, `just release` (clean checkout at the tag, jar, SHA-256, notes from the changelog; tools/release_notes.py tested), docs/modrinth.md listing text. The cut itself (release branch, security pass, production merge, tag, dist, Modrinth upload) is BC-9, since a tag on production cannot precede this branch's merge into development.
- [x] Modrinth listing text ready (docs/modrinth.md); the upload is Kevin's, manual, under BC-9. CurseForge deferred by Kevin, 2026-09-19; the spec keeps both channels.
- [x] `SUPPORT.md` exists and `README.md` states issues-only support and disclosure (Kevin, 2026-09-19: no e-mail address published; `operations/compliance.md` amended).

## Constraints and prior findings

`kontor release prepare` (gitkontor 1.0.1) fails with `ModuleNotFoundError: No module named 'release'`; checksums are done by `just release` with `shasum` instead. Reported to Kevin 2026-09-19.

`docs/spec/operations/release.md`.
