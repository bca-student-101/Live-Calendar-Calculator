
package backend;

import java.time.LocalDate;

public class lunarcalendar {

    public String getlunarDate() {

        LocalDate today = LocalDate.now();

        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();

        String paksha;

        if (day <= 15) {
            paksha = "Shukla Paksha";
        } else {
            paksha = "Krishna Paksha";
        }

        return "Hindu Lunar Date: "
                + day + "/" + month + "/" + year + " | " + paksha;
    }
}