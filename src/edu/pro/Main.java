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
            List<String> topWords = countTopWordsUltraFast();
            topWords.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Помилка: " + e.getMessage());
            return;
        }

        LocalDateTime end = LocalDateTime.now();
        long duration = ChronoUnit.MILLIS.between(start, end);

        System.out.println("------");
        System.out.println("Час виконання: " + duration + " мс");
    }

    private static List<String> countTopWordsUltraFast() throws IOException {
        ConcurrentHashMap<String, LongAdder> map = new ConcurrentHashMap<>(16_384, 0.75f, 4);

        // 1. Читаємо байти + пропускаємо BOM
        byte[] bytes = Files.readAllBytes(Paths.get(INPUT_FILE));
        int offset = 0;
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            offset = 3;
        }
        char[] chars = new String(bytes, offset, bytes.length - offset, StandardCharsets.UTF_8).toCharArray();

        // 2. Ручний парсинг: без split, без regex, без stream
        StringBuilder word = new StringBuilder(16);
        for (char c : chars) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                word.append(Character.toLowerCase(c));
            } else if (word.length() > 0) {
                String w = word.toString();
                map.computeIfAbsent(w, k -> new LongAdder()).increment();
                word.setLength(0);
            }
        }
        if (word.length() > 0) {
            map.computeIfAbsent(word.toString(), k -> new LongAdder()).increment();
        }

        // 3. Топ-30: сортування через масив (швидше stream)
        return map.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().sum(), a.getValue().sum()))
                .limit(TOP_WORDS_COUNT)
                .map(e -> e.getKey() + " " + e.getValue().sum())
                .toList();
    }
}