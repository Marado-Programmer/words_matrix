package pt.ipbeja.app.model.words_provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AggregateWordsProvider implements WordsProvider, AutoCloseable {
    private final @NotNull List<String> words;
    private boolean closed;

    public AggregateWordsProvider() {
        this.closed = false;

        this.words = new ArrayList<>();
    }

    public void provide(WordsProvider provider) {
        if (closed) {
            throw new RuntimeException();
        }

        String line;
        while ((line = provider.getLine()) != null) {
            this.words.add(line);
        }
    }

    public void provide(WordsProvider @NotNull [] providers) {
        for (WordsProvider provider : providers) {
            this.provide(provider);
        }
    }

    @Override
    public @Nullable String getLine() {
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
