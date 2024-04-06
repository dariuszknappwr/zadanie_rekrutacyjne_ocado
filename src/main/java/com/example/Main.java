package com.example;

import com.ocado.basket.BasketSplitter;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        BasketSplitter basket = new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config.json");
        List<String> items = List.of("Pepper - Julienne, Frozen", "Pepper - Red, Finger Hot");
        Map<String, List<String>> b = basket.split(items);
        System.out.println(b);
    }
}