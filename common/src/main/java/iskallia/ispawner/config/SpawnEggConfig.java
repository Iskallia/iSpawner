package iskallia.ispawner.config;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpawnEggConfig extends Config {

	@Expose public Map<Identifier, EggColor> colors;

    @Override
    public String getName() {
        return "spawn_egg";
    }

    @Override
    protected void reset() {
        this.colors = new LinkedHashMap<>();

        for(EntityType<?> type : Registry.ENTITY_TYPE) {
            SpawnEggItem egg = SpawnEggItem.forEntity(type);
            if(egg == null) continue;
            this.colors.put(EntityType.getId(type), new EggColor(egg.getColor(0), egg.getColor(1)));
        }
    }

    public int getColor(EntityType<?> type, int tintIndex) {
        EggColor color = this.colors.get(EntityType.getId(type));
        SpawnEggItem egg = SpawnEggItem.forEntity(type);

        if(color != null) {
            return tintIndex == 0 ? color.primary : color.secondary;
        } else if(egg != null) {
            return egg.getColor(tintIndex);
        }

        return tintIndex == 0 ? 0xFFFFFF : 0x000000;
    }

    private static class EggColor {
        @Expose public int primary;
        @Expose public int secondary;

        public EggColor(int primary, int secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }
    }

}
