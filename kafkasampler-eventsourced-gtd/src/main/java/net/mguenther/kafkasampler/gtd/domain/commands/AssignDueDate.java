package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class AssignDueDate extends ItemCommand {

    private final long dueDate;

    public AssignDueDate(final String id, final long dueDate) {
        super(id);
        this.dueDate = dueDate;
    }
}
