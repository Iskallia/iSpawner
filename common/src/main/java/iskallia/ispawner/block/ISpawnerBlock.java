package iskallia.ispawner.block;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class ISpawnerBlock extends BlockWithEntity {

	protected ISpawnerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return null;
	}

}
