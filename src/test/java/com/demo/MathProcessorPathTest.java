package com.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MathProcessorPathTest {

    MathProcessor processor = new MathProcessor();

    @Test
    public void testPathA_NegativeRun() {
        assertThrows(IllegalArgumentException.class, () -> processor.sumEvenNumbers(-5));
    }

    @Test
    public void testPathB_ZeroRun() { // Không vào vòng for
        assertEquals(0, processor.sumEvenNumbers(0));
    }

    @Test
    public void testPathC_NoEvenNumbers() { // Vào for nhưng không vào if
        assertEquals(0, processor.sumEvenNumbers(1));
    }

    @Test
    public void testPathD_WithEvenNumbers() { // Cả for và if đều được pass
        assertEquals(6, processor.sumEvenNumbers(5)); 
    }
}