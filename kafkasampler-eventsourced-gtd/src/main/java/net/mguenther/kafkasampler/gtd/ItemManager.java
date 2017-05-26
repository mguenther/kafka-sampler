package net.mguenther.kafkasampler.gtd;

import lombok.RequiredArgsConstructor;
import net.mguenther.kafkasampler.gtd.domain.Item;
import net.mguenther.kafkasampler.gtd.domain.commands.AssignDueDate;
import net.mguenther.kafkasampler.gtd.domain.commands.AssignRequiredTime;
import net.mguenther.kafkasampler.gtd.domain.commands.AssignTag;
import net.mguenther.kafkasampler.gtd.domain.commands.ConcludeItem;
import net.mguenther.kafkasampler.gtd.domain.commands.CreateItem;
import net.mguenther.kafkasampler.gtd.domain.commands.ItemCommand;
import net.mguenther.kafkasampler.gtd.domain.commands.MoveItemToList;
import net.mguenther.kafkasampler.gtd.domain.commands.RemoveTag;
import net.mguenther.kafkasampler.gtd.domain.events.DueDateAssigned;
import net.mguenther.kafkasampler.gtd.domain.events.ItemConcluded;
import net.mguenther.kafkasampler.gtd.domain.events.ItemCreated;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;
import net.mguenther.kafkasampler.gtd.domain.events.ItemMovedToList;
import net.mguenther.kafkasampler.gtd.domain.events.RequiredTimeAssigned;
import net.mguenther.kafkasampler.gtd.domain.events.TagAssigned;
import net.mguenther.kafkasampler.gtd.domain.events.TagRemoved;
import net.mguenther.kafkasampler.gtd.persistence.ItemEventProducer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class ItemManager implements CommandHandler, EventReceiver, ItemView {

    private final Map<String, Item> items = new ConcurrentHashMap<>();
    private final ItemEventProducer eventEmitter;

    @Override
    public CompletableFuture<Void> onCommand(final ItemCommand command) {

        return CompletableFuture.runAsync(() -> validate(command).ifPresent(eventEmitter::log));
    }

    @Override
    public CompletableFuture<Void> onCommand(final List<ItemCommand> commands) {

        return CompletableFuture.runAsync(() ->
            commands
                    .stream()
                    .map(this::validate)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(eventEmitter::log));
    }

    private Optional<ItemEvent> validate(final ItemCommand command) {

        if (command instanceof CreateItem) {
            return validate((CreateItem) command);
        }


        final Item item = items.get(command.getId());
        if (item == null) return Optional.empty();

        if (command instanceof AssignDueDate) return validate(item, (AssignDueDate) command);
        else if (command instanceof AssignRequiredTime) return validate(item, (AssignRequiredTime) command);
        else if (command instanceof AssignTag) return validate(item, (AssignTag) command);
        else if (command instanceof ConcludeItem) return validate(item, (ConcludeItem) command);
        else if (command instanceof MoveItemToList) return validate(item, (MoveItemToList) command);
        else if (command instanceof RemoveTag) return validate(item, (RemoveTag) command);
        else return Optional.empty();
    }

    private Optional<ItemEvent> validate(final CreateItem command) {
        return Optional.of(new ItemCreated(command.getId(), command.getDescription()));
    }

    private Optional<ItemEvent> validate(final Item item, final AssignDueDate command) {
        final long now = System.currentTimeMillis();
        if (item.isDone() || command.getDueDate() < now) {
            return Optional.empty();
        } else {
            return Optional.of(new DueDateAssigned(item.getId(), command.getDueDate()));
        }
    }

    private Optional<ItemEvent> validate(final Item item, final AssignRequiredTime command) {
        if (item.isDone() || command.getRequiredTime() < 0) {
            return Optional.empty();
        } else {
            return Optional.of(new RequiredTimeAssigned(item.getId(), command.getRequiredTime()));
        }
    }

    private Optional<ItemEvent> validate(final Item item, final AssignTag command) {
        if (item.isDone() || item.getTags().contains(command.getTag())) {
            return Optional.empty();
        } else {
            return Optional.of(new TagAssigned(item.getId(), command.getTag()));
        }
    }

    private Optional<ItemEvent> validate(final Item item, final ConcludeItem command) {
        if (item.isDone()) {
            return Optional.empty();
        } else {
            return Optional.of(new ItemConcluded(item.getId()));
        }
    }

    private Optional<ItemEvent> validate(final Item item, final MoveItemToList command) {
        if (item.isDone() || command.getList().equals(item.getAssociatedList())) {
            return Optional.empty();
        } else {
            return Optional.of(new ItemMovedToList(item.getId(), command.getList()));
        }
    }

    private Optional<ItemEvent> validate(final Item item, final RemoveTag command) {
        if (item.isDone() || !item.getTags().contains(command.getTag())) {
            return Optional.empty();
        } else {
            return Optional.of(new TagRemoved(item.getId(), command.getTag()));
        }
    }

    @Override
    public CompletableFuture<Void> onEvent(final ItemEvent event) {

        return CompletableFuture.runAsync(() -> {

            Item updatedItem;

            if (event instanceof ItemCreated) {
                updatedItem = project((ItemCreated) event);
            } else {
                final Item currentState = items.get(event.getItemId());
                if (currentState == null) throw new IllegalStateException("Event " + event.toString() + " cannot be applied. There is no state for item with ID " + event.getItemId() + ".");

                if (event instanceof DueDateAssigned) updatedItem = project(currentState, (DueDateAssigned) event);
                else if (event instanceof RequiredTimeAssigned) updatedItem = project(currentState, (RequiredTimeAssigned) event);
                else if (event instanceof TagAssigned) updatedItem = project(currentState, (TagAssigned) event);
                else if (event instanceof ItemConcluded) updatedItem = project(currentState, (ItemConcluded) event);
                else if (event instanceof ItemMovedToList) updatedItem = project(currentState, (ItemMovedToList) event);
                else if (event instanceof TagRemoved) updatedItem = project(currentState, (TagRemoved) event);
                else throw new IllegalStateException("Unrecognized event: " + event.toString());
            }

            items.put(updatedItem.getId(), updatedItem);
        });
    }

    private Item project(final ItemCreated event) {
        return new Item(event.getItemId(), event.getDescription());
    }

    private Item project(final Item currentState, final DueDateAssigned event) {
        return currentState.assignDueDate(event.getDueDate());
    }

    private Item project(final Item currentState, final RequiredTimeAssigned event) {
        return currentState.assignRequiredTime(event.getRequiredTime());
    }

    private Item project(final Item currentState, final TagAssigned event) {
        return currentState.addTag(event.getTag());
    }

    private Item project(final Item currentState, final ItemConcluded event) {
        return currentState.conclude();
    }

    private Item project(final Item currentState, final ItemMovedToList event) {
        return currentState.moveTo(event.getList());
    }

    private Item project(final Item currentState, final TagRemoved event) {
        return currentState.removeTag(event.getTag());
    }

    @Override
    public CompletableFuture<List<Item>> getItems() {

        return CompletableFuture.supplyAsync(() -> {
            final List<Item> itemView = new ArrayList<>(items.size());
            items.values().forEach(itemView::add);
            return Collections.unmodifiableList(itemView);
        });
    }

    @Override
    public CompletableFuture<Optional<Item>> getItem(final String id) {

        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(items.get(id)));
    }
}
