package iskallia.ispawner.init;

import dev.architectury.registry.registries.RegistrySupplier;
import iskallia.ispawner.item.SpawnerControllerItem;
import iskallia.ispawner.item.nbt.ItemNBT;
import iskallia.ispawner.item.nbt.SpawnData;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ModItems extends ModRegistries {

	public static RegistrySupplier<SpawnerControllerItem> SPAWNER_CONTROLLER;
	public static RegistrySupplier<Item> CAGE_DUST;
	public static RegistrySupplier<Item> CAGE_PIECE;

	public static void register() {
		SPAWNER_CONTROLLER = register("spawner_controller", () -> new SpawnerControllerItem(new Item.Settings().maxCount(1).group(ItemGroup.SEARCH)));
		CAGE_DUST = register("cage_dust", () -> new Item(new Item.Settings().maxCount(7).group(ItemGroup.SEARCH)));
		CAGE_PIECE = register("cage_piece", () -> new Item(new Item.Settings().maxCount(3).group(ItemGroup.SEARCH)));
	}

	public static <V extends Item> RegistrySupplier<V> register(Identifier id, Supplier<V> item) {
		return register(ITEMS, id, item);
	}

	public static <V extends Item> RegistrySupplier<V> register(String name, Supplier<V> item) {
		return register(ITEMS, name, item);
	}

	public static class NBT {
		public static void register() {
			ItemNBT.registerOverride(stack -> {
				if(ModConfigs.SURVIVAL_SPAWNER == null) return false;
				return ModConfigs.SURVIVAL_SPAWNER.isWhitelisted(stack);
			}, SpawnData::new);
		}
	}

}
