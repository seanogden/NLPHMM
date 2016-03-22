package edu.cornell.cs.nlp.assignments;
import edu.cornell.cs.nlp.assignments.POSTaggerTester.*;
import edu.cornell.cs.nlp.assignments.counting.Counter;
import edu.cornell.cs.nlp.util.log.*;

import java.util.*;

/**
 * Created by sean on 3/21/16.
 */
public class ViterbiTester {

    public static final ILogger LOG	= LoggerFactory
            .create(POSTaggerTester.class);

    public static void main(String[] args) {
        LogLevel.setLogLevel(LogLevel.DEBUG);
        Logger.DEFAULT_LOG = new Log(System.out);

        final Trellis<State> t = buildTrellis();
        final ViterbiDecoder<State> v = new ViterbiDecoder<>();
        final List<State> states = v.getBestPath(t);
        System.out.println(states);
    }

    MostFrequentTagScorer localTrigramScorer;

    public static Trellis<State> buildTrellis() {
        final Trellis<State> t = new Trellis<State>();

        t.setStartState(State.getStartState());
        final State stopState = State.getStopState(3 + 2);
        t.setStopState(stopState);

        State start = State.getStartState();
        State start_a = start.getNextState("a"); t.setTransitionCount(start, start_a, Math.log(0.51));
        State start_b = start.getNextState("b"); t.setTransitionCount(start, start_b, Math.log(0.49));

        State a_a2 = start_a.getNextState("a"); t.setTransitionCount(start_a, a_a2, Math.log(0.51));
        State a_b2 = start_a.getNextState("b"); t.setTransitionCount(start_a, a_b2, Math.log(0.49));
        State b_a2 = start_b.getNextState("a"); t.setTransitionCount(start_b, b_a2, Math.log(0.01));
        State b_b2= start_b.getNextState("b");  t.setTransitionCount(start_b, b_b2, Math.log(0.99));

        State a_a3 = a_a2.getNextState("a"); t.setTransitionCount(a_a2, a_a3, Math.log(0.49));
                                             t.setTransitionCount(b_a2, a_a3, Math.log(0.51));
        State a_b3 = a_a2.getNextState("b"); t.setTransitionCount(a_a2, a_b3, Math.log(0.51));
                                             t.setTransitionCount(b_a2, a_b3, Math.log(0.49));
        State b_a3 = a_b2.getNextState("a"); t.setTransitionCount(a_b2, b_a3, Math.log(0.51));
                                             t.setTransitionCount(b_b2, b_a3, Math.log(0.01));
        State b_b3 = a_b2.getNextState("b"); //t.setTransitionCount(a_b2, b_b3, Math.log(0.49));
//                                             t.setTransitionCount(b_b2, b_b3, Math.log(0.99));

        State a_end4 = b_a3.getNextState("</S>"); t.setTransitionCount(a_a3, a_end4, Math.log(1.0));
                                                  t.setTransitionCount(b_a3, a_end4, Math.log(1.0));
        State b_end4 = a_b3.getNextState("</S>"); t.setTransitionCount(a_b3, b_end4, Math.log(1.0));
                                                  t.setTransitionCount(b_b3, b_end4, Math.log(1.0));

        t.setTransitionCount(a_end4, stopState, Math.log(1.0));
        t.setTransitionCount(b_end4, stopState, Math.log(1.0));

        return t;
    }
}
