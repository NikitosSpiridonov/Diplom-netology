package pages;

import com.codeborne.selenide.*;
import dataHelper.CardInfo;

import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PayPage {

    private final ElementsCollection allInputs = $$(".input__inner");

    private final SelenideElement inputCardNumber = allInputs.get(0);
    private final SelenideElement inputCardMouth = allInputs.get(1);
    private final SelenideElement inputCardYear = allInputs.get(2);
    private final SelenideElement inputCardOwner = allInputs.get(3);
    private final SelenideElement inputCardCvc = allInputs.get(4);

    private final SelenideElement formButton = $("form button");

    public PayPage(String text) {
        $(By.xpath("//h3[contains(text(), 'карт')]")).shouldBe(visible);
        allInputs.shouldHave(size(5));
        allInputs.get(0).$(".input__top").shouldBe(visible).shouldHave(text("Номер карты"));
        formButton.shouldBe(visible).shouldHave(text("Продолжить"));
    }

    public void enterCardInfo(CardInfo cardInfo) {
        inputCardNumber.$(".input__control").setValue(cardInfo.getNumber());
        inputCardMouth.$(".input__control").setValue(cardInfo.getMouth());
        inputCardYear.$(".input__control").setValue(cardInfo.getYear());
        inputCardOwner.$(".input__control").setValue(cardInfo.getOwner());
        inputCardCvc.$(".input__control").setValue(cardInfo.getCvc());
    }

    public void clickSubmit() {
        formButton.click();
    }

    public void getNoticeText(String msg) {
        $(".notification__title").shouldBe(visible, Duration.ofSeconds(25)).shouldHave(text(msg));
    }

    public int getNumberOfErrorsUnderInputs() {
        return $$(".input__sub").size();
    }

    public String getInputValue(int index) {
        return allInputs.get(index).$(".input__control").getValue();
    }

    public String getNoticeInputNumber() {
        return inputCardNumber.$(".input__sub").shouldBe(visible).getText();
    }

    public String getNoticeInputMouth() {
        return inputCardMouth.$(".input__sub").shouldBe(visible).getText();
    }

    public String getNoticeInputYear() {
        return inputCardYear.$(".input__sub").shouldBe(visible).getText();
    }

    public String getNoticeInputOwner() {
        return inputCardOwner.$(".input__sub").shouldBe(visible).getText();
    }

    public String getNoticeInputCvc() {
        return inputCardCvc.$(".input__sub").shouldBe(visible).getText();
    }
}