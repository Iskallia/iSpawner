package iskallia.ispawner.forge.mixin;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import net.minecraft.util.math.Box;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnerBlockEntity.class)
public abstract class MixinSpawnerBlockEntity implements IForgeBlockEntity {

	@Override
	public Box getRenderBoundingBox() {
		return IForgeBlockEntity.INFINITE_EXTENT_AABB;
	}

}
