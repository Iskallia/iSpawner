package iskallia.ispawner.forge.mixin;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import net.minecraft.util.math.Box;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin(SpawnerBlockEntity.class)
public abstract class MixinSpawnerBlockEntity implements IForgeTileEntity {

	@Override
	public Box getRenderBoundingBox() {
		return IForgeTileEntity.INFINITE_EXTENT_AABB;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (Object)this instanceof SurvivalSpawnerBlockEntity) {
			SurvivalSpawnerBlockEntity entity = (SurvivalSpawnerBlockEntity) (Object) this;
			return LazyOptional.of(() -> new InvWrapper(entity.getInventory())).cast();
		}

		return LazyOptional.empty();
	}

}
