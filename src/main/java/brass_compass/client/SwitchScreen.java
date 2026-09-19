package brass_compass.client;

import brass_compass.BrassCompass;
import brass_compass.ui.SwitchListing;
import brass_compass.ui.SwitchMenu;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The switch screen on Create Fly's stock-keeper <em>categories</em> layout (UI-DEC-001): the
 * header names the dimension, the brown panel holds one tan entry row per waypoint with an icon,
 * the name, the distance and three actions on the right: a green check that chooses and closes, a
 * yellow pencil that opens the edit screen, and the row's own X that removes (UI-REQ-004,
 * UI-REQ-011). No footer: the panel closes with the frame's bottom edge. The wheel scrolls.
 */
public final class SwitchScreen extends AbstractSimiContainerScreen<SwitchMenu> {
    private static final AllGuiTextures HEADER = AllGuiTextures.STOCK_KEEPER_CATEGORY_HEADER;
    private static final AllGuiTextures PANEL = AllGuiTextures.STOCK_KEEPER_CATEGORY;
    private static final AllGuiTextures ENTRY = AllGuiTextures.STOCK_KEEPER_CATEGORY_ENTRY;
    /** Two 8x8 glyphs drawn to the row's own X: same 7-pixel body, same two-pixel stroke, same baseline. */
    private static final Identifier CHECK = BrassCompass.id("textures/gui/check.png");
    private static final Identifier PENCIL = BrassCompass.id("textures/gui/pencil.png");
    /** The row texture bakes its X in brown; the same glyph is drawn over it in red (Kevin, 2026-09-19: one red X everywhere). */
    private static final Identifier X = BrassCompass.id("textures/gui/x.png");
    private static final int GLYPH = 8;
    private static final int GLYPH_Y = 4;
    /** The frame's bottom edge: the first two rows of the categories footer, dark line and highlight. */
    private static final int EDGE_U = 32;
    private static final int EDGE_V = 80;
    private static final int EDGE_H = 2;
    static final int PANELS = 5;
    static final int ROW_STRIDE = 20;
    private static final int ROW_LEFT = 7;
    /** The panel's usable inner margin on each side, past the frame's edge. */
    private static final int PANEL_INSET = 10;
    private static final int FIRST_ROW_TOP = 25;
    private static final int ROW_ICON_X = 14;
    private static final int ROW_TEXT_X = 35;
    /** Glyph columns inside the row: the X sits at 159..165, the others keep its 14-pixel pitch. */
    private static final int CHECK_X = 131;
    private static final int PENCIL_X = 145;
    private static final int X_GLYPH = 159;
    /** Click bands, each centred on its glyph. */
    private static final int CHECK_HIT = CHECK_X - 4;
    private static final int PENCIL_HIT = PENCIL_X - 4;
    private static final int X_HIT = X_GLYPH - 4;
    private static final int COLOUR_HEADER = 0xFF3D3C48;
    private static final int COLOUR_ROW = 0xFF656565;
    private static final int COLOUR_ROW_CHOSEN = 0xFF3D3C48;
    private static final int COLOUR_ROW_LOST = 0xFFA04040;
    private static final int COLOUR_ON_PANEL = 0xFFEEEEEE;
    private static final int COLOUR_CHECK = 0xFF4FB05A;
    private static final int COLOUR_PENCIL = 0xFFE8B84A;
    public static final int COLOUR_X = 0xFFC94C4C;

    private int scroll;

    public SwitchScreen(SwitchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, PANEL.getWidth(), HEADER.getHeight() + PANELS * PANEL.getHeight() + EDGE_H);
    }

    public static SwitchScreen create(Minecraft minecraft, MenuType<SwitchListing> type, int syncId, Inventory inventory, Component title, RegistryFriendlyByteBuf buf) {
        SwitchListing listing = SwitchListing.STREAM_CODEC.decode(buf);
        return new SwitchScreen(new SwitchMenu(syncId, inventory, listing), inventory, title);
    }

    @Override
    protected void init() {
        setWindowOffset(0, 0);
        super.init();
    }

    private List<SwitchListing.Row> rows() {
        return menu.listing().rows();
    }

    private int listTop() {
        return topPos + HEADER.getHeight();
    }

    private int listBottom() {
        return listTop() + PANELS * PANEL.getHeight();
    }

    private int rowTop(int index) {
        return topPos + FIRST_ROW_TOP + (index - scroll) * ROW_STRIDE;
    }

    private int visibleRows() {
        return (PANELS * PANEL.getHeight() - (FIRST_ROW_TOP - HEADER.getHeight())) / ROW_STRIDE;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int y = topPos;
        HEADER.render(graphics, leftPos, y);
        Component heading = Component.translatable("screen.brass_compass.dimension", dimensionName());
        graphics.text(font, heading, leftPos + PANEL.getWidth() / 2 - font.width(heading) / 2, y + 4, COLOUR_HEADER, false);
        y += HEADER.getHeight();
        for (int i = 0; i < PANELS; i++) {
            PANEL.render(graphics, leftPos, y);
            y += PANEL.getHeight();
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, PANEL.getLocation(), leftPos, y, EDGE_U, EDGE_V, PANEL.getWidth(), EDGE_H, 256, 256);

        if (rows().isEmpty()) {
            // UI-REQ-005: wrapped to the panel's inner width and centred, whatever the translation's length.
            int line = listTop() + 12;
            for (FormattedCharSequence text : font.split(Component.translatable("screen.brass_compass.empty"), PANEL.getWidth() - 2 * PANEL_INSET)) {
                graphics.text(font, text, leftPos + PANEL.getWidth() / 2 - font.width(text) / 2, line, COLOUR_ON_PANEL, false);
                line += font.lineHeight + 2;
            }
            return;
        }
        graphics.enableScissor(leftPos + 3, listTop() - 2, leftPos + PANEL.getWidth() - 5, listBottom() - 1);
        for (int index = scroll; index < rows().size() && rowTop(index) < listBottom(); index++) {
            renderRow(graphics, index, rows().get(index));
        }
        graphics.disableScissor();
    }

    private void renderRow(GuiGraphicsExtractor graphics, int index, SwitchListing.Row row) {
        int x = leftPos + ROW_LEFT;
        int y = rowTop(index);
        ENTRY.render(graphics, x, y);
        boolean chosen = index == menu.listing().chosenRow();
        ItemStack icon = new ItemStack(chosen ? BrassCompass.BRASS_COMPASS : Items.LODESTONE);
        graphics.item(icon, x + ROW_ICON_X, y + 1);
        int colour = !row.present() ? COLOUR_ROW_LOST : chosen ? COLOUR_ROW_CHOSEN : COLOUR_ROW;
        String name = row.present() ? row.name() : row.name() + " " + Component.translatable("screen.brass_compass.lost").getString();
        Component distance = Component.translatable("screen.brass_compass.distance", row.distance());
        int distanceWidth = font.width(distance);
        int distanceX = x + CHECK_HIT - 4 - distanceWidth;
        String shown = font.plainSubstrByWidth(name, distanceX - 4 - (x + ROW_TEXT_X));
        graphics.text(font, Component.literal(shown), x + ROW_TEXT_X, y + 5, colour, false);
        graphics.text(font, distance, distanceX, y + 5, colour, false);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHECK, x + CHECK_X, y + GLYPH_Y, 0, 0, GLYPH, GLYPH, GLYPH, GLYPH, COLOUR_CHECK);
        graphics.blit(RenderPipelines.GUI_TEXTURED, PENCIL, x + PENCIL_X, y + GLYPH_Y, 0, 0, GLYPH, GLYPH, GLYPH, GLYPH, COLOUR_PENCIL);
        graphics.blit(RenderPipelines.GUI_TEXTURED, X, x + X_GLYPH - 1, y + GLYPH_Y, 0, 0, GLYPH, GLYPH, GLYPH, GLYPH, COLOUR_X);
    }

    private String dimensionName() {
        String id = menu.listing().dimension();
        String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        String key = "screen.brass_compass.dimension." + path;
        String text = Component.translatable(key).getString();
        return text.equals(key) ? path.replace('_', ' ') : text;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int max = Math.max(0, rows().size() - visibleRows());
        scroll = (int) Math.max(0, Math.min(max, scroll - Math.signum(scrollY)));
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return super.mouseClicked(event, doubleClick);
        }
        int x = leftPos + ROW_LEFT;
        for (int index = scroll; index < rows().size() && rowTop(index) < listBottom(); index++) {
            int top = rowTop(index);
            if (event.x() < x + CHECK_HIT || event.x() >= x + ENTRY.getWidth() || event.y() < top || event.y() >= top + ENTRY.getHeight()) {
                continue;
            }
            double rel = event.x() - x;
            if (rel >= X_HIT) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, SwitchMenu.REMOVE + index);
            } else if (rel >= PENCIL_HIT) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, SwitchMenu.EDIT + index);
            } else {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, index);
                onClose();
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
