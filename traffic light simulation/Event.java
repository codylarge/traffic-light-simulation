//=============================================================================================================================================================
/**
 * Defines an event for a car arriving or a traffic light changing state.
 * <p>
 * DO NOT CHANGE THIS CLASS (PROVIDED BY PROFESSOR).
 */
public class Event implements Comparable<Event>
{
   // =========================================================================================================================================================
   /**
    * Defines the event types.
    */
   public enum E_EventType
   {
      /** a car arrived */
      CAR_ARRIVAL,

      /** the light changed to green */
      LIGHT_TO_GREEN,

      /** the light changed to red */
      LIGHT_TO_RED
   }

   /** the time at which the event occurred */
   private final double _time;

   /** the arbitrary identifier of the event */
   private final String _id;

   /** the event type */
   private final E_EventType _type;

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Creates an event. Add it with {@link TrafficQueue#addEvent(Event)}.
    *
    * @param time - the time at which the event occurred
    * @param id - the arbitrary identifier of the event
    * @param type - the event type
    */
   public Event(final double time, final String id, final E_EventType type)
   {
      _time = time;
      _id = id;
      _type = type;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Compares the event times.
    *
    * @return the result
    */
   @Override
   public int compareTo(final Event entry)
   {
      return (int) ((_time - entry._time) * 100000);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the arbitrary identifier of the event.
    *
    * @return the identifier
    */
   public String getID()
   {
      return _id;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the time at which the event occurred.
    *
    * @return the time
    */
   public double getTime()
   {
      return _time;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the event type.
    *
    * @return the type
    */
   public E_EventType getType()
   {
      return _type;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return ("Event[time=" + String.format("%1$.1f", _time) + " id='" + _id + "' type=" + _type + "]");
   }
}
