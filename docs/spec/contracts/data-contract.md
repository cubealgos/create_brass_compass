---
title: "create_brass_compass spec — data contract: the destinations component"
type: "spec"
category: "create_brass_compass"
---

# Data contract (`DATA`)

## The component

`brass_compass:destinations`, a data component on the item stack, codec-serialised into the world
save like any vanilla component.

```
{
  version: 1,
  current: { "minecraft:overworld": index, … },   // chosen entry per dimension; absent means none
  entries: [ { dimension: "minecraft:overworld", x, y, z, name: "…", present: true } … ]
}
```

- `version` is the schema version; 1 at 1.0.
- `current` maps a dimension id to an index into `entries` whose entry lies in that dimension; a dimension without a key has no choice.
- `entries` keep insertion order; a position appears at most once.
- `name` is plain text, at most 32 characters, no formatting codes.
- `present` is the last observation; it is refreshed on the held item's server tick.

## Rules

| ID | Rule |
|---|---|
| `DATA-REQ-001` | The component shall carry `version`; a build shall refuse to edit a component with a version newer than its own, keeping it intact and listing it read-only. |
| `DATA-REQ-002` | A version bump shall ship a migration that reads every older version forward; migrations never run backward. |
| `DATA-REQ-003` | A malformed component (codec failure) shall be treated as empty for display and shall not be overwritten until the player saves an entry. |
| `DATA-REQ-004` | The vanilla `lodestone_tracker` component shall always be derivable from `current` and the holder's dimension; it is never the source of truth. |

## Out of scope (sheet §8)

No import from other compass mods; no export. A vanilla compass bound to a lodestone is not
converted when crafted into a brass compass at 1.0 (open for a later version: keep the bound
lodestone as the first entry).
