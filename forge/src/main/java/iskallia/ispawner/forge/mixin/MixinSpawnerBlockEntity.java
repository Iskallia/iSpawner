package iskallia.ispawner.forge.mixin;

import iskallia.ispawner.block.entity.BaseBlockEntity;
import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin(value = SpawnerBlockEntity.class, priority = 1001)
public abstract class MixinSpawnerBlockEntity extends BaseBlockEntity implements Tickable, NamedScreenHandlerFactory, InventoryChangedListener {

	public MixinSpawnerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	/**
	 * @author iSpawners (Iskallia)
	 */
	@Overwrite
	public Box getRenderBoundingBox() {
		return IForgeTileEntity.INFINITE_EXTENT_AABB;
	}

	/**
	 * @author iSpawners (Iskallia)
	 */
	@Overwrite
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (Object)this instanceof SurvivalSpawnerBlockEntity) {
			SurvivalSpawnerBlockEntity entity = (SurvivalSpawnerBlockEntity)(Object)this;
			return LazyOptional.of(() -> new InvWrapper(entity.getInventory())).cast();
		}

		return LazyOptional.empty();
	}

}
