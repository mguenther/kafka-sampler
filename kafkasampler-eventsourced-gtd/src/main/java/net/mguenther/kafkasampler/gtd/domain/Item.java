package net.mguenther.kafkasampler.gtd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Item {

    private final String id;
    private final String description;
    private int requiredTime;
    private long dueDate;
    private Set<String> tags = new HashSet<>();
    private String associatedList;
    private boolean done;

    public Item addTag(final String tag) {
        final Set<String> updatedTags = new HashSet<>(tags);
        updatedTags.add(tag);
        return new Item(id, description, requiredTime, dueDate, updatedTags, associatedList, done);
    }

    public Item removeTag(final String tag) {
        final Set<String> updatedTags = new HashSet<>(tags);
        updatedTags.remove(tag);
        return new Item(id, description, requiredTime, dueDate, updatedTags, associatedList, done);
    }

    public Item assignDueDate(final long dueDate) {
        return new Item(id, description, requiredTime, dueDate, tags, associatedList, done);
    }

    public Item assignRequiredTime(final int requiredTime) {
        return new Item(id, description, requiredTime, dueDate, tags, associatedList, done);
    }

    public Item conclude() {
        return new Item(id, description, requiredTime, dueDate, tags, associatedList, true);
    }

    public Item moveTo(final String targetList) {
        return new Item(id, description, requiredTime, dueDate, tags, targetList, done);
    }
}
