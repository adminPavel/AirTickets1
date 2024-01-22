package com.pavel;

import java.io.*;
import java.nio.charset.Charset;

public class ReadFile {

    // Метод, который считывает содержимое файла и возвращает его в виде объекта StringBuilder
    public static StringBuilder toImport(String name) throws FileNotFoundException {
        // Путь к файлу передается в качестве аргумента метода
        String path = name;
        // Создается объект StringBuilder для хранения содержимого файла
        StringBuilder str = new StringBuilder();
        // Создается объект File на основе указанного пути
        File file = new File(path);

        // Создается объект BufferedReader для чтения файла
        BufferedReader br = null;
        try {
            // Создается объект BufferedReader для чтения файла
            br = new BufferedReader(new FileReader(file));
            String line = null;

            // Чтение каждой строки файла и добавление ее в объект StringBuilder
            while ((line = br.readLine()) != null) {
                // Декодирование строки с помощью метода decodeText и добавление ее к объекту StringBuilder
                str.append(decodeText(line, "UTF-8"));
            }
        } catch (IOException e) {
            // Вывод стека исключений в случае возникновения ошибки ввода-вывода
            e.printStackTrace();
        } finally {
            // Закрытие объекта BufferedReader в блоке finally
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    // Пропуск возможного исключения при закрытии BufferedReader
                }
            }
        }
        // Возвращение объекта StringBuilder, содержащего весь текст файла
        return str;
    }

    // Метод для декодирования переданной строки в указанную кодировку
    static String decodeText(String input, String encoding) throws IOException {
        // Создается объект BufferedReader для чтения байтов из входной строки
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(input.getBytes()),
                        Charset.forName(encoding)));

        // Чтение одной строки из входных данных и возвращение полученной строки
        return reader.readLine();
    }
}