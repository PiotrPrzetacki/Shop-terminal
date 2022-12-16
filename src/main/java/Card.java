public class Card {
    public String number;
    public String pin;

    public Card(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "Card{" +
                "number='" + number + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}
