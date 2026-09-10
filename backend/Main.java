package backend;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {

        public static void main(String[] args) throws IOException {
                calculator calculator = new calculator();
                solarcalendar solarcalendar = new solarcalendar();
                lunarcalendar lunarcalendar = new lunarcalendar();
                festival festival = new festival();
                timezone timezone = new timezone();
                HttpServer server = HttpServer.create(
                                new InetSocketAddress(8080), 0);

                // Backend Test
                server.createContext("/", exchange -> {

                        String response = "java Backend working";

                        exchange.getResponseHeaders().set("Content-Type", "application/json");

                        exchange.sendResponseHeaders(200, response.getBytes().length);

                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });

                // Calculator API
                server.createContext("/api/calculator", exchange -> {

                        exchange.getResponseHeaders().set(
                                        "Access-Control-Allow-Origin",
                                        "*");

                        String query = exchange.getRequestURI().getQuery();

                        String[] values = query.split("&");

                        int a = Integer.parseInt(
                                        values[0].split("=")[1]);

                        int b = Integer.parseInt(
                                        values[1].split("=")[1]);

                        String operation = values[2].split("=")[1];

                        // Calculator class से calculation
                        int result = calculator.calculate(a, b, operation);
                        String response = """
                                        {
                                            "number1": %d,
                                            "number2": %d,
                                            "operation": "%s",
                                            "result": %d
                                        }
                                        """.formatted(a, b, operation, result);

                        exchange.getResponseHeaders().set(
                                        "Content-Type",
                                        "application/json");

                        exchange.sendResponseHeaders(
                                        200,
                                        response.getBytes().length);

                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });
                // solar calendar API
                server.createContext("/api/calendar", exchange -> {
                        exchange.getResponseHeaders().set("Access-control-Allow-Origin", "*");
                        String Date = solarcalendar.getCurrentDate();
                        int month = solarcalendar.getCurrentMonth();
                        int year = solarcalendar.getCurrentYear();
                        int days = solarcalendar.getDaysInMonth();
                        String lunarDate = lunarcalendar.getlunarDate();
                        String festivalName = festival.getfestival();
                        String currenttime = timezone.getCurrentTime("Asia/Kolkata");
                        String response = """
                                        { "date": "%s",
                                         "month": "%d",
                                         "year": "%d",
                                         "days": "%d",
                                         "lunarDate": "%s",
                                         "festival": "%s",
                                         "time": "%s"
                                         }
                                        """.formatted(Date, month, year, days, lunarDate, festivalName, currenttime);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });
                // Time Zone API
                server.createContext("/api/timezone", exchange -> {

                        exchange.getResponseHeaders().set(
                                        "Access-Control-Allow-Origin", "*");

                        String query = exchange.getRequestURI().getQuery();

                        String zone = query.split("=")[1];

                        String currentTime = timezone.getCurrentTime(zone);

                        String response = """
                                        {
                                        "timezone": "%s",
                                        "time": "%s"
                                        }
                                        """.formatted(zone, currentTime);

                        exchange.getResponseHeaders().set("Content-Type", "application/json");

                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });
                // month api
                server.createContext("/api/calendar/month", exchange -> {
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        String query = exchange.getRequestURI().getQuery();
                        String[] values = query.split("&");
                        int year = Integer.parseInt(values[0].split("=")[1]);
                        int month = Integer.parseInt(values[1].split("=")[1]);
                        int days = solarcalendar.getDaysInMonth(year, month);
                        String response = """
                                        {
                                         "year": %d,
                                         "month": %d,
                                         "days": %d
                                         }
                                         """.formatted(year, month, days);
                        exchange.getResponseHeaders().set("content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });

                // Previous Month API
                server.createContext("/api/calendar/previous", exchange -> {

                        exchange.getResponseHeaders().set(
                                        "Access-Control-Allow-Origin", "*");

                        String query = exchange.getRequestURI().getQuery();

                        String[] values = query.split("&");

                        int year = Integer.parseInt(values[0].split("=")[1]);
                        int month = Integer.parseInt(values[1].split("=")[1]);

                        String previousMonth = solarcalendar.getPreviousMonth(year, month);

                        String response = """
                                        {
                                            "previousMonth": "%s"
                                        }
                                        """.formatted(previousMonth);

                        exchange.getResponseHeaders().set(
                                        "Content-Type", "application/json");

                        exchange.sendResponseHeaders(
                                        200,
                                        response.getBytes().length);

                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });
                // Next Month API
                server.createContext("/api/calendar/next", exchange -> {

                        exchange.getResponseHeaders().set(
                                        "Access-Control-Allow-Origin", "*");

                        String query = exchange.getRequestURI().getQuery();

                        String[] values = query.split("&");

                        int year = Integer.parseInt(values[0].split("=")[1]);
                        int month = Integer.parseInt(values[1].split("=")[1]);

                        String nextMonth = solarcalendar.getNextMonth(year, month);

                        String response = """
                                        {
                                            "nextMonth": "%s"
                                        }
                                        """.formatted(nextMonth);

                        exchange.getResponseHeaders().set(
                                        "Content-Type", "application/json");

                        exchange.sendResponseHeaders(
                                        200,
                                        response.getBytes().length);

                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });
                // day of week api

                server.createContext("/api/calendar/day", exchange -> {
                        exchange.getResponseHeaders().set("access-control-allow-origin", "*");
                        String query = exchange.getRequestURI().getQuery();
                        String[] values = query.split("&");
                        int year = Integer.parseInt(values[0].split("=")[1]);
                        int month = Integer.parseInt(values[1].split("=")[1]);
                        int day = Integer.parseInt(values[2].split("=")[1]);
                        String dayofweek = solarcalendar.getDayOfWeek(year, month, day);
                        String response = """
                                        {
                                          "dayOfweek": "%s"
                                        }
                                        """.formatted(dayofweek);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });

                // First Day of Month API
                server.createContext("/api/calendar/first-day", exchange -> {

                        exchange.getResponseHeaders().set(
                                        "Access-Control-Allow-Origin", "*");

                        String query = exchange.getRequestURI().getQuery();

                        String[] values = query.split("&");

                        int year = Integer.parseInt(values[0].split("=")[1]);
                        int month = Integer.parseInt(values[1].split("=")[1]);

                        int firstDay = solarcalendar.getFirstDayOfMonth(year, month);

                        String response = """
                                        {
                                            "firstDay": %d
                                        }
                                        """.formatted(firstDay);

                        exchange.getResponseHeaders().set(
                                        "Content-Type", "application/json");

                        exchange.sendResponseHeaders(
                                        200,
                                        response.getBytes().length);

                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });

                // festival api
                server.createContext("/api/festival", exchange -> {
                        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                        String query = exchange.getRequestURI().getQuery();
                        String[] values = query.split("&");
                        int year = Integer.parseInt(values[0].split("=")[1]);
                        int month = Integer.parseInt(values[1].split("=")[1]);
                        int day = Integer.parseInt(values[2].split("=")[1]);
                        String festivalName = festival.getfestival(year, month, day);
                        String response = """
                                                        {
                                        "festival": "%s"
                                        }
                                                        """.formatted(festivalName);
                        exchange.getResponseHeaders().set("content-type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream output = exchange.getResponseBody();
                        output.write(response.getBytes());
                        output.close();
                });

                server.start();
                System.out.println("Server started at http://localhost:8080");
        }
}