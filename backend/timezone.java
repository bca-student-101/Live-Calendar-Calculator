
package backend;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class timezone {

    public String getCurrentTime(String zone) {

        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(zone));

        return currentTime.toString();
    }
}