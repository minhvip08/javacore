package com.simi.lambda;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

public class BiFunctionsDemo {

    public static void main(String[] args) {
        BiPredicate<Integer, Integer> isSumEven = (num1, num2) -> (num1+num2) % 2 == 0;
        isSumEven.test(4,9); // false

    }

}
