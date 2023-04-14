package iskallia.ispawner.world.spawner;

import iskallia.ispawner.block.entity.SpawnerBlockEntity;
import iskallia.ispawner.init.ModBlocks;
import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.util.WeightedList;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

public class SpawnerManager implements INBTSerializable<NbtCompound> {

	public WeightedList<SpawnerAction> actions = new WeightedList<>();
	public SpawnerSettings settings = new SpawnerSettings();
	public int spawnTimer;
	public int usesLeft = -1;
	public int waveCounter;

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

	public void tick(World world, Random random, SpawnerBlockEntity entity, SpawnerExecution execution) {
		BlockPos pos = entity.getPos();

		if(this.settings.getPlayerRadius() >= 0) {
			PlayerEntity closestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(),
					this.settings.getPlayerRadius(), false);
			if(closestPlayer == null) return;
		}

		if(this.settings.getSpawnDelay() < 0) return;
		int power = world.getReceivedRedstonePower(pos);

		if(this.settings.getMode() == SpawnerSettings.Mode.ALWAYS_ON
			|| this.settings.getMode() == SpawnerSettings.Mode.REDSTONE_ON && power > 0) {
			boolean shouldSpawn = this.spawnTimer == 0;
			this.spawnTimer = MathHelper.clamp(this.spawnTimer, 0, this.settings.spawnDelay);
			this.spawnTimer = shouldSpawn ? this.settings.spawnDelay : this.spawnTimer - 1;
			if(!shouldSpawn) return;
			this.spawn(world, random, entity, execution);
			this.tickUses(world, pos);
			this.waveCounter++;
		}
	}

	private void tickUses(World world, BlockPos pos) {
		if(this.usesLeft < 0) return;
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.2F, 0.2F);
		world.addBlockBreakParticles(pos, ModBlocks.SPAWNER.get().getDefaultState());
		if(--this.usesLeft != 0) return;
		world.breakBlock(pos, false);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.2F, 0.2F);
	}

	public void spawn(World world, Random random, SpawnerBlockEntity entity, SpawnerExecution execution) {
		if(this.actions.isEmpty()) return;

		WeightedList<Entry> pool = new WeightedList<>();

		IntStream.range(0, entity.inventory.size())
				.mapToObj(i -> new Entry(i, entity.inventory.getStack(i).copy()))
				.filter(entry -> !entry.stack.isEmpty())
				.forEach(entry -> pool.add(entry, entry.stack.getCount()));

		if(pool.isEmpty()) return;
		BlockPos pos = entity.getPos();

		Map<SpawnGroup, Integer> entityMap = new HashMap<>();

		BlockBox spawnerBox = BlockBox.create(pos, pos);

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

			if(entity.canUseCharge(entry.stack, entry.index)) {
				boolean result = this.actions.getRandom(random)
						.toAbsolute(entity.getCenterPos(), entity.getRotation(), entity.getMirror())
						.execute(world, entry.stack.copy(), new SpawnerContext(execution, entity));

				if(result) {
					entity.onChargeUsed(entry.stack, entry.index);
				}
			}
		}
	}

	@Override
	public NbtCompound writeToNBT() {
		NbtCompound nbt = new NbtCompound();
		NbtList actionsList = new NbtList();

		this.actions.forEach(entry -> {
			NbtCompound tag = new NbtCompound();
			tag.put("Action", entry.value.writeToNBT());
			tag.putInt("Weight", entry.weight);
			actionsList.add(tag);
		});

		nbt.put("Actions", actionsList);
		nbt.put("Settings", this.settings.writeToNBT());
		nbt.putInt("SpawnTimer", this.spawnTimer);
		if(this.usesLeft >= 0) nbt.putInt("UsesLeft", this.usesLeft);
		nbt.putInt("WaveCounter", this.waveCounter);
		return nbt;
	}

	@Override
	public void readFromNBT(NbtCompound nbt) {
		this.actions.clear();
		NbtList actionsList = nbt.getList("Actions", NBTConstants.COMPOUND);

		IntStream.range(0, actionsList.size()).mapToObj(actionsList::getCompound).forEach(tag -> {
			SpawnerAction action = new SpawnerAction();
			action.readFromNBT(tag.getCompound("Action"));
			this.actions.add(action, tag.getInt("Weight"));
		});

		this.settings.readFromNBT(nbt.getCompound("Settings"));
		this.spawnTimer = nbt.getInt("SpawnTimer");
		this.usesLeft = nbt.contains("UsesLeft", NBTConstants.INT) ? nbt.getInt("UsesLeft") : -1;
		this.waveCounter = nbt.getInt("WaveCounter");
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
