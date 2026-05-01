package com.simi.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class FileReaderWithScanner {

    public static void main(String[] args) throws  IOException {
        File file = new File("/Users/simi/Desktop/story.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()){
            System.out.println(sc.nextLine());
        }
        sc.close();
    }

}
