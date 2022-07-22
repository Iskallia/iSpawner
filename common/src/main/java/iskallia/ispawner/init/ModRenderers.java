package iskallia.ispawner.init;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.block.render.SpawnerBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

import java.util.Map;

public class ModRenderers extends ModRegistries {

	public static class BlockEntities extends ModRenderers {
		public static SpawnerBlockRenderer<SpawnerBlockEntity> SPAWNER;
		public static SpawnerBlockRenderer<SurvivalSpawnerBlockEntity> SURVIVAL_SPAWNER;

		public static void register(Map<BlockEntityType<?>, BlockEntityRenderer<?>> registry) {
			SPAWNER = register(registry, ModBlocks.Entities.SPAWNER, new SpawnerBlockRenderer<>());
			SURVIVAL_SPAWNER = register(registry, ModBlocks.Entities.SURVIVAL_SPAWNER, new SpawnerBlockRenderer<>());
		}
	}

	public static class RenderLayers {
		public static void register(Map<Block, RenderLayer> registry, boolean fancyGraphicsOrBetter) {
			ModRenderers.register(registry, ModBlocks.SPAWNER, RenderLayer.getCutout());
			ModRenderers.register(registry, ModBlocks.SURVIVAL_SPAWNER, RenderLayer.getCutout());
		}
	}

	public static <T extends BlockEntity, R extends BlockEntityRenderer<T>> R register(Map<BlockEntityType<?>, BlockEntityRenderer<?>> registry,
	                                                                                   BlockEntityType<T> type, R renderer) {
		registry.put(type, renderer);
		return renderer;
	}

	public static void register(Map<Block, RenderLayer> registry, Block block, RenderLayer layer) {
		registry.put(block, layer);
	}

}
