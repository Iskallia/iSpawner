package iskallia.ispawner.init;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.block.render.SpawnerBlockRenderer;
import iskallia.ispawner.item.GenericSpawnEggItem;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

import java.util.Map;

public class ModRenderers extends ModRegistries {

	public static class BlockEntities extends ModRenderers {
		public static BlockEntityRendererFactory<SpawnerBlockEntity> SPAWNER;
		public static BlockEntityRendererFactory<SurvivalSpawnerBlockEntity> SURVIVAL_SPAWNER;

		public static void register(Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry) {
			try {
				register(registry, ModBlocks.Entities.SPAWNER.get(), SpawnerBlockRenderer::new);
				register(registry, ModBlocks.Entities.SURVIVAL_SPAWNER.get(), SpawnerBlockRenderer::new);
			} catch(Exception e) {
				ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
					SPAWNER = register(registry, ModBlocks.Entities.SPAWNER.get(), SpawnerBlockRenderer::new);
					SURVIVAL_SPAWNER = register(registry, ModBlocks.Entities.SURVIVAL_SPAWNER.get(), SpawnerBlockRenderer::new);
				});
			}
		}
	}

	public static class RenderLayers {
		public static void register(Map<Block, RenderLayer> registry, boolean fancyGraphicsOrBetter) {
			try {
				ModRenderers.register(registry, ModBlocks.SPAWNER.get(), RenderLayer.getCutout());
				ModRenderers.register(registry, ModBlocks.SURVIVAL_SPAWNER.get(), RenderLayer.getCutout());
			} catch(Exception e) {
				ClientLifecycleEvent.CLIENT_SETUP.register(minecraft -> {
					ModRenderers.register(registry, ModBlocks.SPAWNER.get(), RenderLayer.getCutout());
					ModRenderers.register(registry, ModBlocks.SURVIVAL_SPAWNER.get(), RenderLayer.getCutout());
				});
			}
		}
	}

	public static class Colors {
		public static void register(ItemColors itemColors, BlockColors blockColors) {
			itemColors.register(GenericSpawnEggItem::getColor, ModItems.SPAWN_EGG.get());
		}
	}

	public static <T extends BlockEntity> BlockEntityRendererFactory<T> register(
			Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> registry,
			BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> renderer) {
		registry.put(type, renderer);
		return renderer;
	}

	public static void register(Map<Block, RenderLayer> registry, Block block, RenderLayer layer) {
		registry.put(block, layer);
	}

}
