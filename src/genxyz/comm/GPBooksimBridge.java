package genxyz.comm;

import genxyz.GPModule.RoutingAlgoBreeder;
import org.epochx.gp.representation.GPCandidateProgram;

public interface GPBooksimBridge {

    public void setCandidateProgram(RoutingAlgoBreeder breeder, GPCandidateProgram program);

    public String startBroker();

    public double getFitness();
}
