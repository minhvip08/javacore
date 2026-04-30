package com.simi.marvel.app;

import com.simi.marvel.animals.Cat;
import com.simi.marvel.animals.Dog;
import com.simi.marvel.base.Animal;

public class DownCastingDemo {

    public static void main(String[] args) {
        Animal anm;
        Dog dog = new Dog();
        anm = dog; // upcasting

        dog = (Dog) anm; // downcasting
        AnimalUtility.performAction(dog);

        if (anm instanceof Cat ) {
            Cat cat = (Cat) anm;
        }

    }

}
