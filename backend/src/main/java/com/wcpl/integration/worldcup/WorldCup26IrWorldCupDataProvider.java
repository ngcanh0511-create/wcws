package com.wcpl.integration.worldcup;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.football-api", name = "provider", havingValue = "worldcup26", matchIfMissing = true)
@RequiredArgsConstructor
public class WorldCup26IrWorldCupDataProvider implements WorldCupDataProvider {

    private static final String EXTERNAL_PREFIX = "worldcup26:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    private final WebClient.Builder webClientBuilder;

    @Value("${app.football-api.worldcup26.base-url}")
    private String baseUrl;

    @Override
    public WorldCupSyncPayload fetchTournamentData() {
        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        return new WorldCupSyncPayload(fetchTeams(client), fetchMatches(client));
    }

    private List<SyncedTeam> fetchTeams(WebClient client) {
        JsonNode root = client.get()
                .uri("/teams")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<SyncedTeam> teams = new ArrayList<>();
        JsonNode response = root == null ? null : root.path("teams");
        if (response == null || !response.isArray()) return teams;

        for (JsonNode item : response) {
            String id = textOrNull(item.path("id"));
            String name = textOrNull(item.path("name_en"));
            if (id == null || name == null) continue;

            String group = textOrNull(item.path("groups"));
            teams.add(new SyncedTeam(
                    EXTERNAL_PREFIX + id,
                    name,
                    textOrFallback(item.path("fifa_code"), buildShortName(name)),
                    textOrNull(item.path("flag")),
                    group == null ? null : "Group " + group
            ));
        }

        return teams;
    }

    private List<SyncedMatch> fetchMatches(WebClient client) {
        JsonNode root = client.get()
                .uri("/games")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<SyncedMatch> matches = new ArrayList<>();
        JsonNode response = root == null ? null : root.path("games");
        if (response == null || !response.isArray()) return matches;

        for (JsonNode item : response) {
            String id = textOrNull(item.path("id"));
            String homeTeamId = textOrNull(item.path("home_team_id"));
            String awayTeamId = textOrNull(item.path("away_team_id"));
            String date = textOrNull(item.path("local_date"));
            if (id == null || homeTeamId == null || awayTeamId == null || date == null) continue;

            matches.add(new SyncedMatch(
                    EXTERNAL_PREFIX + id,
                    EXTERNAL_PREFIX + homeTeamId,
                    EXTERNAL_PREFIX + awayTeamId,
                    LocalDateTime.parse(date, DATE_FORMAT),
                    mapStage(textOrNull(item.path("type"))),
                    scoreOrNull(item.path("home_score"), item.path("finished")),
                    scoreOrNull(item.path("away_score"), item.path("finished")),
                    mapStatus(textOrNull(item.path("finished")), textOrNull(item.path("time_elapsed")))
            ));
        }

        return matches;
    }

    private String mapStage(String type) {
        if (type == null) return "GROUP";
        return switch (type.toLowerCase()) {
            case "round_of_32", "r32" -> "R32";
            case "round_of_16", "r16" -> "R16";
            case "quarter", "quarter_final", "qf" -> "QF";
            case "semi", "semi_final", "sf" -> "SF";
            case "final" -> "FINAL";
            case "third_place" -> "THIRD_PLACE";
            default -> "GROUP";
        };
    }

    private String mapStatus(String finished, String elapsed) {
        if ("TRUE".equalsIgnoreCase(finished)) return "FINISHED";
        if (elapsed == null || "notstarted".equalsIgnoreCase(elapsed)) return "SCHEDULED";
        return "LIVE";
    }

    private Integer scoreOrNull(JsonNode scoreNode, JsonNode finishedNode) {
        if (!"TRUE".equalsIgnoreCase(textOrNull(finishedNode))) return null;
        String score = textOrNull(scoreNode);
        return score == null ? null : Integer.parseInt(score);
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        String value = node.asText();
        return value == null || value.isBlank() || "null".equalsIgnoreCase(value) ? null : value;
    }

    private String textOrFallback(JsonNode node, String fallback) {
        String value = textOrNull(node);
        return value == null ? fallback : value;
    }

    private String buildShortName(String name) {
        String compact = name == null ? "TBD" : name.replaceAll("[^A-Za-z]", "").toUpperCase();
        if (compact.isBlank()) return "TBD";
        return compact.length() <= 3 ? compact : compact.substring(0, 3);
    }
}
