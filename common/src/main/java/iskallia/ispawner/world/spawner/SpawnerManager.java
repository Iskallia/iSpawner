package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.util.WeightedList;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

		if(weight > 0) {
			this.actions.add(action, weight);
		}
	}

	public void tick(World world, Random random, SpawnerBlockEntity entity) {
		if(this.settings.getSpawnDelay() == 0
				|| world.getTime() % this.settings.getSpawnDelay() != 0)return;

		int power = world.getReceivedRedstonePower(entity.getPos());

		if(this.settings.getMode() == SpawnerSettings.Mode.ALWAYS_ON ||
				(this.settings.getMode() == SpawnerSettings.Mode.REDSTONE_ON && power != 0)) {
			this.spawn(world, random, entity);
		}
	}

	public void spawn(World world, Random random, SpawnerBlockEntity entity) {
		if(this.actions.isEmpty())return;

		WeightedList<Entry> pool = new WeightedList<>();

		IntStream.range(0, entity.inventory.size())
				.mapToObj(i -> new Entry(i, entity.inventory.getStack(i)))
				.filter(entry -> !entry.stack.isEmpty())
				.forEach(entry -> pool.add(entry, entry.stack.getCount()));

		if(pool.isEmpty())return;
		BlockPos pos = entity.getPos();

		PlayerEntity closestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(),
			this.settings.getPlayerRadius(), false);

		if(closestPlayer == null)return;

		Map<SpawnGroup, Integer> entityMap = new HashMap<>();

		BlockBox spawnerBox = BlockBox.create(
			pos.getX(), pos.getY(), pos.getZ(),
			pos.getX(), pos.getY(), pos.getZ()
		);

		world.getOtherEntities(null, Box.from(spawnerBox).expand(this.settings.getCheckRadius())).forEach(e -> {
			SpawnGroup spawnGroup = e.getType().getSpawnGroup();
			entityMap.put(spawnGroup, entityMap.getOrDefault(spawnGroup, 0) + 1);
		});

		for(Map.Entry<SpawnGroup, Integer> entry: entityMap.entrySet()) {
			int limit = this.settings.getCapRestrictions().get(entry.getKey()).limit;

			if(limit > 0 && entry.getValue() >= limit) {
				return;
			}
		}

		for(int i = 0; i < this.settings.getAttempts(); i++) {
			Entry entry = pool.getRandom(random);

			this.actions.getRandom(random)
					.toAbsolute(entity.getCenterPos(), entity.getRotation())
					.execute(world, entry.stack.copy());

			entity.onChargeUsed(entry.stack, entry.index);
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
		nbt.put("Settings", this.settings.writeToNBT());
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

		this.settings.readFromNBT(nbt.getCompound("Settings"));
	}

	public static class Entry {
		public int index;
		public ItemStack stack;

		public Entry(int index, ItemStack stack) {
			this.index = index;
			this.stack = stack;
		}
	}

}
