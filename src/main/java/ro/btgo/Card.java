package ro.btgo;

import com.sdl.selenium.web.WebLocator;

public class Card extends WebLocator {

        public Card() {
            setClasses("card");
        }

        public Card(WebLocator container) {
            this();
            setContainer(container);
        }

        public Card(WebLocator container, String title) {
            this(container);
            WebLocator titleEl = new WebLocator().setText(title);
            setChildNodes(titleEl);
        }


}
