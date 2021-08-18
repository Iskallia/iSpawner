package iskallia.ispawner.block;

import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import me.shedaniel.architectury.registry.MenuRegistry;
import me.shedaniel.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SurvivalSpawnerBlock extends SpawnerBlock {

    public SurvivalSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void openMenu(BlockState state, World world, BlockPos pos, ServerPlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ExtendedMenuProvider) {
            MenuRegistry.openExtendedMenu(player, (ExtendedMenuProvider) be);
            PiglinBrain.onGuardedBlockInteracted(player, true);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SurvivalSpawnerBlockEntity();
    }
}
