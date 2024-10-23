package ro.sheet;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserTO {
    private String name;
    private String category;
    private String data;
    private String value;
    private String description;
    private String type;
}
