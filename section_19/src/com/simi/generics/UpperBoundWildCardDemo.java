package com.simi.generics;

import com.simi.generics.model.Developer;
import com.simi.generics.model.Employee;
import com.simi.generics.model.Manager;

import java.util.ArrayList;
import java.util.List;

public class UpperBoundWildCardDemo {

    public static void main(String[] args) {

        List<Employee> employees = List.of(new Employee(), new Employee());
        printEmployeeNames(employees);
        List<Developer> developers = List.of(new Developer(), new Developer());
        printEmployeeNames(developers);
        List<Manager> managers = List.of(new Manager(), new Manager());
        printEmployeeNames(managers);
    }

    public static  void printEmployeeNames(List<? extends Employee> employees) {
        for(Employee employee: employees) {
            System.out.println(employee);
        }
    }



}
