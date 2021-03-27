package iskallia.ispawner.forge.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RenderLayers.class)
public class MixinRenderLayer {

	@Shadow @Final @Deprecated private static Map<Block, RenderLayer> BLOCKS;

	@Inject(method = "setFancyGraphicsOrBetter", at = @At("RETURN"))
	private static void setFancyGraphicsOrBetter(boolean fancyGraphicsOrBetter, CallbackInfo ci) {
		BLOCKS.forEach(RenderLayers::setRenderLayer);
	}

}
