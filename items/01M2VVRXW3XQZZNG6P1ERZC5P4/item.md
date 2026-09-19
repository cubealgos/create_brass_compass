---
schema_version: 1
id: 01M2VVRXW3XQZZNG6P1ERZC5P4
key: BC-10
type: bug
title: "Hotfix 1.0.1+26.2: the switch screen's empty-state line overflows the panel"
created_by: kevin
created_at: 2026-09-19T03:36:17Z
---

## Scope

Found by Kevin playtesting 1.0.0+26.2 (2026-09-19): with no entries, the switch screen's line "No lodestones saved here. Right-click one." is wider than the 192-pixel panel and runs past the frame (`UI-REQ-005`). Hotfix from `production` per `07-releases.md`: `hotfix/*`, patch bump to 1.0.1+26.2, changelog entry, tag, `just release`, merge into `production` and back into `development`.

## Approach

Wrap the empty-state text with the font's own line splitting to the panel's inner width and centre each line; no string change needed, so translations stay valid. Guard against any other long string the same way where a single line is drawn on the panel.

## Acceptance criteria

- [x] The empty state wraps inside the panel (Kevin's client check on the hotfix build, 2026-09-19: "seems like it all works now"). Two more findings from the same playtest fixed here: the remove X is one red in both screens; a fresh compass keeps its creative tab line in the tooltip (the untracked tracker is a default component, so the stack equals the tab entry).
- [x] Version 1.0.1+26.2, CHANGELOG.md entries, `just check` green (12 game tests), `just release` at tag `v1.0.1+26.2` (83a1e11) wrote `dist/`, SHA-256 `9ea085c654e55567ced772743dacfc226a59dacc45de17fcfcbe13f927935159`.
- [x] Merged into `production` and back into `development` at finish; `dist/` copied to the main checkout for Kevin, 2026-09-19.

## Constraints and prior findings

The panel is `STOCK_KEEPER_CATEGORY` (192 wide; frame columns 3..189 usable). `Font.split(Component, int)` gives wrapped lines; `GuiGraphicsExtractor.text(Font, FormattedCharSequence, int, int, int, boolean)` draws one.
