package iskallia.ispawner.block.entity;

import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.inventory.SimpleInventory;
import iskallia.ispawner.screen.handler.SpawnerScreenHandler;
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
import net.minecraft.util.Tickable;

public class ISpawnerBlockEntity extends BaseBlockEntity implements Tickable, NamedScreenHandlerFactory, InventoryChangedListener {

	public SimpleInventory eggsInventory = new SimpleInventory(27);

	public ISpawnerBlockEntity() {
		super(ModBlocks.Entities.SPAWNER);
		this.eggsInventory.addListener(this);
	}

	@Override
	public CompoundTag write(CompoundTag tag, UpdateType type) {
		return tag;
	}

	@Override
	public void read(BlockState state, CompoundTag tag, UpdateType type) {

	}

	@Override
	public void tick() {

	}

	@Override
	public void onInventoryChanged(Inventory sender) {

	}

	@Override
	public Text getDisplayName() {
		return new TranslatableText("container.spawner");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new SpawnerScreenHandler(syncId, inv);
	}

}
