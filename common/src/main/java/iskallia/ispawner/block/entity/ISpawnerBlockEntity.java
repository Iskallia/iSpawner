package iskallia.ispawner.block.entity;

import iskallia.ispawner.init.ModBlocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class ISpawnerBlockEntity extends BlockEntity implements Tickable {

	public ISpawnerBlockEntity() {
		super(ModBlocks.Entity.SPAWNER);
	}

	@Override
	public void tick() {

	}

}
