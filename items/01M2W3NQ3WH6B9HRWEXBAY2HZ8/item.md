---
schema_version: 1
id: 01M2W3NQ3WH6B9HRWEXBAY2HZ8
key: BC-16
type: docs
title: Gallery screenshots kept in the repo with their captions
created_by: kevin
created_at: 2026-09-19T05:54:20Z
---

## Scope

Kevin (2026-09-19): the six gallery screenshots taken for the Modrinth page live in a Desktop folder; they belong in the repository. `docs/modrinth/gallery/` holds them under kebab-case names, and `docs/modrinth/gallery.md` maps each file to its caption and its place in the shot list.

## Approach

Copy the PNGs as they are (1 to 1.6 MB each, under Modrinth's 5 MiB), name them by shot, replace the shot list's "to take" wording with the file names and captions. Through a Forgejo pull request; the first push after Woodpecker was enabled, so its status is read on this ticket.

## Acceptance criteria

- [x] Six PNGs under `docs/modrinth/gallery/` and a captions table in `docs/modrinth/gallery.md` naming each file; merged through Forgejo pull request #6 (0f14489), verified against `origin/development`. Woodpecker's first run on this repo reported failure on the branch head within a minute; the cause is being read from the pipeline log and tracked as its own ticket if it is the pipeline's, not this change's (the change is six images and a markdown table).

## Constraints and prior findings

Binary files in git: six images once, no churn expected; a resource pack of screenshots would be wrong, the repo is right.
