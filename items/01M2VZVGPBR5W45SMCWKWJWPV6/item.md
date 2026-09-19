---
schema_version: 1
id: 01M2VZVGPBR5W45SMCWKWJWPV6
key: BC-15
type: feat
title: "Development-only /brass_compass debug command: a compass with realistic mock destinations for UI checks and screenshots"
created_by: kevin
created_at: 2026-09-19T04:47:36Z
---

## Scope

Kevin (2026-09-19), while taking gallery screenshots: lodestones all within ten metres look unreal, and a hand-typed `/give` with the component exceeds chat's 256-character cap. A command `/brass_compass debug [count]` fills the brass compass in the main hand (or gives a new one) with `count` entries (default 5, at most 32) at realistic bearings and distances from the player, named from a fixed list, one chosen, the last one lost, plus one entry in the other dimension. Registered only when Fabric reports a development environment (Kevin's ruling: the release jar has no command, the mod stays one item).

## Approach

`brass_compass.debug.DebugCommand` on Fabric's command API, wired from the entrypoint behind `FabricLoader.isDevelopmentEnvironment()`. The entry builder is a plain method the game test calls directly and the command wraps; the test also parses and runs the command through the server's dispatcher to prove the registration. Feedback is a translation key. The spec's testing page records the tool.

## Acceptance criteria

- [x] Game test: the builder yields `count` entries around a mock player at 150 metres or more, one chosen, the last one lost, one in the other dimension; the command parses and executes on the development server (`DebugCommandGameTest`, 14 game tests pass); the entrypoint registers it only behind `FabricLoader.isDevelopmentEnvironment()`, reviewed 2026-09-19.
- [x] `just check` green; merged through a Forgejo pull request into `development`, verified against `origin/development` (pull request #4 was recorded merged without the branch moving; #5 landed). Kevin's screenshots are the gallery step of the Modrinth page.

## Constraints and prior findings

Chat input is capped at 256 characters; a component-bearing `/give` for five entries is about 600. Far entries stay `present` because their chunks are not loaded, so the tick never inspects them.
