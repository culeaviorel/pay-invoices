package ro.homeAssistant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(force = true)
@ToString
public class Helper {
    private final String name;
    private final String type;
    private final String sensor;
    private final String hysteresis;
    private final String lower;
    private final String upper;
}
