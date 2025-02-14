package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppleLoginRequestDTO {
    private String authorizationCode;
    private String identityToken;
    private UserInfo user;
    private String fcmToken;

    /**
     * username을 자동으로 생성합니다.
     */
    public String getUsername() {
        if (user == null || user.getName() == null) {
            return null;
        }
        String firstName = user.getName().getFirstName();
        String lastName = user.getName().getLastName();

        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Name name;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Name {
            private String firstName;
            private String lastName;
        }
    }
}
