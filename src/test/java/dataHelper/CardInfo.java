package dataHelper;

import lombok.Getter;

@Getter
public class CardInfo {

    String number;
    String mouth;
    String year;
    String owner;
    String cvc;

    public CardInfo(String number, String mouth, String year, String owner, String cvc) {
        this.number = number;
        this.mouth = mouth;
        this.year = year;
        this.owner = owner;
        this.cvc = cvc;
    }
}