---
title: "create_brass_compass spec — compliance, security and governance"
type: "spec"
category: "create_brass_compass"
---

# Compliance, security and governance (`COMP`, sheet §6)

A distributed product carries the same obligations, landing in different places
(`workflows/session/start-new-project.md`).

| Area | Position |
|---|---|
| GDPR: what leaves the user's machine | Nothing. No telemetry, no update check, no outbound network call of any kind (`COMP-REQ-001`). Player names never enter the mod's data; entries hold positions and player-typed names on the item. |
| Hosted parts we run | None. Distribution is Modrinth and CurseForge, which host the file and their own pages. |
| Impressumspflicht | Attaches to a public web presence; there is none beyond the platform pages. Revisit if a site exists. |
| Licence and notices | MIT (`decisions/DEC-003-licence.md`); `NOTICE` credits Create Fly (CC0), Create (MIT), Fabric (Apache-2.0). |
| Supply chain and release integrity | Builds from a tagged commit with a pinned wrapper and pinned dependencies; the release checksum is in the release notes; no signing at 1.0 (open below). |
| Vulnerability disclosure | The public issue tracker only; no private channel (Kevin, 2026-09-19: no e-mail address is published). `README.md` and `SUPPORT.md` say so. |
| Server trust boundary | Every client message is validated against the item the player holds (`COMPASS-REQ-007`, `UI-REQ-007`); names are stripped of formatting; positions must hold a lodestone unless the entry exists. A client cannot write arbitrary item data. |
| AI Act, GoBD, sector regulation | Not applicable: no AI component, no financial records, no regulated sector. |

`COMP-REQ-001`: the mod shall make no network call of its own; a game test asserts no socket is
opened by the mod's classes (or the `NOTICE` records why that cannot be tested and a code review
line stands in).

Open: release signing (minisign) before 1.0 or after.
