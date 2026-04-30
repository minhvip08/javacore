package com.simi.streams;

import java.util.Random;
import java.util.stream.Stream;

public class StreamLimitOperationDemo {

    public static void main(String[] args) {
        Stream.generate(new Random()::nextInt)
                .limit(10)
                .map(num -> num *2)
                .forEach(System.out::println);
    }

}
