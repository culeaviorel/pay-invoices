package ro.homeAssistant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(force = true)
@ToString
public class Option {
    private final String triggerId;
    private final String service;
    private final String device;
    private final String temperature;
    private final String mode;
    private final String message;
}
