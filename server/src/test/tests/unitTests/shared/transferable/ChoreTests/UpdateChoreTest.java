package unitTests.shared.transferable.ChoreTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shared.transferable.Chore;

/**
 * UpdateChore Tester.
 *
 * @author Christopher O'Driscoll
 * @since <pre>feb. 8, 2023</pre>
 * @version 1.0
 */
public class UpdateChoreTest {
    //obs method does not have a return value, tests should be updated accordingly when return value implemented
    @Test
    public void testNull() {
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore nullChore = null;
        testChore.updateChore(nullChore);
        Assertions.assertEquals("testChore", testChore.getName());
    }
    @Test
    public void testNullName() {
        //should fail to update name
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore nullNameChore = new Chore(null, 100, "testDescription");
        testChore.updateChore(nullNameChore);
        Assertions.assertEquals("testChore", testChore.getName());
    }
    @Test
    public void testNullDesc() {
        //should fail to update description
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore nullDescChore = new Chore("testChore", 100, null);
        testChore.updateChore(nullDescChore);
        Assertions.assertEquals("testDescription", testChore.getDescription());
    }
    @Test
    public void testEmptyName() {
        //should fail to update name
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore emptyNameChore = new Chore("", 100, "");
        testChore.updateChore(emptyNameChore);
        Assertions.assertEquals("testChore", testChore.getName());
    }
    @Test
    public void testEmptyDesc() {
        //should fail to update description
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore emptyDescChore = new Chore("", 100, "");
        testChore.updateChore(emptyDescChore);
        Assertions.assertEquals("testDescription", testChore.getDescription());
    }
    @Test
    public void testSpecialCharsName() {
        //should fail to update name
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore specialNameChore = new Chore("$*=+*@&+$@$#=@*@+=&=", 100, "testDescription");
        testChore.updateChore(specialNameChore);
        Assertions.assertEquals("$*=+*@&+$@$#=@*@+=&=", testChore.getName());
    }

    @Test
    public void testSpecialCharsDesc() {
        //should fail to update description
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore specialDescChore = new Chore("testChore", 100, "%$!+%!#+#@*@%#&!==&+");
        testChore.updateChore(specialDescChore);
        Assertions.assertEquals("%$!+%!#+#@*@%#&!==&+", testChore.getDescription());
    }
    @Test
    public void testSingleCharName() {
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore singleCharNameChore = new Chore("t", 100, "testDescription");
        testChore.updateChore(singleCharNameChore);
        Assertions.assertEquals("t", testChore.getName());
    }
    @Test
    public void testSingleCharDesc() {
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore singleCharDescChore = new Chore("testChore", 100, "t");
        testChore.updateChore(singleCharDescChore);
        Assertions.assertEquals("t", testChore.getDescription());
    }
    @Test
    public void testLongName() {
        //test with max 100 characters
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore longNameChore = new Chore("sBX2Sna3VCGrmUKWU0HXPBYcaCNTuT7ofk5sWMQZSzGsNj7Rh8TX8Wvbff9Q52puBn6HjBwQcSN1BKcCyoyaAtA51H0RtTfecBMM", 100, "testDescription");
        testChore.updateChore(longNameChore);
        Assertions.assertEquals("sBX2Sna3VCGrmUKWU0HXPBYcaCNTuT7ofk5sWMQZSzGsNj7Rh8TX8Wvbff9Q52puBn6HjBwQcSN1BKcCyoyaAtA51H0RtTfecBMM", testChore.getName());
    }
    @Test
    public void testLongDesc() {
        //test with max 100 words
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore longNameChore = new Chore("testChore", 100, "wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long");
        testChore.updateChore(longNameChore);
        Assertions.assertEquals("wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long", testChore.getDescription());
    }
    @Test
    public void testTooLongName() {
        //test with 101 characters, should fail to update
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore tooLongNameChore = new Chore("1sBX2Sna3VCGrmUKWU0HXPBYcaCNTuT7ofk5sWMQZSzGsNj7Rh8TX8Wvbff9Q52puBn6HjBwQcSN1BKcCyoyaAtA51H0RtTfecBMM", 100, "testDescription");
        testChore.updateChore(tooLongNameChore);
        Assertions.assertEquals("testChore", testChore.getName());
    }
    @Test
    public void testTooLongDesc() {
        //test with 101 words, should fail to update
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore longNameChore = new Chore("testChore", 100, "Chore wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long");
        testChore.updateChore(longNameChore);
        Assertions.assertEquals("testDescription", testChore.getDescription());
    }
    @Test
    public void testNegativePoints() {
        //should fail to update points
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore negPointsChore = new Chore("testChore", -1, "testDescription");
        testChore.updateChore(negPointsChore);
        Assertions.assertEquals(100, testChore.getScore());
    }
    @Test
    public void testZeroPoints() {
        //should fail to update points
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore negPointsChore = new Chore("testChore", 0, "testDescription");
        testChore.updateChore(negPointsChore);
        Assertions.assertEquals(100, testChore.getScore());
    }
    @Test
    public void testLowPoints() {
        //lowest allowed points = 5
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore lowPointsChore = new Chore("testChore", 5, "testDescription");
        testChore.updateChore(lowPointsChore);
        Assertions.assertEquals(1, testChore.getScore());
    }
    @Test
    public void testHighPoints() {
        //test with maximum 500 points
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore lowPointsChore = new Chore("testChore", 500, "testDescription");
        testChore.updateChore(lowPointsChore);
        Assertions.assertEquals(500, testChore.getScore());
    }
    @Test
    public void testTooHighPoints() {
        //should fail to update points
        Chore testChore = new Chore("testChore", 100, "testDescription");
        Chore lowPointsChore = new Chore("testChore", 5000, "testDescription");
        testChore.updateChore(lowPointsChore);
        Assertions.assertEquals(100, testChore.getScore());
    }
}
