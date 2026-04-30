package com.simi;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║  Thrift Calculator - Client & Server Test Suite  ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  gradle runServer  - Start the Thrift server");
        System.out.println("  gradle runClient  - Run the Thrift client");
        System.out.println("  gradle test       - Run all JUnit tests");
        System.out.println();
        System.out.println("To manually test:");
        System.out.println("  1. Open Terminal 1: gradle runServer");
        System.out.println("  2. Open Terminal 2: gradle runClient");
        System.out.println();
        System.out.println("Services available:");
        System.out.println("  - ping()              : Test connectivity");
        System.out.println("  - add(a, b)           : Simple addition");
        System.out.println("  - calculate(...)      : Arithmetic operations");
        System.out.println("  - getStruct(key)      : Retrieve shared struct");
        System.out.println("  - zip()               : Oneway method test");
        System.out.println();
    }
}
