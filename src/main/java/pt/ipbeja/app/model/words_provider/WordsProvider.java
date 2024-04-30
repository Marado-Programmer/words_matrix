package pt.ipbeja.app.model.words_provider;

import org.jetbrains.annotations.Nullable;

public interface WordsProvider {
    @Nullable String getLine();
}
