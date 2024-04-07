package com.ocado.basket;

import java.util.ArrayList;
import java.util.List;

public class Permutator {
    public static List<List<String>> permute(List<String> nums) {
        List<List<String>> results = new ArrayList<>();
        if (nums == null || nums.size() == 0) {
            return results;
        }
        List<String> result = new ArrayList<>();
        dfs(nums, results, result);
        return results;
    }

    public static void dfs(List<String> nums, List<List<String>> results, List<String> result) {
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
