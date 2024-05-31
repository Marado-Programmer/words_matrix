package pt.ipbeja.app.model.words_provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DBWordsProvider implements WordsProvider {
    Scanner scanner;

    public DBWordsProvider(File file) throws FileNotFoundException {
        this.scanner = new Scanner(file);
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
