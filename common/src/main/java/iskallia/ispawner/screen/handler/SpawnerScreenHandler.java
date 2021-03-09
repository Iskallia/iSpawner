package iskallia.ispawner.screen.handler;

import iskallia.ispawner.init.ModMenus;
import iskallia.ispawner.inventory.SimpleInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SpawnerScreenHandler extends ScreenHandler {

	private final SimpleInventory spawnerInventory;

	public SpawnerScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(27));
	}

	public SpawnerScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory spawnerInventory) {
		super(ModMenus.SPAWNER, syncId);
		this.spawnerInventory = spawnerInventory;
		spawnerInventory.onOpen(playerInventory.player);

		int n;
		int m;

		for(n = 0; n < 3; ++n) {
			for(m = 0; m < 9; ++m) {
				this.addSlot(new Slot(this.spawnerInventory, m + n * 9, 8 + m * 18, 18 + n * 18));
			}
		}

		for(n = 0; n < 3; ++n) {
			for(m = 0; m < 9; ++m) {
				this.addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 - 18));
			}
		}

		for(n = 0; n < 9; ++n) {
			this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 161 - 18));
		}

	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

}
