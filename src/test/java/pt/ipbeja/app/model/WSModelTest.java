package pt.ipbeja.app.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipbeja.app.throwables.NotInGameException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

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
        WSModel model;
        try {
            model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.registerView(new EmptyView());
        assertDoesNotThrow(model::startGame);

        String word = Arrays.stream(contents.split("\n")).findFirst().orElse("");
        try {
            assertEquals(word.toUpperCase(Locale.ROOT), model.wordFound(word));
        } catch (NotInGameException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testWordWithWildcardFound() {
        WSModel model;
        try {
            model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.registerView(new EmptyView());
        assertDoesNotThrow(model::startGame);

        String word = Arrays.stream(contents.split("\n")).findFirst().orElse("");
        try {
            assertEquals(word.toUpperCase(Locale.ROOT), model.wordFound(word));
        } catch (NotInGameException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAllWordsWereFound() {
        WSModel model;
        try {
            model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.registerView(new EmptyView());
        assertDoesNotThrow(model::startGame);

        for (String w :
                contents.split("\n")) {
            w = w.toUpperCase(Locale.ROOT);
            try {
                assertEquals(w, model.wordFound(w));
            } catch (NotInGameException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            assertTrue(model.allWordsWereFound());
        } catch (NotInGameException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void game() {
        WSModel model;
        try {
            model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.registerView(new EmptyView());
        assertDoesNotThrow(model::startGame);

        for (String w :
                contents.split("\n")) {
            w = w.toUpperCase(Locale.ROOT);
            assertFalse(model.gameEnded());
            try {
                assertEquals(w, model.wordFound(w));
            } catch (NotInGameException e) {
                throw new RuntimeException(e);
            }
        }
        assertTrue(model.gameEnded());
        GameResults res = model.endGame();
        assertEquals(res.words().size(), res.words_found().size());
    }
}