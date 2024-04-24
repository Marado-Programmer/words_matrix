package pt.ipbeja.app.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WSModelTest {
    static final String contents = """
            test
            words
            matrix
            list
            database""";
    @TempDir
    static Path tmp;
    static Path tmp_db;

    @BeforeAll
    static void beforeAll() throws IOException {
        tmp_db = Files.createFile(tmp.resolve("db.txt"));
        try (BufferedWriter writer = Files.newBufferedWriter(tmp_db)) {
            writer.write(contents);
        } catch (IOException ignored) {
        }
    }

    @Test
    void testWordFound() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);

        assertTrue(model.wordFound(Arrays.stream(contents.split("\n")).findFirst().orElse("")));
    }

    @Test
    void testWordWithWildcardFound() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);
        assertTrue(model.wordFound(Arrays.stream(contents.split("\n")).findFirst().orElse("")));
    }

    @Test
    void testAllWordsWereFound() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);

        for (String w :
                contents.split("\n")) {
            assertTrue(model.wordFound(w));
        }
        assertTrue(model.allWordsWereFound());
    }

    @Test
    void game() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);

        for (String w :
                contents.split("\n")) {
            assertFalse(model.gameEnded());
            assertTrue(model.wordFound(w));
        }
        assertTrue(model.gameEnded());
        GameResults res = model.endGame();
        assertEquals(res.words().size(), res.words_found().size());
    }

    private void registerEmptyView(@NotNull WSModel model) {
        model.registerView(new WSView() {
            @Override
            public void update(MessageToUI messageToUI) {

            }

            @Override
            public void wordFound(Position start, Position end) {

            }

            @Override
            public void gameEnded(GameResults res) {

            }
        });
    }

    @Test
    void test() {
        // create game model
        // set database
        // set dimensions
        // start game
        // play
        // end game
        // start game
        // play
        // ...
    }
}