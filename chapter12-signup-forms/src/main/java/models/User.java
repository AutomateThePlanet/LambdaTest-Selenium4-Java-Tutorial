package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private String password;
    private String passwordConfirm;
    private Boolean shouldSubscribe;
    private Boolean agreedPrivacyPolicy;
}
