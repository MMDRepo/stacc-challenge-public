/*
package no.stacc.payforjoy.model.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void testUserDtoBuilder() {
        // Arrange
        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String phoneNumber = "1234567890";

        // Act
        UserDto userDto = UserDto.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();

        // Assert
        assertThat(userDto.getId()).isEqualTo(id);
        assertThat(userDto.getFirstName()).isEqualTo(firstName);
        assertThat(userDto.getLastName()).isEqualTo(lastName);
        assertThat(userDto.getEmail()).isEqualTo(email);
        assertThat(userDto.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void testUserDtoSettersAndGetters() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPhoneNumber("1234567890");

        // Assert
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getFirstName()).isEqualTo("John");
        assertThat(userDto.getLastName()).isEqualTo("Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(userDto.getPhoneNumber()).isEqualTo("1234567890");
    }
}*/
