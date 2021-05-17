package iskallia.ispawner.init;

import iskallia.ispawner.screen.SpawnerScreen;
import iskallia.ispawner.screen.SurvivalSpawnerScreen;
import me.shedaniel.architectury.registry.MenuRegistry;

public class ModScreens extends ModRegistries {

	public static void register() {
		MenuRegistry.registerScreenFactory(ModMenus.SPAWNER, SpawnerScreen::new);
		MenuRegistry.registerScreenFactory(ModMenus.SURVIVAL_SPAWNER, SurvivalSpawnerScreen::new);
	}

}
