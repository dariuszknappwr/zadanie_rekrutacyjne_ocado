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

    private JSONObject config;

    public BasketSplitter(String absolutePathToConfigFile) {
        // Using try-with-resources to automatically close the FileReader
        try (FileReader reader = new FileReader(absolutePathToConfigFile)) {
            config = (JSONObject) new JSONParser().parse(reader);
        } catch (IOException e) {
            System.out.println("Can't read config file");
            throw new RuntimeException(e);
        } catch (ParseException e) {
            System.out.println("File's content is incorrect");
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<String>> split(List<String> items) {
        var itemToMethodsMap = filterConfigByItems(items);
        Set<String> deliveryMethods = itemToMethodsMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
        List<List<String>> permutations =  Permutator.permute(new ArrayList<>(deliveryMethods));

        Map<String, List<String>> bestMethodYet = new HashMap<>();
        for (List<String> permutation : permutations) {
            Map<String, List<String>> currentMethodItemsMap = new HashMap<>();
            Map<String, List<String>> itemToMethodsMapCopy = new HashMap<>(itemToMethodsMap);

            for (String method : permutation) {
                if(itemToMethodsMapCopy.isEmpty()){
                    break;
                }
                assignItemsToDeliveryMethod(currentMethodItemsMap, itemToMethodsMapCopy, method);
            }
            bestMethodYet = bestMethodYet.isEmpty() ? currentMethodItemsMap : checkIfMethodIsBetter(bestMethodYet, currentMethodItemsMap);
        }
        return bestMethodYet;
    }

    private void assignItemsToDeliveryMethod(Map<String, List<String>> currentMethod, Map<String, List<String>> itemToMethodsMap, String method) {
        //get all items that can be delivered by this method
        Iterator<Map.Entry<String, List<String>>> iterator = itemToMethodsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            if (entry.getValue().contains(method)) {
                //if method doesn't exist yet, then create new ArrayList
                currentMethod.computeIfAbsent(method, k -> new ArrayList<>()).add(entry.getKey());
                //remove item from itemToMethodsMap
                iterator.remove();
            }
        }
    }

    private Map<String, List<String>> checkIfMethodIsBetter(Map<String, List<String>> bestMethodYet,
            Map<String, List<String>> currentMethod) {
        if (currentMethod.size() < bestMethodYet.size()) {
            return currentMethod;
        }
        if (currentMethod.size() == bestMethodYet.size()) {
            if (hasMoreItems(currentMethod, bestMethodYet)) {
                return currentMethod;
            }
        }
        return bestMethodYet;
    }
    
    private boolean hasMoreItems(Map<String, List<String>> currentMethod, Map<String, List<String>> bestMethodYet) {
        int maxCountCurrentMethod = getMaxItemCount(currentMethod);
        int maxCountBestMethodYet = getMaxItemCount(bestMethodYet);
        return maxCountCurrentMethod > maxCountBestMethodYet;
    }
    
    private int getMaxItemCount(Map<String, List<String>> methodMap) {
        return methodMap.values().stream().mapToInt(List::size).max().orElse(0);
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
}