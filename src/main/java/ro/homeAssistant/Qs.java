package ro.homeAssistant;

public class Qs {
    private StringBuilder qs = new StringBuilder("return document");

    public Qs() {
    }

    public Qs(Qs qs) {
        this.qs = new StringBuilder(qs.get());
    }

    public Qs child(Qs child) {
        this.qs = get().append(child.get().toString().replaceFirst("return document", ""));
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
