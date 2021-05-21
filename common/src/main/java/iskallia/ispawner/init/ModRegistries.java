package iskallia.ispawner.init;

import iskallia.ispawner.ISpawner;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.utils.Env;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

		if(Platform.getEnvironment() == Env.CLIENT) {
			ModScreens.register();
		}

		BLOCKS.register();
		ITEMS.register();
		BLOCK_ENTITY_TYPES.register();
		SCREEN_HANDLERS.register();

		ModConfigs.register();
	}

	public static <T, V extends T> V register(DeferredRegister<T> registry, Identifier id, V value) {
		registry.register(id, () -> value);
		return value;
	}

	public static <T, V extends T> V register(DeferredRegister<T> registry, String name, V value) {
		registry.register(name, () -> value);
		return value;
	}

}
