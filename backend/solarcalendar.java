
package backend;

import java.time.LocalDate;
import java.time.YearMonth;

public class solarcalendar {

    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static int getDaysInMonth() {
        YearMonth currentMonth = YearMonth.now();
        return currentMonth.lengthOfMonth();
    }

    public static int getDaysInMonth(int year, int month) {
        YearMonth selectedMonth = YearMonth.of(year, month);
        return selectedMonth.lengthOfMonth();
    }

    public static String getPreviousMonth(int year, int month) {
        YearMonth current = YearMonth.of(year, month);
        return current.minusMonths(1).toString();
    }

    public static String getNextMonth(int year, int month) {
        YearMonth current = YearMonth.of(year, month);
        return current.plusMonths(1).toString();
    }

    public static String getDayOfWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        return date.getDayOfWeek().toString();
    }

    public static int getFirstDayOfMonth(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        return firstDay.getDayOfWeek().getValue();
    }
}