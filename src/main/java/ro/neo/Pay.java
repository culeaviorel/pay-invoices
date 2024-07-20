package ro.neo;

import java.util.List;

public record Pay(String name,
                  String description,
                  String somethingNew,
                  String teenChallenge,
                  String casaFilip,
                  String tanzania,
                  String caminulFelix,
                  String alegeViata,
                  String apme
) {
    public List<String> toList() {
        return List.of(
                somethingNew,
                teenChallenge,
                casaFilip,
                tanzania,
                caminulFelix,
                alegeViata,
                apme
        );
    }
}
