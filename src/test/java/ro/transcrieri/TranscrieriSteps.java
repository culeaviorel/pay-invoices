package ro.transcrieri;

import com.google.api.services.sheets.v4.Sheets;
import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.fasttrackit.util.AppUtils;
import org.fasttrackit.util.TestBase;
import org.fasttrackit.util.UserCredentials;
import ro.btgo.Pay;
import ro.neo.MemberPay;

import java.util.List;
import java.util.Locale;

@Slf4j
public class TranscrieriSteps extends TestBase {

    private final Transcrieri transcrieri = new Transcrieri();
    private final AppUtils appUtils = new AppUtils();
    private static List<Pay> pays;
    private static List<MemberPay> memberPays;
    private static Sheets sheetsService;
    private final Locale roLocale = new Locale("ro", "RO");
    private final UserCredentials credentials = new UserCredentials();

    @And("I make transcriere")
    public void iMakeTranscriere(List<Item> items) {
        transcrieri.make(items);
    }
}
