package iskallia.ispawner.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class IdentifierAdapter extends TypeAdapter<Identifier> {

	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
			return typeToken.getRawType() == Identifier.class ? (TypeAdapter<T>)new IdentifierAdapter() : null;
		}
	};

	public IdentifierAdapter() {

	}

	@Override
	public void write(JsonWriter out, Identifier value) throws IOException {
		out.value(value.toString());
	}

	@Override
	public Identifier read(JsonReader in) throws IOException {
		if(in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}

		return new Identifier(in.nextString());
	}

}
