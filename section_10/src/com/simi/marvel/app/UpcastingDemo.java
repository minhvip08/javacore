package com.simi.marvel.app;

import com.simi.marvel.animals.Cat;
import com.simi.marvel.animals.Dog;
import com.simi.marvel.base.Animal;

public class UpcastingDemo {

    public static void main(String[] args) {
        Animal anm = new Animal();
        anm.setName("Scooby");
        AnimalUtility.printName(anm);

        anm = new Dog();
        anm.setName("Charlie");
        AnimalUtility.printName(anm);

        Cat cat = new Cat();
        cat.setName("Snoopy");
        AnimalUtility.printName(cat);

    }
}
