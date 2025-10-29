package edu.pro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    private static final String INPUT_FILE = "src/edu/pro/txt/harry.txt";
    private static final int TOP_WORDS_COUNT = 30;

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();

        try {
            String content = readAndCleanFile(INPUT_FILE);
            List<String> topWords = countAndSortWords(content);
            printTopWords(topWords);
        } catch (IOException e) {
            System.err.println("Помилка читання файлу: " + e.getMessage());
            return;
        }

        LocalDateTime finish = LocalDateTime.now();
        long durationMs = ChronoUnit.MILLIS.between(start, finish);
        System.out.println("------");
        System.out.println("Час виконання: " + durationMs + " мс");
    }

    private static String readAndCleanFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        return content
                .replaceAll("[^A-Za-z ]", " ")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static List<String> countAndSortWords(String content) {
        return Arrays.stream(content.split("\\s+"))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TOP_WORDS_COUNT)
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.toList());
    }

    private static void printTopWords(List<String> words) {
        words.forEach(System.out::println);
    }
}