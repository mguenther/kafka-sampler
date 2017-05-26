package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class AssignTag extends ItemCommand {

    private final String tag;

    public AssignTag(final String id, final String tag) {
        super(id);
        this.tag = tag;
    }
}
