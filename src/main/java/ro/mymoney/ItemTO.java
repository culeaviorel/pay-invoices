package ro.mymoney;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemTO {
    private String category;
    private String subCategory;
    private String name;
    private String data;
    private String value;

}
