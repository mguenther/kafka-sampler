package net.mguenther.kafkasampler.gtd;

import net.mguenther.kafkasampler.gtd.domain.Item;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public interface ItemView {

    CompletableFuture<List<Item>> getItems();

    CompletableFuture<Optional<Item>> getItem(String id);
}
