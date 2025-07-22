package ro.sheet;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RowTO {
    private String category;
    private String method;
    private String data;
    private String value;
    private String description;
    private String link;
    private String dovada;
    private String eFactura;
    private String decont;
    private String plataDecont;
}
