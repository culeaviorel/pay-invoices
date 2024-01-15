package ro.homeAssistant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(force = true)
@ToString
public class Trigger {
    private final String device;
    private final String trigger;
    private final String duration;
    private final String triggerId;
}
