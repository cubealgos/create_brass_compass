# create_brass_compass

A Create Fly add-on for Minecraft 26.2 on Fabric: a brass compass with any number of saved
lodestones and a chosen destination per dimension, two screens on Create Fly's GUI framework.

**This file routes. It does not hold content.** The specification is `docs/spec/`.

## Read this before you do that

| about to… | read first |
|---|---|
| anything at all | `docs/spec/README.md`, then the one domain file you need |
| find where something lives | `docs/map.md`; generated, never edited |
| touch `brass_compass.destinations` | it has no Minecraft imports; the build's `verifyPurePackage` enforces it |
| touch the component | `docs/spec/contracts/data-contract.md`: versioned, forward-only migrations |
| add a screen or packet | `docs/spec/domains/ui.md`, `docs/spec/04-architecture.md` `ARCH-DEC-002`: the server validates every edit against the held item |
| add a dependency | `docs/spec/decisions/DEC-003-licence.md` (MIT) and heimathafen's dependency policy |
| commit | scope `create_brass_compass`, the ticket key (`BC-N`) in the subject |

## Working here

```
kontor claim BC-N
kontor branch new BC-N <slug>
just check
```

`just --list` shows the task surface; `just spec-sync` refreshes `docs/spec/` from the vault; `just map` regenerates the map.

## Standing rules

- The spec is authoritative; `docs/spec/` is a copy of heimathafen's vault.
- A design question the spec does not answer is asked, never decided inline.
- Nothing leaves the player's machine: no telemetry, no network calls (`docs/spec/operations/compliance.md`).
- Always keep a playable build: `just client` boots with Create Fly at every merge.
