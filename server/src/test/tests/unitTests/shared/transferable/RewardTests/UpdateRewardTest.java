package unitTests.shared.transferable.RewardTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shared.transferable.Chore;
import shared.transferable.Reward;

/**
 * UpdateReward Tester.
 *
 * @author Christopher O'Driscoll
 * @since <pre>feb. 8, 2023</pre>
 * @version 1.0
 */
public class UpdateRewardTest {

    //obs method does not have a return value, tests should be updated accordingly when return value implemented
    @Test
    public void testNull() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward nullReward = null;
        testReward.updateReward(nullReward);
        Assertions.assertEquals("testReward", testReward.getName());
    }
    @Test
    public void testNullName() {
        //should fail to update name
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward nullNameReward = new Reward(null, 100, "testDescription");
        testReward.updateReward(nullNameReward);
        Assertions.assertEquals("testReward", testReward.getName());
    }
    @Test
    public void testNullDesc() {
        //should fail to update description
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward nullDescReward = new Reward("testReward", 100, null);
        testReward.updateReward(nullDescReward);
        Assertions.assertEquals("testReward", testReward.getDescription());
    }
    @Test
    public void testEmptyName() {
        //should fail to update name
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward emptyNameReward = new Reward("", 100, "testDescription");
        testReward.updateReward(emptyNameReward);
        Assertions.assertEquals("testReward", testReward.getName());
    }
    @Test
    public void testEmptyDescription() {
        //should fail to update description
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward emptyDescReward = new Reward("testReward", 100, "");
        testReward.updateReward(emptyDescReward);
        Assertions.assertEquals("testDescription", testReward.getDescription());
    }
    @Test
    public void testSpecialCharsName() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward specialNameReward = new Reward("$*=+*@&+$@$#=@*@+=&=", 100, "testDescription");
        testReward.updateReward(specialNameReward);
        Assertions.assertEquals("$*=+*@&+$@$#=@*@+=&=", testReward.getName());
    }
    @Test
    public void testSpecialCharsDesc() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward specialDescReward = new Reward("testReward", 100, "%$!+%!#+#@*@%#&!==&+");
        testReward.updateReward(specialDescReward);
        Assertions.assertEquals("%$!+%!#+#@*@%#&!==&+", testReward.getDescription());
    }
    @Test
    public void testSingleCharName() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward singleNameReward = new Reward("t", 100, "testDescription");
        testReward.updateReward(singleNameReward);
        Assertions.assertEquals("t", testReward.getName());
    }
    @Test
    public void testSingleCharDesc() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward singleDescReward = new Reward("testReward", 100, "t");
        testReward.updateReward(singleDescReward);
        Assertions.assertEquals("t", testReward.getDescription());
    }
    @Test
    public void testLongName() {
        Reward testReward = new Reward("sBX2Sna3VCGrmUKWU0HXPBYcaCNTuT7ofk5sWMQZSzGsNj7Rh8TX8Wvbff9Q52puBn6HjBwQcSN1BKcCyoyaAtA51H0RtTfecBMM", 100, "testDescription");
        Reward longNameReward = new Reward("testReward", 100, "t");
        testReward.updateReward(longNameReward);
        Assertions.assertEquals("sBX2Sna3VCGrmUKWU0HXPBYcaCNTuT7ofk5sWMQZSzGsNj7Rh8TX8Wvbff9Q52puBn6HjBwQcSN1BKcCyoyaAtA51H0RtTfecBMM", testReward.getName());
    }
    @Test
    public void testLongDesc() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward longDescReward = new Reward("testReward", 100, "wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long");
        testReward.updateReward(longDescReward);
        Assertions.assertEquals("wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long", testReward.getDescription());
    }
    @Test
    public void testTooLongName() {
        //should fail to update name
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward singleDescReward = new Reward("1sBX2Sna3VCGrmUKWU0HXPBYcaCNTuT7ofk5sWMQZSzGsNj7Rh8TX8Wvbff9Q52puBn6HjBwQcSN1BKcCyoyaAtA51H0RtTfecBMM", 100, "testDescription");
        testReward.updateReward(singleDescReward);
        Assertions.assertEquals("testReward", testReward.getName());
    }
    @Test
    public void testTooLongDesc() {
        //should fail to update description
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward singleDescReward = new Reward("testReward", 100, "Reward wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long wife open teach analysis theft law frequency club perfume vote front lump able river dive electronics tear whole nuclear band disagreement youth begin upset engine expectation exploration finish ambiguity pony pound council suburb flow draw waste float twist breathe apparatus background favorable elaborate prescription robot athlete see needle constitution long");
        testReward.updateReward(singleDescReward);
        Assertions.assertEquals("testDescription", testReward.getDescription());
    }
    @Test
    public void testNegativePrice() {
        //should fail to update price
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward negPriceReward = new Reward("testReward", -1, "testDescription");
        testReward.updateReward(negPriceReward);
        Assertions.assertEquals(100, testReward.getRewardPrice());
    }
    @Test
    public void testZeroPrice() {
        //should fail to update price
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward zeroPriceReward = new Reward("testReward", 0, "testDescription");
        testReward.updateReward(zeroPriceReward);
        Assertions.assertEquals(100, testReward.getRewardPrice());
    }
    @Test
    public void testLowPrice() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward lowPriceReward = new Reward("testReward", 5, "testDescription");
        testReward.updateReward(lowPriceReward);
        Assertions.assertEquals(5, testReward.getRewardPrice());
    }
    @Test
    public void testHighPrice() {
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward highPriceReward = new Reward("testReward", 500, "testDescription");
        testReward.updateReward(highPriceReward);
        Assertions.assertEquals(500, testReward.getRewardPrice());
    }
    @Test
    public void testTooHighPrice() {
        //should fail to update price
        Reward testReward = new Reward("testReward", 100, "testDescription");
        Reward tooHighPriceReward = new Reward("testReward", 5000, "testDescription");
        testReward.updateReward(tooHighPriceReward);
        Assertions.assertEquals(100, testReward.getRewardPrice());
    }
}
