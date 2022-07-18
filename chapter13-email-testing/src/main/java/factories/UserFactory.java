package factories;

import com.github.javafaker.Faker;
import models.User;
import utilities.TimestampBuilder;

public class UserFactory {
    private static final String DEFAULT_PASSWORD = "thesecret";
    private static final Faker faker;

    static {
        faker = new Faker();
    }

    public static User createDefault() {
        var user = new User();
        user.setEmail(TimestampBuilder.buildUniqueTextBySuffix("test@mailsurp.com"));
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setUserName(faker.name().username());
        user.setTelephone(faker.phoneNumber().phoneNumber());
        user.setPassword(DEFAULT_PASSWORD);
        user.setPasswordConfirm(DEFAULT_PASSWORD);
        user.setAgreedPrivacyPolicy(true);
        user.setShouldSubscribe(false);
        return user;
    }
}
