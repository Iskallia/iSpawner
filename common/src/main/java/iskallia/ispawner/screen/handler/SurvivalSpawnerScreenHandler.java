package iskallia.ispawner.screen.handler;

import iskallia.ispawner.block.entity.SurvivalSpawnerBlockEntity;
import iskallia.ispawner.init.ModConfigs;
import iskallia.ispawner.init.ModMenus;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.screen.handler.slot.FilteredSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SurvivalSpawnerScreenHandler extends ScreenHandler {

	private final PlayerInventory playerInventory;
	private final SimpleInventory spawnerInventory;
	private final BlockPos spawnerPos;

	public SurvivalSpawnerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf extraData) {
		this(syncId, playerInventory, new SimpleInventory(1), extraData.readBlockPos());
	}

	public SurvivalSpawnerScreenHandler(int syncId, PlayerInventory playerInventory, SurvivalSpawnerBlockEntity spawner) {
		this(syncId, playerInventory, spawner.getInput(), spawner.getPos());
	}

	protected SurvivalSpawnerScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory spawnerInventory, BlockPos spawnerPos) {
		super(ModMenus.SURVIVAL_SPAWNER, syncId);
		this.playerInventory = playerInventory;
		this.spawnerInventory = spawnerInventory;
		this.spawnerPos = spawnerPos;
		spawnerInventory.onOpen(playerInventory.player);

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlot(new Slot(this.playerInventory, column + row * 9 + 9, 8 + column * 18, 159 + row * 18));
			}
		}
		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			this.addSlot(new Slot(this.playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 217));
		}

		this.addSlot(new FilteredSlot(this.spawnerInventory, 0, 47, 99, ModConfigs.SURVIVAL_SPAWNER::isWhitelisted));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		SurvivalSpawnerBlockEntity spawner = this.getSpawner();
		if (spawner == null) {
			return false;
		}
		return this.getSpawnerInventory().canPlayerUse(player) &&
				player.squaredDistanceTo(this.spawnerPos.getX() + 0.5D, this.spawnerPos.getY() + 0.5D, this.spawnerPos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			itemStack = slotStack.copy();

			if (index >= 0 && index < 36) {
				if (this.insertItem(slotStack, 36, 37, false)) {
					return itemStack;
				}
			}
			if (index >= 0 && index < 27) {
				if (!this.insertItem(slotStack, 27, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 27 && index < 36) {
				if (!this.insertItem(slotStack, 0, 27, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(slotStack, 0, 36, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			slot.onTakeItem(player, slotStack);
		}
		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.getSpawnerInventory().onClose(player);
	}

	public Inventory getSpawnerInventory() {
		return this.spawnerInventory;
	}

	@Nullable
	public EntityType<?> getSpawningEntity() {
		SurvivalSpawnerBlockEntity spawner = this.getSpawner();
		if (spawner == null) {
			return null;
		}
		//Survival spawner will always have an egg at index 0 if any slot
		ItemStack stack = spawner.getInventory().getStack(0);
		if (stack.getItem() instanceof SpawnEggItem) {
			return ((SpawnEggItem) stack.getItem()).getEntityType(stack.getTag());
		}
		return null;
	}

	public int getSpawnerCharges() {
		SurvivalSpawnerBlockEntity spawner = this.getSpawner();
		if (spawner == null) {
			return -1;
		}
		ItemStack stack = spawner.getInventory().getStack(0);
		return stack.getCount();
	}

	@Nullable
	public SurvivalSpawnerBlockEntity getSpawner() {
		BlockEntity be = this.playerInventory.player.getEntityWorld().getBlockEntity(this.spawnerPos);
		if (!(be instanceof SurvivalSpawnerBlockEntity)) {
			return null;
		}
		return (SurvivalSpawnerBlockEntity) be;
	}
}
