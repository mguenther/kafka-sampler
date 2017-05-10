package net.mguenther.kafkasampler.gtd.api;

import net.mguenther.kafkasampler.gtd.CommandHandler;
import net.mguenther.kafkasampler.gtd.ItemView;
import net.mguenther.kafkasampler.gtd.domain.commands.CreateItem;
import net.mguenther.kafkasampler.gtd.domain.commands.ItemCommand;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Path("items")
public class ItemsResource {

    private final ItemView itemView;
    private final CommandHandler commandHandler;

    @Inject
    public ItemsResource(final ItemView itemView, final CommandHandler commandHandler) {
        this.itemView = itemView;
        this.commandHandler = commandHandler;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void listOfItems(@Suspended final AsyncResponse asyncResponse) {

        itemView.getItems()
                .thenApply(result -> asyncResponse.resume(Response.ok(result).build()))
                .exceptionally(e -> asyncResponse.resume(Response.status(500).entity(e).build()));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createItem(@Suspended final AsyncResponse asyncResponse,
                           final CreateItemDto payload) {

        commandHandler
                .onCommand(commandsFor(payload))
                .thenApply(dontCare -> asyncResponse.resume(Response.ok().build()))
                .exceptionally(e -> asyncResponse.resume(Response.status(500).entity(e).build()));
    }

    private ItemCommand commandsFor(final CreateItemDto payload) {
        return new CreateItem(payload.getDescription());
    }
}
