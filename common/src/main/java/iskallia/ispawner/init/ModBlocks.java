package iskallia.ispawner.init;

import com.mojang.datafixers.types.Type;
import iskallia.ispawner.block.ISpawnerBlock;
import iskallia.ispawner.block.entity.ISpawnerBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks extends ModRegistries {

	public static Block SPAWNER;

	public static void register() {
		SPAWNER = register("spawner", new ISpawnerBlock(AbstractBlock.Settings.of(Material.STONE)
				.requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque()),
				block -> new BlockItem(block, new Item.Settings().group(ItemGroup.SEARCH)));
	}

	public static class Entity extends ModRegistries {
		public static BlockEntityType<ISpawnerBlockEntity> SPAWNER;

		public static void register() {
			SPAWNER = ModBlocks.register("spawner", ISpawnerBlockEntity::new, ModBlocks.SPAWNER);
		}
	}

	public static <V extends Block> V register(Identifier id, V block) {
		return register(id, block);
	}

	public static <V extends Block> V register(String name, V block) {
		return register(name, block);
	}

	public static <V extends Block> V register(Identifier id, V block, Function<V, BlockItem> item) {
		return register(id, block, id, item);
	}

	public static <V extends Block> V register(String name, V block, Function<V, BlockItem> item) {
		return register(name, block, name, item);
	}

	public static <V extends Block> V register(Identifier id, V block, Identifier itemId, Function<V, BlockItem> item) {
		if(item != null) {
			BlockItem value = item.apply(block);

			if(value != null) {
				ModItems.register(itemId, value);
			}
		}

		return register(BLOCKS, id, block);
	}

	public static <V extends Block> V register(String name, V block, String itemName, Function<V, BlockItem> item) {
		if(item != null) {
			BlockItem value = item.apply(block);

			if(value != null) {
				ModItems.register(itemName, value);
			}
		}

		return register(BLOCKS, name, block);
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(Identifier id, Supplier<T> blockEntity, Block... blocks) {
		return register(id, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, null), blocks);
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(Identifier id, Supplier<T> blockEntity, String typeId, Block... blocks) {
		return register(id, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, typeId), blocks);
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(Identifier id, Supplier<T> blockEntity, Type<?> type, Block... blocks) {
		return register(BLOCK_ENTITY_TYPES, id, BlockEntityType.Builder.create(blockEntity, blocks).build(type));
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> blockEntity, Block... blocks) {
		return register(name, blockEntity, (Type<?>)null, blocks);
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> blockEntity, String typeId, Block... blocks) {
		return register(name, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, typeId), blocks);
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<T> blockEntity, Type<?> type, Block... blocks) {
		return register(BLOCK_ENTITY_TYPES, name, BlockEntityType.Builder.create(blockEntity, blocks).build(type));
	}

}
