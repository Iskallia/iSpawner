package iskallia.ispawner.screen.handler;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.init.ModMenus;
import iskallia.ispawner.init.ModNetwork;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.net.packet.UpdateSettingsS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class SpawnerScreenHandler extends ScreenHandler {

	private final PlayerInventory playerInventory;
	private final SimpleInventory spawnerInventory;
	private SpawnerBlockEntity spawner;
	private boolean sentSettings;

	public SpawnerScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(27));
	}

	public SpawnerScreenHandler(int syncId, PlayerInventory playerInventory, SpawnerBlockEntity spawner) {
		this(syncId, playerInventory, spawner.getInventory());
		this.spawner = spawner;
	}

	protected SpawnerScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory spawnerInventory) {
		super(ModMenus.SPAWNER, syncId);
		this.playerInventory = playerInventory;
		this.spawnerInventory = spawnerInventory;
		spawnerInventory.onOpen(playerInventory.player);

		int n;
		int m;

		for(n = 0; n < 3; ++n) {
			for(m = 0; m < 9; ++m) {
				this.addSlot(new Slot(this.spawnerInventory, m + n * 9, 8 + m * 18 + 110, 18 + n * 18));
			}
		}

		for(n = 0; n < 3; ++n) {
			for(m = 0; m < 9; ++m) {
				this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18 + 110, 103 + n * 18 - 18));
			}
		}

		for(n = 0; n < 9; ++n) {
			this.addSlot(new Slot(playerInventory, n, 8 + n * 18 + 110, 161 - 18));
		}
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();

		if(this.spawner != null && !this.spawner.getWorld().isClient && !this.sentSettings) {
			ModNetwork.CHANNEL.sendToPlayer((ServerPlayerEntity)this.playerInventory.player,
					new UpdateSettingsS2CPacket(this.getSpawner().manager.settings));
			this.sentSettings = true;
		}
	}

	public SpawnerBlockEntity getSpawner() {
		return this.spawner;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.getSpawnerInventory().canPlayerUse(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if(slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();

			if(index < 3 * 9) {
				if(!this.insertItem(itemStack2, 3 * 9, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if(!this.insertItem(itemStack2, 0, 3 * 9, false)) {
				return ItemStack.EMPTY;
			}

			if(itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
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

}
