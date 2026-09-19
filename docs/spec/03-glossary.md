---
title: "create_brass_compass spec — glossary"
type: "spec"
category: "create_brass_compass"
---

# 03 — Glossary

| Term | Means |
|---|---|
| **compass** | Vanilla's item: points to the world spawn, or to one lodestone once bound. |
| **brass compass** | This mod's item: keeps any number of lodestone entries and points at the chosen one. |
| **lodestone** | Vanilla's block a compass can be bound to. |
| **entry** | One saved lodestone on a brass compass: position, dimension, name, and whether it was last seen present. |
| **destination** | The entry the needle currently points at; at most one. |
| **tracker** | Vanilla's `lodestone_tracker` data component: target position and whether it is tracked; what the needle model reads. |
| **lost** | An entry whose lodestone block is no longer there; the entry stays until removed. |
| **switch screen** | The list of entries, opened by right-clicking anywhere but a lodestone. |
| **add/edit screen** | The one-entry form, opened by right-clicking a lodestone or from the switch screen. |
| **needle** | The item model's frame, chosen by vanilla from the tracker; nothing this mod draws. |
