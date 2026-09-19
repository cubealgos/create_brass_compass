package brass_compass.client;

import brass_compass.destinations.Names;
import brass_compass.network.SavePayload;
import brass_compass.ui.EditListing;
import brass_compass.ui.EditMenu;
import com.zurrtum.create.client.content.trains.station.NoShadowFontWrapper;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.zurrtum.create.client.foundation.gui.widget.IconButton;
import com.zurrtum.create.foundation.gui.menu.MenuType;
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
 * brown panel strips holding the place and the package-address label as the name field, Create's
 * icon buttons for save and remove beside it, and the window's grey bottom band. The atlas is the
 * request window's own texture; regions are blitted by coordinate, so the dialog is 224 wide
 * (texture columns 16..240) and 91 tall.
 */
public final class EditScreen extends AbstractSimiContainerScreen<EditMenu> {
    private static final AllGuiTextures ATLAS = AllGuiTextures.STOCK_KEEPER_REQUEST_HEADER;
    private static final int U = 16;
    private static final int WIDTH = 224;
    private static final int TITLE_V = 0;
    private static final int TITLE_H = 19;
    private static final int PANEL_V = 48;
    private static final int PANEL_H = 20;
    private static final int PANELS = 3;
    private static final int BOTTOM_V = 148;
    private static final int BOTTOM_H = 12;
    private static final int HEIGHT = TITLE_H + PANELS * PANEL_H + BOTTOM_H;
    /** The package-address label with its string tail, texture columns 16..158, rows 115..142. */
    private static final int LABEL_V = 115;
    private static final int LABEL_W = 142;
    private static final int LABEL_H = 27;
    private static final int LABEL_Y = TITLE_H + 26;
    private static final int BUTTONS_Y = LABEL_Y + 4;
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

    @Override
    protected void init() {
        setWindowOffset(0, 0);
        super.init();
        EditListing listing = menu.listing();
        String keep = nameBox != null ? nameBox.getValue() : listing.existing() ? listing.name() : "";
        nameBox = new EditBox(new NoShadowFontWrapper(font), leftPos + 22, topPos + LABEL_Y + 8, 110, 10, Component.translatable("screen.brass_compass.name"));
        nameBox.setBordered(false);
        nameBox.setTextColor(COLOUR_NAME);
        nameBox.setMaxLength(Names.MAX_LENGTH);
        nameBox.setValue(keep);
        nameBox.setCanLoseFocus(false);
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);

        IconButton confirm = new IconButton(leftPos + LABEL_W + 8, topPos + BUTTONS_Y, AllIcons.I_CONFIRM);
        confirm.setToolTip(Component.translatable("screen.brass_compass.confirm"));
        confirm.withCallback(this::save);
        addRenderableWidget(confirm);
        if (listing.existing()) {
            IconButton remove = new IconButton(leftPos + LABEL_W + 8 + 22, topPos + BUTTONS_Y, AllIcons.I_TRASH);
            remove.setToolTip(Component.translatable("screen.brass_compass.remove"));
            remove.withCallback(this::remove);
            addRenderableWidget(remove);
        }
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
        graphics.item(new ItemStack(Items.LODESTONE), leftPos + 16, panelTop + 4);
        Component place = Component.translatable("screen.brass_compass.position", listing.pos().getX(), listing.pos().getY(), listing.pos().getZ());
        graphics.text(font, place, leftPos + 38, panelTop + 8, COLOUR_ON_PANEL, false);
        region(graphics, atlas, leftPos, topPos + LABEL_Y, U, LABEL_V, LABEL_W, LABEL_H);
        if (nameBox != null && nameBox.getValue().isBlank()) {
            Component placeholder = Component.translatable("screen.brass_compass.name").withStyle(ChatFormatting.ITALIC);
            graphics.text(font, placeholder, nameBox.getX(), nameBox.getY(), COLOUR_PLACEHOLDER, false);
        }
    }
}
