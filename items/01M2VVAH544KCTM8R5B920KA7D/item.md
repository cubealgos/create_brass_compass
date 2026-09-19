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

## Security pass (2026-09-19, release/bc-9-v1-0-0-26-2 at 19dd142)

| Area | Finding |
|---|---|
| Dependency audit | Every dependency is pinned to an exact version in `gradle/libs.versions.toml`: Minecraft 26.2, Fabric Loader 0.19.5, Fabric API 0.160.0+26.2, Loom 1.17.21, Create Fly 26.2-rc-2-6.0.9-1, JUnit 6.1.3. Resolved from maven.fabricmc.net, Maven Central and api.modrinth.com/maven (exclusive-content filtered to `maven.modrinth`). Nothing is shaded: the jar holds only `brass_compass/**` classes, the mod's assets and data, `fabric.mod.json` and the manifest. |
| Secrets scan | `git grep` over `development` for key, secret, token, password and private-key markers: nothing. No `.env`, no credentials in the build. |
| New unsafe or FFI surface | None: no mixins, no reflection, no `Unsafe`, no native code. The mod uses Fabric and Create Fly public APIs only. |
| Network surface | One client-to-server payload (`brass_compass:save`), applied only to a brass compass in the sending player's hand, in the player's own dimension, at a lodestone or an existing entry, with the name sanitised and capped at 32 characters; menu button ids are range-checked server-side. No outbound connection of any kind: `SourceSurfaceTest` fails the build on any networking type outside Fabric's packet API. No telemetry, no update check. |
| Supply-chain diff | First release; the whole tree is the diff. Build inputs are the pinned artefacts above plus the Gradle wrapper 9.5.1 and Temurin 25. |
| Data handled | Positions, dimension ids and player-typed names on the item; no player identity, no account data. |
| Residual note | `fabric.mod.json` depends on `create: "*"`, any Create Fly version. A compatibility risk, not a security one; the changelog names the tested version. |

**Verdict**: approve for `production`. No blocking finding.

