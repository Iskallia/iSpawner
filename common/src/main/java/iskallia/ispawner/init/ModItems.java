package iskallia.ispawner.init;

import iskallia.ispawner.item.SpawnerControllerItem;
import iskallia.ispawner.item.nbt.ItemNBT;
import iskallia.ispawner.item.nbt.SpawnData;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class ModItems extends ModRegistries {

	public static Item SPAWNER_CONTROLLER;
	public static Item CAGE_DUST;
	public static Item CAGE_PIECE;

	public static void register() {
		SPAWNER_CONTROLLER = register("spawner_controller", new SpawnerControllerItem(new Item.Settings().maxCount(1).group(ItemGroup.SEARCH)));
		CAGE_DUST = register("cage_dust", new Item(new Item.Settings().maxCount(7).group(ItemGroup.SEARCH)));
		CAGE_PIECE = register("cage_piece", new Item(new Item.Settings().maxCount(3).group(ItemGroup.SEARCH)));
	}

	public static <V extends Item> V register(Identifier id, V item) {
		if(item instanceof BlockItem) {
			((BlockItem)item).appendBlocks(BlockItem.BLOCK_ITEMS, item);
		}

		return register(ITEMS, id, item);
	}

	public static <V extends Item> V register(String name, V item) {
		if(item instanceof BlockItem) {
			((BlockItem)item).appendBlocks(BlockItem.BLOCK_ITEMS, item);
		}

		return register(ITEMS, name, item);
	}

	public static class NBT {
		public static void register() {
			ItemNBT.registerOverride(stack -> ModConfigs.SURVIVAL_SPAWNER.isWhitelisted(stack), SpawnData::new);
		}
	}

}
