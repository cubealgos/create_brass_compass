# Modrinth listing (paste-ready)

## Project settings

| Field | Value |
|---|---|
| Name | Create Fly: Brass Compass |
| Slug | `brass-compass` |
| Summary | A brass compass that remembers every lodestone it has been shown and points at the one you choose. |
| Categories | Utility, Equipment (secondary: Adventure) |
| Licence | MIT |
| Client side | Required |
| Server side | Required |
| Loaders | Fabric |
| Game versions | 26.2 |
| Dependencies | Create Fly (required), Fabric API (required) |
| Icon | `icon.png` in this folder: the compass on the round blueprint badge Create add-ons share (`just icon` regenerates it) |
| Links | none yet (no public repository); support and reports through the Modrinth page's issue link once a tracker exists |

## Version settings

| Field | Value |
|---|---|
| Version number | `1.0.1+26.2` |
| Version title | Brass Compass 1.0.1 for Minecraft 26.2 |
| Channel | Release |
| File | `dist/create_brass_compass-1.0.1+26.2.jar` |
| Changelog | paste `dist/release-notes-1.0.1+26.2.md` |

## Body

A regular compass binds to one lodestone. A brass compass has no such limit.

### What it does

- **Save a lodestone.** Right-click it with the compass, give it a name, confirm. The needle swings to it and the compass glints, as a bound compass does.
- **Switch destinations.** Right-click anywhere else to open the list for the dimension you are in. Each row shows the name and the distance in blocks; the green check points the needle there, the yellow pencil renames, the red X forgets it.
- **Per dimension.** Entries in the Nether stay in the Nether. In each dimension the compass points at your choice there, and spins where nothing is chosen.
- **Lost and found.** Break a saved lodestone and its row goes red and the needle spins; place it back and the compass finds it again.
- **Stays out of the way.** Sneak and the right-click is a plain right-click.
- **Hand it over.** Every saved lodestone travels with the item.

### Crafting

A compass ringed by eight brass ingots in the crafting grid, or a compass and a precision mechanism together, shapeless. The brass compass sits in Create's own creative tab.

### Made for Create

The screens are built from Create Fly's own frames, textures and icons, so the compass looks like the rest of your workshop. It adds one item and nothing else: no blocks, no world generation, no new mechanics to learn.

### Privacy

Nothing leaves your machine. No telemetry, no update checks, no network calls of its own. The mod keeps only the lodestone positions and the names you typed, on the item itself.

### Requirements

Minecraft 26.2, Fabric Loader 0.19.5 or newer, Fabric API 0.160.0 or newer, and Create Fly 6.0.9-1 (the build this version was tested with; the mod declares exactly that version).

### Support

Through the issue tracker only, as time allows. Include your Minecraft, Fabric and Create Fly versions, the mod version from the jar name, and the steps that show the problem. MIT licensed.
