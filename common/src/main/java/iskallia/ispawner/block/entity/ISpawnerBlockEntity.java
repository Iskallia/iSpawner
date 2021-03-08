package iskallia.ispawner.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;

public class ISpawnerBlockEntity extends BlockEntity implements Tickable {

	public ISpawnerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void tick() {

	}

}
