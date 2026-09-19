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
import net.minecraft.client.input.MouseButtonEvent;
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
 * (UI-DEC-001): its cream title strip, two brown panel strips for the place and the state, and
 * the lower footer with the package-address label as the name field and the grey send arrow as
 * the save button. The atlas is the request window's own texture; the regions are blitted by
 * coordinate, so the dialog is 224 wide (texture columns 16..240) and 113 tall.
 */
public final class EditScreen extends AbstractSimiContainerScreen<EditMenu> {
    private static final AllGuiTextures ATLAS = AllGuiTextures.STOCK_KEEPER_REQUEST_HEADER;
    private static final AllGuiTextures SEND_HOVER = AllGuiTextures.STOCK_KEEPER_REQUEST_SEND_HOVER;
    private static final int U = 16;
    private static final int WIDTH = 224;
    private static final int TITLE_V = 0;
    private static final int TITLE_H = 19;
    private static final int PANEL_V = 48;
    private static final int PANEL_H = 20;
    private static final int PANELS = 2;
    private static final int FOOTER_V = 106;
    private static final int FOOTER_H = 54;
    private static final int HEIGHT = TITLE_H + PANELS * PANEL_H + FOOTER_H;
    /** The cream address label inside the footer region, dialog coordinates. */
    private static final int LABEL_X = 14;
    private static final int LABEL_Y = TITLE_H + PANELS * PANEL_H + 14;
    private static final int LABEL_W = 125;
    private static final int LABEL_H = 15;
    /** The grey send arrow inside the footer region, dialog coordinates. */
    private static final int ARROW_X = 145;
    private static final int ARROW_Y = TITLE_H + PANELS * PANEL_H + 6;
    private static final int ARROW_W = 78;
    private static final int ARROW_H = 30;
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
        String keep = nameBox == null ? listing.name() : nameBox.getValue();
        nameBox = new EditBox(new NoShadowFontWrapper(font), leftPos + LABEL_X + 8, topPos + LABEL_Y + 3, LABEL_W - 16, 10, Component.translatable("screen.brass_compass.name"));
        nameBox.setBordered(false);
        nameBox.setTextColor(COLOUR_NAME);
        nameBox.setMaxLength(Names.MAX_LENGTH);
        nameBox.setValue(keep);
        nameBox.setCanLoseFocus(false);
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);
        if (listing.existing()) {
            IconButton remove = new IconButton(leftPos + WIDTH - 8 - 18 - 6, topPos + TITLE_H + (PANELS * PANEL_H - 18) / 2, AllIcons.I_TRASH);
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

    private boolean overArrow(double x, double y) {
        return x >= leftPos + ARROW_X && x < leftPos + ARROW_X + ARROW_W && y >= topPos + ARROW_Y && y < topPos + ARROW_Y + ARROW_H;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && overArrow(event.x(), event.y())) {
            save();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
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

    private void region(GuiGraphicsExtractor graphics, Identifier atlas, int y, int v, int h) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, atlas, leftPos, y, U, v, WIDTH, h, 256, 256);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        EditListing listing = menu.listing();
        Identifier atlas = ATLAS.getLocation();
        int y = topPos;
        region(graphics, atlas, y, TITLE_V, TITLE_H);
        Component heading = Component.translatable("screen.brass_compass.position", listing.pos().getX(), listing.pos().getY(), listing.pos().getZ());
        graphics.text(font, heading, leftPos + WIDTH / 2 - font.width(heading) / 2, y + 4, COLOUR_TITLE, false);
        y += TITLE_H;
        for (int i = 0; i < PANELS; i++) {
            region(graphics, atlas, y, PANEL_V, PANEL_H);
            y += PANEL_H;
        }
        int panelTop = topPos + TITLE_H;
        graphics.item(new ItemStack(Items.LODESTONE), leftPos + 16, panelTop + 12);
        Component state = listing.existing()
            ? Component.translatable("screen.brass_compass.editing", listing.name())
            : Component.translatable("screen.brass_compass.adding");
        graphics.text(font, state, leftPos + 38, panelTop + 16, COLOUR_ON_PANEL, false);
        region(graphics, atlas, y, FOOTER_V, FOOTER_H);
        if (overArrow(mouseX, mouseY)) {
            SEND_HOVER.render(graphics, leftPos + ARROW_X, topPos + ARROW_Y + 5);
        }
        Component save = Component.translatable("screen.brass_compass.save");
        graphics.text(font, save, leftPos + ARROW_X + 30 - font.width(save) / 2, topPos + ARROW_Y + 11, COLOUR_NAME, false);
        if (nameBox != null && nameBox.getValue().isBlank() && !nameBox.isFocused()) {
            Component placeholder = Component.translatable("screen.brass_compass.name").withStyle(ChatFormatting.ITALIC);
            graphics.text(font, placeholder, nameBox.getX(), nameBox.getY(), COLOUR_PLACEHOLDER, false);
        }
    }
}
