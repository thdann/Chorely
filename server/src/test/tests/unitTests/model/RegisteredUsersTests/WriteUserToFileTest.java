package unitTests.model.RegisteredUsersTests;
import model.RegisteredUsers;
import org.junit.jupiter.api.*;
import shared.transferable.*;
/**
 * WriteUserToFile Tester.
 *
 * @author Christopher O'Driscoll
 * @since <pre>feb. 8, 2023</pre>
 * @version 1.0
 */
public class WriteUserToFileTest {

    //todo: these tests rely on getUserFromFile because writeUserToFile returns void. all tests need to be updated once return value implemented

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
        Assertions.assertNull(testResult);
    }

    @Test
    public void testSingleCharacter() {
        User testUser = new User("t", "t");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertEquals(testUser, testResult);
    }
    @Test
    public void testLongerThanTen() {
        //placeholder awaiting requirement to be implemented or not
        User testUser = new User("testusername", "testpassword");
        RegisteredUsers.getInstance().writeUserToFile(testUser);
        User testResult = RegisteredUsers.getInstance().getUserFromFile(testUser);
        Assertions.assertNull(testResult);
    }

    @Test
    public void testStringLowercaseTenCharacters() {
        User testUser = new User("testuseron", "testpasswo");
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
