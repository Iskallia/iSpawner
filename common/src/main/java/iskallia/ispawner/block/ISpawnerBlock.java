package iskallia.ispawner.block;

import iskallia.ispawner.block.entity.ISpawnerBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ISpawnerBlock extends BlockWithEntity implements InventoryProvider {

	public ISpawnerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(world.isClient) {
			return ActionResult.SUCCESS;
		}

		NamedScreenHandlerFactory factory = this.createScreenHandlerFactory(state, world, pos);

		if(factory != null) {
			player.openHandledScreen(factory);
			PiglinBrain.onGuardedBlockInteracted(player, true);
		}

		return ActionResult.CONSUME;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new ISpawnerBlockEntity();
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof ISpawnerBlockEntity ? ((ISpawnerBlockEntity)blockEntity).eggsInventory : null;
	}

}
