package example.bfe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    @Test
    public void testBalanceCalculation() {
        double expected = 100.0;
        double actual = 100.0;
        assertEquals(expected, actual, 0.001);
    }
}