package iskallia.ispawner.init;

import com.mojang.datafixers.types.Type;
import dev.architectury.registry.registries.RegistrySupplier;
import iskallia.ispawner.block.SpawnerBlock;
import iskallia.ispawner.block.SurvivalSpawnerBlock;
import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
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

	public static RegistrySupplier<SpawnerBlock> SPAWNER;
	public static RegistrySupplier<SurvivalSpawnerBlock> SURVIVAL_SPAWNER;

	public static void register() {
		SPAWNER = register("spawner", () -> new SpawnerBlock(AbstractBlock.Settings.of(Material.STONE)
				.requiresTool().strength(100.0F, 1200.0F).sounds(BlockSoundGroup.METAL).nonOpaque()),
				entry -> () -> new BlockItem(entry.get(), new Item.Settings().group(ItemGroup.SEARCH)));

		SURVIVAL_SPAWNER = register("survival_spawner", () -> new SurvivalSpawnerBlock(AbstractBlock.Settings.of(Material.STONE)
				.requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.METAL).nonOpaque()),
				entry -> () -> new BlockItem(entry.get(), new Item.Settings().group(ItemGroup.SEARCH)));
	}

	public static class Entities extends ModBlocks {
		public static RegistrySupplier<BlockEntityType<SpawnerBlockEntity>> SPAWNER;
		public static RegistrySupplier<BlockEntityType<SurvivalSpawnerBlockEntity>> SURVIVAL_SPAWNER;

		public static void register() {
			SPAWNER = register("spawner", SpawnerBlockEntity::new, ModBlocks.SPAWNER);
			SURVIVAL_SPAWNER = register("survival_spawner", SurvivalSpawnerBlockEntity::new, ModBlocks.SURVIVAL_SPAWNER);
		}
	}

	public static <V extends Block> RegistrySupplier<V> register(Identifier id, Supplier<V> block) {
		return register(id, block);
	}

	public static <V extends Block> RegistrySupplier<V> register(String name, Supplier<V> block) {
		return register(name, block);
	}

	public static <V extends Block> RegistrySupplier<V> register(Identifier id, Supplier<V> block, Function<RegistrySupplier<V>, Supplier<BlockItem>> item) {
		return register(id, block, id, item);
	}

	public static <V extends Block> RegistrySupplier<V> register(String name, Supplier<V> block, Function<RegistrySupplier<V>, Supplier<BlockItem>> item) {
		return register(name, block, name, item);
	}

	public static <V extends Block> RegistrySupplier<V> register(Identifier id, Supplier<V> block, Identifier itemId, Function<RegistrySupplier<V>, Supplier<BlockItem>> item) {
		RegistrySupplier<V> entry = register(BLOCKS, id, block);

		if(item != null) {
			Supplier<BlockItem> value = item.apply(entry);

			if(value != null) {
				ModItems.register(itemId, value);
			}
		}

		return entry;
	}

	public static <V extends Block> RegistrySupplier<V> register(String name, Supplier<V> block, String itemName, Function<RegistrySupplier<V>, Supplier<BlockItem>> item) {
		RegistrySupplier<V> entry = register(BLOCKS, name, block);

		if(item != null) {
			Supplier<BlockItem> value = item.apply(entry);

			if(value != null) {
				ModItems.register(itemName, value);
			}
		}

		return entry;
	}

	public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(Identifier id, BlockEntityType.BlockEntityFactory<T> blockEntity, RegistrySupplier<Block>... blocks) {
		return register(id, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, null), blocks);
	}

	public static <T extends BlockEntity>RegistrySupplier<BlockEntityType<T>> register(Identifier id, BlockEntityType.BlockEntityFactory<T>blockEntity, String typeId, RegistrySupplier<Block>... blocks) {
		return register(id, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, typeId), blocks);
	}

	public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(Identifier id, BlockEntityType.BlockEntityFactory<T> blockEntity, Type<?> type, RegistrySupplier<Block>... blocks) {
		return register(BLOCK_ENTITY_TYPES, id, () -> {
			Block[] values = new Block[blocks.length];
			for(int i = 0; i < blocks.length; i++) values[i] = blocks[i].get();
			return BlockEntityType.Builder.create(blockEntity, values).build(type);
		});
	}

	public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntityFactory<T> blockEntity, RegistrySupplier<? extends Block>... blocks) {
		return register(name, blockEntity, (Type<?>)null, blocks);
	}

	public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntityFactory<T> blockEntity, String typeId, RegistrySupplier<? extends Block>... blocks) {
		return register(name, blockEntity, Util.getChoiceType(TypeReferences.BLOCK_ENTITY, typeId), blocks);
	}

	public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntityFactory<T> blockEntity, Type<?> type, RegistrySupplier<? extends Block>... blocks) {
		return register(BLOCK_ENTITY_TYPES, name, () -> {
			Block[] values = new Block[blocks.length];
			for(int i = 0; i < blocks.length; i++) values[i] = blocks[i].get();
			return BlockEntityType.Builder.create(blockEntity, values).build(type);
		});
	}

}
