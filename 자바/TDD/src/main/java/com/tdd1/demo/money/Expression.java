package com.tdd1.demo.money;

public interface Expression {
    Money reduce(Bank bank, String to);
}
