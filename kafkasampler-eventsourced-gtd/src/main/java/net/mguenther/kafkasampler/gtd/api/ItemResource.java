package net.mguenther.kafkasampler.gtd.api;

import lombok.RequiredArgsConstructor;
import net.mguenther.kafkasampler.gtd.CommandHandler;
import net.mguenther.kafkasampler.gtd.ItemView;
import net.mguenther.kafkasampler.gtd.domain.Item;
import net.mguenther.kafkasampler.gtd.domain.commands.AssignDueDate;
import net.mguenther.kafkasampler.gtd.domain.commands.AssignRequiredTime;
import net.mguenther.kafkasampler.gtd.domain.commands.AssignTag;
import net.mguenther.kafkasampler.gtd.domain.commands.ConcludeItem;
import net.mguenther.kafkasampler.gtd.domain.commands.ItemCommand;
import net.mguenther.kafkasampler.gtd.domain.commands.MoveItemToList;
import net.mguenther.kafkasampler.gtd.domain.commands.RemoveTag;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Path("items/{itemId}")
public class ItemResource {

    private final ItemView itemView;
    private final CommandHandler commandHandler;

    @Inject
    public ItemResource(final ItemView itemView, final CommandHandler commandHandler) {
        this.itemView = itemView;
        this.commandHandler = commandHandler;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void showItem(@PathParam("itemId") final String itemId,
                         @Suspended final AsyncResponse asyncResponse) {

        itemView.getItem(itemId)
                .thenApply(result -> asyncResponse.resume(Response.ok(result.orElse(null)).build()))
                .exceptionally(e -> asyncResponse.resume(Response.status(500).entity(e).build()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateItem(@PathParam("itemId") final String itemId,
                           @Suspended final AsyncResponse asyncResponse,
                           UpdateItemDto updateItem) {

        itemView.getItem(itemId)
                .thenApply(optionalItem -> optionalItem.flatMap(item -> commandsFor(item, updateItem)))
                .thenApply(optionalCommands -> optionalCommands.orElse(Collections.emptyList()))
                .thenCompose(commandHandler::onCommand)
                .thenApply(dontcare -> asyncResponse.resume(Response.accepted().build()))
                .exceptionally(e -> asyncResponse.resume(Response.status(500).entity(e).build()));
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void concludeItem(@PathParam("itemId") final String itemId,
                             @Suspended final AsyncResponse asyncResponse) {

        commandHandler
                .onCommand(new ConcludeItem(itemId))
                .thenApply(dontCare -> asyncResponse.resume(Response.accepted().build()))
                .exceptionally(e -> asyncResponse.resume(Response.status(500).entity(e).build()));
    }

    private Optional<List<ItemCommand>> commandsFor(final Item item, final UpdateItemDto updateItem) {
        final List<ItemCommand> commands = new ArrayList<>();
        if (!updateItem.getAssociatedList().equals(item.getAssociatedList())) {
            commands.add(new MoveItemToList(item.getId(), updateItem.getAssociatedList()));
        }
        if (updateItem.getDueDate() != item.getDueDate()) {
            commands.add(new AssignDueDate(item.getId(), updateItem.getDueDate()));
        }
        if (updateItem.getRequiredTime() != item.getRequiredTime()) {
            commands.add(new AssignRequiredTime(item.getId(), updateItem.getRequiredTime()));
        }
        commands.addAll(
                updateItem
                        .getTags()
                        .stream()
                        .filter(tag -> !item.getTags().contains(tag))
                        .map(tag -> new AssignTag(item.getId(), tag))
                        .collect(Collectors.toList()));
        commands.addAll(
                item
                        .getTags()
                        .stream()
                        .filter(tag -> !item.getTags().contains(tag))
                        .map(tag -> new RemoveTag(item.getId(), tag))
                        .collect(Collectors.toList()));
        return Optional.of(Collections.unmodifiableList(commands));
    }
}
