# Changelog

All notable changes to this project are recorded here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/); the version scheme is
`<mod SemVer>+<minecraft version>`.

## [1.0.0+26.2] - 2026-09-19

Tested with Minecraft 26.2, Fabric Loader 0.19.5, Fabric API 0.160.0 and Create Fly
26.2-rc-2-6.0.9-1.

### Added

- The brass compass item: stackable to one, crafted from a compass ringed by eight brass ingots or
  shapeless from a compass and a precision mechanism, listed in Create's base tab.
- Right-click a lodestone to save it under a name of up to 32 characters; the same place opens
  its existing entry for renaming or removal.
- Right-click anywhere else to open the destination list for the dimension you are in, with the
  distance to each lodestone; a check points the needle there, a pencil edits, an X removes.
- The needle is vanilla's: it follows the chosen lodestone per dimension, spins when nothing is
  chosen or the lodestone is gone, and comes back when the lodestone is placed again.
- The enchantment glint while the needle has a target, drawn the way vanilla draws it for
  compasses.
- Sneaking passes the right-click through, as if the hand were empty.
- Screens on Create Fly's own frames and textures; every string is a translation key.
