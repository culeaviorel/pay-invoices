package ro.leiibvb;

public class Transaction {
    private String prenume;
    private String nume;
    private String email;
    private String phone;
    private String data;
    private String hour;
    private String symbol;
    private String amount;
    private String broker;

    public Transaction(String prenume, String nume, String email, String phone, String data, String hour, String symbol, String amount, String broker) {
        this.prenume = prenume;
        this.nume = nume;
        this.email = email;
        this.phone = phone;
        this.data = data;
        this.hour = hour;
        this.symbol = symbol;
        this.amount = amount;
        this.broker = broker;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getData() {
        return data;
    }

    public String getHour() {
        return hour;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getAmount() {
        return amount;
    }

    public String getBroker() {
        return broker;
    }
}
