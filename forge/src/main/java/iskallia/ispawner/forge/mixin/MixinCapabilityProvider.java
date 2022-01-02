package iskallia.ispawner.forge.mixin;

import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CapabilityProvider.class)
public abstract class MixinCapabilityProvider implements ICapabilityProvider {

	@Inject(method = "getCapability", at = @At("HEAD"), cancellable = true, remap = false)
	public <T> void getCapability(Capability<T> cap, Direction side, CallbackInfoReturnable<LazyOptional<T>> ci) {
		if((Object)this instanceof SurvivalSpawnerBlockEntity) {
			if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				SurvivalSpawnerBlockEntity entity = (SurvivalSpawnerBlockEntity)(Object)this;
				ci.setReturnValue(LazyOptional.of(() -> new InvWrapper(entity.getInput())).cast());
			} else {
				ci.setReturnValue(LazyOptional.empty());
			}
		}
	}

}
