package dataHelper;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataHelper {

    private static final String approvedCard = "4444 4444 4444 4441";
    private static final String declinedCard = "4444 4444 4444 4442";
    private static final Faker fakerRU = new Faker(new Locale("ru"));
    private static final Faker fakerEN = new Faker(new Locale("en"));
    private static final String symbolStr = " *?/\\|<>,.()[]{};:'\"!@#$%^&";
    private static final int validShift = 3;

    private DataHelper() {
    }

    public static String getSymbolStr() {
        return symbolStr;
    }

    public static String generateCardNumber(int count) {
        return fakerEN.numerify("#".repeat(count));
    }

    public static String generateCardMouth() {
        int shift = new Random().nextInt(12) + 1;
        return LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateCardYear(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateCardYear() {
        return LocalDate.now().plusYears(validShift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateCardOwner() {
        return generateCardFirstName() + " " + generateCardLastName();
    }

    public static String generateCardFirstName() {
        return fakerEN.name().firstName();
    }

    public static String generateCardLastName() {
        return fakerEN.name().lastName();
    }

    public static String generateCardOwnerInCyrillic() {
        return fakerRU.name().firstName() + " " + fakerRU.name().lastName();
    }

    public static String getCardCvc() {
        return generateCardNumber(3);
    }

    public static CardInfo getCardInfo(boolean status) {
        if (status) {
            return new CardInfo(approvedCard, generateCardMouth(), generateCardYear(validShift), generateCardOwner(), getCardCvc());
        }
        return new CardInfo(declinedCard, generateCardMouth(), generateCardYear(validShift), generateCardOwner(), getCardCvc());
    }

    public static String getApprovedCard() {
        return approvedCard;
    }

    public static String removeSpace(String str) {
        return str.replace(" ", "");
    }
}