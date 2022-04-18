package org.suganthan.revise.basics.atomicInteger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicInHashMapTest {
    public static void main(String[] args) {
        Map<AtomicInteger, String> mapAtomic = new HashMap<>();
        Map<Integer, String> mapInteger = new HashMap<>();

        AtomicInteger fiveAtomic = new AtomicInteger(5);
        AtomicInteger fiveAtomicToo = new AtomicInteger(5);

        // create two Integer instances
        Integer fiveInt = 5;
        Integer fiveIntToo = 5;

        // Though the key is 5, but the two AtomicInteger instances
        // have different hashcodes
        mapAtomic.put(fiveAtomic, "first five atomic");
        mapAtomic.put(fiveAtomicToo, "second five atomic");
        System.out.println("value for key 5 : " + mapAtomic.get(fiveAtomic));

        // With Integer type key, the second put overwrites the
        // key with Integer value 5.
        mapInteger.put(fiveInt, "first five int");
        mapInteger.put(fiveIntToo, "second five int");
        System.out.println("value for key 5 : " + mapInteger.get(fiveInt));
    }
}
