import java.util.ArrayList;
import java.util.List;

//=============================================================================================================================================================
/**
 * Defines an analysis group as an ordered collection of events from which final statistics will be generated.
 * <p>
 * The wait time for an event is defined as the difference between the final interval time and the event time.
 * <p>
 * Be careful with your statistics: NaN and Infinity are not valid results.
 * <p>
 * You do not need to enforce any error checking or rules; "valid" means simply do not do something.
 * <p>
 * DO NOT CHANGE THIS CLASS (PROVIDED BY PROFESSOR).
 */
public class AnalysisGroup
{
   /** the arbitrary identifier of this group */
   private final String _id;

   /** the start time at which this group starts recording */
   private final double _timeIntervalInitial;

   /** the end time of this group */
   private double _timeIntervalFinal = -Double.MAX_VALUE;

   /** the event times from the start of the simulation */
   private final List<Double> _timesRaw = new ArrayList<>();

   /** the wait times for each event. This is valid only after calling finalizeInterval() */
   private final List<Double> _timesWait = new ArrayList<>();

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Creates an analysis group.
    *
    * @param id - the arbitrary identifier of this group
    * @param timeInitial - the start time at which this group starts recording (regardless of when the first event actually arrives)
    */
   public AnalysisGroup(final String id, final double timeInitial)
   {
      _id = id;

      _timeIntervalInitial = timeInitial;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Adds an event-block separator to indicate when the block ended. Subsequent event times belong to the next block. This separator allows the raw times to be
    * converted to wait times in {@link #finalizeInterval(double)} interval. This solution is kind of stupid, but at this point, it suffices.
    *
    * @param time - the end time of the block, which is when the current light changed to the next light
    */
   public void addEventSeparator(final double time)
   {
      _timesRaw.add(-time);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Adds an event time to this group. Add only car arrivals, not light changes.
    * <p>
    * This is not valid after calling {@link #finalizeInterval(double)}.
    *
    * @param time - the event time
    */
   public void addEventTime(final double time)
   {
      _timesRaw.add(time);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Calculates the number of cars processed per second over the entire time interval span; i.e., {@link #getEventCount()} / {@link #getTimeIntervalSpan()}.
    *
    * @return the rate
    */
   public double calculateCarsPerSecond()
   {
      int numCars = getEventCount();

      double intervalSpan = getTimeIntervalSpan();

      double rate = ((intervalSpan > 0) ? (numCars / intervalSpan) : intervalSpan);

      return rate;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Calculates the average wait time for all events.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the average
    */
   public double calculateWaitTimeAverage()
   {
      double timeTotal = 0;

      for (double time : _timesWait)
      {
         timeTotal += time;
      }

      int numCars = getEventCount();

      double timeAverage = ((numCars > 0) ? (timeTotal / numCars) : 0);

      return timeAverage;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Calculates the <u>sample</u> standard deviation on wait time for all events. See
    * <a href="http://www.miniwebtool.com/sample-standard-deviation-calculator/">here</a>.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the standard deviation
    */
   public double calculateWaitTimeDeviation()
   {
      double timeAverage = calculateWaitTimeAverage();

      double timeSubtotal = 0;

      for (double time : _timesWait)
      {
         timeSubtotal += Math.pow((time - timeAverage), 2);
      }

      int numCars = getEventCount();

      double timeDeviation = ((numCars > 1) ? (Math.sqrt(timeSubtotal) / (numCars - 1)) : 0);

      return timeDeviation;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Calculates the maximum wait time for all events.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the time
    */
   public double calculateWaitTimeMax()
   {
      double timeWaitMax = 0;

      for (double time : _timesWait)
      {
         if (time > timeWaitMax)
         {
            timeWaitMax = time;
         }
      }

      return timeWaitMax;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Calculates the minimum wait time for all events.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the time
    */
   public double calculateWaitTimeMin()
   {
      double timeWaitMin = Double.MAX_VALUE;

      boolean isSet = false;

      for (double time : _timesWait)
      {
         if (time < timeWaitMin)
         {
            timeWaitMin = time;

            isSet = true;
         }
      }

      if (!isSet)
      {
         timeWaitMin = 0;
      }

      return timeWaitMin;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Calculates the total wait time for all events.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the time
    */
   public double calculateWaitTimeTotal()
   {
      double timeTotal = 0;

      for (double time : _timesWait)
      {
         timeTotal += time;
      }

      return timeTotal;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Sets the end time of this group and prints out the statistics as defined in the task document.
    *
    * @param timeFinal - the end time
    */
   public void finalizeInterval(final double timeFinal)
   {
      _timeIntervalFinal = timeFinal;

      addEventSeparator(timeFinal);

      double referenceTime = timeFinal;

      System.out.println("\nFINALIZE ANALYSIS GROUP '"+ _id +"'");
      
      for (int iTime = (_timesRaw.size() - 1); iTime >= 0; iTime--)
      {
         double time = _timesRaw.get(iTime);

         if (time < 0)
         {
            referenceTime = Math.abs(time);

            System.out.println("NEW BLOCK AT "+ format(referenceTime));
         }
         else
         {
            double waitTime = (referenceTime - time);

            _timesWait.add(waitTime);

            System.out.println("CAR AT "+ format(time) +" WAIT TIME "+ format(waitTime));
         }
      }

      System.out.println();
      System.out.println("id                  = " + _id);

      System.out.println("timeIntervalInitial = " + format(_timeIntervalInitial));
      System.out.println("timeIntervalFinal   = " + format(_timeIntervalFinal));
      System.out.println("timeIntervalSpan    = " + format(getTimeIntervalSpan()));

      System.out.println("numCars             = " + getEventCount());
      System.out.println("carsPerSecond       = " + format(calculateCarsPerSecond()));

      System.out.println("waitTimeTotal       = " + format(calculateWaitTimeTotal()));
      System.out.println("waitTimeMax         = " + format(calculateWaitTimeMax()));
      System.out.println("waitTimeMin         = " + format(calculateWaitTimeMin()));
      System.out.println("waitTimeAverage     = " + format(calculateWaitTimeAverage()));
      System.out.println("waitTimeDeviation   = " + format(calculateWaitTimeDeviation()));
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Returns a value formatted with one decimal place.
    *
    * @param value - the value
    *
    * @return the formatted value
    */
   private String format(final double value)
   {
      return String.format("%1$.1f", value);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Writes to standard output the results of a histogram on the events in this group.
    *
    * @param bucketSizeSeconds - the bucket size in seconds
    */
   public void generateHistogram(final double bucketSizeSeconds)
   {
      double timeIntervalSpan = getTimeIntervalSpan();
       int numBuckets = (int) Math.ceil(timeIntervalSpan/ bucketSizeSeconds);
      if(numBuckets < 0) return;
      int[] bucketCounts = new int[numBuckets];

      double bucketEndTime = bucketSizeSeconds;

      int iEvent = 0;

      for (int iBucket = 0; iBucket < numBuckets; iBucket++)
      {
         while (iEvent < _timesRaw.size())
         {
            double time = _timesRaw.get(iEvent);

            if (time < bucketEndTime)
            {
               bucketCounts[iBucket]++;

               iEvent++;
            }
            else
            {
               break;
            }
         }

         bucketEndTime += bucketSizeSeconds;
      }

      System.out.println();
      System.out.println("HISTOGRAM OF ANALYSIS GROUP '" + _id +"'");
      System.out.println("Time,Count");

      double bucketTime = 0;

      for (int iBucket = 0; iBucket < numBuckets; iBucket++)
      {
         System.out.println(bucketTime + "," + bucketCounts[iBucket]);

         bucketTime += bucketSizeSeconds;
      }
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the number of events.
    *
    * @return the count
    */
   public int getEventCount()
   {
      return _timesWait.size();
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the identifier.
    *
    * @return the identifier
    */
   public String getID()
   {
      return _id;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the end time of this group as set in {@link #finalizeInterval(double)}.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the end time
    */
   public double getTimeIntervalFinal()
   {
      return _timeIntervalFinal;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the start time of this group as set in the constructor.
    *
    * @return the start time
    */
   public double getTimeIntervalInitial()
   {
      return _timeIntervalInitial;
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Gets the time span of this group as the difference between the initial and final interval time. See {@link #getTimeIntervalInitial()} and
    * {@link #getTimeIntervalFinal()}.
    * <p>
    * This is valid only after calling {@link #finalizeInterval(double)}.
    *
    * @return the time span
    */
   public double getTimeIntervalSpan()
   {
      return (_timeIntervalFinal - _timeIntervalInitial);
   }
}
