---
title: "create_brass_compass spec — platform matrix"
type: "spec"
category: "create_brass_compass"
---

# Platform matrix (`PLATFORM`)

| Row | Value | How it is checked |
|---|---|---|
| Minecraft | 26.2 (`~26.2` in `fabric.mod.json`) | `just doctor`, game tests on a dedicated server |
| Fabric Loader | ≥ 0.19.5 | `fabric.mod.json` |
| Fabric API | ≥ 0.160.0 (networking, item events) | `fabric.mod.json` |
| Create Fly | `26.2-rc-2-6.0.9-1`, mod id `create`, `implementation` coordinate `maven.modrinth:create-fly` | `fabric.mod.json` depends `create`; `NOTICE` |
| Java | 25 | `just doctor` |
| Gradle / Loom | 9.5.1 wrapper / 1.17 | wrapper properties |
| Operating systems | macOS, Linux, Windows: the JVM's | not tested separately; nothing native |
| Client and server | Both; screens client-side, all logic server-side | game tests (server), `just client` (client) |

`PLATFORM-REQ-001`: **If** any row moves, **then** `just doctor` fails naming the row.
