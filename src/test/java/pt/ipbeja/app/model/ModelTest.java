package pt.ipbeja.app.model;

import org.junit.jupiter.api.Test;
import pt.ipbeja.app.model.throwables.*;
import pt.ipbeja.app.model.words_provider.ManualWordsProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static pt.ipbeja.app.model.WSModel.*;

public class ModelTest {
    @Test
    void setValidDimensions() {
        WSModel model = new WSModel();

        char[] small_word = new char[0];
        char[] large_word = new char[MAX_SIDE_LEN + 1];
        Arrays.fill(large_word, 'a');
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{
                new String(small_word),
                new String(large_word)
        });
        provider.close();
        model.setWords(provider);

        int lines = MAX_SIDE_LEN;
        int cols = MIN_SIDE_LEN;
        try {
            model.setDimensions(lines, cols);
        } catch (InvalidInGameChangeException e) {
            throw new RuntimeException(e);
        }
        assertEquals(lines, model.getLines());
        assertEquals(cols, model.getCols());

        assertEquals(0, model.wordsInUse());
    }

    @Test
    void setDimensionsInGame() {
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{
                "Some",
                "words"
        });
        provider.close();
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, provider);
        model.registerView(new EmptyView());
        assertDoesNotThrow(model::startGame);

        Exception err = assertThrows(InvalidInGameChangeException.class, () -> model.setDimensions(MIN_SIDE_LEN, MIN_SIDE_LEN));
        assertEquals(INVALID_IN_GAME_CHANGE_MSG_ERR, err.getMessage());
    }

    @Test
    void setNonNaturalDimensions() {
        WSModel model = new WSModel();
        Exception err = assertThrows(IllegalArgumentException.class, () -> model.setDimensions(0, -1));
        assertNotNull(err);
    }

    @Test
    void setInvalidDimensions() {
        WSModel model = new WSModel();
        Exception err = assertThrows(IllegalArgumentException.class, () -> model.setDimensions(MIN_SIDE_LEN - 1, MAX_SIDE_LEN + 1));
        assertNotNull(err);
    }

    @Test
    void provideProvider() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{"test", "words"});
        provider.close();
        model.setWords(provider);
        assert model.getWords() != null;
        assertEquals(2, model.getWords().size());
    }

    @Test
    void provideProviderWithSameWords() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{"test", "words"});
        provider.close();
        model.setWords(provider);
        model.setWords(provider);
        assert model.getWords() != null;
        assertEquals(2, model.getWords().size());
    }

    @Test
    void provideProviderWithDifferentWords() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{"test", "words"});
        provider.close();
        model.setWords(provider);
        provider = new ManualWordsProvider();
        provider.provide(new String[]{"different", "ones"});
        provider.close();
        model.setWords(provider);
        assert model.getWords() != null;
        assertEquals(4, model.getWords().size());
    }

    @Test
    void provideProviderWithDifferentWordsWithoutKeepingThePrevious() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{"test", "words"});
        provider.close();
        model.setWords(provider);
        provider = new ManualWordsProvider();
        provider.provide(new String[]{"different", "ones"});
        provider.close();
        model.setWords(provider, false);
        assert model.getWords() != null;
        assertEquals(2, model.getWords().size());
    }

    @Test
    void provideProviderToTestParsing() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide("  \t\0\0\n   ]]]`````~%^       test_+___-++@#$% words!!!áçêñtòs!!!! \n\t");
        provider.close();
        model.setWords(provider);
        assert model.getWords() != null;
        assertEquals(3, model.getWords().size());
    }

    @Test
    void startGameWithNoDimensionsSpecified() {
        WSModel model = new WSModel();
        Exception err = assertThrows(NoDimensionsDefinedException.class, model::startGame);
        assertNotNull(err);
    }

    @Test
    void startGameWithNoWordsGiven() {
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN);
        Exception err = assertThrows(NoWordsException.class, model::startGame);
        assertNotNull(err);
    }

    @Test
    void startGameWithZeroWordsGiven() {
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN);
        Exception err = assertThrows(NoWordsException.class, model::startGame);
        assertNotNull(err);
    }

    @Test
    void startGameWithNoWords() {
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.close();
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, provider);
        Exception err = assertThrows(NoWordsException.class, model::startGame);
        assertNotNull(err);
    }
    @Test
    void startGameWithNoUsableWords() {
        ManualWordsProvider provider = new ManualWordsProvider();
        char[] large_word = new char[MAX_SIDE_LEN + 1];
        Arrays.fill(large_word, 'a');
        provider.provide(new String(large_word));
        provider.close();
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, provider);
        Exception err = assertThrows(CouldNotPopulateMatrixException.class, model::startGame);
        assertNotNull(err);
    }

    @Test
    void startGameWhileInGame() {
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{"test", "words"});
        provider.close();
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, provider);
        assertDoesNotThrow(model::startGame);
        Exception err = assertThrows(InvalidInGameChangeException.class, model::startGame);
        assertNotNull(err);
    }

    @Test
    void startGame() {
        ManualWordsProvider provider = new ManualWordsProvider();
        String[] words = new String[]{"TEST", "WORDS"};
        provider.provide(words);
        provider.close();
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, provider);
        assertDoesNotThrow(model::startGame);
        GameResults res = model.curGameResults();
        assertEquals(res.words().size(), words.length);
        assertTrue(res.words().containsAll(List.of(words))); // The res.words are all in uppercase
        assertTrue(res.words_found().isEmpty());
        assertTrue(model.isInGame());
    }

    @Test
    void startGameWithMaxWordsSet() {
        ManualWordsProvider provider = new ManualWordsProvider();
        String[] words = new String[]{"TEST", "WORDS"};
        provider.provide(words);
        provider.close();
        WSModel model = new WSModel(MAX_SIDE_LEN, MAX_SIDE_LEN, provider);
        int max = 1;
        model.setMaxWords(max);
        assertDoesNotThrow(model::startGame);
        GameResults res = model.curGameResults();
        assertEquals(res.words().size(), max);
        assertTrue(Arrays.stream(words).anyMatch(s -> res.words().contains(s))); // The res.words are all in uppercase
        assertTrue(res.words_found().isEmpty());
        assertTrue(model.isInGame());
    }

    @Test
    void findWordForSingleWordGame() {
        ManualWordsProvider provider = new ManualWordsProvider();
        char[] largestWordPossible = new char[MAX_SIDE_LEN];
        Arrays.fill(largestWordPossible, 'A');
        String[] words = new String[]{new String(largestWordPossible)};
        provider.provide(words);
        provider.close();
        WSModel model = new WSModel(MIN_SIDE_LEN, MAX_SIDE_LEN, provider);
        assertDoesNotThrow(model::startGame);

        // The word will be 100% horizontally

        AtomicBoolean found = new AtomicBoolean(false);
        for (int i = 0; i < MIN_SIDE_LEN; i++) {
            int line = i;
            assertDoesNotThrow(() -> {
                if (model.isInGame()) {
                    model.findWord(new Position(line, 0));
                    if (!found.get() && model.findWord(new Position(line, MAX_SIDE_LEN - 1)) != null) {
                        found.set(true);
                    }
                }
            });
        }

        assertTrue(found.get());
        assertFalse(model.isInGame());
        GameResults res = model.curGameResults();
        assertEquals(res.words().size(), words.length);
        assertEquals(res.words_found().size(), words.length);
        assertTrue(res.words().containsAll(List.of(words))); // The res.words are all in uppercase
        assertEquals(res.words_found().size(), res.words().size());
    }

    @Test
    void findWordWhileNotInGame() {
        ManualWordsProvider provider = new ManualWordsProvider();
        char[] largestWordPossible = new char[MAX_SIDE_LEN];
        Arrays.fill(largestWordPossible, 'a');
        provider.provide(new String(largestWordPossible));
        provider.close();
        WSModel model = new WSModel(MIN_SIDE_LEN, MAX_SIDE_LEN, provider);

        assertThrows(NotInGameException.class, () -> model.findWord(new Position(0, 0)));
    }

    @Test
    void findWordFirstClick() {
        ManualWordsProvider provider = new ManualWordsProvider();
        char[] largestWordPossible = new char[MAX_SIDE_LEN];
        Arrays.fill(largestWordPossible, 'a');
        provider.provide(new String(largestWordPossible));
        provider.close();
        WSModel model = new WSModel(MIN_SIDE_LEN, MAX_SIDE_LEN, provider);
        assertDoesNotThrow(model::startGame);
        assertDoesNotThrow(() -> assertTrue(Objects.requireNonNull(model.findWord(new Position(0, 0))).isEmpty()));
    }
}
