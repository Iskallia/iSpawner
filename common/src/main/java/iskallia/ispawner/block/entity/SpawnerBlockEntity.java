package iskallia.ispawner.block.entity;

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
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

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

	public BlockPos getCenterPos() {
		return this.getPos();
	}

}
