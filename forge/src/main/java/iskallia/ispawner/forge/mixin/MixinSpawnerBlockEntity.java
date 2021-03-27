package iskallia.ispawner.forge.mixin;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import net.minecraft.util.math.Box;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnerBlockEntity.class)
public abstract class MixinSpawnerBlockEntity implements IForgeTileEntity {

	@Override
	public Box getRenderBoundingBox() {
		return IForgeTileEntity.INFINITE_EXTENT_AABB;
	}

}
