package ro.shelly;

public record Item(String id, String name, String ip) {

    public Item(String id, String name, String ip) {
        this.id = id;
        this.name = name;
        this.ip = ip;
    }
}
