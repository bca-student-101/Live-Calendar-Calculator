package backend;
import java.time.LocalDate;

public class festival {
   public String getfestival() {
    LocalDate today = LocalDate.now();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();

      if (month == 1 && day == 26) {
        return "Republic Day";
        }
         else if (month == 8 && day == 15) {
            return "Independence Day";
        } 
        else if (month == 10 && day == 2) {
            return "Gandhi Jayanti";
        } 
        else if (month == 12 && day == 25) {
            return "Christmas";
        } 
        else {
            return "No Festival Today";
        }
      } 

      public String getfestival(int year, int month, int day) {
      if (month == 1 && day == 26) {
        return "Republic Day";
        }
         else if (month == 8 && day == 15) {
            return "Independence Day";
        } 
        else if (month == 10 && day == 2) {
            return "Gandhi Jayanti";
        } 
        else if (month == 12 && day == 25) {
            return "Christmas";
        } 
        else {
            return "No Festival Today";
        }
      } 
   }