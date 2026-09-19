package brass_compass.client;

import brass_compass.BrassCompass;
import com.zurrtum.create.client.AllMenuScreens;
import net.fabricmc.api.ClientModInitializer;

/** The client entrypoint: screens only. */
public final class BrassCompassClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AllMenuScreens.register(BrassCompass.SWITCH_MENU, SwitchScreen::create);
        AllMenuScreens.register(BrassCompass.EDIT_MENU, EditScreen::create);
    }
}
