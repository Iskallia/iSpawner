package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.util.WeightedList;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpawnerManager implements INBTSerializable<CompoundTag> {

	public WeightedList<SpawnerAction> actions = new WeightedList<>();
	public SpawnerSettings settings = new SpawnerSettings();
	public BlockBox boundingBox;

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

	private void updateCache(SpawnerBlockEntity entity) {
		if(actions.isEmpty()) {
			this.boundingBox = BlockBox.empty();
			return;
		}

		List<BlockPos> positions = this.actions.stream()
				.map(entry -> entry.value)
				.map(action -> action.toAbsolute(entity.getCenterPos(), entity.getRotation()))
				.map(SpawnerAction::getPos)
				.collect(Collectors.toList());

		this.boundingBox = new BlockBox(positions.get(0), positions.get(0));

		for(int i = 1; i < this.actions.size(); i++) {
			this.boundingBox.encompass(new BlockBox(positions.get(i), positions.get(i)));
		}
	}

	public void tick(World world, Random random, SpawnerBlockEntity entity) {
		if(this.settings.getSpawnDelay() == 0
				|| world.getTime() % this.settings.getSpawnDelay() != 0)return;

		int power = world.getReceivedRedstonePower(entity.getPos());

		if(this.settings.getMode() == SpawnerSettings.Mode.ALWAYS_ON ||
				(this.settings.getMode() == SpawnerSettings.Mode.REDSTONE_ON && power != 0)) {
			this.spawn(world, random, entity); }

	}

	public void spawn(World world, Random random, SpawnerBlockEntity entity) {
		WeightedList<ItemStack> pool = new WeightedList<>();

		IntStream.range(0, entity.inventory.size())
				.mapToObj(i -> entity.inventory.getStack(i))
				.filter(stack -> !stack.isEmpty())
				.forEach(stack -> pool.add(stack.copy(), stack.getCount()));


		if(pool.isEmpty())return;

		this.updateCache(entity);
		Map<SpawnGroup, Integer> entityMap = new HashMap<>();

		world.getOtherEntities(null, Box.from(this.boundingBox).expand(3.0D)).forEach(e -> {
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
			this.actions.getRandom(random)
					.toAbsolute(entity.getCenterPos(), entity.getRotation())
					.execute(world, pool.getRandom(random).copy());
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

}
