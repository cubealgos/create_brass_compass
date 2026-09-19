---
schema_version: 1
id: 01M2VM622ZCS4KFQSW7HGTGY1Q
key: BC-7
type: test
title: "Game-test sweep and the playable check: every COMPASS and UI requirement named by a test, hand-over between players, no network calls"
created_by: kevin
created_at: 2026-09-19T01:23:38Z
---

## Scope

`TEST-REQ-001`: a table in the ticket mapping every `COMPASS-REQ` and `UI-REQ` to its test; hand-over between two mock players (`UC-005`); `COMP-REQ-001` no network call (code review line recorded). The client checklist run once by Kevin.

Kevin's first client look (2026-09-19) came back with three changes, done here: the add/edit screen becomes a dialog composed from the stock keeper's request window (title strip, brown panel, the package-address label as the name field, the grey send arrow as save); the switch rows get a check icon to choose and a pencil icon to edit beside the X instead of left and right clicks; the item glints while the needle has a target, like a bound vanilla compass (`COMPASS-REQ-016`).

## Approach

One table in the ticket, one test class per domain, two mock server players for the hand-over; the network claim is a code-review line because the mod opens no socket anywhere.

## Requirement to test (`TEST-REQ-001`)

| Requirement | Test |
|---|---|
| `COMPASS-REQ-001` item, stack of one | `SmokeGameTest.theModLoadsBesideCreateFly`, `RecipeGameTest` (both recipes yield it) |
| `COMPASS-REQ-002` use on lodestone opens edit | `EditGameTest.useOnALodestoneOpensTheEditMenuUnlessSneaking` |
| `COMPASS-REQ-003` use elsewhere opens switch | `EditGameTest.useOnALodestoneOpensTheEditMenuUnlessSneaking` (stone passes through to `use`); the open itself needs a client, Kevin's checklist |
| `COMPASS-REQ-004` save stores, chooses, sound | `EditGameTest.theSavePacketWritesOnlyToTheHeldCompassAtALodestone`; the sound is a world event and is heard in the client check |
| `COMPASS-REQ-005` tracker follows the choice | `NeedleGameTest.theNeedleFollowsTheChoiceAndNoticesALostLodestone` |
| `COMPASS-REQ-006` nothing chosen: untracked | `NeedleGameTest` ("nothing chosen: untracked, the needle spins") |
| `COMPASS-REQ-007` refuse not-in-hand or missing entry | `EditGameTest` (other item in hand), `SwitchGameTest` (out of range, compass gone), `HandoverGameTest` (previous holder) |
| `COMPASS-REQ-008` lost mark | `NeedleGameTest`, `EditGameTest` (rename of a lost entry) |
| `COMPASS-REQ-009` revived mark | `NeedleGameTest` ("placed back, the entry is present again") |
| `COMPASS-REQ-010` same place opens the existing entry | `DestinationsTest.savingAppendsAndChoosesAndSavingThePlaceAgainReturnsTheExistingEntry`, `EditGameTest` (rename keeps one entry) |
| `COMPASS-REQ-011` other dimension stored and pointed like vanilla | `DestinationsTest.theChoiceIsPerDimensionAndAChoiceAcrossDimensionsIsRefused`, `SwitchGameTest` (nether entry kept, not listed); the spin in another dimension is vanilla's tracker behaviour |
| `COMPASS-REQ-012` tooltip | `HandoverGameTest` (no destination, then the destination's key) |
| `COMPASS-REQ-013` datapack recipes | `RecipeGameTest` asserts through the recipe manager, which is what a datapack overrides |
| `COMPASS-REQ-014` sneaking passes through | `SwitchGameTest.sneakingPassesTheUseThrough`, `EditGameTest` (sneaking on a lodestone) |
| `COMPASS-REQ-015` per-dimension choice | `DestinationsTest`, `SwitchGameTest.theListingIsPerDimensionAndChoosingARowMovesTheNeedle` |
| `COMPASS-REQ-016` glint with a target | `HandoverGameTest` (`isFoil` after choosing) |
| `UI-REQ-001` Create's framework and textures | Structural: `SwitchScreen`/`EditScreen` extend `AbstractSimiContainerScreen` and draw only `AllGuiTextures` regions; look is Kevin's checklist |
| `UI-REQ-002` per-dimension rows, saved order, dimension indicator | `SwitchGameTest` (rows and `dimension()`); scrolling is client-only, Kevin's checklist |
| `UI-REQ-003` current and lost marked | `SwitchListing.Row.present` and `chosenRow` asserted in `SwitchGameTest`; the colours and icons are Kevin's checklist |
| `UI-REQ-004` check chooses and closes | `SwitchGameTest` (menu button chooses); closing is client-only |
| `UI-REQ-005` empty state | `HandoverGameTest` (fresh compass lists nothing); the text is Kevin's checklist |
| `UI-REQ-006` name field, confirm, remove | `EditGameTest.removingThroughTheMenuClearsTheChoice`; the field's 32-char cap is `Names.MAX_LENGTH`, tested in `DestinationsTest.namesAreSanitisedBoundedAndDefaulted` |
| `UI-REQ-007` one packet, applied only to the hand | `EditGameTest`, `HandoverGameTest` |
| `UI-REQ-008` every string a key with `en_us` | `SourceSurfaceTest.everyTranslationKeyNamedInCodeHasAnEnglishEntry` |
| `UI-REQ-009` item changed: nothing applied, menu invalid | `SwitchGameTest` (compass gone), `HandoverGameTest` (`stillValid` false for the previous holder) |
| `UI-REQ-010` name and distance per row | `SwitchGameTest` (distance of the row under the holder) |
| `UI-REQ-011` check, pencil, X | `SwitchGameTest` (`EDIT` and `REMOVE` button ranges, list reopened); the icons are Kevin's checklist |
| `COMP-REQ-001` no network call | `SourceSurfaceTest.noNetworkingTypeIsReferencedByTheMod` (static scan of the sources); code-review line: the only networking types are Fabric's play-payload API, whose one packet is client-to-server inside the game connection |
| `TEST-REQ-002` deliberate-break proof | Done once under BC-1 (`verifyPurePackage`) |

## Acceptance criteria

- [x] Every requirement has a named test or a recorded reason it cannot be tested headless (table above, 2026-09-19).
- [x] `just check` green three runs in a row (2026-09-19, 12 game tests, unit tests, map and spec copy each time).
- [x] Kevin's client checklist done and noted here. 2026-09-19, five passes: needle, tab, both screens, glint. Changes asked and done: address-style dialog, then slimmer dialog with centred rows and a capped label, row glyphs sized to the X, footer-less list, tracker written only on change, the compasses tag for the special glint, glyphs as the dialog's buttons, the X in cream. Kevin: "apart from that everything seems to work fine now".

## Constraints and prior findings

`docs/spec/operations/testing.md`.
