package ro.mymoney;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    private String date;
    private String status;
    private String name;
    private String sum;

}
