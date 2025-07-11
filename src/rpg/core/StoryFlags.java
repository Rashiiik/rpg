package rpg.core;

import java.util.HashSet;
import java.util.Set;

public class StoryFlags {
    private Set<String> flags;

    public StoryFlags() {
        this.flags = new HashSet<>();
    }

    public void addFlag(String flag) {
        flags.add(flag.toLowerCase());
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag.toLowerCase());
    }

    public void removeFlag(String flag) {
        flags.remove(flag.toLowerCase());
    }

    public Set<String> getAllFlags() {
        return new HashSet<>(flags);
    }

    public void clearFlags() {
        flags.clear();
    }
}