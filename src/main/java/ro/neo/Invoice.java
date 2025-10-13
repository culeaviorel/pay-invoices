package ro.neo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {
    private String fileName;
    private String decizia;
    private String decont;
    private String category;
    private String value;
    private String description;
    private String nr;
    private String cod;
    private String furnizor;
    private String iban;
    private LocalDate data = LocalDate.now();
}
