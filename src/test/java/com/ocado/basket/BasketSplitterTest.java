package com.ocado.basket;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasketSplitterTest {

    private BasketSplitter basket;

    @Before
    public void setUp(){
        basket = new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config.json");
    }

    @Test
    public void configFileShouldReturnRuntimeExceptionOnLoadingIncorrectPath(){
        Assert.assertThrows(RuntimeException.class, () -> new BasketSplitter(""));
    }

    @Test
    public void configFileShouldReturnParseExceptionOnLoadingIncorrectConfigContent(){
        Assert.assertThrows(RuntimeException.class, () -> new BasketSplitter("C:\\Users\\darek\\IdeaProjects\\untitled1\\src\\test\\java\\com\\ocado\\basket\\config_bad.json"));
    }

    @Test
    public void filterConfigByItemsShouldReturnItemToMethodsMap() {
        List<String> items = List.of("Dc Hikiage Hira Huba", "Longos - Chicken Curried");
        Map<String, List<String>> expected = Map.of("Dc Hikiage Hira Huba", List.of("In-store pick-up", "Next day shipping"), "Longos - Chicken Curried", List.of("Express Collection", "Same day delivery", "Courier"));
        Assert.assertEquals(expected, basket.filterConfigByItems(items));

    }

    @Test
    public void splitShouldReturnMapWithLeastDeliveryGroups() {
        List<String> items = List.of("Pepper - Julienne, Frozen", "Pepper - Red, Finger Hot");
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("Next day shipping", List.of("Pepper - Julienne, Frozen", "Pepper - Red, Finger Hot"));
        Assert.assertEquals(expected, basket.split(items));
    }

    @Test
    public void splitShouldReturnMapWithLeastDeliveryGroups2() {
        List<String> items = List.of("Dried Peach", "Cake - Miini Cheesecake Cherry", "Spinach - Frozen", "Cabbage - Nappa");
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("Courier", List.of("Cabbage - Nappa", "Cake - Miini Cheesecake Cherry", "Dried Peach"));
        expected.put("Mailbox delivery", List.of("Spinach - Frozen"));
        Assert.assertEquals(expected, basket.split(items));
    }

    @Test
    public void splitShouldReturnMapWithLeastDeliveryGroups3() {
        List<String> items = List.of("Cookies Oatmeal Raisin", "Sugar - Cubes", "Sole - Dover, Whole, Fresh",
         "Juice - Ocean Spray Cranberry", "Garlic - Peeled", "Puree - Strawberry");
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("Pick-up point", List.of("Cookies Oatmeal Raisin", "Juice - Ocean Spray Cranberry"));
        expected.put("In-store pick-up", List.of("Sole - Dover, Whole, Fresh", "Puree - Strawberry"));
        expected.put("Same day delivery", List.of("Sugar - Cubes", "Garlic - Peeled"));
        Assert.assertEquals(expected, basket.split(items));
    }
}