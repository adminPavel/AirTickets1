package com.pavel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Main {
    private static String dir() {
        String path = System.getProperty("user.dir") + File.separator + "src\\main\\java" + File.separator;
        String clDir = com.pavel.Main.class.getName().replace(com.pavel.Main.class.getSimpleName(), "").replace(".", File.separator);
        return path + clDir;
    }
    public static void main(String[] args) {
       // String name = args[0]; // Получение пути к файлу tickets1.json из аргументов командной строки

         String name = dir()+"tickets.json"; // Получение пути к файлу tickets.json
        StringBuilder list;
        try {
            list = ReadFile.toImport(name); // Чтение файла tickets.json и запись его содержимого в объект StringBuilder
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String res = list.toString();

        Gson gson = new Gson(); // Создание экземпляра Gson
        Type type = new TypeToken<Map<String, List<Ticket>>>() {}.getType(); // Создание типа для парсинга JSON
        Map<String, List<Ticket>> myMap = gson.fromJson(res, type); // Парсинг JSON строки и преобразование в Map

        List<Ticket> collect = new ArrayList<>();
        try {
            collect = myMap.values().stream().flatMap(r -> r.stream()).collect(Collectors.toList()); // Преобразование Map в List
        } catch (NullPointerException e) {
            e.getMessage();
        }

        Map<String, List<Long>> airline = new TreeMap<>(Comparator.reverseOrder()); // Карта для хранения времени полета для каждого авиаперевозчика
        List<Integer> prices = new ArrayList<>(); // Список для хранения цен полетов

        for (Ticket it : collect) {
            if ((it.origin.equals("VVO") && it.destination.equals("TLV"))
                    || (it.origin.equals("TLV") && it.destination.equals("VVO"))) {
                Date departure;
                Date arrival;
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.UK);
                try {
                    departure = formatter.parse(it.departure_date + " " + it.departure_time); // Преобразование строки времени отправления в объект Date
                    arrival = formatter.parse(it.arrival_date + " " + it.arrival_time); // Преобразование строки времени прибытия в объект Date
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                prices.add(it.price); // Добавление цены полета в список

                long diffInMillies = Math.abs(arrival.getTime() - departure.getTime()); // Вычисление разницы времени в миллисекундах
                long diff = TimeUnit.MILLISECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS); // Конвертация разницы времени в миллисекундах в определенную единицу измерения
                long diff1 = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS); // Конвертация разницы времени в миллисекундах в определенную единицу измерения

                if (airline.containsKey(it.carrier)) {
                    airline.get(it.carrier).add(diff); // Добавление времени полета в список для данного авиаперевозчика
                }
                if (!airline.containsKey(it.carrier)) {
                    List<Long> timeDiffList = new ArrayList<>();
                    airline.put(it.carrier, timeDiffList); // Добавление нового списка времени полета для данного авиаперевозчика в карту
                    airline.get(it.carrier).add(diff); // Добавление времени полета в список для данного авиаперевозчика
                }
            }
        }

        // Вывод минимального времени полета для каждого авиаперевозчика
        for (Map.Entry<String, List<Long>> entry : airline.entrySet()) {
            Long min = Collections.min(entry.getValue()); // Получение минимального времени полета для данного авиаперевозчика
            long hours = TimeUnit.MILLISECONDS.toHours(min) % 24; // Конвертация времени в часы
            String hour = (hours < 10) ? "0" + hours : String.valueOf(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(min) % 60; // Конвертация времени в минуты
            String minut = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

            System.out.format("Минимальное время полета между городами " +
                    "Владивосток и Тель-Авив для авиаперевозчика %s - %s:%s часов:минут\n", entry.getKey(), hour, minut);
        }

        double average = prices.stream().mapToDouble(d -> d).average().orElse(0); // Вычисление средней цены полета
        System.out.println("Средняя цена: " + average);

        Collections.sort(prices); // Сортировка списка цен полетов
        int median;
        if (prices.size() % 2 == 0)
            median = (prices.get(prices.size() / 2) + prices.get(prices.size() / 2 - 1)) / 2; // Вычисление медианы цен полетов
        else
            median = prices.get(prices.size() / 2);

        System.out.println("Медиана: " + (double) median);

        double difference = Math.abs(median - average); // Вычисление разницы между средней ценой и медианой
        System.out.format("Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив %.0f",
                Math.abs(median - average));
    }
}
