package com.simi.marvel.app;

import com.simi.marvel.base.Person;
import com.simi.marvel.heros.IronMan;

public class MarvelsMain {

    public static void main(String[] args) {
        IronMan ironMan = new IronMan();
        ironMan.walk();
        ironMan.eat("Pasta");
        ironMan.sleep();
        ironMan.usePower();

        Person person = new Person();
        int hc = person.hashCode();
        String str = person.toString();
    }

}
