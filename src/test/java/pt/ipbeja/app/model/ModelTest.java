package pt.ipbeja.app.model;

import org.junit.jupiter.api.Test;
import pt.ipbeja.app.model.throwables.InvalidInGameChangeException;
import pt.ipbeja.app.model.words_provider.ManualWordsProvider;

import java.util.Arrays;

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
        model.startGame();

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
    void provideProvider() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide(new String[]{"test", "words"});
        provider.close();
        model.setWords(provider);
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
        assertEquals(2, model.getWords().size());
    }

    @Test
    void provideProviderToTestParsing() {
        WSModel model = new WSModel();
        ManualWordsProvider provider = new ManualWordsProvider();
        provider.provide("  \t\0\0\n   ]]]`````~%^       test_+___-++@#$% words!!!áçêñtòs!!!! \n\t");
        provider.close();
        model.setWords(provider);
        model.getWords().forEach(System.out::println);
        assertEquals(3, model.getWords().size());
    }
}
