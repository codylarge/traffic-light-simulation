import java.util.List;
import java.util.Random;
import java.util.Scanner;

//=============================================================================================================================================================
/**
 * Defines the simulation
 * <p>
 * Made by Cody Large (ALL OTHER CLASSES PROVIDED BY PROFESSOR)
 */
public class Simulation
{
   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   public static void main(final String[] arguments)
   {
      Simulation simulation = new Simulation();
      Scanner sc = new Scanner(System.in);
      System.out.print("Select test case to run (1-8): ");
      int test = sc.nextInt();
      test = (test >= 1 && test <= 8) ? test : 0;

      simulation.run(test);
   }


   private void run(int test)
   {
      /**
       * @param randomSeed - the random seed
       * @param lambda - the lambda term of the exponential random distribution
       * @param lengthGreen - the length of time the light is green
       * @param lengthRed - the length of time the light is red
       * @param bucketSize - the bucket size in seconds
       */
      switch(test)
      {
         case 1:
            doTest(2, 0.75, 30, 30, 10);
            break;
         case 2:
            doTest(2, 0.5, 30, 30, 10);
            break;
         case 3:
            doTest(2, 5, 30, 30, 10);
            break;
         case 4:
            doTest(2, 0.75, 45, 30, 10);
            break;
         case 5:
            doTest(2, 0.75, 30, 45, 10);
            break;
         case 6:
            doTest(2, 0.75, 10, 10, 10);
            break;
         case 7:
            doTest(2, 5, 30, 30, 15);
            break;
         case 8:
            doTest(2, 5, 30, 30, 5);
            break;
      }

   }
   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Executes the initial test
    */
   private void doTest(int randomSeed, double lambda, double lengthGreen, double lengthRed, double bucketSize)
   {
      double simulationRunTime = 300; // the total number of seconds to simulate
      double spread = (lengthGreen + lengthRed);

      // populate the traffic-light transitions
      TrafficQueue queue = new TrafficQueue("queue");

      populateLights(queue, simulationRunTime, lengthGreen, spread, spread, spread); 

      // populate the cars randomly
      populateCars(queue, simulationRunTime, lambda, randomSeed);

      // execute the simulation and generate a histogram 
      executeSimulation(queue, bucketSize);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Executes the simulation after setting it up with events.
    *
    * @param queue - the event queue
    * @param bucketSize - the bucket size in seconds
    */
   private void executeSimulation(final TrafficQueue queue, double bucketSize)
   {
      // Create the five analysis groups
      AnalysisGroup all = new AnalysisGroup("all", bucketSize);
      AnalysisGroup on_green_all = new AnalysisGroup("on_green:all", bucketSize);
      AnalysisGroup on_red_all = new AnalysisGroup("on_red:all", bucketSize);
      AnalysisGroup on_green_n = new AnalysisGroup("on_green:n", bucketSize);
      AnalysisGroup on_red_n = new AnalysisGroup("on_red:n", bucketSize);
      boolean greenLight = true;
      int nInterval = 1;
      double finalTime = 0;

      // Keep looping while the queue has events
      while(queue.hasEvents())
      {
         // Pop off all events queued up for this turn of the light
         List<Event> events = queue.service();

         // Loop through the serviced events and handle them accordingly
         for (Event event : events) {

            switch (event.getType()) {
               case CAR_ARRIVAL:
                  // For a CAR_ARRIVAL event, add it to the all analysis group and the on_green:n/green:all or on_red:n/red:all groups, depending on the light state
                  if(greenLight)
                  {
                     on_green_all.addEventTime(event.getTime());
                     on_green_n.addEventTime(event.getTime());
                  }
                  else
                  {
                     on_red_all.addEventTime(event.getTime());
                     on_red_n.addEventTime(event.getTime());
                  }

                  all.addEventTime(event.getTime());
                  break;

               case LIGHT_TO_GREEN:
                  greenLight = true;

                  // For a LIGHT_TO_GREEN event, add an event separator with the current time to all and red:all
                  all.addEventSeparator(event.getTime());
                  on_red_all.addEventSeparator(event.getTime());

                  on_red_n.finalizeInterval(event.getTime());
                  on_green_n = new AnalysisGroup("on_green:"+nInterval++, bucketSize);
                  //  finalize the on_red:n group with this time, and create a new on_green:n group for the next turn. Finalizing prints its statistics
                  break;

               case LIGHT_TO_RED:
                  greenLight = false;
                  // For a LIGHT_TO_RED event, add an event separator with the current time to all and green:all
                  all.addEventSeparator(event.getTime());
                  on_green_all.addEventSeparator(event.getTime());

                  on_green_n.finalizeInterval(event.getTime());
                  on_red_n = new AnalysisGroup("on_red:"+nInterval++, bucketSize);
                  //  finalize the on_green:n group with this time, and create a new on_red:n group for the next turn. Finalizing prints its statistics
                  break;

               default:
            }
            finalTime = event.getTime();
         }
      }

      on_red_all.finalizeInterval(finalTime);
      on_green_all.finalizeInterval(finalTime);
      all.finalizeInterval(finalTime);

      on_red_all.generateHistogram(bucketSize);
      on_green_all.generateHistogram(bucketSize);
      all.generateHistogram(bucketSize);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Populates a queue with car arrivals.
    *
    * @param queue - the event queue
    * @param simulationRunTime - the end time of the simulation
    * @param lambda - the lambda term of the exponential random distribution
    * @param randomSeed - the random seed
    */

   // Starting at current time 0, loop until it reaches or exceeds simulationRunTime
   private void populateCars(final TrafficQueue queue, final double simulationRunTime, final double lambda, final int randomSeed)
   {
      // Create a java.util.Random object random with randomSeed
      Random seed = new Random(randomSeed);
      double currentTime = 0.0;

      for(int i = 0; i < simulationRunTime; i++)
      {
        // Generate a random time with lambda from the exponential-distribution formula in the specs
        double time = Math.log(1 - seed.nextDouble()) / -lambda;
        currentTime += time;
        // Create a CAR_ARRIVAL event at this time, add it to the queue, and advance the current time by this amount
        Event carEvent;
        if(currentTime <= simulationRunTime + time)
        {
           carEvent = new Event(currentTime, "car_" + i, Event.E_EventType.CAR_ARRIVAL);
           queue.addEvent(carEvent);
        }
      }
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Populates a queue with traffic-light transitions of both types.
    *
    * @param queue - the event queue
    * @param simulationRunTime - the end time of the simulation
    * @param startTimeRed - the start time of the red lights
    * @param intervalRed - the time interval between red lights 
    * @param startTimeGreen - the start time of the green lights
    * @param intervalGreen - the time interval between green lights
    */
   private void populateLights(final TrafficQueue queue,
                               final double simulationRunTime,
                               final double startTimeRed,
                               final double intervalRed,
                               final double startTimeGreen,
                               final double intervalGreen)
   {
      // Call populateTransitions for the LIGHT_TO_RED transitions
      populateTransitions(queue, Event.E_EventType.LIGHT_TO_RED, startTimeRed, simulationRunTime, intervalRed);

      // Call populateTransitions for the LIGHT_TO_GREEN transitions
      populateTransitions(queue, Event.E_EventType.LIGHT_TO_GREEN, startTimeGreen, simulationRunTime, intervalGreen);
   }

   // ---------------------------------------------------------------------------------------------------------------------------------------------------------
   /**
    * Populates a queue with traffic-light transitions of one type.
    *
    * @param queue - the queue
    * @param type - the type of event, either LIGHT_TO_GREEN or LIGHT_TO_RED
    * @param timeStart - the time of the first event
    * @param timeEnd - the time of the last event
    * @param timeStep - the interval between each event; the last interval may not divide evenly
    */
   private void populateTransitions(final TrafficQueue queue, 
                                    final Event.E_EventType type, 
                                    final double timeStart, 
                                    final double timeEnd, 
                                    final double timeStep)
   {
      int transitionCount = 1;

      // Loop from timeStart up to and including timeEnd by timeStep, create an event for each, and add it to the queue
      for(double i = timeStart; i <= timeEnd; i += timeStep)
      {
        Event lightEvent = new Event(i, "transition" + transitionCount++, type);
        queue.addEvent(lightEvent);
      }
   }
}
