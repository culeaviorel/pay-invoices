package ro.anaf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {

    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Gson getGson() {
        return GSON;
    }
}
