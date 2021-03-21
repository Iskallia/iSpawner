package iskallia.ispawner.world.spawner;

import com.google.common.collect.Maps;
import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.net.packet.IByteSerializable;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;

import java.util.Map;
import java.util.stream.Collectors;

public class SpawnerSettings implements IByteSerializable<SpawnerSettings>, INBTSerializable<CompoundTag> {

	protected int attempts = 4;
	protected int spawnDelay = 500;
	protected Mode mode = Mode.REDSTONE_PULSE;

	protected Map<SpawnGroup, CapRestriction> capRestrictions = Util.make(Maps.newHashMap(), map -> {
		map.put(SpawnGroup.MONSTER, new CapRestriction(SpawnGroup.MONSTER, 16));
		map.put(SpawnGroup.CREATURE, new CapRestriction(SpawnGroup.CREATURE, -1));
		map.put(SpawnGroup.AMBIENT, new CapRestriction(SpawnGroup.AMBIENT, -1));
		map.put(SpawnGroup.WATER_CREATURE, new CapRestriction(SpawnGroup.WATER_CREATURE, -1));
		map.put(SpawnGroup.WATER_AMBIENT, new CapRestriction(SpawnGroup.WATER_AMBIENT, -1));
		map.put(SpawnGroup.MISC, new CapRestriction(SpawnGroup.MISC, -1));
	});

	public int getAttempts() {
		return this.attempts;
	}

	public int getSpawnDelay() {
		return this.spawnDelay;
	}

	public Mode getMode() {
		return this.mode;
	}

	public Map<SpawnGroup, CapRestriction> getCapRestrictions() {
		return this.capRestrictions;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public void setSpawnDelay(int spawnDelay) {
		this.spawnDelay = spawnDelay;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void setMode(int ordinal) {
		this.mode = Mode.values()[ordinal];
	}

	public void setCapRestrictions(Map<SpawnGroup, CapRestriction> capRestrictions) {
		this.capRestrictions = capRestrictions;
	}

	@Override
	public SpawnerSettings writeToBuf(PacketByteBuf buf) {
		buf.writeVarInt(this.getAttempts());
		buf.writeVarInt(this.getSpawnDelay());
		buf.writeVarInt(this.getMode().ordinal());

		for(SpawnGroup spawnGroup: SpawnGroup.values()) {
			buf.writeVarInt(this.getCapRestrictions().getOrDefault(spawnGroup, new CapRestriction(spawnGroup, -1)).limit);
		}

		return this;
	}

	@Override
	public SpawnerSettings readFromBuf(PacketByteBuf buf) {
		this.setAttempts(buf.readVarInt());
		this.setSpawnDelay(buf.readVarInt());
		this.setMode(buf.readVarInt());

		for(SpawnGroup spawnGroup: SpawnGroup.values()) {
			this.getCapRestrictions().put(spawnGroup, new CapRestriction(spawnGroup, buf.readVarInt()));
		}

		return this;
	}

	@Override
	public CompoundTag writeToNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("Attempts", this.getAttempts());
		nbt.putInt("SpawnDelay", this.getSpawnDelay());
		nbt.putInt("Mode", this.getMode().ordinal());

		ListTag capList = new ListTag();

		this.getCapRestrictions().values().forEach(cap -> {
			capList.add(cap.writeToNBT());
		});

		nbt.put("CapRestrictions", capList);
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.setAttempts(nbt.getInt("Attempts"));
		this.setSpawnDelay(nbt.getInt("SpawnDelay"));
		this.setMode(nbt.getInt("Mode"));

		ListTag capList = nbt.getList("CapRestrictions", NBTConstants.COMPOUND);

		capList.stream().map(tag -> (CompoundTag)tag).forEach(cap -> {
			CapRestriction capRestriction = new CapRestriction(null, 0);
			capRestriction.readFromNBT(cap);
			this.getCapRestrictions().put(capRestriction.spawnGroup, capRestriction);
		});
	}

	public SpawnerSettings copy() {
		SpawnerSettings copy = new SpawnerSettings();
		copy.setAttempts(this.getAttempts());
		copy.setSpawnDelay(copy.getSpawnDelay());
		copy.setMode(this.getMode());
		copy.setCapRestrictions(this.getCapRestrictions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
				o -> new CapRestriction(o.getValue().spawnGroup, o.getValue().limit))));
		return copy;
	}

	public enum Mode {
		REDSTONE_PULSE("Redstone Pulse"), REDSTONE_ON("Redstone On"), ALWAYS_ON("Always On");

		public final String text;

		Mode(String text) {
			this.text = text;
		}
	}

	public static class CapRestriction implements INBTSerializable<CompoundTag> {
		protected SpawnGroup spawnGroup;
		public int limit;

		public CapRestriction(SpawnGroup spawnGroup, int limit) {
			this.spawnGroup = spawnGroup;
			this.limit = limit;
		}

		@Override
		public CompoundTag writeToNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("SpawnGroup", this.spawnGroup.ordinal());
			nbt.putInt("Limit", this.limit);
			return nbt;
		}

		@Override
		public void readFromNBT(CompoundTag nbt) {
			this.spawnGroup = SpawnGroup.values()[nbt.getInt("SpawnGroup")];
			this.limit = nbt.getInt("Limit");
		}
	}

}
