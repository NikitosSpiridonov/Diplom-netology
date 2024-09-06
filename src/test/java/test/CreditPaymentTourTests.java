package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import dataHelper.*;
import pages.*;

import static com.codeborne.selenide.Selenide.open;
import static dataHelper.DataHelper.*;
import static dataHelper.SqlHelper.*;

public class CreditPaymentTourTests {

    private final String msgError = "Ошибка";
    private final String msgSuccess = "Успешно";
    private final String msgWrongFormat = "Неверный формат";
    private final String msgCardExpired = "Истёк срок действия карты";
    private final String msgRequiredField = "Поле обязательно для заполнения";
    private final String msgInvalidExpirationDate = "Неверно указан срок действия карты";
    private final int countCardNumber = 16;

    private final int inputNumberScore = 0;
    private final int inputMouthScore = 1;
    private final int inputYearScore = 2;
    private final int inputCvcScore = 4;

    @BeforeAll
    static void setupAllureReports() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        open(System.getProperty("sut.url"));
    }

    @AfterEach
    public void tearDown() {
        cleanBase();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    @Test
    @DisplayName("Оплата тура картой со статусом “APPROVED” (кредитная)")
    void approvedCard() {
        CardInfo cardInfo = DataHelper.getCardInfo(true);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();
        payPage.getNoticeText(msgSuccess);
    }

    @Test
    @DisplayName("Оплата тура картой со статусом “DECLINED” (кредитная)")
    void declinedCard() throws InterruptedException {
        CardInfo cardInfo = DataHelper.getCardInfo(false);

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();
        payPage.getNoticeText(msgError);

        //Баг - отображается уведомление об успехе операции
    }

    @Test
    @DisplayName("Отправка пустой формы (кредитная)")
    void emptyForm() {

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.sendForm();

        payPage.getInputsSubSize(5);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputNumber());
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputMouth());
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputYear());
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputOwner());
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputCvc());
    }

    @Test
    @DisplayName("Оплата тура картой с незарегистрированным номером (кредитная)")
    void notExistCard() {
        CardInfo cardInfo = new CardInfo(generateCardNumber(16), generateCardMouth(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();
        payPage.getNoticeText("Ошибка");

        //Баг - отображается оба уведомления об успехе и неудаче операции
    }

    @Test
    @DisplayName("Валидация данных в поле Номер карты. Ввод 15 цифр (кредитная)")
    void Number15Digits() {
        CardInfo cardInfo = new CardInfo(generateCardNumber(countCardNumber - 1), generateCardMouth(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputNumber());
    }

    @Test
    @DisplayName("Валидация данных в поле Номер карты. Ввод 17 цифр (кредитная)")
    void Number17Digits() {
        CardInfo cardInfo = new CardInfo(generateCardNumber(countCardNumber + 1), generateCardMouth(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(0);
        Assertions.assertEquals(countCardNumber, payPage.getInputValue(inputNumberScore).length() - 3);
    }

    @Test
    @DisplayName("Валидация данных в поле Номер карты. Ввод букв (кредитная)")
    void NumberLetters() {
        CardInfo cardInfo = new CardInfo(generateCardOwner(), generateCardMouth(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputNumberScore));
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputNumber());
    }

    @Test
    @DisplayName("Валидация данных в поле Номер карты. Ввод спецсимволов (кредитная)")
    void NumberSymbols() {
        CardInfo cardInfo = new CardInfo(getSymbolStr(), generateCardMouth(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputNumberScore));
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputNumber());
    }

    @Test
    @DisplayName("Валидация данных в поле Номер карты. Пустое поле при заполненных остальных полях (кредитная)")
    void NumberEmpty() {
        CardInfo cardInfo = new CardInfo("", generateCardMouth(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputNumberScore));
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputNumber());
        // Баг - отображается подсказка неверного формата
    }


    @Test
    @DisplayName("Валидация данных в поле Месяц. Ввод значения 00 (кредитная)")
    void MouthField00() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), "00", generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgInvalidExpirationDate, payPage.getNoticeInputMouth());
        // Баг - нет валидации поля на ввод 00
    }

    @Test
    @DisplayName("Валидация данных в поле Месяц. Ввод значения 13 (кредитная)")
    void MouthField13() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), String.valueOf(12 + 1), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgInvalidExpirationDate, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Валидация данных в поле Месяц. Ввод значения с одной цифрой (кредитная)")
    void MouthField1Digit() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardNumber(1), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Валидация данных в поле Месяц. Ввод букв (кредитная)")
    void MouthLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardOwner(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputMouthScore));
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Валидация данных в поле Месяц. Ввод спецсимволов (кредитная)")
    void MouthSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), getSymbolStr(), generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputMouthScore));
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputMouth());
    }

    @Test
    @DisplayName("Валидация данных в поле Месяц. Пустое поле при заполненных остальных полях (кредитная)")
    void MouthEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), "", generateCardYear(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputMouthScore));
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputMouth());
        // Баг - отображается подсказка неверного формата
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Ввод значения предыдущего года (кредитная)")
    void YearFieldLastYear() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(-1), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgCardExpired, payPage.getNoticeInputYear());
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Ввод года плюс 5 лет (кредитная)")
    void YearFieldPlus5Year() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(+5), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getNoticeText(msgSuccess);

        payPage.getInputsSubSize(0);
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Ввод года плюс 6 лет (кредитная)")
    void YearFieldPlus6Year() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(+6), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgInvalidExpirationDate, payPage.getNoticeInputYear());
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Формат указания года 4-мя цифрами (кредитная)")
    void YearField4Digits() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), ("20" + generateCardYear()), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgCardExpired, payPage.getNoticeInputYear());
        Assertions.assertEquals("20", payPage.getInputValue(inputYearScore));
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Ввод букв (кредитная)")
    void YearLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardOwner(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputYearScore));
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputYear());
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Ввод спецсимволов (кредитная)")
    void YearSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), getSymbolStr(), generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputYearScore));
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputYear());
    }

    @Test
    @DisplayName("Валидация данных в поле Год. Пустое поле при заполненных остальных полях (кредитная)")
    void YearEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), "", generateCardOwner(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals("", payPage.getInputValue(inputYearScore));
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputYear());
        // Баг - отображается подсказка неверного формата
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Ввод цифр (кредитная)")
    void OwnerDigits() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardNumber(5) + " " + generateCardNumber(5), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputOwner());
        // Баг - нет валидации поля на ввод цифр
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Ввод кириллицы (кредитная)")
    void OwnerCyrillic() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardOwnerInCyrillic(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputOwner());
        // Баг - нет валидации поля на ввод кириллицы
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Пробел в начале (кредитная)")
    void OwnerSpaceInAtFirst() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), " " + removeSpace(generateCardOwner()), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();
        payPage.getNoticeText(msgSuccess);

        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputOwner());
        // Баг - нет валидации поля на ввод пробела в начале
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Пробел в конце (кредитная)")
    void OwnerSpaceInAtTheEnd() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), removeSpace(generateCardOwner()) + " ", getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();
        payPage.getNoticeText(msgSuccess);

        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputOwner());
        // Баг - нет валидации поля на ввод пробела в конце
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Без пробела (кредитная)")
    void OwnerSpaceLess() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), removeSpace(generateCardOwner()), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();
        payPage.getNoticeText(msgSuccess);

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputOwner());
        // Баг - нет валидации поля на ввод без пробела
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Ввод спецсимволов (кредитная)")
    void OwnerSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), getSymbolStr(), getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputOwner());
        // Баг - нет валидации поля на ввод спец. символов
    }

    @Test
    @DisplayName("Валидация данных в поле Владелец. Пустое поле (кредитная)")
    void OwnerEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), "", getCardCvc());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputOwner());
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Ввод 2-х цифр (кредитная)")
    void Cvc2Digits() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardOwner(), generateCardNumber(2));

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgWrongFormat, payPage.getNoticeInputCvc());
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Ввод 4-х цифр (кредитная)")
    void Cvc4Digits() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardOwner(), generateCardNumber(4));

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        Assertions.assertEquals(3, payPage.getInputValue(inputCvcScore).length());
        payPage.getNoticeText(msgSuccess);
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Ввод букв (кредитная)")
    void CvcLetters() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardOwner(), generateCardFirstName());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputCvc());
        Assertions.assertEquals("", payPage.getInputValue(inputCvcScore));
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Ввод спец. символов (кредитная)")
    void CvcSymbols() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardOwner(), getSymbolStr());

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputCvc());
        Assertions.assertEquals("", payPage.getInputValue(inputCvcScore));
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }

    @Test
    @DisplayName("Валидация данных в поле CVC/CVV. Пустое поле (кредитная)")
    void CvcEmpty() {
        CardInfo cardInfo = new CardInfo(getApprovedCard(), generateCardMouth(), generateCardYear(), generateCardOwner(), "");

        MainPage mainPage = new MainPage();
        PayPage payPage = mainPage.clickToPayCredit();
        payPage.enterCardInfo(cardInfo);
        payPage.sendForm();

        payPage.getInputsSubSize(1);
        Assertions.assertEquals(msgRequiredField, payPage.getNoticeInputCvc());
        // Баг - отображается две подсказки валидации поля, должна быть одна
    }
}