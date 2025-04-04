package ro.transcrieri;

import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.TestBase;

import java.util.List;

@Slf4j
public class TranscrieriSteps extends TestBase {

    private final Transcrieri transcrieri = new Transcrieri();

    @And("I make transcriere")
    public void iMakeTranscriere(List<Item> items) {
        transcrieri.make(items);
    }
}
