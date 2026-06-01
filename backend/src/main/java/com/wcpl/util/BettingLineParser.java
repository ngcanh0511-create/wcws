package com.wcpl.util;

import java.util.ArrayList;
import java.util.List;

public class BettingLineParser {

    public record ParsedLine(Long id, String lineType, String description, double odds) {}

    public record ParsedMatch(String matchId, List<ParsedLine> lines) {}

    public static List<ParsedMatch> parse(String content) {
        List<ParsedMatch> result = new ArrayList<>();
        ParsedMatch current = null;

        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.isBlank() || line.startsWith("#")) continue;

            if (line.startsWith("MATCH=")) {
                if (current != null) result.add(current);
                current = new ParsedMatch(line.substring(6).trim(), new ArrayList<>());
                continue;
            }

            if (current == null || !line.contains("|")) continue;

            String[] parts = line.split("\\|");
            if (parts.length < 2) continue;

            String description = parts[0].trim();
            double odds;
            try {
                odds = Double.parseDouble(parts[1].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            Long id = parts.length >= 3 ? parseLineId(parts[2].trim()) : null;
            String lineType = detectType(description);
            current.lines().add(new ParsedLine(id, lineType, description, odds));
        }

        if (current != null) result.add(current);
        return result;
    }

    private static Long parseLineId(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String normalized = raw.replace("LINE=", "")
                .replace("ID=", "")
                .replace("#", "")
                .trim();
        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String detectType(String description) {
        String lower = description.toLowerCase();
        if (lower.contains("win") || lower.contains("thắng")) return "WIN_HOME";
        if (lower.contains("draw") || lower.contains("hòa")) return "DRAW";
        if (lower.contains("over")) return "OVER";
        if (lower.contains("under")) return "UNDER";
        if (lower.contains("both teams score") || lower.contains("btts")) return "BTTS";
        if (lower.contains("no goal")) return "BTTS";
        if (lower.contains("first goal") || lower.contains("ghi bàn")) return "FIRST_SCORER";
        if (lower.matches(".*\\d+-\\d+.*")) return "EXACT_SCORE";
        return "SPECIAL";
    }
}
