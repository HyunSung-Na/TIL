package com.tdd1.demo.money;

public class Sum implements Expression{

    Expression augend;
    Expression addend;

    Sum(Expression augend, Expression addend){
        this.addend = addend;
        this.augend = augend;
    }

    public Money reduce(Bank bank, String  to) {
        int amount = augend.reduce(bank, to).amount + addend.reduce(bank, to).amount;
        return new Money(amount, to);
    }

    @Override
    public Expression plus(Expression tenFrancs) {
        return new Sum(this, addend);
    }

    @Override
    public Expression times(int multiplier) {
        return new Sum(augend.times(multiplier),
                addend.times(multiplier));
    }
}
