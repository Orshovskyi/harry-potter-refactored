package edu.pro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class Main {

    private static final String INPUT_FILE = "src/edu/pro/txt/harry.txt";
    private static final int TOP_WORDS_COUNT = 30;

    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();

        try {
            List<String> topWords = countTopWords();
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

    private static List<String> countTopWords() throws IOException {
        Map<String, LongAdder> wordCount = new ConcurrentHashMap<>();

        byte[] bytes = Files.readAllBytes(Paths.get(INPUT_FILE));
        int start = 0;
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            start = 3;
        }
        String content = new String(bytes, start, bytes.length - start, StandardCharsets.UTF_8);

        Arrays.stream(content.split("\\s+"))
                .map(word -> word.replaceAll("[^A-Za-z]", "").toLowerCase(Locale.ROOT))
                .filter(word -> !word.isEmpty())
                .forEach(word -> wordCount.computeIfAbsent(word, k -> new LongAdder()).increment());

        return wordCount.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().sum(), e1.getValue().sum()))
                .limit(TOP_WORDS_COUNT)
                .map(e -> e.getKey() + " " + e.getValue().sum())
                .toList();
    }

    private static void printTopWords(List<String> words) {
        words.forEach(System.out::println);
    }
}