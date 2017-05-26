package net.mguenther.kafkasampler.gtd;

import net.mguenther.kafkasampler.gtd.domain.commands.ItemCommand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface CommandHandler {

    CompletableFuture<Void> onCommand(ItemCommand command);

    CompletableFuture<Void> onCommand(List<ItemCommand> commands);
}
