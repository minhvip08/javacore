package com.simi.object.demo;

import java.lang.reflect.InvocationTargetException;

public class GetClassDemo {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Person person = new Person();
        person.toString();
        Class<? extends Person> prsnClass = person.getClass();
        Person sample = prsnClass.getDeclaredConstructor().newInstance();
        System.out.println(prsnClass.getName());
        System.out.println(prsnClass.getSimpleName());
        System.out.println(prsnClass.getPackageName());
    }

}
