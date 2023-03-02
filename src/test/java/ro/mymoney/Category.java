package ro.mymoney;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {
    private String name;
    private List<String> values;

    public Category(String name, String value) {
        this.name = name;
        this.values = List.of(value);
    }
}
