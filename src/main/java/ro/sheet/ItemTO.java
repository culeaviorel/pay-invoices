package ro.sheet;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemTO {
    private String fileName;
    private String type;
    private String category;
    private String data;
    private String value;
    private String description;
}
