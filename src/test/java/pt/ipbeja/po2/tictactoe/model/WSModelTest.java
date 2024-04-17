package pt.ipbeja.po2.tictactoe.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WSModelTest {
    @TempDir
    static Path tmp;
    static Path tmp_db;

    @BeforeAll
    static void beforeAll() throws IOException {
        tmp_db = Files.createFile(tmp.resolve("db.txt"));
        try (BufferedWriter writer = Files.newBufferedWriter(tmp_db)) {
            writer.write("""
                    test
                    words
                    matrix
                    list
                    database""");
        } catch (IOException ignored) {
        }
    }

    @Test
    void testWordFound() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);

        assertEquals("CASA", model.wordFound("CASA"));
    }

    @Test
    void testWordWithWildcardFound() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);
        assertEquals("MALA", model.wordWithWildcardFound("MALA"));
    }

    @Test
    void testallWordsWereFound() {
        WSModel model = new WSModel(WSModel.MAX_SIDE_LEN, WSModel.MAX_SIDE_LEN, tmp_db);
        this.registerEmptyView(model);
        assertEquals("MALA", model.wordFound("MALA"));
        assertEquals("CA", model.wordFound("CA"));
        assertTrue(model.allWordsWereFound());
    }




    private void registerEmptyView(@NotNull WSModel model) {
        model.registerView(message -> {
            // do nothing
        });
    }
}

