package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
public class AssignRequiredTime extends ItemCommand {

    private final int requiredTime;

    public AssignRequiredTime(final String id, final int requiredTime) {
        super(id);
        this.requiredTime = requiredTime;
    }
}
