package iskallia.ispawner.mixin;

import iskallia.ispawner.init.ModRenderers;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class MixinBlockEntityRenderDispatcher {

	@Shadow @Final private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers;

	@Inject(method = "<init>",
			at = @At("RETURN"))
	private void ctor(CallbackInfo ci) {
		ModRenderers.BlockEntities.register(this.renderers);
	}

}
