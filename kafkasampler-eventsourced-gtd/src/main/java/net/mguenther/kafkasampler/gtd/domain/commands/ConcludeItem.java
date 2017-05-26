package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@ToString
public class ConcludeItem extends ItemCommand {

    public ConcludeItem(final String id) {
        super(id);
    }
}
