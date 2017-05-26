package net.mguenther.kafkasampler.gtd.domain.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@RequiredArgsConstructor
@Getter
abstract public class ItemCommand {

    private final String id;
}
