---
title: "create_brass_compass spec — testing"
type: "spec"
category: "create_brass_compass"
---

# Testing (`TEST`)

| Layer | What | Where |
|---|---|---|
| Unit | The component's codec, migrations, entry rules (unique position, name limits, current index bounds); no Minecraft needed for the pure parts, which live in a package with no Minecraft imports and a build check like `create_civilization`'s `verifyPureCore` | `src/test` |
| Game tests | Save at a lodestone, switch, rename, remove, lost and revived, hand-over between two mock players, refusal of an edit for an item not in hand, recipes yield the item, the tracker follows the current entry | `src/gametest`, Loom `runGameTest` |
| Client | Screens open and close; a manual check in `just client` per release (screens cannot be game-tested headless) | release checklist |

`TEST-REQ-001`: every `COMPASS-REQ` and `UI-REQ` names its test in the ticket that implements it.
`TEST-REQ-002`: a deliberate-break proof for the pure-package check, once.
