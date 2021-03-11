package iskallia.ispawner;

import iskallia.ispawner.init.ModRegistries;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ISpawner {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "ispawner";
    
    public static void init() {
        ModRegistries.register();
    }

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

}
