package iskallia.ispawner.config.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;

public class ItemAdapter extends TypeAdapter<Item> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Item.class ? (TypeAdapter<T>)new ItemAdapter() : null;
        }
    };

    public ItemAdapter() {

    }

    @Override
    public void write(JsonWriter out, Item value) throws IOException {
        out.value(Registry.ITEM.getId(value).toString());
    }

    @Override
    public Item read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return Registry.ITEM.get(new Identifier(in.nextString()));
    }

}
