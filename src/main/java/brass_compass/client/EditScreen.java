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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

/**
 * The add/edit screen on Create Fly's frogport layout (UI-DEC-001): the name lives in the blue
 * header strip's text box, the grey body names the place, the confirm arrow sits bottom right and
 * a trash button beside it for an existing entry. Enter confirms, Escape closes. The numbers are
 * the package port screen's own.
 */
public final class EditScreen extends AbstractSimiContainerScreen<EditMenu> {
    private static final AllGuiTextures HEADER = AllGuiTextures.FROGPORT_HEADER;
    private static final AllGuiTextures BG = AllGuiTextures.FROGPORT_BG;
    private static final AllGuiTextures SLOT = AllGuiTextures.FROGPORT_SLOT;
    private static final int COLOUR_TEXT = 0xFF3D3C48;
    private static final int COLOUR_MUTED = 0xFF656565;

    private EditBox nameBox;

    public EditScreen(EditMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, BG.getWidth(), HEADER.getHeight() + BG.getHeight());
    }

    public static EditScreen create(Minecraft minecraft, MenuType<EditListing> type, int syncId, Inventory inventory, Component title, RegistryFriendlyByteBuf buf) {
        EditListing listing = EditListing.STREAM_CODEC.decode(buf);
        return new EditScreen(new EditMenu(syncId, inventory, listing), inventory, title);
    }

    private int headerLeft() {
        return leftPos + (BG.getWidth() - HEADER.getWidth()) / 2;
    }

    private int bodyTop() {
        return topPos + HEADER.getHeight();
    }

    @Override
    protected void init() {
        setWindowOffset(0, 0);
        super.init();
        EditListing listing = menu.listing();
        String keep = nameBox == null ? listing.name() : nameBox.getValue();
        nameBox = new EditBox(new NoShadowFontWrapper(font), headerLeft() + 23, topPos + 6, HEADER.getWidth() - 46, 10, Component.translatable("screen.brass_compass.name"));
        nameBox.setBordered(false);
        nameBox.setTextColor(COLOUR_TEXT);
        nameBox.setMaxLength(Names.MAX_LENGTH);
        nameBox.setValue(keep);
        nameBox.setHint(Component.translatable("screen.brass_compass.name"));
        nameBox.setCanLoseFocus(false);
        addRenderableWidget(nameBox);
        setInitialFocus(nameBox);

        IconButton confirm = new IconButton(leftPos + imageWidth - 33, topPos + imageHeight - 24, AllIcons.I_CONFIRM);
        confirm.setToolTip(Component.translatable("screen.brass_compass.confirm"));
        confirm.withCallback(this::save);
        addRenderableWidget(confirm);
        if (listing.existing()) {
            IconButton remove = new IconButton(leftPos + imageWidth - 33 - 22, topPos + imageHeight - 24, AllIcons.I_TRASH);
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

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        EditListing listing = menu.listing();
        HEADER.render(graphics, headerLeft(), topPos);
        BG.render(graphics, leftPos, bodyTop());
        int slotX = leftPos + 10;
        int slotY = bodyTop() + 10;
        SLOT.render(graphics, slotX, slotY);
        graphics.item(new ItemStack(Items.LODESTONE), slotX + 1, slotY + 1);
        graphics.text(font, Component.translatable("screen.brass_compass.position", listing.pos().getX(), listing.pos().getY(), listing.pos().getZ()),
            slotX + SLOT.getWidth() + 6, slotY + 1, COLOUR_TEXT, false);
        graphics.text(font, Component.translatable(listing.existing() ? "screen.brass_compass.editing" : "screen.brass_compass.adding"),
            slotX + SLOT.getWidth() + 6, slotY + 11, COLOUR_MUTED, false);
    }
}
