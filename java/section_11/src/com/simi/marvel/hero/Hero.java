package com.simi.marvel.hero;

public interface Hero {

    default void walk() {
        System.out.println("Walking");
    }

}
