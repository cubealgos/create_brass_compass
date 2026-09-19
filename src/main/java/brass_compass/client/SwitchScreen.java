package brass_compass.client;

import brass_compass.ui.SwitchListing;
import brass_compass.ui.SwitchMenu;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The switch screen on Create Fly's stock-keeper frame (UI-DEC-001): a header naming the
 * dimension, one body strip per visible row with the entry's name and distance, a footer. The
 * wheel scrolls, a click on a row chooses it through a menu button click and closes.
 *
 * <p>The frame occupies columns 24..231 of the 256-wide textures: a light title strip, then a dark
 * brown panel for the header's lower part and the body, and a tan footer. Text colours follow that.
 */
public final class SwitchScreen extends AbstractSimiContainerScreen<SwitchMenu> {
    private static final AllGuiTextures HEADER = AllGuiTextures.STOCK_KEEPER_REQUEST_HEADER;
    private static final AllGuiTextures BODY = AllGuiTextures.STOCK_KEEPER_REQUEST_BODY;
    private static final AllGuiTextures FOOTER = AllGuiTextures.STOCK_KEEPER_REQUEST_FOOTER;
    static final int VISIBLE_ROWS = 6;
    private static final int FRAME_LEFT = 24;
    private static final int FRAME_RIGHT = 232;
    private static final int TEXT_LEFT = FRAME_LEFT + 8;
    private static final int TEXT_RIGHT = FRAME_RIGHT - 8;
    private static final int COLOUR_TITLE = 0xFF592424;
    private static final int COLOUR_ON_PANEL = 0xFFCDB8A8;
    private static final int COLOUR_CHOSEN = 0xFFFFD27A;
    private static final int COLOUR_LOST = 0xFFE08A8A;
    private static final int COLOUR_ON_FOOTER = 0xFF4A3628;

    private int scroll;

    public SwitchScreen(SwitchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, HEADER.getWidth(), HEADER.getHeight() + rowsShown(menu) * BODY.getHeight() + FOOTER.getHeight());
    }

    public static SwitchScreen create(Minecraft minecraft, MenuType<SwitchListing> type, int syncId, Inventory inventory, Component title, RegistryFriendlyByteBuf buf) {
        SwitchListing listing = SwitchListing.STREAM_CODEC.decode(buf);
        return new SwitchScreen(new SwitchMenu(syncId, inventory, listing), inventory, title);
    }

    private static int rowsShown(SwitchMenu menu) {
        return Math.max(1, Math.min(VISIBLE_ROWS, menu.listing().rows().size()));
    }

    @Override
    protected void init() {
        setWindowOffset(0, 0);
        super.init();
    }

    private List<SwitchListing.Row> rows() {
        return menu.listing().rows();
    }

    private int rowTop(int visibleIndex) {
        return topPos + HEADER.getHeight() + visibleIndex * BODY.getHeight();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int y = topPos;
        HEADER.render(graphics, leftPos, y);
        graphics.text(font, title, leftPos + TEXT_LEFT, y + 4, COLOUR_TITLE, false);
        graphics.text(font, Component.translatable("screen.brass_compass.dimension", dimensionName()), leftPos + TEXT_LEFT, y + 22, COLOUR_ON_PANEL, false);
        y += HEADER.getHeight();
        int shown = rowsShown(menu);
        for (int i = 0; i < shown; i++) {
            BODY.render(graphics, leftPos, y);
            int index = scroll + i;
            if (index < rows().size()) {
                SwitchListing.Row row = rows().get(index);
                boolean chosen = index == menu.listing().chosenRow();
                int colour = !row.present() ? COLOUR_LOST : chosen ? COLOUR_CHOSEN : COLOUR_ON_PANEL;
                String marker = chosen ? "> " : "  ";
                String suffix = row.present() ? "" : " " + Component.translatable("screen.brass_compass.lost").getString();
                graphics.text(font, Component.literal(marker + row.name() + suffix), leftPos + TEXT_LEFT, y + 6, colour, false);
                Component distance = Component.translatable("screen.brass_compass.distance", row.distance());
                graphics.text(font, distance, leftPos + TEXT_RIGHT - font.width(distance), y + 6, colour, false);
            } else if (rows().isEmpty() && i == 0) {
                graphics.text(font, Component.translatable("screen.brass_compass.empty"), leftPos + TEXT_LEFT, y + 6, COLOUR_ON_PANEL, false);
            }
            y += BODY.getHeight();
        }
        FOOTER.render(graphics, leftPos, y);
        if (rows().size() > shown) {
            graphics.text(font, Component.translatable("screen.brass_compass.scroll", scroll + 1, Math.min(rows().size(), scroll + shown), rows().size()),
                leftPos + TEXT_LEFT, y + 8, COLOUR_ON_FOOTER, false);
        }
    }

    private String dimensionName() {
        String id = menu.listing().dimension();
        String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
        String key = "screen.brass_compass.dimension." + path;
        Component known = Component.translatable(key);
        String text = known.getString();
        return text.equals(key) ? path.replace('_', ' ') : text;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int max = Math.max(0, rows().size() - rowsShown(menu));
        scroll = (int) Math.max(0, Math.min(max, scroll - Math.signum(scrollY)));
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int shown = rowsShown(menu);
        for (int i = 0; i < shown; i++) {
            int index = scroll + i;
            if (index >= rows().size()) {
                break;
            }
            int top = rowTop(i);
            if (event.x() >= leftPos + FRAME_LEFT && event.x() < leftPos + FRAME_RIGHT && event.y() >= top && event.y() < top + BODY.getHeight()) {
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, index);
                onClose();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
