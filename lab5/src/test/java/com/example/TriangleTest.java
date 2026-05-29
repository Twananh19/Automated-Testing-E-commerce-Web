package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleTest {

    // --- CÁC TEST CASES ĐỂ BAO PHỦ STATEMENT (5 test cases cơ bản) ---
    
    @Test
    public void testInvalid() {
        assertEquals("Invalid", Triangle.classify(0, 5, 5));
    }

    @Test
    public void testNotATriangle() {
        assertEquals("Not a triangle", Triangle.classify(1, 2, 4));
    }

    @Test
    public void testEquilateral() {
        assertEquals("Equilateral", Triangle.classify(3, 3, 3));
    }

    @Test
    public void testIsosceles() {
        assertEquals("Isosceles", Triangle.classify(3, 3, 4));
    }

    @Test
    public void testScalene() {
        assertEquals("Scalene", Triangle.classify(3, 4, 5));
    }
    
    // --- CÁC TEST CASES BỔ SUNG ĐỂ ĐẠT 100% BRANCH COVERAGE ---
    
    @Test
    public void testInvalid_BIsZeroOrNegative() {
        assertEquals("Invalid", Triangle.classify(5, 0, 5));
    }

    @Test
    public void testInvalid_CIsZeroOrNegative() {
        assertEquals("Invalid", Triangle.classify(5, 5, -1));
    }

    @Test
    public void testNotATriangle_ACLessThanB() {
        assertEquals("Not a triangle", Triangle.classify(2, 5, 2));
    }

    @Test
    public void testNotATriangle_BCLessThanA() {
        assertEquals("Not a triangle", Triangle.classify(5, 2, 2));
    }

    @Test
    public void testIsosceles_AEqualsB_NotC() {
        assertEquals("Isosceles", Triangle.classify(4, 4, 5));
    }
    
    @Test
    public void testIsosceles_BEqualsC() {
        assertEquals("Isosceles", Triangle.classify(5, 4, 4));
    }

    @Test
    public void testIsosceles_AEqualsC() {
        assertEquals("Isosceles", Triangle.classify(4, 5, 4));
    }
}