package iskallia.ispawner.init;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.menu.MenuRegistry;
import iskallia.ispawner.screen.SpawnerScreen;
import iskallia.ispawner.screen.SurvivalSpawnerScreen;

public class ModScreens extends ModRegistries {

	public static void register() {
		ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
			MenuRegistry.registerScreenFactory(ModMenus.SPAWNER.get(), SpawnerScreen::new);
			MenuRegistry.registerScreenFactory(ModMenus.SURVIVAL_SPAWNER.get(), SurvivalSpawnerScreen::new);
		});
	}

}
