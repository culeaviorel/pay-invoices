package ro.imobiliare;

import com.sdl.selenium.WebLocatorUtils;
import com.sdl.selenium.web.WebLocator;
import com.sdl.selenium.web.link.WebLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListView extends WebLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListView.class);

    public ListView() {
        setClasses("box-anunt");
        setExcludeClasses("proiect", "profesional", "premium", "standard");
    }

    private WebLocator title = new WebLocator(this).setTag("h2").setClasses("titlu-anunt");
    private WebLink link = new WebLink(title).setAttribute("itemprop", "name");
    private WebLocator titleEl = new WebLocator(link);
    private WebLocator localizare = new WebLocator(this).setClasses("localizare");
    private WebLocator caracteristici = new WebLocator(this).setTag("ul").setClasses("caracteristici");
    private WebLocator pret = new WebLocator(this).setClasses("pret");
    private WebLocator pretEl = new WebLocator(pret).setClasses("pret-mare");

    public void getData() {
        List<Notice> list = new ArrayList<>();
        int page = 1;
        do {
            setPosition(-1);
            int size = findElements().size();
            for (int i = 1; i < size; i++) {
                setPosition(i);
                WebLocatorUtils.scrollToWebLocator(this);
                String text = titleEl.getText();
                if (!localizare.ready()) {
                    LOGGER.debug("loc");
                }
                String localizareText = localizare.getText();
                localizareText = localizareText.contains("zona") ? localizareText.split("zona ")[1] : localizareText;
                String car = caracteristici.getText();
                String[] split = car != null ? car.split("\n") : new String[0];
                String caracteristiciText = "";
                int mp = 0;
                for (String s : split) {
                    caracteristiciText = caracteristiciText + " " + s;
                    if (s.contains("mp")) {
                        String meter = s.replaceAll("\\.", " ").split(" ")[0];
                        mp = Integer.parseInt(meter);
                    }
                }
                String pretText = pretEl.getText();
                String s = pretText.split(" ")[0];
                pretText = s.replaceAll("\\.", "");
                int pr = Integer.parseInt(pretText);
                long ff = 0;
                if (pr > 0 && mp > 0) {
                    ff = pr / mp;
                }
                link.mouseOver();
                String href = link.getAttribute("href");
                list.add(new Notice(text, href, localizareText, caracteristiciText, pretText, ff));
            }
            page++;
            WebLink next = new WebLink().setAttribute("data-pagina", page + "");
            WebLocatorUtils.scrollToWebLocator(next);
            next.click();
        } while (page < 7);

        list.sort(Comparator.comparing(Notice::getOneMeter));

        for (Notice notice : list) {
            LOGGER.debug("{}", notice.toCSV());
        }
    }
}
