package iskallia.ispawner.init;

import iskallia.ispawner.screen.SpawnerScreen;
import me.shedaniel.architectury.registry.MenuRegistry;

public class ModScreens extends ModRegistries {

	public static void register() {
		MenuRegistry.registerScreenFactory(ModMenus.SPAWNER, SpawnerScreen::new);
	}

}
