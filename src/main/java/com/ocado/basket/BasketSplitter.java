package com.ocado.basket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BasketSplitter {

    JSONObject config;

    public BasketSplitter(String absolutePathToConfigFile) {
        try {
            config = (JSONObject) new JSONParser().parse(new FileReader(absolutePathToConfigFile));
        } catch (IOException e) {
            System.out.println("Can't read config file");
            throw new RuntimeException(e);
        } catch (ParseException e) {
            System.out.println("File's content is incorrect");
            throw new RuntimeException(e);
        }
    }
    public Map<String, List<String>> split(List<String> items) {

        //get map of items and their delivery methods
        var itemsMap = filterConfigByItems(items);
        System.out.println("Items Map: " + itemsMap);

        //get all unique delivery methods for these items
        Set<String> deliveryMethods = itemsMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());

        //System.out.println("Delivery methods: " + deliveryMethods);

        //get all permutations of delivery methods
        List<List<String>> permutations = permute(new ArrayList<>(deliveryMethods));

        //System.out.println("Permutations: " + permutations);


        
        Map<String, List<String>> bestMethodYet = new HashMap<String, List<String>>();
        for (List<String> permutation : permutations) {
            Map<String, List<String>> currentMethod = new HashMap<String, List<String>>();
            Map<String, List<String>> itemsWithMethods = new HashMap<String, List<String>>(itemsMap);
            if (bestMethodYet.isEmpty()) {
                Map<String, List<String>>tmpMethod = new HashMap<>(itemsWithMethods);
                bestMethodYet = new HashMap<>(tmpMethod);
                //System.out.println("--------------------Best method Yet: " + bestMethodYet);
            }
            for (String method : permutation) {
                if(itemsWithMethods.isEmpty()){
                    break;
                }
                //System.out.println("Items for method: " + itemsWithMethods);
                //get all items that can be delivered by this method
                Iterator<Map.Entry<String, List<String>>> iterator = itemsWithMethods.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<String>> entry = iterator.next();
                    if (entry.getValue().contains(method)) {
                        //if method doesn't exist yet
                        if (!currentMethod.containsKey(method)) {
                            currentMethod.put(method, new ArrayList<String>());
                        }
                        //if list exists, add item to it
                        currentMethod.get(method).add(entry.getKey());

                        iterator.remove();
                    }
                }
            }

            if(currentMethod.size() == 2) {
                //System.out.println(currentMethod);
            }
            if (currentMethod.size() < bestMethodYet.size()) {
                bestMethodYet = currentMethod;
            } else if (currentMethod.size() == bestMethodYet.size()) {
                //get method with the most items
                int maxCountcurrentMethod = 0;
                int maxCountbestMethodYet = 0;
                for (String method : currentMethod.keySet()) {
                    // count the number of items for the current method
                    int itemCount = currentMethod.get(method).size();
                    if (itemCount > maxCountcurrentMethod) {
                        maxCountcurrentMethod = itemCount;
                    }
                    // count the number of items for the best method yet
                    if (bestMethodYet.containsKey(method)) {
                        itemCount = bestMethodYet.get(method).size();
                        if (itemCount > maxCountbestMethodYet) {
                            maxCountbestMethodYet = itemCount;
                        }
                    }
                }
                //if current method has more items than the best method yet, then swap
                if (maxCountcurrentMethod > maxCountbestMethodYet) {
                    bestMethodYet = currentMethod;
                }
            }
        }
        Map<String, List<String>> result = new HashMap<>(bestMethodYet);
        return result;
    }

    public Map<String, List<String>> filterConfigByItems(List<String> items) {
        Map<String, List<String>> filteredConfig = new HashMap<>();
        for (Object key : config.keySet()) {
            if (items.contains(key)) {
                JSONArray methods = (JSONArray) config.get(key);
                List<String> methodsList = new ArrayList<>();
                for (Object method : methods) {
                    methodsList.add((String) method);
                }
                filteredConfig.put((String) key, methodsList);
            }
        }
        return filteredConfig;
    }

    private static List<List<String>> permute(List<String> nums) {
        List<List<String>> results = new ArrayList<>();
        if (nums == null || nums.size() == 0) {
            return results;
        }
        List<String> result = new ArrayList<>();
        dfs(nums, results, result);
        return results;
    }

    private static void dfs(List<String> nums, List<List<String>> results, List<String> result) {
        if (nums.size() == result.size()) {
            List<String> temp = new ArrayList<>(result);
            results.add(temp);
        }
        for (int i = 0; i < nums.size(); i++) {
            if (result.contains(nums.get(i))) {
                continue;
            }
            result.add(nums.get(i));
            dfs(nums, results, result);
            result.remove(result.size() - 1);
        }
    }
}