package pages;

import com.codeborne.selenide.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {

    private final ElementsCollection buttons = $$("button");

    private final SelenideElement buttonPayInDebit = buttons.get(0);
    private final SelenideElement buttonPayInCredit = buttons.get(1);

    public MainPage() {
        $("h2").shouldBe(visible);
        buttonPayInDebit.shouldBe(visible).shouldHave(text("Купить"));
        buttonPayInCredit.shouldBe(visible).shouldHave(text("Купить в кредит"));
    }

    public PayPage clickToPayDebit() {
        buttonPayInDebit.click();
        return new PayPage("Оплата по карте");
    }

    public PayPage clickToPayCredit() {
        buttonPayInCredit.click();
        return new PayPage("Кредит по данным карты");
    }
}