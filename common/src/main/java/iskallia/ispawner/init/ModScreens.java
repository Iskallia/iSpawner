package iskallia.ispawner.init;

import dev.architectury.registry.menu.MenuRegistry;
import iskallia.ispawner.screen.SpawnerScreen;
import iskallia.ispawner.screen.SurvivalSpawnerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;

import java.util.Map;

public class ModScreens extends ModRegistries {

	public static void register() {
		MenuRegistry.registerScreenFactory(ModMenus.SPAWNER.get(), SpawnerScreen::new);
		MenuRegistry.registerScreenFactory(ModMenus.SURVIVAL_SPAWNER.get(), SurvivalSpawnerScreen::new);
	}

}
