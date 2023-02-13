package unitTests.model.RegisteredUsersTests;

import model.RegisteredUsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shared.transferable.User;
/**
 * GetUserFromFile Tester.
 *
 * @author Christopher O'Driscoll
 * @since <pre>feb. 8, 2023</pre>
 * @version 1.0
 */
public class GetUserFromFileTest {

    /*
    todo: these tests rely on writeUserToFile
    */
    @Test
    public void testNull() {
        User testUser = null;
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertNull(testResult);
    }

    @Test
    public void testEmpty() {
        User testUser = new User("", "");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }

    @Test
    public void testSingleCharacter() {
        User testUser = new User("t", "t");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }

    @Test
    public void testStringLowercase() {
        User testUser = new User("testuser", "testpassword");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }

    @Test
    public void testStringUppercase() {
        User testUser = new User("TESTUSER", "PASSWORD");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }

    @Test
    public void testStringWithNumbers() {
        User testUser = new User("1234567890", "1234567890");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }

    @Test
    public void testStringWithUnusualCharacters() {
        User testUser = new User("!\"'åö#$.{[", "!\"'åö#$.)&");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }
}
