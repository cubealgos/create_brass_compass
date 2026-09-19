#!/usr/bin/env python3
"""Make the brass compass's needle frames from vanilla's (BC-3, docs/spec/README.md verification #3).
Reads `assets/minecraft/textures/item/compass_00..31.png` from the Minecraft 26.2 jar in the
Gradle cache, tints the grey body brass and keeps the red needle, and writes the 32 textures, the
32 frame models and the item model under `src/main/resources/assets/brass_compass/`. Standard
library only; run `just needle` after changing the tint. The outputs are committed."""
from __future__ import annotations

import glob
import json
import struct
import sys
import zipfile
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "brass_compass"
JAR_GLOB = str(Path.home() / ".gradle" / "caches" / "fabric-loom" / "minecraftMaven" / "net" / "minecraft" / "minecraft-merged-deobf" / "26.2" / "*.jar")
FRAMES = 32
# A brass tint: grey pixels multiply toward this; saturated (red needle) pixels stay.
BRASS = (0.93, 0.72, 0.36)


def read_png(data: bytes) -> tuple[int, int, list[list[tuple[int, int, int, int]]]]:
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError("not a PNG")
    pos, chunks, palette, trns = 8, {}, None, None
    idat = b""
    while pos < len(data):
        length, kind = struct.unpack(">I4s", data[pos:pos + 8])
        body = data[pos + 8:pos + 8 + length]
        pos += 12 + length
        if kind == b"IHDR":
            chunks["ihdr"] = struct.unpack(">IIBBBBB", body)
        elif kind == b"PLTE":
            palette = [tuple(body[i:i + 3]) for i in range(0, len(body), 3)]
        elif kind == b"tRNS":
            trns = body
        elif kind == b"IDAT":
            idat += body
    w, h, depth, ctype, _, _, interlace = chunks["ihdr"]
    if depth != 8 or interlace != 0:
        raise ValueError(f"unsupported PNG: depth {depth}, interlace {interlace}")
    channels = {0: 1, 2: 3, 3: 1, 4: 2, 6: 4}[ctype]
    raw = zlib.decompress(idat)
    stride = w * channels
    rows, prev, off = [], bytearray(stride), 0
    for _ in range(h):
        filt = raw[off]
        line = bytearray(raw[off + 1:off + 1 + stride])
        off += 1 + stride
        for i in range(stride):
            a = line[i - channels] if i >= channels else 0
            b = prev[i]
            c = prev[i - channels] if i >= channels else 0
            if filt == 1:
                line[i] = (line[i] + a) & 255
            elif filt == 2:
                line[i] = (line[i] + b) & 255
            elif filt == 3:
                line[i] = (line[i] + ((a + b) >> 1)) & 255
            elif filt == 4:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                pr = a if pa <= pb and pa <= pc else (b if pb <= pc else c)
                line[i] = (line[i] + pr) & 255
        rows.append(bytes(line))
        prev = line
    pixels = []
    for line in rows:
        row = []
        for x in range(w):
            px = line[x * channels:(x + 1) * channels]
            if ctype == 6:
                row.append((px[0], px[1], px[2], px[3]))
            elif ctype == 2:
                row.append((px[0], px[1], px[2], 255))
            elif ctype == 3:
                r, g, b = palette[px[0]]
                a = trns[px[0]] if trns and px[0] < len(trns) else 255
                row.append((r, g, b, a))
            elif ctype == 4:
                row.append((px[0], px[0], px[0], px[1]))
            else:
                row.append((px[0], px[0], px[0], 255))
        pixels.append(row)
    return w, h, pixels


def write_png(w: int, h: int, pixels: list[list[tuple[int, int, int, int]]]) -> bytes:
    raw = b"".join(b"\x00" + b"".join(struct.pack("4B", *px) for px in row) for row in pixels)

    def chunk(kind: bytes, body: bytes) -> bytes:
        return struct.pack(">I", len(body)) + kind + body + struct.pack(">I", zlib.crc32(kind + body) & 0xFFFFFFFF)

    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0)) + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")


def tint(px: tuple[int, int, int, int]) -> tuple[int, int, int, int]:
    r, g, b, a = px
    if a == 0:
        return px
    spread = max(r, g, b) - min(r, g, b)
    if spread > 40:  # the needle and anything already coloured stays
        return px
    lum = (r + g + b) / 3 / 255
    return (min(255, round(255 * lum * BRASS[0] * 1.15)), min(255, round(255 * lum * BRASS[1] * 1.15)), min(255, round(255 * lum * BRASS[2] * 1.15)), a)


def main() -> int:
    jars = sorted(glob.glob(JAR_GLOB))
    if not jars:
        print("needle: no Minecraft 26.2 jar in the Gradle cache; run ./gradlew build once", file=sys.stderr)
        return 1
    z = zipfile.ZipFile(jars[0])
    (ASSETS / "textures" / "item").mkdir(parents=True, exist_ok=True)
    (ASSETS / "models" / "item").mkdir(parents=True, exist_ok=True)
    (ASSETS / "items").mkdir(parents=True, exist_ok=True)
    for i in range(FRAMES):
        name = f"compass_{i:02d}"
        w, h, pixels = read_png(z.read(f"assets/minecraft/textures/item/{name}.png"))
        tinted = [[tint(px) for px in row] for row in pixels]
        (ASSETS / "textures" / "item" / f"brass_{name}.png").write_bytes(write_png(w, h, tinted))
        (ASSETS / "models" / "item" / f"brass_{name}.json").write_text(json.dumps({
            "parent": "minecraft:item/generated", "textures": {"layer0": f"brass_compass:item/brass_{name}"}}, indent=2) + "\n")
    vanilla = json.loads(z.read("assets/minecraft/items/compass.json"))

    def rename(node):
        if isinstance(node, dict):
            if node.get("type") == "minecraft:model" and str(node.get("model", "")).startswith("minecraft:item/compass_"):
                node["model"] = "brass_compass:item/brass_" + node["model"].split("/")[-1]
            for v in node.values():
                rename(v)
        elif isinstance(node, list):
            for v in node:
                rename(v)

    rename(vanilla)
    (ASSETS / "items" / "brass_compass.json").write_text(json.dumps(vanilla, indent=2) + "\n")
    print(f"needle: wrote {FRAMES} textures, {FRAMES} models and the item model under {ASSETS.relative_to(ROOT)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
