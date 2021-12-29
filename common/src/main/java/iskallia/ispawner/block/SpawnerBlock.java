package iskallia.ispawner.block;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.init.ModItems;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import me.shedaniel.architectury.registry.MenuRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Random;

public class SpawnerBlock extends BlockWithEntity implements InventoryProvider {

	public static final DirectionProperty FACING = Properties.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;

	public SpawnerBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.getStackInHand(hand).getItem() == ModItems.SPAWNER_CONTROLLER) {
			return ActionResult.PASS;
		}
		if (world.isClient() || !(player instanceof ServerPlayerEntity)) {
			return ActionResult.SUCCESS;
		}
		this.openMenu(state, world, pos, (ServerPlayerEntity) player);
		return ActionResult.CONSUME;
	}

	protected void openMenu(BlockState state, World world, BlockPos pos, ServerPlayerEntity player) {
		NamedScreenHandlerFactory factory = this.createScreenHandlerFactory(state, world, pos);
		if (factory != null) {
			MenuRegistry.openMenu(player, factory);
			PiglinBrain.onGuardedBlockInteracted(player, true);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new SpawnerBlockEntity();
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
		//BlockEntity blockEntity = world.getBlockEntity(pos);
		//return blockEntity instanceof SpawnerBlockEntity ? ((SpawnerBlockEntity)blockEntity).inventory : null;
		return null; //Please don't hopper items out of the creative spawners...
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean powered = world.isReceivingRedstonePower(pos);

		if (powered != state.get(POWERED)) {
			if (powered) this.onPowered(world, pos);
			world.setBlockState(pos, state.with(POWERED, powered), 3);
		}
	}

	private void onPowered(World world, BlockPos pos) {
		if (world.isClient()) return;

		world.getServer().execute(() -> {
			BlockEntity blockEntity = world.getBlockEntity(pos);

			if (blockEntity instanceof SpawnerBlockEntity) {
				SpawnerBlockEntity spawner = (SpawnerBlockEntity) blockEntity;
				if (spawner.manager.settings.getMode() == SpawnerSettings.Mode.REDSTONE_PULSE) {
					spawner.manager.spawn(world, world.random, spawner);
				}
			}
		});
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if(state.get(POWERED) || blockEntity instanceof SpawnerBlockEntity
				&& ((SpawnerBlockEntity)blockEntity).manager.settings.getMode() == SpawnerSettings.Mode.ALWAYS_ON) {
			for(int i = 0; i < 5; i++) {
				double x = (double)pos.getX() + world.random.nextDouble();
				double y = (double)pos.getY() + world.random.nextDouble();
				double z = (double)pos.getZ() + world.random.nextDouble();
				world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
				world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}

		super.randomDisplayTick(state, world, pos, random);
	}

}
