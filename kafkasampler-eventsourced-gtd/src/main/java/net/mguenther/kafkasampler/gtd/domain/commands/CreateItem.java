package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class CreateItem extends ItemCommand {

    private final String description;

    public CreateItem(final String description) {
        super(UUID.randomUUID().toString().substring(0, 7));
        this.description = description;
    }
}
