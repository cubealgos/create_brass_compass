---
schema_version: 1
id: 01M2VY550X859BT82JN3R9RPW4
key: BC-13
type: chore
title: "Public home: links to Forgejo, GitHub mirror and Modrinth in the mod metadata, README, support and listing"
created_by: kevin
created_at: 2026-09-19T04:17:54Z
---

## Scope

Kevin (2026-09-19): the mod is released under the cubealgos organisation, source on Forgejo (`https://git.cubealgos.de/cubealgos/create_brass_compass`), mirrored to GitHub (`https://github.com/cubealgos/create_brass_compass`, issues off). Put the links where they belong: `fabric.mod.json` `contact` (homepage, sources, issues), `README.md` (source, mirror, issues), `SUPPORT.md` (the tracker URL), `docs/modrinth/body.md` (links row), `docs/spec/contracts/platform-matrix.md` and `.woodpecker.yml` no longer say "no remote". Ignore `.DS_Store`.

## Approach

First ticket after the push, so also the first through a forge pull request: `development` and `production` are protected on Forgejo now, and merges land through `kontor pr open` and the forge, never a local merge.

## Acceptance criteria

- [ ] `fabric.mod.json`, `README.md`, `SUPPORT.md`, `docs/modrinth/body.md` carry the Forgejo, mirror and Modrinth links; the spec's platform matrix and the CI file no longer describe the repo as remote-less (spec amended in the cubealgos vault, copy synced).
- [ ] `just check` green; merged through a Forgejo pull request into `development`.

## Constraints and prior findings

The history was rewritten to the `scheeren@cubealgos.de` identity before the first push (Kevin's ruling); nothing published changed. The spec moved to the cubealgos heimathafen layer the same day.
