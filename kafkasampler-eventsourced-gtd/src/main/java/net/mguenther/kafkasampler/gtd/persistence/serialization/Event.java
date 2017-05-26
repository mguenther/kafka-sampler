/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package net.mguenther.kafkasampler.gtd.persistence.serialization;

import org.apache.avro.specific.SpecificData;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class Event extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2341132714571160842L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Event\",\"namespace\":\"net.mguenther.kafkasampler.gtd.persistence.serialization\",\"fields\":[{\"name\":\"eventId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"data\",\"type\":[{\"type\":\"record\",\"name\":\"ItemCreated\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"description\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]},{\"type\":\"record\",\"name\":\"ItemConcluded\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]},{\"type\":\"record\",\"name\":\"RequiredTimeAssigned\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"requiredTime\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"DueDateAssigned\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"dueDate\",\"type\":\"long\"}]},{\"type\":\"record\",\"name\":\"TagAssigned\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"tag\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]},{\"type\":\"record\",\"name\":\"TagRemoved\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"tag\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]},{\"type\":\"record\",\"name\":\"ItemMovedToList\",\"fields\":[{\"name\":\"itemId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"list\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  @Deprecated public java.lang.String eventId;
  @Deprecated public long timestamp;
  @Deprecated public java.lang.Object data;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Event() {}

  /**
   * All-args constructor.
   * @param eventId The new value for eventId
   * @param timestamp The new value for timestamp
   * @param data The new value for data
   */
  public Event(java.lang.String eventId, java.lang.Long timestamp, java.lang.Object data) {
    this.eventId = eventId;
    this.timestamp = timestamp;
    this.data = data;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return eventId;
    case 1: return timestamp;
    case 2: return data;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: eventId = (java.lang.String)value$; break;
    case 1: timestamp = (java.lang.Long)value$; break;
    case 2: data = (java.lang.Object)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'eventId' field.
   * @return The value of the 'eventId' field.
   */
  public java.lang.String getEventId() {
    return eventId;
  }

  /**
   * Sets the value of the 'eventId' field.
   * @param value the value to set.
   */
  public void setEventId(java.lang.String value) {
    this.eventId = value;
  }

  /**
   * Gets the value of the 'timestamp' field.
   * @return The value of the 'timestamp' field.
   */
  public java.lang.Long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the value of the 'timestamp' field.
   * @param value the value to set.
   */
  public void setTimestamp(java.lang.Long value) {
    this.timestamp = value;
  }

  /**
   * Gets the value of the 'data' field.
   * @return The value of the 'data' field.
   */
  public java.lang.Object getData() {
    return data;
  }

  /**
   * Sets the value of the 'data' field.
   * @param value the value to set.
   */
  public void setData(java.lang.Object value) {
    this.data = value;
  }

  /**
   * Creates a new Event RecordBuilder.
   * @return A new Event RecordBuilder
   */
  public static net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder newBuilder() {
    return new net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder();
  }

  /**
   * Creates a new Event RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Event RecordBuilder
   */
  public static net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder newBuilder(net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder other) {
    return new net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder(other);
  }

  /**
   * Creates a new Event RecordBuilder by copying an existing Event instance.
   * @param other The existing instance to copy.
   * @return A new Event RecordBuilder
   */
  public static net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder newBuilder(net.mguenther.kafkasampler.gtd.persistence.serialization.Event other) {
    return new net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder(other);
  }

  /**
   * RecordBuilder for Event instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Event>
    implements org.apache.avro.data.RecordBuilder<Event> {

    private java.lang.String eventId;
    private long timestamp;
    private java.lang.Object data;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.eventId)) {
        this.eventId = data().deepCopy(fields()[0].schema(), other.eventId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.data)) {
        this.data = data().deepCopy(fields()[2].schema(), other.data);
        fieldSetFlags()[2] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing Event instance
     * @param other The existing instance to copy.
     */
    private Builder(net.mguenther.kafkasampler.gtd.persistence.serialization.Event other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.eventId)) {
        this.eventId = data().deepCopy(fields()[0].schema(), other.eventId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.data)) {
        this.data = data().deepCopy(fields()[2].schema(), other.data);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'eventId' field.
      * @return The value.
      */
    public java.lang.String getEventId() {
      return eventId;
    }

    /**
      * Sets the value of the 'eventId' field.
      * @param value The value of 'eventId'.
      * @return This builder.
      */
    public net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder setEventId(java.lang.String value) {
      validate(fields()[0], value);
      this.eventId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'eventId' field has been set.
      * @return True if the 'eventId' field has been set, false otherwise.
      */
    public boolean hasEventId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'eventId' field.
      * @return This builder.
      */
    public net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder clearEventId() {
      eventId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'timestamp' field.
      * @return The value.
      */
    public java.lang.Long getTimestamp() {
      return timestamp;
    }

    /**
      * Sets the value of the 'timestamp' field.
      * @param value The value of 'timestamp'.
      * @return This builder.
      */
    public net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder setTimestamp(long value) {
      validate(fields()[1], value);
      this.timestamp = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'timestamp' field has been set.
      * @return True if the 'timestamp' field has been set, false otherwise.
      */
    public boolean hasTimestamp() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'timestamp' field.
      * @return This builder.
      */
    public net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder clearTimestamp() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'data' field.
      * @return The value.
      */
    public java.lang.Object getData() {
      return data;
    }

    /**
      * Sets the value of the 'data' field.
      * @param value The value of 'data'.
      * @return This builder.
      */
    public net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder setData(java.lang.Object value) {
      validate(fields()[2], value);
      this.data = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'data' field has been set.
      * @return True if the 'data' field has been set, false otherwise.
      */
    public boolean hasData() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'data' field.
      * @return This builder.
      */
    public net.mguenther.kafkasampler.gtd.persistence.serialization.Event.Builder clearData() {
      data = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public Event build() {
      try {
        Event record = new Event();
        record.eventId = fieldSetFlags()[0] ? this.eventId : (java.lang.String) defaultValue(fields()[0]);
        record.timestamp = fieldSetFlags()[1] ? this.timestamp : (java.lang.Long) defaultValue(fields()[1]);
        record.data = fieldSetFlags()[2] ? this.data : (java.lang.Object) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  private static final org.apache.avro.io.DatumWriter
    WRITER$ = new org.apache.avro.specific.SpecificDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  private static final org.apache.avro.io.DatumReader
    READER$ = new org.apache.avro.specific.SpecificDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
