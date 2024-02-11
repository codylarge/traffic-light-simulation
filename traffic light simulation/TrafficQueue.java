import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

//=============================================================================================================================================================
/**
 * Defines a queue of events (cars) behind a single traffic light that can be red or green. Add cars at all once at the start of the simulation, then extract
 * them at each change of the light with {@link #service()}.
 * <p>
 * DO NOT CHANGE THIS CLASS (PROVIDED BY PROFESSOR).
 */
public class TrafficQueue
{
   /** the arbitrary identifier of this queue */
   private final String _id;

   /** the event queue */
   private final Queue<Event> _queue = new PriorityQueue<>();

   /** whether the light is currently in the red or green state */
   private boolean _isGreenElseRed;

   /** the current time in the queue based on the last event processed in service() */
   private double _currentTime = 0;

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Creates a traffic queue.
    *
    * @param id - the arbitrary identifier of this queue
    */
   public TrafficQueue(final String id)
   {
      _id = id;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Adds an event to the queue. The event time must not be before the previously added event.
    *
    * @param event - the event
    */
   public void addEvent(final Event event)
   {
      _queue.add(event);

      System.out.println("QUEUE '" + _id + "' ADDED " + event);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the current time in the queue based on the last event processed in {@link #service()} on the most recent call. This corresponds to when the light
    * changed last.
    *
    * @return the current time
    */
   public double getCurrentTime()
   {
      return _currentTime;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the arbitrary identifier of this queue.
    *
    * @return the identifier
    */
   public String getID()
   {
      return _id;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets whether there are any events in the queue.
    *
    * @return the state
    */
   public boolean hasEvents()
   {
      return !_queue.isEmpty();
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets whether the light is currently in the red or green state.
    *
    * @return the state
    */
   public boolean isGreenElseRed()
   {
      return _isGreenElseRed;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Removes and returns the events leading up to the next change in state of the traffic light. {@link TrafficQueue#isGreenElseRed()} indicates the state
    * before and after calling this method.
    *
    * @return the events
    */
   public List<Event> service()
   {
      List<Event> eventsServiced = new ArrayList<>();

      while (!_queue.isEmpty())
      {
         Event event = _queue.remove();

         System.out.println("QUEUE '" + _id + "' SERVICED " + event);

         _currentTime = event.getTime();

         eventsServiced.add(event);

         Event.E_EventType type = event.getType();

         if (type == Event.E_EventType.LIGHT_TO_GREEN)
         {
            _isGreenElseRed = true;

            break;
         }
         else if (type == Event.E_EventType.LIGHT_TO_RED)
         {
            _isGreenElseRed = false;

            break;
         }
      }

      return eventsServiced;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return ("TrafficQueue[id='" + _id + "' queue=" + _queue + "]");
   }
}
