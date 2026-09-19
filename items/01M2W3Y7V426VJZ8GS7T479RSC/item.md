---
schema_version: 1
id: 01M2W3Y7V426VJZ8GS7T479RSC
key: BC-17
type: bug
title: "CI: the pipeline image has no curl or python3, the tool installer dies before just exists"
created_by: kevin
created_at: 2026-09-19T05:58:59Z
---

## Scope

Woodpecker's first run on this repository (pipeline 1, on BC-16's branch) failed in the `check` step with `./.ci/install-tools.sh: 12: curl: not found`: the image `eclipse-temurin:25-jdk` ships neither `curl` nor `python3`, and `just check` needs `python3` for `tools/map.py --check` and the tool tests. The installer gains an idempotent apt step for `curl`, `python3` and `ca-certificates` before it downloads `just`; POSIX `sh` throughout, since the image runs it with `sh`.

## Approach

Change only `.ci/install-tools.sh`; push the branch and read Woodpecker's verdict on it through its API before merging; the same fix goes into `create_metered_motor`'s scaffold (MM-1, in flight).

## Acceptance criteria

- [x] Woodpecker pipeline 6 on the branch: success in 98 s, lint, map, unit and tool tests, and all 14 game tests on the runner (pipelines 4 and 5 found the next two missing tools: just's bash installer and git). Merged through a Forgejo pull request into `development`, verified against `origin/development` (2026-09-19).

## Constraints and prior findings

Woodpecker 3.18.0 at `woodpecker.cubealgos.de`; logs need a user API token (one per account); the token lives in `~/.config/woodpecker/token` on Kevin's machine, never in a repo.
