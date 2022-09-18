package iskallia.ispawner.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.architectury.utils.Env;
import iskallia.ispawner.ISpawner;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModRegistries {

	public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ISpawner.MOD_ID, Registry.ITEM_KEY);
	public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ISpawner.MOD_ID, Registry.BLOCK_KEY);
	public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ISpawner.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);
	public static DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(ISpawner.MOD_ID, Registry.MENU_KEY);

	public static void register() {
		ModItems.register();
		ModBlocks.register();
		ModItems.NBT.register();
		ModBlocks.Entities.register();
		ModMenus.register();
		ModNetwork.register();

		BLOCKS.register();
		ITEMS.register();
		BLOCK_ENTITY_TYPES.register();
		SCREEN_HANDLERS.register();

		if(Platform.getEnvironment() == Env.CLIENT) {
			ModScreens.register();
		}

		ModConfigs.register();
	}

	public static <T, V extends T> RegistrySupplier<V> register(DeferredRegister<T> registry, Identifier id, Supplier<V> value) {
		return registry.register(id, value);
	}

	public static <T, V extends T> RegistrySupplier<V> register(DeferredRegister<T> registry, String name, Supplier<V> value) {
		return registry.register(name, value);
	}

}
