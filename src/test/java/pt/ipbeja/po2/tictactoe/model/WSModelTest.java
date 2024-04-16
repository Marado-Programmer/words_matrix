package pt.ipbeja.po2.tictactoe.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WSModelTest {

    @Test
    void testWordFound() {
        WSModel model = new WSModel("ACCD\nEAGH\nISKL\nMAOP");
        this.registerEmptyView(model);

        assertEquals("CASA", model.wordFound("CASA"));
    }

    @Test
    void testWordWithWildcardFound() {
        WSModel model = new WSModel("MA*A\nEAGH\nISKL\nMSOP");
        this.registerEmptyView(model);
        assertEquals("MALA", model.wordWithWildcardFound("MALA"));
    }

    @Test
    void testallWordsWereFound() {
        WSModel model = new WSModel("MALA\nECGH\nIAKL\nMSOP");
        this.registerEmptyView(model);
        assertEquals("MALA", model.wordFound("MALA"));
        assertEquals("CA", model.wordFound("CA"));
        assertTrue(model.allWordsWereFound());
    }




    private void registerEmptyView(WSModel model) {
        model.registerView(message -> {
            // do nothing
        });
    }
}

