package iskallia.ispawner.world.spawner;

import com.google.gson.annotations.Expose;
import iskallia.ispawner.nbt.INBTSerializable;
import iskallia.ispawner.nbt.NBTConstants;
import iskallia.ispawner.net.packet.IByteSerializable;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpawnerSettings implements IByteSerializable<SpawnerSettings>, INBTSerializable<NbtCompound> {

	@Expose protected int attempts = 4;
	@Expose protected int spawnDelay = 500;
	@Expose protected Mode mode = Mode.REDSTONE_ON;
	@Expose protected int checkRadius = 16;
	@Expose protected int playerRadius = 16;

	@Expose protected Map<SpawnGroup, CapRestriction> capRestrictions = Util.make(new HashMap<>(), map -> {
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

	public int getCheckRadius() {
		return this.checkRadius;
	}

	public int getPlayerRadius() {
		return this.playerRadius;
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

	public void setCheckRadius(int checkRadius) {
		this.checkRadius = checkRadius;
	}

	public void setPlayerRadius(int playerRadius) {
		this.playerRadius = playerRadius;
	}

	@Override
	public SpawnerSettings writeToBuf(PacketByteBuf buf) {
		buf.writeVarInt(this.getAttempts());
		buf.writeVarInt(this.getSpawnDelay());
		buf.writeVarInt(this.getMode().ordinal());

		for(SpawnGroup spawnGroup: SpawnGroup.values()) {
			buf.writeVarInt(this.getCapRestrictions().getOrDefault(spawnGroup, new CapRestriction(spawnGroup, -1)).limit);
		}

		buf.writeVarInt(this.getCheckRadius());
		buf.writeVarInt(this.getPlayerRadius());
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

		this.setCheckRadius(buf.readVarInt());
		this.setPlayerRadius(buf.readVarInt());
		return this;
	}

	@Override
	public NbtCompound writeToNBT() {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("Attempts", this.getAttempts());
		nbt.putInt("SpawnDelay", this.getSpawnDelay());
		nbt.putInt("Mode", this.getMode().ordinal());

		NbtList capList = new NbtList();
		this.getCapRestrictions().values().forEach(cap -> {
			capList.add(cap.writeToNBT());
		});
		nbt.put("CapRestrictions", capList);

		nbt.putInt("CheckRadius", this.getCheckRadius());
		nbt.putInt("PlayerRadius", this.getPlayerRadius());
		return nbt;
	}

	@Override
	public void readFromNBT(NbtCompound nbt) {
		this.setAttempts(nbt.getInt("Attempts"));
		this.setSpawnDelay(nbt.getInt("SpawnDelay"));
		this.setMode(nbt.getInt("Mode"));

		NbtList capList = nbt.getList("CapRestrictions", NBTConstants.COMPOUND);

		capList.stream().map(tag -> (NbtCompound)tag).forEach(cap -> {
			CapRestriction capRestriction = new CapRestriction(null, 0);
			capRestriction.readFromNBT(cap);
			this.getCapRestrictions().put(capRestriction.spawnGroup, capRestriction);
		});

		if(nbt.contains("CheckRadius", NBTConstants.INT)) {
			this.checkRadius = nbt.getInt("CheckRadius");
		}

		if(nbt.contains("PlayerRadius", NBTConstants.INT)) {
			this.playerRadius = nbt.getInt("PlayerRadius");
		}
	}

	public SpawnerSettings copy() {
		SpawnerSettings copy = new SpawnerSettings();
		copy.setAttempts(this.getAttempts());
		copy.setSpawnDelay(this.getSpawnDelay());
		copy.setMode(this.getMode());
		copy.setCapRestrictions(this.getCapRestrictions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
				o -> new CapRestriction(o.getValue().spawnGroup, o.getValue().limit))));
		copy.setCheckRadius(this.getCheckRadius());
		copy.setPlayerRadius(this.getPlayerRadius());
		return copy;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)return true;
		if(!(o instanceof SpawnerSettings))return false;
		SpawnerSettings other = (SpawnerSettings)o;
		return this.getAttempts() == other.getAttempts()
			&& this.getSpawnDelay() == other.getSpawnDelay()
			&& this.getCheckRadius() == other.getCheckRadius()
			&& this.getPlayerRadius() == other.getPlayerRadius()
			&& this.getMode() == other.getMode()
			&& Objects.equals(this.getCapRestrictions(), other.getCapRestrictions());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAttempts(), getSpawnDelay(), getMode(), getCheckRadius(), getPlayerRadius(), getCapRestrictions());
	}

	public enum Mode {
		REDSTONE_PULSE("Redstone Pulse"), REDSTONE_ON("Redstone On"), ALWAYS_ON("Always On");

		public final String text;

		Mode(String text) {
			this.text = text;
		}
	}

	public static class CapRestriction implements INBTSerializable<NbtCompound> {
		@Expose protected SpawnGroup spawnGroup;
		@Expose public int limit;

		public CapRestriction(SpawnGroup spawnGroup, int limit) {
			this.spawnGroup = spawnGroup;
			this.limit = limit;
		}

		@Override
		public NbtCompound writeToNBT() {
			NbtCompound nbt = new NbtCompound();
			nbt.putInt("SpawnGroup", this.spawnGroup.ordinal());
			nbt.putInt("Limit", this.limit);
			return nbt;
		}

		@Override
		public void readFromNBT(NbtCompound nbt) {
			this.spawnGroup = SpawnGroup.values()[nbt.getInt("SpawnGroup")];
			this.limit = nbt.getInt("Limit");
		}
	}

}
