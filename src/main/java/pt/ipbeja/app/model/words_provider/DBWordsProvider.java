package pt.ipbeja.app.model.words_provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DBWordsProvider implements WordsProvider {
    Scanner scanner;

    public DBWordsProvider(File file) {
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLine() {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine();
            }
            scanner.close();
        } catch (IllegalStateException e) {
            return null;
        }
        return null;
    }
}
