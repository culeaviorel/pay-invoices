package ro.anaf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequest {
    //    private Fields fields;
//    private Integer cui;
//    private String data;
    List<Fields> fields;

    @Override
    public String toString() {
        return fields.toString();
//        return "[{" +
//                "cui=" + fields.get(0).getCui() +
//                "data=" + fields.get(0).getData() +
//                "}]";
    }
}
