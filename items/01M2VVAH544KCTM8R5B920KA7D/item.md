---
schema_version: 1
id: 01M2VVAH544KCTM8R5B920KA7D
key: BC-9
type: chore
title: "Cut release 1.0.0+26.2: release branch, security pass, production merge, tag, dist, Modrinth hand-over"
created_by: kevin
created_at: 2026-09-19T03:28:25Z
---

## Scope

The release cut per `workflows/gitkontor/repo-workflow/07-releases.md` and `docs/spec/operations/release.md`: `release/v1.0.0+26.2` from `development`; a recorded security pass (dependency audit, secrets scan, new surface, supply chain) with a verdict and Kevin's approval; tag `v1.0.0+26.2` at the release tip; `just release` from that clean, tagged checkout producing `dist/` (jar, SHA-256, notes); merge into `production` and back into `development`; the jar, checksum and `docs/modrinth.md` handed to Kevin for the manual Modrinth upload (his ruling, 2026-09-19; CurseForge deferred).

## Approach

Everything mechanical happens on the release branch before the production merge, so every criterion is checkable at merge time: the pass is written here, the tag sits on the branch tip, `dist/` is built from it and kept out of git. The Modrinth listing going live is recorded here as a note when Kevin confirms; it is not a merge criterion.

## Acceptance criteria

- [ ] Security pass recorded below with a verdict, and Kevin's approval of that verdict.
- [ ] `just check` green on the release branch; `just release` from the tagged, clean checkout wrote `dist/` with the jar, its SHA-256 and the notes.
- [ ] `dist/` and `docs/modrinth.md` handed to Kevin for the upload.

## Constraints and prior findings

No remote exists; the tag lives locally until one does. `kontor release prepare` is broken in gitkontor 1.0.1 (see BC-8), so checksums come from `just release`.
