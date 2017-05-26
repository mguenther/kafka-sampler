package net.mguenther.kafkasampler.gtd.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
@Getter
@ToString
@RequiredArgsConstructor
public class UpdateItemDto {

    private final long dueDate;
    private final int requiredTime;
    private final List<String> tags;
    private final String associatedList;
}
