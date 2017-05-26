package net.mguenther.kafkasampler.gtd.persistence;

import net.mguenther.kafkasampler.gtd.domain.events.DueDateAssigned;
import net.mguenther.kafkasampler.gtd.domain.events.ItemConcluded;
import net.mguenther.kafkasampler.gtd.domain.events.ItemCreated;
import net.mguenther.kafkasampler.gtd.domain.events.ItemEvent;
import net.mguenther.kafkasampler.gtd.domain.events.ItemMovedToList;
import net.mguenther.kafkasampler.gtd.domain.events.RequiredTimeAssigned;
import net.mguenther.kafkasampler.gtd.domain.events.TagAssigned;
import net.mguenther.kafkasampler.gtd.domain.events.TagRemoved;
import net.mguenther.kafkasampler.gtd.persistence.serialization.Event;

/**
 * @author Markus Günther (markus.guenther@gmail.com)
 */
public class AvroDomainConverter {

    private Event wrap(final ItemEvent event, final Object eventPayload) {

        return Event
                .newBuilder()
                .setEventId(event.getEventId())
                .setTimestamp(event.getTimestamp())
                .setData(eventPayload)
                .build();
    }

    public Event from(final ItemEvent event) {

        if (event instanceof ItemCreated) return from((ItemCreated) event);
        else if (event instanceof RequiredTimeAssigned) return from((RequiredTimeAssigned) event);
        else if (event instanceof DueDateAssigned) return from((DueDateAssigned) event);
        else if (event instanceof TagAssigned) return from((TagAssigned) event);
        else if (event instanceof TagRemoved) return from((TagRemoved) event);
        else if (event instanceof ItemMovedToList) return from((ItemMovedToList) event);
        else if (event instanceof ItemConcluded) return from((ItemConcluded) event);
        else throw new IllegalArgumentException("Unsupported event type " + event.getClass());
    }

    private Event from(final ItemCreated event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.ItemCreated avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.ItemCreated.newBuilder()
                .setItemId(event.getItemId())
                .setDescription(event.getDescription())
                .build();

        return wrap(event, avroEvent);
    }

    private Event from(final RequiredTimeAssigned event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.RequiredTimeAssigned avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.RequiredTimeAssigned.newBuilder()
                .setItemId(event.getItemId())
                .setRequiredTime(event.getRequiredTime())
                .build();

        return wrap(event, avroEvent);
    }

    private Event from(final DueDateAssigned event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.DueDateAssigned avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.DueDateAssigned.newBuilder()
                .setItemId(event.getItemId())
                .setDueDate(event.getDueDate())
                .build();

        return wrap(event, avroEvent);
    }

    private Event from(final TagAssigned event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.TagAssigned avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.TagAssigned.newBuilder()
                .setItemId(event.getItemId())
                .setTag(event.getTag())
                .build();

        return wrap(event, avroEvent);
    }

    private Event from(final TagRemoved event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.TagRemoved avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.TagRemoved.newBuilder()
                .setItemId(event.getItemId())
                .setTag(event.getTag())
                .build();

        return wrap(event, avroEvent);
    }

    private Event from(final ItemMovedToList event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.ItemMovedToList avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.ItemMovedToList.newBuilder()
                .setItemId(event.getItemId())
                .setList(event.getList())
                .build();

        return wrap(event, avroEvent);
    }

    private Event from(final ItemConcluded event) {

        final net.mguenther.kafkasampler.gtd.persistence.serialization.ItemConcluded avroEvent = net.mguenther.kafkasampler.gtd.persistence.serialization.ItemConcluded.newBuilder()
                .setItemId(event.getItemId())
                .build();

        return wrap(event, avroEvent);
    }

    public ItemEvent to(final Event event) {

        final String eventId = String.valueOf(event.getEventId());
        final long timestamp = event.getTimestamp();

        ItemEvent domainEvent;

        if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.ItemCreated) {

            final net.mguenther.kafkasampler.gtd.persistence.serialization.ItemCreated payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.ItemCreated) event.getData();
            domainEvent = new ItemCreated(eventId, timestamp, payload.getItemId(), payload.getDescription());

        } else if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.ItemConcluded) {

            final net.mguenther.kafkasampler.gtd.persistence.serialization.ItemConcluded payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.ItemConcluded) event.getData();
            domainEvent = new ItemConcluded(eventId, timestamp, payload.getItemId());

        } else if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.RequiredTimeAssigned) {

            final net.mguenther.kafkasampler.gtd.persistence.serialization.RequiredTimeAssigned payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.RequiredTimeAssigned) event.getData();
            domainEvent = new RequiredTimeAssigned(eventId, timestamp, payload.getItemId(), payload.getRequiredTime());

        } else if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.DueDateAssigned) {


            final net.mguenther.kafkasampler.gtd.persistence.serialization.DueDateAssigned payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.DueDateAssigned) event.getData();
            domainEvent = new DueDateAssigned(eventId, timestamp, payload.getItemId(), payload.getDueDate());

        } else if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.TagAssigned) {

            final net.mguenther.kafkasampler.gtd.persistence.serialization.TagAssigned payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.TagAssigned) event.getData();
            domainEvent = new TagAssigned(eventId, timestamp, payload.getItemId(), payload.getTag());

        } else if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.TagRemoved) {

            final net.mguenther.kafkasampler.gtd.persistence.serialization.TagRemoved payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.TagRemoved) event.getData();
            domainEvent = new TagRemoved(eventId, timestamp, payload.getItemId(), payload.getTag());

        } else if (event.getData() instanceof net.mguenther.kafkasampler.gtd.persistence.serialization.ItemMovedToList) {

            final net.mguenther.kafkasampler.gtd.persistence.serialization.ItemMovedToList payload = (net.mguenther.kafkasampler.gtd.persistence.serialization.ItemMovedToList) event.getData();
            domainEvent = new ItemMovedToList(eventId, timestamp, payload.getItemId(), payload.getList());

        } else {
            throw new IllegalArgumentException("Unsupported event payload for event with ID " + eventId);
        }

        return domainEvent;
    }
}
