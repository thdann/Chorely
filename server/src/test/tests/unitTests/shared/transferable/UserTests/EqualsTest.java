package unitTests.shared.transferable.UserTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shared.transferable.User;

/**
 * Equals Tester.
 *
 * @author Christopher O'Driscoll
 * @since <pre>feb. 8, 2023</pre>
 * @version 1.0
 */
public class EqualsTest {


    @Test
    public void testNull() {
        User testUser = new User("testuser", "testpass");
        User nullUser = null;
        boolean result = testUser.equals(nullUser);
        Assertions.assertFalse(result);
    }
    @Test
    public void testNotUserObject() {
        User testUser = new User("testuser", "testpass");
        Object objectUser = new Object() {
            public String getUsername() {
                return "testuser";
            }
        };
        boolean result = testUser.equals(objectUser);
        Assertions.assertFalse(result);
    }
    @Test
    public void testDifferentUsername() {
        User testUser = new User("testuser", "testpass");
        User lowUser = new User("usertest", "testpass");
        boolean result = testUser.equals(lowUser);
        Assertions.assertFalse(result);
    }
    @Test
    public void testLowercase() {
        User testUser = new User("testuser", "testpass");
        User lowUser = new User("testuser", "testpass");
        boolean result = testUser.equals(lowUser);
        Assertions.assertTrue(result);
    }
    @Test
    public void testUppercase() {
        User testUser = new User("testuser", "testpass");
        User uppUser = new User("TESTUSER", "TESTPASS");
        boolean result = testUser.equals(uppUser);
        Assertions.assertTrue(result);
    }
}
