#!/usr/bin/env python3
"""Render docs/modrinth/icon.png: the brass compass on the cubealgos navy badge Create add-ons share.

The palette is the cubealgos navy: a white rim, a pale band, a navy disc `#0d1226` with its edge
darkened to `#090c1b`, and a blueprint grid lifted to `#344c80` so it still reads on the navy. The
sprite is frame 00 of the needle, scaled without smoothing to a 320 px fit box, with a one-pixel
white outline and a soft shadow scaled to match. Requires Pillow.
"""
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

SIZE = 512
CENTRE = SIZE // 2
SUPERSAMPLE = 4
RIM = (255, 255, 255, 255)
BAND = (232, 236, 244, 255)
RING = (9, 12, 27, 255)
BLUEPRINT = (13, 18, 38, 255)
GRID = (52, 76, 128, 255)
OUTLINE = (255, 255, 255, 235)
SHADOW = (20, 50, 90, 130)
SPRITE = Path("src/main/resources/assets/brass_compass/textures/item/brass_compass_00.png")
OUT = Path("docs/modrinth/icon.png")
BOX = 320


def badge() -> Image.Image:
    big = SIZE * SUPERSAMPLE
    img = Image.new("RGBA", (big, big), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for radius, colour in ((256, RIM), (250, BAND), (238, RING), (200, BLUEPRINT)):
        r = radius * SUPERSAMPLE
        c = CENTRE * SUPERSAMPLE
        draw.ellipse((c - r, c - r, c + r, c + r), fill=colour)
    img = img.resize((SIZE, SIZE), Image.LANCZOS)

    grid = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    g = ImageDraw.Draw(grid)
    for k in range(-4, 5):
        p = CENTRE + k * 48
        g.line((p, 0, p, SIZE), fill=GRID, width=3)
        g.line((0, p, SIZE, p), fill=GRID, width=3)
    glow = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    ImageDraw.Draw(glow).ellipse((CENTRE - 120, CENTRE - 120, CENTRE + 120, CENTRE + 120), fill=(34, 48, 92, 150))
    glow = glow.filter(ImageFilter.GaussianBlur(50))
    inner = Image.alpha_composite(glow, grid)
    mask = Image.new("L", (SIZE, SIZE), 0)
    ImageDraw.Draw(mask).ellipse((CENTRE - 238, CENTRE - 238, CENTRE + 238, CENTRE + 238), fill=255)
    clipped = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    clipped.paste(inner, (0, 0), mask)
    return Image.alpha_composite(img, clipped)


def subject(img: Image.Image) -> Image.Image:
    raw = Image.open(SPRITE).convert("RGBA")
    w, h = raw.size
    factor = max(1, BOX // max(w, h))
    size = (w * factor, h * factor)
    sprite = raw.resize(size, Image.NEAREST)
    alpha = sprite.getchannel("A")
    x = CENTRE - size[0] // 2
    y = CENTRE - size[1] // 2
    step = factor
    grown = Image.new("L", (SIZE, SIZE), 0)
    for dx in (-step, 0, step):
        for dy in (-step, 0, step):
            grown.paste(alpha, (x + dx, y + dy), alpha)
    shadow = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    shadow.paste(SHADOW, (0, 0), grown.transform(grown.size, Image.AFFINE, (1, 0, -14, 0, 1, -14)))
    shadow = shadow.filter(ImageFilter.GaussianBlur(10))
    outline = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    outline.paste(OUTLINE, (0, 0), grown)
    img = Image.alpha_composite(img, shadow)
    img = Image.alpha_composite(img, outline)
    img.alpha_composite(sprite, (x, y))
    return img


def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    subject(badge()).save(OUT, optimize=True)
    print(f"wrote {OUT} ({OUT.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
