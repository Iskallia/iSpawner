package iskallia.ispawner.block.entity;

import iskallia.ispawner.init.ModBlocks;
import net.minecraft.item.ItemStack;

public class SurvivalSpawnerBlockEntity extends SpawnerBlockEntity {

	public SurvivalSpawnerBlockEntity() {
		super(ModBlocks.Entities.SURVIVAL_SPAWNER);
	}

	@Override
	public void onChargeUsed(ItemStack stack, int index) {
		stack.decrement(1);
		this.inventory.setStack(index, stack);
	}

}
