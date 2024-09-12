package ro.neo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {
    private String pdfPath;
    private String category;
    private String value;
    private String description;
    private String nr;
    private String cod;
    private String furnizor;
    private String iban;
}
