package com.example.myapplication2;

import java.util.ArrayList;
import java.util.Collections;


public class Util {
/*
Shuffle and pick four random cards
 */
    private static ArrayList<Integer> getUniqueDigits(){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 1; i < 14; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        ArrayList<Integer> digits = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            digits.add(list.get(i));
        }
        return digits;
    }

    private static ArrayList<String> getUniqueOperators(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+");
        list.add("-");
        list.add("*");
        list.add("/");
        Collections.shuffle(list);
        ArrayList<String> operators = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            operators.add(list.get(i));
        }
        return operators;
    }

    static ArrayList<Integer> getValidDigits(){
        float total = 0;
        ArrayList<Integer> digits = getUniqueDigits();
        while (total != 24) {
            digits = getUniqueDigits();
            ArrayList<String> operators = getUniqueOperators();
            total = digits.get(0);
            for (int i = 0; i < 3; i++){
                if (operators.get(i) == "+"){
                    total = total + digits.get(i+1);
                }
                if (operators.get(i) == "-"){
                    total = total - digits.get(i+1);
                }
                if (operators.get(i) == "*"){
                    total = total * digits.get(i+1);
                }
                if (operators.get(i) == "/"){
                    total = total / digits.get(i+1);
                }
            }
        }
        return digits;
    }
}
