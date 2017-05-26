package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class MoveItemToList extends ItemCommand {

    private final String list;

    public MoveItemToList(final String id, final String list) {
        super(id);
        this.list = list;
    }
}
