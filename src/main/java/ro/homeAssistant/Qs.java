package ro.homeAssistant;

public class Qs {
    private StringBuilder qs = new StringBuilder("return document");

    public Qs parent(StringBuilder parent) {
        this.qs = new StringBuilder(parent);
        return this;
    }

    public Qs selector(String selector) {
        qs.append(".querySelector('").append(selector).append("')");
        return this;
    }

    public Qs selectors(String selectors) {
        qs.append(".querySelectorAll('").append(selectors).append("')");
        return this;
    }

    public Qs shadow() {
        qs.append(".shadowRoot");
        return this;
    }

    public Qs nth(int nth) {
        qs.append("[").append(nth).append("]");
        return this;
    }

    public StringBuilder get() {
        return qs;
    }

    public String toSting() {
        return qs.toString();
    }
}
