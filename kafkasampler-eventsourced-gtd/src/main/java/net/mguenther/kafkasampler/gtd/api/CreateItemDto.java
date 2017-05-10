package net.mguenther.kafkasampler.gtd.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
@RequiredArgsConstructor
public class CreateItemDto {

    private final String description;
}
