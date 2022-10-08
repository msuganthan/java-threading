package org.suganthan.miscellaneous;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCompute {
    public static void main( String args[] ) {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("Maths", 50);
        map.put("Science", 60);
        map.put("Programming", 70);
        System.out.println( "The map is - " + map);

        System.out.println( "\nCalling compute function for key Maths");

        Integer newVal = map.compute("Maths", (key, oldVal) -> { return oldVal + 10; });
        System.out.println("\nThe return value is " + newVal);
        System.out.println( "The map is - " + map);

        System.out.println( "\n---------------\n");
        System.out.println( "Calling compute function for key Economics\n");
        newVal =
                map.compute("Economics",
                        (key, oldVal) ->  {
                            System.out.print("Inside BiFunction: The key is ");
                            System.out.print(key);

                            System.out.print(". The value is ");
                            System.out.println(oldVal + ".");

                            if(oldVal != null) {
                                return oldVal + 10;
                            }
                            return null;
                        });
        System.out.println("\nThe return value is " + newVal);
        System.out.println( "The map is - " + map);

    }
}
