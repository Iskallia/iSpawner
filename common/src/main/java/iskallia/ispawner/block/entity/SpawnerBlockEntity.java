package iskallia.ispawner.block.entity;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import iskallia.ispawner.block.SpawnerBlock;
import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerExecution;
import iskallia.ispawner.world.spawner.SpawnerManager;
import iskallia.ispawner.world.spawner.SpawnerRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpawnerBlockEntity extends BaseBlockEntity implements ExtendedMenuProvider, NamedScreenHandlerFactory, InventoryChangedListener {

	public final SimpleInventory inventory;
	public SpawnerManager manager = new SpawnerManager();
	public SpawnerRenderer renderer = new SpawnerRenderer();
	public BlockPos offset = BlockPos.ORIGIN;

	public SpawnerBlockEntity(BlockPos pos, BlockState state) {
		this(ModBlocks.Entities.SPAWNER.get(), pos, state);
	}

	public SpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.inventory = this.createInventory();
		this.inventory.addListener(this);
	}

	protected SimpleInventory createInventory() {
		return new SimpleInventory(27);
	}

	public final SimpleInventory getInventory() {
		return this.inventory;
	}

	@Override
	public NbtCompound write(NbtCompound tag, UpdateType type) {
		tag.put("Inventory", this.getInventory().writeToNBT());
		tag.put("Manager", this.manager.writeToNBT());
		tag.put("Offset", NbtHelper.fromBlockPos(this.offset));
		return tag;
	}

	@Override
	public void read(NbtCompound tag, UpdateType type) {
		if(tag.contains("Inventory", NBTConstants.COMPOUND)) {
			this.getInventory().readFromNBT(tag.getCompound("Inventory"));
		}

		if(tag.contains("Manager", NBTConstants.COMPOUND)) {
			this.manager.readFromNBT(tag.getCompound("Manager"));
		}

		this.offset = NbtHelper.toBlockPos(tag.getCompound("Offset"));
	}

	@Override
	public void saveExtraData(PacketByteBuf buf) {
		buf.writeBlockPos(this.getPos());
	}

	public static void tick(World world, BlockPos pos, BlockState state, SpawnerBlockEntity spawner) {
		if(world == null || world.isClient()) return;
		spawner.manager.tick(world, world.getRandom(), spawner, SpawnerExecution.USE);
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		this.sendClientUpdates();
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("container.spawner");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return player.isCreative() ? new SpawnerScreenHandler(syncId, inv, this) : null;
	}

	public boolean canUseCharge(ItemStack stack, int index) {
		return true;
	}

	public void onChargeUsed(ItemStack stack, int index) {

	}

	public BlockPos getOffset() {
		return mirror(this.offset.rotate(this.getRotation()), this.getMirror());
	}

	public void setOffset(BlockPos offset) {
		this.offset = offset;
	}

	public BlockPos getCenterPos() {
		return this.getPos().add(this.getOffset());
	}

	public BlockRotation getRotation() {
		if(this.getWorld() != null) {
			Direction facing = this.getCachedState().get(SpawnerBlock.FACING);

			if(facing == Direction.NORTH) {
				return BlockRotation.NONE;
			} else if(facing == Direction.SOUTH) {
				return BlockRotation.CLOCKWISE_180;
			} else if(facing == Direction.WEST) {
				return BlockRotation.COUNTERCLOCKWISE_90;
			} else if(facing == Direction.EAST) {
				return BlockRotation.CLOCKWISE_90;
			}
		}

		return null;
	}

	public BlockRotation getReverseRotation() {
		BlockRotation rotation = this.getRotation();

		if(rotation == BlockRotation.NONE) {
			return BlockRotation.NONE;
		} else if(rotation == BlockRotation.CLOCKWISE_90) {
			return BlockRotation.COUNTERCLOCKWISE_90;
		} else if(rotation == BlockRotation.CLOCKWISE_180) {
			return BlockRotation.CLOCKWISE_180;
		} else if(rotation == BlockRotation.COUNTERCLOCKWISE_90) {
			return BlockRotation.CLOCKWISE_90;
		}

		return null;
	}

	public BlockMirror getMirror() {
		return this.getCachedState().get(SpawnerBlock.MIRROR).toBlockMirror();
	}

	public static BlockPos mirror(BlockPos pos, SpawnerBlock.Mirror mirror) {
		return switch(mirror) {
			case FRONT_BACK -> new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
			case LEFT_RIGHT -> new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
			case NONE       -> pos;
		};
	}

	public static BlockPos mirror(BlockPos pos, BlockMirror mirror) {
		return switch(mirror) {
			case FRONT_BACK -> new BlockPos(-pos.getX(), pos.getY(), pos.getZ());
			case LEFT_RIGHT -> new BlockPos(pos.getX(), pos.getY(), -pos.getZ());
			case NONE       -> pos;
		};
	}

	public static Direction mirror(Direction facing, BlockMirror mirror) {
		if(facing.getAxis() == Direction.Axis.X && mirror == BlockMirror.FRONT_BACK
			|| facing.getAxis() == Direction.Axis.Z && mirror == BlockMirror.LEFT_RIGHT) {
			return facing.getOpposite();
		}

		return facing;
	}

	public static Vec3d mirror(Vec3d pos, BlockMirror mirror) {
		return switch(mirror) {
			case FRONT_BACK -> new Vec3d(-pos.getX(), pos.getY(), pos.getZ());
			case LEFT_RIGHT -> new Vec3d(pos.getX(), pos.getY(), -pos.getZ());
			case NONE       -> pos;
		};
	}

}
