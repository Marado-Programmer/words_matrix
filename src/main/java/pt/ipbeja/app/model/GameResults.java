package pt.ipbeja.app.model;

import java.util.Set;

public record GameResults(Set<String> words, Set<String> words_found, boolean onReplay) {
}
