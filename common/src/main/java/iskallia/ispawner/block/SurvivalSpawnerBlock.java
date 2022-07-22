package iskallia.ispawner.block;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

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
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof SurvivalSpawnerBlockEntity ? ((SurvivalSpawnerBlockEntity)blockEntity).input : null;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SurvivalSpawnerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlocks.Entities.SURVIVAL_SPAWNER, SurvivalSpawnerBlockEntity::tick);
    }

}
