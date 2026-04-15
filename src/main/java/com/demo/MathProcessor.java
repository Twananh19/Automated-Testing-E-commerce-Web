package com.demo;

public class MathProcessor {
    
    // Yêu cầu: 1 vòng lặp (for), có rẽ nhánh (if)
    public int sumEvenNumbers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            if (i % 2 == 0) {
                sum += i;
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        MathProcessor processor = new MathProcessor();
        System.out.println("Tổng các số chẵn từ 1 đến 5 là: " + processor.sumEvenNumbers(5));
        System.out.println("Tổng các số chẵn từ 1 đến 10 là: " + processor.sumEvenNumbers(10));
    }
}