package com.tdd1.demo.money;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class tddTest1 {

    @DisplayName("dollar Times 테스트")
    @Test
    public void testMultiplication() {
        Money five = Money.dollar(5);
        assertEquals(Money.dollar(10), five.times(2));
        assertEquals(Money.dollar(15), five.times(3));

    }

    @DisplayName("franc Times 테스트")
    @Test
    public void francTestMultiplication() {
        Money five = Money.franc(5);
        assertEquals(Money.franc(10), five.times(2));
        assertEquals(Money.franc(15), five.times(3));

    }


    @DisplayName("TDD 테스트")
    @Test
    public void testEquality() {
        assertTrue(Money.dollar(5).equals(Money.dollar(5)));
        assertFalse(Money.dollar(5).equals(Money.dollar(6)));
        assertFalse(Money.franc(5).equals(Money.dollar(5)));
    }

    @DisplayName("통화개념 테스트")
    @Test
    public void testCkurrency() {
        assertEquals("USD", Money.dollar(1).currency());
        assertEquals("CHF", Money.franc(1).currency());
    }

    @DisplayName("드디어 더하기")
    @Test
    public void testSimpleAddition() {
        Money sum = Money.dollar(5).plus(Money.dollar(5));
        assertEquals(Money.dollar(10), sum);
        Money five = Money.dollar(5);
        Expression sum1 = five.plus(five);
        Bank bank = new Bank();
        Money reduced = bank.reduce(sum1, "USD");
        assertEquals(Money.dollar(10), reduced);
    }
}
