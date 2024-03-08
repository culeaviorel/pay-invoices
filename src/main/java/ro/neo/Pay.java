package ro.neo;

import java.util.List;

public record Pay(String name, String description, String somethingNew, String teenChallenge, String casaFilip,
                  String tanzania, String caminulFelix) {
    public List<String> toList() {
        return List.of(somethingNew, teenChallenge, caminulFelix, tanzania, caminulFelix);
    }
}
