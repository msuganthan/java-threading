package org.suganthan.miscellaneous;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class ThreadLocalDemonstration {
    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            String birthDate = new ThreadLocalDemonstration().birthDate();
            System.out.println(birthDate);
        }).start();

        new Thread(() -> {
            String birthDate = new ThreadLocalDemonstration().birthDate();
            System.out.println(birthDate);
        }).start();
    }

    public String birthDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }
}
