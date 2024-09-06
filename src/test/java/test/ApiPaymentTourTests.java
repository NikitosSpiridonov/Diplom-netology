package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import dataHelper.CardInfo;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static dataHelper.ApiHelper.*;
import static dataHelper.DataHelper.*;
import static dataHelper.SqlHelper.*;

public class ApiPaymentTourTests {
    private final String approved = "APPROVED";
    private final String declined = "DECLINED";

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        cleanBase();
    }

    @AfterEach
    public void tearDown() {
        cleanBase();
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “APPROVED”")
    public void successfulPayFromApprovedDebitCard() {
        CardInfo card = getCardInfo(true);

        String paymentStatusResponse = debitCard(card);
        String paymentStatusDB = getStatusPaymentEntity();

        Assertions.assertEquals(approved, paymentStatusResponse);
        Assertions.assertEquals(approved, paymentStatusDB);
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по кредитной карте со статусом “APPROVED”")
    public void successfulPayFromApprovedCreditCard() {
        CardInfo card = getCardInfo(true);

        String paymentStatusResponse = creditCard(card);
        String paymentStatusDB = getStatusCreditEntity();

        Assertions.assertEquals(approved, paymentStatusResponse);
        Assertions.assertEquals(approved, paymentStatusDB);
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “DECLINED”")
    public void failedPayFromApprovedDebitCard() {
        CardInfo card = getCardInfo(false);

        String paymentStatusResponse = debitCard(card);
        String paymentStatusDB = getStatusPaymentEntity();

        Assertions.assertEquals(declined, paymentStatusResponse);
        Assertions.assertEquals(declined, paymentStatusDB);
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по кредитной карте со статусом “DECLINED”")
    public void failedPayFromApprovedCreditCard() {
        CardInfo card = getCardInfo(false);

        String paymentStatusResponse = creditCard(card);
        String paymentStatusDB = getStatusCreditEntity();

        Assertions.assertEquals(declined, paymentStatusResponse);
        Assertions.assertEquals(declined, paymentStatusDB);
    }
}