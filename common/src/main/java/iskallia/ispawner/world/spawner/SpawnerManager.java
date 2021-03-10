package iskallia.ispawner.world.spawner;

import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.util.WeightedList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;

public class SpawnerManager implements INBTSerializable<CompoundTag> {

	public WeightedList<SpawnerAction> actions = new WeightedList<>();
	public SpawnerSettings settings = new SpawnerSettings();

	public SpawnerManager() {

	}

	public void addAction(SpawnerAction action, int weight) {
		Iterator<WeightedList.Entry<SpawnerAction>> iterator = this.actions.iterator();

		while(iterator.hasNext()) {
			WeightedList.Entry<SpawnerAction> entry = iterator.next();
			if(!entry.value.equals(action))continue;
			entry.weight += weight;

			if(entry.weight <= 0) {
				iterator.remove();
			}

			return;
		}

		this.actions.add(action, weight);
	}

	public void spawn(World world, Random random) {
		for(int i = 0; i < this.settings.attempts; i++) {
			this.actions.getRandom(random).execute(world, ItemStack.EMPTY); //TODO
		}
	}

	@Override
	public CompoundTag writeToNBT() {
		CompoundTag nbt = new CompoundTag();

		ListTag actionsList = new ListTag();
		this.actions.forEach(entry -> {
			CompoundTag tag = new CompoundTag();
			tag.put("Action", entry.value.writeToNBT());
			tag.putInt("Weight", entry.weight);
			actionsList.add(tag);
		});
		nbt.put("Actions", actionsList);

		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.actions.clear();
		ListTag actionsList = nbt.getList("Actions", NBTConstants.COMPOUND);

		IntStream.range(0, actionsList.size()).mapToObj(actionsList::getCompound).forEach(tag -> {
			SpawnerAction action = new SpawnerAction();
			action.readFromNBT(tag.getCompound("Action"));
			this.actions.add(action, tag.getInt("Weight"));
		});
	}

}
