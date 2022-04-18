package org.suganthan.revise.basics.concurrentModifitionException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConcurrentModificationException_1 {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        int i;
        for (i = 0; i < 100; i++) {
            map.put("key - "+i, i);
        }
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            map.put("key-" + i, i);
            iterator.next();
            i++;
        }
    }
}
