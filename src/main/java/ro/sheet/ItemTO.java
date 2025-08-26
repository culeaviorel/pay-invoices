package ro.sheet;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemTO {
    private String fileName;
    private String decizia;
    private String decont;
    private String type;
    private String plata;
    private String category;
    private String data;
    private String value;
    private String description;
}
