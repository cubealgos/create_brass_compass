---
schema_version: 1
id: 01M2VYT7S2DWDVVJYQSQDPG7C1
key: BC-14
type: chore
title: "Issue tracker on GitHub: every issues link points at the mirror's tracker"
created_by: kevin
created_at: 2026-09-19T04:29:25Z
---

## Scope

Kevin (2026-09-19): mod users need a place to give feedback, and they have GitHub accounts, not accounts on the cubealgos Forgejo. Issues are enabled on the GitHub mirror (`https://github.com/cubealgos/create_brass_compass/issues`) and become the public tracker; Forgejo stays the source of truth for code. Repoint `fabric.mod.json` `contact.issues`, `README.md`, `SUPPORT.md`, `docs/modrinth/body.md` and the spec's compliance row.

## Approach

One small change through a Forgejo pull request. Tickets in `.gitkontor` remain the working backlog; a GitHub issue that turns into work gets a BC ticket referencing it.

## Acceptance criteria

- [x] Every issues link names the GitHub tracker; the spec's compliance and release rows say so (cubealgos vault amended, copy synced); merged through Forgejo pull request #3 (5c4820d), verified against `origin/development` (2026-09-19). Pull request #2 for the same branch was recorded as merged by Forgejo without `development` moving; #3 was merged with explicit title and message fields and landed.

## Constraints and prior findings

The mirror is push-only from Forgejo; GitHub issues are not mirrored anywhere.
