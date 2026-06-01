package com.wcpl.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BettingLineParserTest {

    @Test
    void parse_validFormat_returnsMatchWithLines() {
        String content = """
                MATCH=1001
                Argentina Win|1.8
                Draw|3.2
                Over 2.5 Goals|2.1
                """;

        List<BettingLineParser.ParsedMatch> result = BettingLineParser.parse(content);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).matchId()).isEqualTo("1001");
        assertThat(result.get(0).lines()).hasSize(3);
    }

    @Test
    void parse_multipleMatches_returnsAll() {
        String content = """
                MATCH=1001
                Home Win|1.5
                Draw|3.0

                MATCH=1002
                Away Win|2.0
                """;

        List<BettingLineParser.ParsedMatch> result = BettingLineParser.parse(content);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).matchId()).isEqualTo("1001");
        assertThat(result.get(1).matchId()).isEqualTo("1002");
    }

    @Test
    void parse_withComments_ignoresComments() {
        String content = """
                # Đây là comment
                MATCH=2001
                # Comment giữa
                Over 2.5|2.1
                """;

        List<BettingLineParser.ParsedMatch> result = BettingLineParser.parse(content);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lines()).hasSize(1);
    }

    @Test
    void parse_withInvalidOdds_skipsLine() {
        String content = """
                MATCH=3001
                Home Win|abc
                Draw|3.2
                """;

        List<BettingLineParser.ParsedMatch> result = BettingLineParser.parse(content);

        assertThat(result.get(0).lines()).hasSize(1);
        assertThat(result.get(0).lines().get(0).description()).isEqualTo("Draw");
    }

    @Test
    void parse_lineWithoutPipe_isSkipped() {
        String content = """
                MATCH=4001
                This line has no pipe separator
                Draw|3.2
                """;

        List<BettingLineParser.ParsedMatch> result = BettingLineParser.parse(content);

        assertThat(result.get(0).lines()).hasSize(1);
    }

    @Test
    void parse_oddsAreParsedCorrectly() {
        String content = """
                MATCH=5001
                Over 2.5 Goals|2.15
                """;

        BettingLineParser.ParsedLine line = BettingLineParser.parse(content).get(0).lines().get(0);

        assertThat(line.odds()).isEqualTo(2.15);
    }

    @Test
    void parse_lineTypeDetection_winKeyword() {
        String content = "MATCH=1\nHome Win|1.5\n";
        BettingLineParser.ParsedLine line = BettingLineParser.parse(content).get(0).lines().get(0);
        assertThat(line.lineType()).isEqualTo("WIN_HOME");
    }

    @Test
    void parse_lineTypeDetection_drawKeyword() {
        String content = "MATCH=1\nDraw|3.0\n";
        BettingLineParser.ParsedLine line = BettingLineParser.parse(content).get(0).lines().get(0);
        assertThat(line.lineType()).isEqualTo("DRAW");
    }

    @Test
    void parse_lineTypeDetection_overKeyword() {
        String content = "MATCH=1\nOver 2.5 Goals|2.1\n";
        BettingLineParser.ParsedLine line = BettingLineParser.parse(content).get(0).lines().get(0);
        assertThat(line.lineType()).isEqualTo("OVER");
    }

    @Test
    void parse_lineTypeDetection_underKeyword() {
        String content = "MATCH=1\nUnder 2.5 Goals|1.8\n";
        BettingLineParser.ParsedLine line = BettingLineParser.parse(content).get(0).lines().get(0);
        assertThat(line.lineType()).isEqualTo("UNDER");
    }

    @Test
    void parse_lineTypeDetection_exactScore() {
        String content = "MATCH=1\n2-1 Correct Score|8.5\n";
        BettingLineParser.ParsedLine line = BettingLineParser.parse(content).get(0).lines().get(0);
        assertThat(line.lineType()).isEqualTo("EXACT_SCORE");
    }

    @Test
    void parse_emptyContent_returnsEmptyList() {
        assertThat(BettingLineParser.parse("")).isEmpty();
        assertThat(BettingLineParser.parse("   \n  \n")).isEmpty();
    }
}
