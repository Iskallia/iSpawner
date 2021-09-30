package iskallia.ispawner.block.entity;

import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.init.ModConfigs;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.item.nbt.SpawnData;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.screen.handler.SurvivalSpawnerScreenHandler;
import iskallia.ispawner.world.spawner.SpawnerAction;
import me.shedaniel.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class SurvivalSpawnerBlockEntity extends SpawnerBlockEntity implements ExtendedMenuProvider {

	public SimpleInventory input = new SimpleInventory(1) {
		@Override
		public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
			return ModConfigs.SURVIVAL_SPAWNER.isWhitelisted(stack);
		}
	};

	public SurvivalSpawnerBlockEntity() {
		super(ModBlocks.Entities.SURVIVAL_SPAWNER);
		this.input.addListener(this);
	}

	public SimpleInventory getInput() {
		return this.input;
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.manager.settings.equals(ModConfigs.SURVIVAL_SPAWNER.defaultSettings)) {
			this.manager.settings = ModConfigs.SURVIVAL_SPAWNER.defaultSettings.copy();
			this.sendClientUpdates();
		}

		if (this.inventory.isEmpty()) {
			for (int i = 0; i < this.input.size(); i++) {
				ItemStack stack = this.input.getStack(i);
				if (stack.isEmpty()) continue;
				if (!this.input.canInsert(i, stack, Direction.NORTH)) continue;

				OptionalInt emptySlot = this.inventory.getEmptySlot();

				if (emptySlot.isPresent()) {
					ItemStack newStack = new ItemStack(stack.getItem());
					newStack.setCount(new SpawnData(stack).getCharges());
					this.inventory.setStack(emptySlot.getAsInt(), newStack);
					stack.decrement(1);
					this.input.setStack(i, stack);
					break;
				}
			}
		}

		if(this.manager.actions.isEmpty()) {
			for(int x = -4; x <= 4; x++) {
				for(int z = -4; z <= 4; z++) {
					for(int y = -2; y <= 1; y++) {
						int weight = 4 - Math.max(Math.abs(x), Math.abs(z)) + 1;
						BlockRotation rotation = this.getReverseRotation();
						Vec3d hitPosOffset = new Vec3d(0.5D, 1.0D, 0.5D);

						this.manager.addAction(new SpawnerAction(
							new BlockPos(x, y, z).rotate(rotation),
							rotation.rotate(Direction.UP),
							SpawnerAction.rotate(rotation, hitPosOffset),
							Hand.MAIN_HAND,
							new Direction[] {Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN}), weight);
					}
				}
			}
		}
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new SurvivalSpawnerScreenHandler(syncId, inv, this);
	}

	@Override
	public void saveExtraData(PacketByteBuf buf) {
		buf.writeBlockPos(this.getPos());
	}

	@Override
	public boolean onChargeUsed(ItemStack stack, int index) {
		stack.decrement(1);
		this.inventory.setStack(index, stack);
		return !stack.isEmpty();
	}

	@Override
	public CompoundTag write(CompoundTag tag, UpdateType type) {
		CompoundTag nbt = super.write(tag, type);
		nbt.put("Input", this.input.writeToNBT());
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundTag tag, UpdateType type) {
		super.read(state, tag, type);

		if(tag.contains("Input", NBTConstants.COMPOUND)) {
			this.input.readFromNBT(tag.getCompound("Input"));
		}
	}

}
