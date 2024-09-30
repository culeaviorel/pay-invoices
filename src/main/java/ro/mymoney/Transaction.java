package ro.mymoney;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
    private String name, subCategory;
}
