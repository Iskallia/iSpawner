package iskallia.ispawner.block;

import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class SurvivalSpawnerBlock extends SpawnerBlock {

    public SurvivalSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SurvivalSpawnerBlockEntity();
    }

}
