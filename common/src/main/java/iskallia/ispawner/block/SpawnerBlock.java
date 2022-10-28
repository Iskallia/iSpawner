package iskallia.ispawner.block;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.init.ModItems;
import iskallia.ispawner.world.spawner.SpawnerSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Locale;
import java.util.Random;

public class SpawnerBlock extends BlockWithEntity implements InventoryProvider {

	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<Mirror> MIRROR = EnumProperty.of("mirror", Mirror.class);
	public static final BooleanProperty POWERED = Properties.POWERED;

	public SpawnerBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(MIRROR, Mirror.NONE)
			.with(POWERED, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, MIRROR, POWERED);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		int index = SpawnerRotation.getIndex(this.toRotation(state.get(FACING)), state.get(MIRROR).toBlockMirror());
		index = SpawnerRotation.multiply(index, rotation, BlockMirror.NONE);
		return state.with(FACING, this.toFacing(SpawnerRotation.getRotation(index)))
					.with(MIRROR, Mirror.fromBlockMirror(SpawnerRotation.getMirror(index)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		int index = SpawnerRotation.getIndex(this.toRotation(state.get(FACING)), state.get(MIRROR).toBlockMirror());
		index = SpawnerRotation.multiply(index, BlockRotation.NONE, mirror);
		return state.with(FACING, this.toFacing(SpawnerRotation.getRotation(index)))
			.with(MIRROR, Mirror.fromBlockMirror(SpawnerRotation.getMirror(index)));
	}

	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	public BlockRotation toRotation(Direction facing) {
		return switch(facing) {
			case NORTH -> BlockRotation.NONE;
			case SOUTH -> BlockRotation.CLOCKWISE_180;
			case WEST -> BlockRotation.COUNTERCLOCKWISE_90;
			case EAST -> BlockRotation.CLOCKWISE_90;
			default -> throw new UnsupportedOperationException();
		};
	}

	public Direction toFacing(BlockRotation rotation) {
		return switch(rotation) {
			case NONE -> Direction.NORTH;
			case CLOCKWISE_180 -> Direction.SOUTH;
			case COUNTERCLOCKWISE_90 -> Direction.WEST;
			case CLOCKWISE_90 -> Direction.EAST;
		};
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.getStackInHand(hand).getItem() == ModItems.SPAWNER_CONTROLLER.get()) {
			return ActionResult.PASS;
		}
		if (world.isClient() || !(player instanceof ServerPlayerEntity)) {
			return ActionResult.SUCCESS;
		}
		this.openMenu(state, world, pos, (ServerPlayerEntity) player);
		return ActionResult.CONSUME;
	}

	protected void openMenu(BlockState state, World world, BlockPos pos, ServerPlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);

		if(be instanceof ExtendedMenuProvider) {
			MenuRegistry.openExtendedMenu(player, (ExtendedMenuProvider)be);
			PiglinBrain.onGuardedBlockInteracted(player, true);
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SpawnerBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlocks.Entities.SPAWNER.get(), SpawnerBlockEntity::tick);
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

	public enum Mirror implements StringIdentifiable {
		NONE, LEFT_RIGHT, FRONT_BACK;

		public BlockMirror toBlockMirror() {
			return BlockMirror.values()[this.ordinal()];
		}

		public static Mirror fromBlockMirror(BlockMirror blockMirror) {
			return Mirror.values()[blockMirror.ordinal()];
		}

		@Override
		public String asString() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

}
