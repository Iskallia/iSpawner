package iskallia.ispawner.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import iskallia.ispawner.ISpawner;

import java.io.*;
import java.lang.reflect.Type;

public abstract class Config {

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
	protected String root = "config/" + ISpawner.MOD_ID + "/";
	protected String extension = ".json";

	public void generateConfig() {
		this.reset();

		try {
			this.writeConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getConfigFile() {
		return new File(this.root + this.getName() + this.extension);
	}

	public abstract String getName();

	public <T extends Config> T readConfig() {
		try {
			return GSON.fromJson(new FileReader(this.getConfigFile()), (Type)this.getClass());
		} catch(FileNotFoundException e) {
			this.generateConfig();
		}

		return (T)this;
	}

	protected abstract void reset();

	public void writeConfig() throws IOException {
		File dir = new File(this.root);
		if(!dir.exists() && !dir.mkdirs())return;
		if(!this.getConfigFile().exists() && !this.getConfigFile().createNewFile())return;
		FileWriter writer = new FileWriter(this.getConfigFile());
		GSON.toJson(this, writer);
		writer.flush();
		writer.close();
	}

}
