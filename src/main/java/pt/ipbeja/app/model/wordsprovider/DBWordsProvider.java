package pt.ipbeja.app.model.wordsprovider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DBWordsProvider implements WordsProvider {
    private final Scanner scanner;

    public DBWordsProvider(File file) throws IOException {
        super();
        this.scanner = new Scanner(file, StandardCharsets.UTF_8);
    }

    @Override
    public String getLine() {
        try {
            if (this.scanner.hasNextLine()) {
                return this.scanner.nextLine();
            }
            this.scanner.close();
        } catch (IllegalStateException e) {
            return null;
        }
        return null;
    }
}
