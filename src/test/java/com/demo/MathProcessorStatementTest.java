package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MathProcessorStatementTest {

    @Test
    public void testNegativeNumber() {
        MathProcessor processor = new MathProcessor();
        assertThrows(IllegalArgumentException.class, () -> {
            processor.sumEvenNumbers(-1);
        });
    }

    @Test
    public void testPositiveNumberWithEvens() {
        MathProcessor processor = new MathProcessor();
        assertEquals(2, processor.sumEvenNumbers(2)); // Chạy vào nhánh if (i % 2 == 0)
    }
}
