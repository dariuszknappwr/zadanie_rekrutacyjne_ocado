package com.ocado.basket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BasketSplitterTest {

    @Test
    public void configFileShouldReturnRuntimeExceptionOnLoadingIncorrectPath(){
        Assert.assertThrows(RuntimeException.class, () -> new BasketSplitter(""));
    }

    @Test
    public void configFileShouldReturnParseExceptionOnLoadingIncorrectConfigContent(){
        Assert.assertThrows(RuntimeException.class, () -> new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config_bad.json"));
    }

    @Test
    public void splitShouldReturnMap() {
        var basket = new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config.json");
        List<String> items = List.of("Dc Hikiage Hira Huba", "Longos - Chicken Curried");
        Map<String, List<String>> expected = Map.of("Dc Hikiage Hira Huba", List.of("In-store pick-up", "Next day shipping"), "Longos - Chicken Curried", List.of("Express Collection", "Same day delivery", "Courier"));
        Assert.assertEquals(expected, basket.filterConfigByItems(items));

    }

    @Test
    public void splitShouldReturnMapWithLeastDeliveryGroups() {
        var basket = new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config.json");
        List<String> items = List.of("Pepper - Julienne, Frozen", "Pepper - Red, Finger Hot");
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("Next day shipping", List.of("Pepper - Julienne, Frozen", "Pepper - Red, Finger Hot"));
        Assert.assertEquals(expected, basket.split(items));
    }

    @Test
    public void splitShouldReturnMapWithLeastDeliveryGroups2() {
        var basket = new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config.json");
        List<String> items = List.of("Dried Peach", "Cake - Miini Cheesecake Cherry", "Spinach - Frozen", "Cabbage - Nappa");
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("Courier", List.of("Cabbage - Nappa", "Cake - Miini Cheesecake Cherry", "Dried Peach"));
        expected.put("Mailbox delivery", List.of("Spinach - Frozen"));
        Assert.assertEquals(expected, basket.split(items));
    }
    /*
    "Cabbage - Nappa": ["Courier", "Parcel locker", "Express Collection", "Mailbox delivery", "Same day delivery", "Pick-up point"],
    "Spinach - Frozen": ["Mailbox delivery"],
    "Cake - Miini Cheesecake Cherry": ["Courier"],
    "Dried Peach": ["Same day delivery", "Courier"],
     */
}

        /*
         Stworzyć mapę:
         "Express Delivery":4
          "Click&Collect":3
          "Courier":3

          Następnie zabrać wszystkie produkty z największej grupy dostawy i stworzyć ponownie mapę. Jednak to jest źle:

          "Courier":2
          "Click&Collect":1


            Powtarzać do wyczerpania produktów;
            Pomyśleć o wartościach brzegowych, co jeżeli Express Delivery i Click&Collect mają tę samą liczbę produktów?



    {
    "Carrots (1kg)": ["Express Delivery", "Click&Collect"],
    "Cold Beer (330ml)": ["Express Delivery"],
    "Steak (300g)": ["Express Delivery", "Click&Collect"],
    "AA Battery (4 Pcs.)": ["Express Delivery", "Courier"],
    "Espresso Machine": ["Courier", "Click&Collect"],
    "Garden Chair": ["Courier", "Click&Collect"]
    }

         "Express Delivery":4
          "Click&Collect":4
          "Courier":3




{
"Carrots (1kg)": ["Express Delivery", "Click&Collect"],
"Cold Beer (330ml)": ["Express Delivery", "Courier"],
"Steak (300g)": ["Express Delivery", "Click&Collect"],
"AA Battery (4 Pcs.)": ["Express Delivery", "Courier"],
"Espresso Machine": ["Click&Collect"],
"Garden Chair": ["Courier"]
}

         "Express Delivery":4
          "Click&Collect":3
          "Courier":3

        Można utworzyć listę unikalnych metod dostawy i testować je po kolei (wyjdzie max 2^n wariantów):

        Express Delivery -> Click&Collect -> Courier : 3 paczki, największa 4 sztuki.
        Express Delivery -> Courier -> Click&Collect : 3 paczki, największa 4 sztuki.
        Click&Collect -> Courier -> Express Delivery : 2 paczki, największa 3 sztuki.
        Click&Collect -> Express Delivery -> Courier : 3 paczki, największa 3 sztuki.
        Courier -> Express Delivery -> Click&Collect : 3 paczki, największa 3 sztuki.
        Courier -> Click&Collect -> Express Delivery : 2 paczki, największa 2 sztuki.


         ... */