package ro.anaf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fields {
    private Integer cui;
    private String data;

    @Override
    public String toString() {
        return JsonUtils.getGson().toJson(this);
    }
}
