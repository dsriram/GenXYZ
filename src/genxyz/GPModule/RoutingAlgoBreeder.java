/**
 * Created by Poojaa on 14-03-2015.
 */

package genxyz.GPModule;

import genxyz.comm.BooksimBroker;
import genxyz.comm.BooksimBrokerFactory;
import genxyz.comm.GPBooksimBridge;
import genxyz.comm.GPBooksimBridgeFactory;
import org.epochx.epox.Node;
import org.epochx.epox.bool.AndFunction;
import org.epochx.epox.lang.IfFunction;
import org.epochx.epox.lang.Seq2Function;
import org.epochx.epox.math.*;
import org.epochx.gp.model.GPModel;
import org.epochx.epox.IntegerERC;
import org.epochx.gp.representation.GPCandidateProgram;
import org.epochx.life.Life;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.JavaRandom;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class RoutingAlgoBreeder extends GPModel {

    // Credit (Congestion metric) at each node
    public IntegerVariable[] credit;

    public IntegerVariable[] hop_cost;

    public IntegerVariable[] available_node;

    public IntegerERC constant_val;

    private String booksimEndPoint, gpEndpoint;

    private int neighboursCount;


    public RoutingAlgoBreeder(int neighboursCount, String booksimEndpoint, String gpEndpoint) {
        this.neighboursCount = neighboursCount;
        this.booksimEndPoint = booksimEndpoint;
        this.gpEndpoint = gpEndpoint;
        constant_val = new IntegerERC(new JavaRandom(System.nanoTime()), 0, 5);
        credit = new IntegerVariable[neighboursCount];
        for (int i = 0; i < credit.length; i++) {
            credit[i] = new IntegerVariable("creditNode_" + i);
        }

        // If there ar N neighbours to a router, 1 would be the src and N-1 of them would be possible destinations
        available_node = new IntegerVariable[neighboursCount];
        for (int i = 0; i < available_node.length; i++) {
            available_node[i] = new IntegerVariable("neighbourNode_" + i);
        }

        hop_cost = new IntegerVariable[neighboursCount];
        for (int i = 0; i < hop_cost.length; i++) {
            hop_cost[i] = new IntegerVariable("hopCostForNode_" + i);
        }

        List<Node> syntax = new ArrayList<>();

        // Terminals
        Collections.addAll(syntax, credit);
        Collections.addAll(syntax, available_node);
        Collections.addAll(syntax, hop_cost);

        syntax.add(constant_val);

        // Functions
        syntax.add(new IfFunction());
        syntax.add(new GreaterThanFunction());
        syntax.add(new LessThanFunction());
        syntax.add(new AndFunction());
        //syntax.add(new SubtractFunction());
        syntax.add(new AbsoluteFunction());
        syntax.add(new MaxFunction());
        syntax.add(new MinFunction());
        syntax.add(new Seq2Function());
        this.setSyntax(syntax);

    }


    @Override
    public Class<?> getReturnType() {
        return Integer.class;
    }

    @Override
    public double getFitness(CandidateProgram candidateProgram) {

        System.out.println(candidateProgram);
        BooksimBroker bksmbrokr = BooksimBrokerFactory.getBooksimBroker(booksimEndPoint);
        GPBooksimBridge bksmbridge = GPBooksimBridgeFactory.getGPBooksimBridge(gpEndpoint);
        bksmbridge.setCandidateProgram(this, (GPCandidateProgram) candidateProgram);
        bksmbridge.startBroker();
        try {
            bksmbrokr.startBooksim(gpEndpoint);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw new RuntimeException("Could not start booksim");
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double fitness = bksmbridge.getFitness();
        System.out.println("Fitness is: "+ fitness);
        return fitness;

    }

    public int getNeighboursCount() {
        return neighboursCount;
    }

    public void setNeighboursCount(int neighboursCount) {
        this.neighboursCount = neighboursCount;
    }
}
