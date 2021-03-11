package iskallia.ispawner.block.entity;

import iskallia.ispawner.block.SpawnerBlock;
import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerManager;
import iskallia.ispawner.world.spawner.SpawnerRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SpawnerBlockEntity extends BaseBlockEntity implements Tickable, NamedScreenHandlerFactory, InventoryChangedListener {

	public SimpleInventory inventory = new SimpleInventory(27);
	public SpawnerManager manager = new SpawnerManager();
	public SpawnerRenderer renderer = new SpawnerRenderer();

	public SpawnerBlockEntity() {
		super(ModBlocks.Entities.SPAWNER);
		this.inventory.addListener(this);
	}

	@Override
	public CompoundTag write(CompoundTag tag, UpdateType type) {
		tag.put("Inventory", this.inventory.writeToNBT());
		tag.put("Manager", this.manager.writeToNBT());
		return tag;
	}

	@Override
	public void read(BlockState state, CompoundTag tag, UpdateType type) {
		if(tag.contains("Inventory", NBTConstants.COMPOUND)) {
			this.inventory.readFromNBT(tag.getCompound("Inventory"));
		}

		if(tag.contains("Manager", NBTConstants.COMPOUND)) {
			this.manager.readFromNBT(tag.getCompound("Manager"));
		}
	}

	@Override
	public void tick() {
		if(this.getWorld() == null)return;

		if(this.getWorld().getTime() % 100 == 0) {
			this.manager.spawn(this.getWorld(), this.getWorld().random, this);
		}
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		this.markDirty();
	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("container.spawner");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new SpawnerScreenHandler(syncId, inv, this.inventory);
	}

	public BlockPos getOffset() {
		return new BlockPos(0, 0, 0);
	}

	public BlockPos getCenterPos() {
		return this.getPos().add(this.getOffset());
	}

	public BlockRotation getRotation() {
		if(this.getWorld() != null) {
			Direction facing = this.getWorld().getBlockState(this.getPos()).get(SpawnerBlock.FACING);

			if(facing == Direction.NORTH) {
				return BlockRotation.NONE;
			} else if(facing == Direction.SOUTH) {
				return BlockRotation.CLOCKWISE_180;
			} else if(facing == Direction.WEST) {
				return BlockRotation.CLOCKWISE_90;
			} else if(facing == Direction.EAST) {
				return BlockRotation.COUNTERCLOCKWISE_90;
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

}
