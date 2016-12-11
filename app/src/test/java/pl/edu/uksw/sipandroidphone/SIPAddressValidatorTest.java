package pl.edu.uksw.sipandroidphone;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SIPAddressValidatorTest {
    @Test
    public void sipAddressValidator_CorrectSipAddress_ReturnsTrue() {
        assertThat(SIPAddressValidator.isValidSipAddress("name@email.com"), is(true));
        assertThat(SIPAddressValidator.isValidSipAddress("prename.name@email.com"), is(true));
        assertThat(SIPAddressValidator.isValidSipAddress("name123@email.email2.com"), is(true));
    }

    @Test
    public void sipAddressValidator_IncorrectSipAddress_ReturnsFalse() {
        assertThat(SIPAddressValidator.isValidSipAddress("name@com"), is(false));
        assertThat(SIPAddressValidator.isValidSipAddress("nameemail.email2.com"), is(false));
        assertThat(SIPAddressValidator.isValidSipAddress("nameemail @email2.com"), is(false));
    }
}