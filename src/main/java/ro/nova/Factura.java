package ro.nova;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Factura {
    private String name;
    private String type;
    private String suma;
    private String path;

}
