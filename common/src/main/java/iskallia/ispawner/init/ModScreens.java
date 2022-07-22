package iskallia.ispawner.init;

import dev.architectury.registry.menu.MenuRegistry;
import iskallia.ispawner.screen.SpawnerScreen;
import iskallia.ispawner.screen.SurvivalSpawnerScreen;

public class ModScreens extends ModRegistries {

	public static void register() {
		MenuRegistry.registerScreenFactory(ModMenus.SPAWNER, SpawnerScreen::new);
		MenuRegistry.registerScreenFactory(ModMenus.SURVIVAL_SPAWNER, SurvivalSpawnerScreen::new);
	}

}
