package pt.ipbeja.app.model.words_provider;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ManualWordsProvider implements WordsProvider, AutoCloseable {
    private final List<String> words;
    private boolean closed;

    public ManualWordsProvider() {
        this.closed = false;

        this.words = new ArrayList<>();
    }

    public boolean isClosed() {
        return closed;
    }

    public void provide(String word) {
        if (closed) {
            throw new RuntimeException();
        }

        this.words.add(word);
    }

    public void provide(String [] words) {
        for (String word : words) {
            this.provide(word);
        }
    }

    @Override
    public String getLine() {
        if (closed && this.words.isEmpty()) {
            return null;
        }

        try {
            return this.words.remove(0);
        } catch (NoSuchElementException e) {
            // TODO: wait until a new word it's provided or provider it's closed
            this.closed = true;
            return this.getLine();
        }
    }

    @Override
    public void close() {
        this.closed = true;
    }
}
