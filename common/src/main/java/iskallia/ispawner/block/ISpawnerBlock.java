package iskallia.ispawner.block;

import iskallia.ispawner.block.entity.ISpawnerBlockEntity;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class ISpawnerBlock extends BlockWithEntity {

	public ISpawnerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ISpawnerBlockEntity();
	}

}
