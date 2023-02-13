package unitTests.shared.transferable.UserTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shared.transferable.User;

/**
 * CompareUsernamePassword Tester.
 *
 * @author Christopher O'Driscoll
 * @since <pre>feb. 8, 2023</pre>
 * @version 1.0
 */
public class CompareUsernamePasswordTest {


    @Test
    public void testNull() {
        User testUser = new User("testuser", "testpass");
        User nullUser = null;
        boolean result = testUser.compareUsernamePassword(nullUser);
        Assertions.assertFalse(result);
    }
    @Test
    public void testDifferentUsername() {
        User testUser = new User("testuser", "testpass");
        User lowUser = new User("usertest", "testpass");
        boolean result = testUser.compareUsernamePassword(lowUser);
        Assertions.assertFalse(result);
    }
    @Test
    public void testDifferentPassword() {
        User testUser = new User("testuser", "testpass");
        User lowUser = new User("testuser", "passtest");
        boolean result = testUser.compareUsernamePassword(lowUser);
        Assertions.assertFalse(result);
    }
    @Test
    public void testLowercase() {
        User testUser = new User("testuser", "testpass");
        User lowUser = new User("testuser", "testpass");
        boolean result = testUser.compareUsernamePassword(lowUser);
        Assertions.assertTrue(result);
    }
    @Test
    public void testUppercase() {
        User testUser = new User("testuser", "testpass");
        User uppUser = new User("TESTUSER", "TESTPASS");
        boolean result = testUser.compareUsernamePassword(uppUser);
        Assertions.assertTrue(result);
    }
}
