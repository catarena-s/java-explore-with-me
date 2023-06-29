package ru.practicum.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practicum.dto.user.NewUserRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.utils.TestValidatorUtil.hasErrorMessage;

class UserValidationTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "               ", "",})
    void create_withWrongName(String name) {
        final NewUserRequest newUser = NewUserRequest.builder()
                .name(name).email("email@mail.com")
                .build();
        assertTrue(hasErrorMessage(newUser, "User name cannot be empty or null"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "               ", "",})
    void create_withWrongNullName(String email) {
        final NewUserRequest newUser = NewUserRequest.builder()
                .name("Name")
                .email(email)
                .build();
        assertTrue(hasErrorMessage(newUser, "Email cannot be empty or null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a",
            "RFkH189hRGmHGycUZvEaGa5mIAwslocA0q2akEyOX0vXm5kvePZhENy5bma9VFn4C0gWINtEXBofFI5KRFBmigyWe7gpcIKE8iWQa1oxeM4QJVV71NTcXpkbACjsoNULwjswrtO79bDpmgtIfffUW04hTA7sBZBVFg3mK5duXeNhHCmqUS1cdSvi2qtNqQYy4mP7EiEjUMZrxv9uEcRFUO2Wy5eIMy31t2eg7TjLUcz8iSrWP59aJjblWD6"})
    void create_withWrongName_tooShort(String name) {
        final NewUserRequest newUser = NewUserRequest.builder()
                .name(name).email("email@mail.com")
                .build();
        assertTrue(hasErrorMessage(newUser, "size must be between 2 and 250"));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "a@m.b",
            "V@AErEcLtlc2NryWeQKDGAyPii5uVPyK0ICObWbE5U6ocOCl3fSsP44Dmo6mlviJO.H6agwKLsvLVJ9Wvq0DLsiFjTiLsv0lU2YV29Ae9CpqknABdJ7WiqL6BR4n7o5P9.LiBDRX1ksFAwZz3uDlE8kvFScnryTkbTFuRME2UBijNiLFxXmnUh10Y2dKt43n5.fgUB9lQo8O0gf3lAbaPQCqG4TsL5U0CjyDV7jSivwNwOY888SWww3IdJgoXTd"
    })
    void create_withWrongSizeEmail(String email) {
        final NewUserRequest newUser = NewUserRequest.builder()
                .name("Name").email(email)
                .build();
        assertTrue(hasErrorMessage(newUser, "size must be between 6 and 254"));
    }

    @Test
    void createWithEmptyNameAndEmail() {
        final NewUserRequest newUser = NewUserRequest.builder().email("").name("").build();
        Assertions.assertAll(
                () -> assertTrue(hasErrorMessage(newUser, "User name cannot be empty or null")),
                () -> assertTrue(hasErrorMessage(newUser, "Email cannot be empty or null"))
        );
    }

    @Test
    void createWithNullEmail() {
        NewUserRequest newUser = NewUserRequest.builder().name("Name").build();
        assertTrue(hasErrorMessage(newUser, "Email cannot be empty or null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "               ", "email @mail.com", "mail.com", "@mail@com"})
    void createNotWrongEmail(String email) {
        NewUserRequest newUser = NewUserRequest.builder()
                .name("Name")
                .email(email)
                .build();
        assertTrue(hasErrorMessage(newUser, "Email must be valid"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ab",
            "IIJbkC9bibB9KXFQ8LEqCOoKV4zcDg2Fj1rPlbghxVO34AQkTICFoIyfExqueSUSPi0US9Ali6ZIbrGpPwCrtoY2yFzJLGqRROhoXxiJElzg69GHi19UzH5DcEswr6zyHjtHi7XYg0RkbqRAb5LrKJZDVPSMQhpQmvLnxnU8ftnfrCxKWerIbQZLnFnN5y0kmN4P72NgGRkBa2eZ0D71ZaW5PNsXx9eTOhlVwTPQUAkicKQuMO1KDN8PUh"})
    void create_withCorrectSizeName_2_or_250(String name) {
        NewUserRequest newUser = NewUserRequest.builder()
                .name(name)
                .email("mail@mail.com")
                .build();

        assertEquals(name, newUser.getName());
        assertEquals("mail@mail.com", newUser.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a@m.by",
            "A@x4vUgGay1bYUa5YbjMRiCnFJNnxeL4faH9zgPPBUNATn5oqWN3WVElAEHf6KfQ5.xzmUAsbWJ0HFoFdEe1X3uFNIUOaeNEIILqxcvw6ExP8jFEjLrxlc7GJ4oJwCklc.kZZW6NODZpEWLoNbOh8E6GghJXuHfbkrnZbBBfoc1AVqqzX2nLVwYXDIddBXUlc.NtPk43Lm9ihuksBiWv7GByHOzJtZyM8N9YbWbP0A4l4HKrSx5YA4tXFKHpA8"
    })
    void create_withCorrectSizeEmail_6_or_254(String email) {
        NewUserRequest newUser = NewUserRequest.builder()
                .name("Name").email(email)
                .build();
        assertEquals("Name", newUser.getName());
        assertEquals(email, newUser.getEmail());
    }

    @Test
    void create_withCorrectNameAndEmail() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("mail@mail.com")
                .name("Name")
                .build();

        assertEquals("Name", newUser.getName());
        assertEquals("mail@mail.com", newUser.getEmail());
    }
}
