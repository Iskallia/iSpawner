package iskallia.ispawner.mixin;

import iskallia.ispawner.init.ModRenderers;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BlockEntityRendererFactories.class)
public class MixinBlockEntityRendererFactories {

	@Shadow @Final private static Map<BlockEntityType<?>, BlockEntityRendererFactory<?>> FACTORIES;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void staticInit(CallbackInfo ci) {
		ModRenderers.BlockEntities.FACTORIES = FACTORIES;
	}

}
