package com.wcpl.integration.worldcup;

import com.fasterxml.jackson.databind.JsonNode;
import com.wcpl.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.football-api", name = "provider", havingValue = "api-football")
@RequiredArgsConstructor
public class ApiFootballWorldCupDataProvider implements WorldCupDataProvider {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.football-api.base-url}")
    private String baseUrl;

    @Value("${app.football-api.api-key:}")
    private String apiKey;

    @Value("${app.football-api.league-id:1}")
    private int leagueId;

    @Value("${app.football-api.season:2026}")
    private int season;

    @Override
    public WorldCupSyncPayload fetchTournamentData() {
        ensureConfigured();

        WebClient client = webClientBuilder.baseUrl(baseUrl).build();
        return new WorldCupSyncPayload(fetchTeams(client), fetchMatches(client));
    }

    private List<SyncedTeam> fetchTeams(WebClient client) {
        JsonNode root = client.get()
                .uri(uri -> uri.path("/teams")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .header("x-apisports-key", apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<SyncedTeam> teams = new ArrayList<>();
        JsonNode response = root == null ? null : root.path("response");
        if (response == null || !response.isArray()) return teams;

        for (JsonNode item : response) {
            JsonNode team = item.path("team");
            if (team.path("id").isMissingNode() || team.path("name").isMissingNode()) continue;

            String externalId = "api-football:" + team.path("id").asText();
            String name = team.path("name").asText();
            String shortName = textOrFallback(team.path("code"), buildShortName(name));
            String logo = textOrNull(team.path("logo"));

            teams.add(new SyncedTeam(externalId, name, shortName, logo, null));
        }

        return teams;
    }

    private List<SyncedMatch> fetchMatches(WebClient client) {
        JsonNode root = client.get()
                .uri(uri -> uri.path("/fixtures")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .header("x-apisports-key", apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<SyncedMatch> matches = new ArrayList<>();
        JsonNode response = root == null ? null : root.path("response");
        if (response == null || !response.isArray()) return matches;

        for (JsonNode item : response) {
            JsonNode fixture = item.path("fixture");
            JsonNode teams = item.path("teams");
            JsonNode goals = item.path("goals");

            String matchId = withPrefix("api-football:", textOrNull(fixture.path("id")));
            String homeTeamId = withPrefix("api-football:", textOrNull(teams.path("home").path("id")));
            String awayTeamId = withPrefix("api-football:", textOrNull(teams.path("away").path("id")));
            String date = textOrNull(fixture.path("date"));

            if (matchId == null || homeTeamId == null || awayTeamId == null || date == null) {
                continue;
            }

            matches.add(new SyncedMatch(
                    matchId,
                    homeTeamId,
                    awayTeamId,
                    OffsetDateTime.parse(date).toLocalDateTime(),
                    mapStage(item.path("league").path("round").asText("")),
                    intOrNull(goals.path("home")),
                    intOrNull(goals.path("away")),
                    mapStatus(fixture.path("status").path("short").asText(""))
            ));
        }

        return matches;
    }

    private void ensureConfigured() {
        if (apiKey == null || apiKey.isBlank() || "your-api-key-here".equals(apiKey)) {
            throw AppException.badRequest(
                    "FOOTBALL_API_NOT_CONFIGURED",
                    "Chua cau hinh FOOTBALL_API_KEY cho API-FOOTBALL"
            );
        }
    }

    private String mapStage(String round) {
        String value = round == null ? "" : round.toLowerCase();
        if (value.contains("final") && !value.contains("semi") && !value.contains("third")) return "FINAL";
        if (value.contains("semi")) return "SF";
        if (value.contains("quarter")) return "QF";
        if (value.contains("round of 16")) return "R16";
        if (value.contains("round of 32")) return "R32";
        if (value.contains("third")) return "THIRD_PLACE";
        return "GROUP";
    }

    private String mapStatus(String status) {
        return switch (status) {
            case "NS", "TBD" -> "SCHEDULED";
            case "1H", "HT", "2H", "ET", "P", "BT", "LIVE", "INT" -> "LIVE";
            case "FT", "AET", "PEN" -> "FINISHED";
            case "CANC", "ABD", "AWD", "WO" -> "CANCELLED";
            default -> "SCHEDULED";
        };
    }

    private Integer intOrNull(JsonNode node) {
        return node == null || node.isNull() || node.isMissingNode() ? null : node.asInt();
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        String value = node.asText();
        return value == null || value.isBlank() ? null : value;
    }

    private String textOrFallback(JsonNode node, String fallback) {
        String value = textOrNull(node);
        return value == null ? fallback : value;
    }

    private String withPrefix(String prefix, String value) {
        return value == null ? null : prefix + value;
    }

    private String buildShortName(String name) {
        String compact = name == null ? "TBD" : name.replaceAll("[^A-Za-z]", "").toUpperCase();
        if (compact.isBlank()) return "TBD";
        return compact.length() <= 3 ? compact : compact.substring(0, 3);
    }
}
