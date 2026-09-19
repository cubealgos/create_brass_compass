package brass_compass.client;

import brass_compass.destinations.Names;
import brass_compass.network.SavePayload;
import brass_compass.ui.EditListing;
import brass_compass.ui.EditMenu;
import com.zurrtum.create.client.content.trains.station.NoShadowFontWrapper;
import brass_compass.BrassCompass;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.minecraft.client.input.MouseButtonEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

/**
 * The add/edit dialog, composed from regions of Create Fly's stock-keeper request window
 * (UI-DEC-001): its cream title strip saying whether this is a new or a saved lodestone, three
 * brown panel strips holding the place and the package-address label as the name field with the
 * switch rows' check and X glyphs for save and remove beside it, and the window's grey bottom band. Each
 * row of content is centred in the frame. The atlas is the request window's own texture; regions
 * are blitted by coordinate, so the dialog is 224 wide (texture columns 16..240) and 90 tall.
 */
public final class EditScreen extends AbstractSimiContainerScreen<EditMenu> {
    private static final AllGuiTextures ATLAS = AllGuiTextures.STOCK_KEEPER_REQUEST_HEADER;
    private static final int U = 16;
    private static final int WIDTH = 224;
    /** The frame's inner span in dialog coordinates: texture columns 24..231. */
    private static final int FRAME_LEFT = 8;
    private static final int FRAME_WIDTH = 208;
    private static final int TITLE_V = 0;
    private static final int TITLE_H = 18;
    private static final int PANEL_V = 48;
    private static final int PANEL_H = 20;
    private static final int PANELS = 3;
    private static final int BOTTOM_V = 148;
    private static final int BOTTOM_H = 12;
    private static final int HEIGHT = TITLE_H + PANELS * PANEL_H + BOTTOM_H;
    /**
     * The package-address label without its pointed string end: texture columns 37..155, rows
     * 119..136, given a left cap built from its own right outline (column 155) and corner pixels.
     */
    private static final int LABEL_U = 37;
    private static final int LABEL_V = 119;
    private static final int LABEL_BODY_W = 119;
    private static final int LABEL_CAP_W = 2;
    private static final int LABEL_W = LABEL_CAP_W + LABEL_BODY_W;
    private static final int LABEL_H = 18;
    private static final int OUTLINE_U = 155;
    private static final int CORNER_U = 154;
    private static final int LABEL_Y = TITLE_H + 30;
    private static final int GAP = 6;
    /** Save and remove are the switch screen's row glyphs: 8x8 check and X, on the label's baseline, at its pitch. */
    private static final Identifier CHECK = BrassCompass.id("textures/gui/check.png");
    private static final Identifier X = BrassCompass.id("textures/gui/x.png");
    private static final int GLYPH = 8;
    private static final int GLYPH_PITCH = 14;
    private static final int GLYPH_Y = LABEL_Y + 5;
    private static final int COLOUR_CHECK = 0xFF4FB05A;
    /** The row X is brown on cream; on the panel brown it is drawn in the label cream instead. */
    private static final int COLOUR_X = 0xFFF8F8EC;
    private static final int COLOUR_TITLE = 0xFF4A2D31;
    private static final int COLOUR_ON_PANEL = 0xFFCDBCA8;
    private static final int COLOUR_NAME = 0xFF714A40;
    private static final int COLOUR_PLACEHOLDER = 0xFFCDBCA8;

    private EditBox nameBox;

    public EditScreen(EditMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
    }

    public static EditScreen create(Minecraft minecraft, MenuType<EditListing> type, int syncId, Inventory inventory, Component title, RegistryFriendlyByteBuf buf) {
        EditListing listing = EditListing.STREAM_CODEC.decode(buf);
        return new EditScreen(new EditMenu(syncId, inventory, listing), inventory, title);
    }

    /** Left edge of the centred label-and-buttons row. */
    private int groupLeft() {
        int glyphs = menu.listing().existing() ? 2 : 1;
        int width = LABEL_W + GAP + (glyphs - 1) * GLYPH_PITCH + GLYPH;
        return leftPos + FRAME_LEFT + (FRAME_WIDTH - width) / 2;
    }

    private int checkX() {
        return groupLeft() + LABEL_W + GAP;
    }

    private int removeX() {
        return checkX() + GLYPH_PITCH;
    }

    private boolean overGlyph(double mx, double my, int x) {
        return mx >= x - 3 && mx < x + GLYPH + 3 && my >= topPos + GLYPH_Y - 5 && my < topPos + GLYPH_Y + GLYPH + 5;
    }

    @Override
    protected void init() {
        setWindowOffset(0, 0);
        super.init();
        EditListing listing = menu.listing();
        String keep = nameBox != null ? nameBox.getValue() : listing.existing() ? listing.name() : "";
        int left = groupLeft();
        nameBox = new EditBox(new NoShadowFontWrapper(font), left + 8, topPos + LABEL_Y + 5, LABEL_W - 14, 10, Component.translatable("screen.brass_compass.name"));
        nameBox.setBordered(false);
        nameBox.setTextColor(COLOUR_NAME);
        nameBox.setMaxLength(Names.MAX_LENGTH);
        nameBox.setValue(keep);
        nameBox.setCanLoseFocus(false);
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            if (overGlyph(event.x(), event.y(), checkX())) {
                save();
                return true;
            }
            if (menu.listing().existing() && overGlyph(event.x(), event.y(), removeX())) {
                remove();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private void save() {
        EditListing listing = menu.listing();
        ClientPlayNetworking.send(new SavePayload(listing.hand(), listing.dimension(), listing.pos(), nameBox.getValue()));
        onClose();
    }

    private void remove() {
        Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, EditMenu.REMOVE);
        onClose();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER) {
            save();
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        if (nameBox.isFocused() && nameBox.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event);
    }

    private void region(GuiGraphicsExtractor graphics, Identifier atlas, int x, int y, int u, int v, int w, int h) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, atlas, x, y, u, v, w, h, 256, 256);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        EditListing listing = menu.listing();
        Identifier atlas = ATLAS.getLocation();
        int y = topPos;
        region(graphics, atlas, leftPos, y, U, TITLE_V, WIDTH, TITLE_H);
        Component heading = Component.translatable(listing.existing() ? "screen.brass_compass.editing" : "screen.brass_compass.adding");
        graphics.text(font, heading, leftPos + WIDTH / 2 - font.width(heading) / 2, y + 4, COLOUR_TITLE, false);
        y += TITLE_H;
        for (int i = 0; i < PANELS; i++) {
            region(graphics, atlas, leftPos, y, U, PANEL_V, WIDTH, PANEL_H);
            y += PANEL_H;
        }
        region(graphics, atlas, leftPos, y, U, BOTTOM_V, WIDTH, BOTTOM_H);

        int panelTop = topPos + TITLE_H;
        Component place = Component.translatable("screen.brass_compass.position", listing.pos().getX(), listing.pos().getY(), listing.pos().getZ());
        int placeWidth = 16 + GAP + font.width(place);
        int placeX = leftPos + FRAME_LEFT + (FRAME_WIDTH - placeWidth) / 2;
        graphics.item(new ItemStack(Items.LODESTONE), placeX, panelTop + 6);
        graphics.text(font, place, placeX + 16 + GAP, panelTop + 10, COLOUR_ON_PANEL, false);

        int labelX = groupLeft();
        int labelY = topPos + LABEL_Y;
        region(graphics, atlas, labelX + LABEL_CAP_W, labelY, LABEL_U, LABEL_V, LABEL_BODY_W, LABEL_H);
        region(graphics, atlas, labelX, labelY + 2, OUTLINE_U, LABEL_V + 2, 1, LABEL_H - 4);
        region(graphics, atlas, labelX + 1, labelY + 1, CORNER_U, LABEL_V + 1, 1, 1);
        region(graphics, atlas, labelX + 1, labelY + LABEL_H - 2, CORNER_U, LABEL_V + LABEL_H - 2, 1, 1);
        region(graphics, atlas, labelX + 1, labelY + 2, LABEL_U, LABEL_V + 2, 1, LABEL_H - 4);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHECK, checkX(), topPos + GLYPH_Y, 0, 0, GLYPH, GLYPH, GLYPH, GLYPH, COLOUR_CHECK);
        if (listing.existing()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, X, removeX(), topPos + GLYPH_Y, 0, 0, GLYPH, GLYPH, GLYPH, GLYPH, COLOUR_X);
        }
        if (nameBox != null && nameBox.getValue().isBlank()) {
            Component placeholder = Component.translatable("screen.brass_compass.name").withStyle(ChatFormatting.ITALIC);
            graphics.text(font, placeholder, nameBox.getX(), nameBox.getY(), COLOUR_PLACEHOLDER, false);
        }
    }
}
