/**
 * Created by Poojaa on 14-03-2015.
 */

import genxyz.GPModule.RoutingAlgoBreeder;
import genxyz.GPModule.examples.CubicFunctionProgram;
import genxyz.GPModule.examples.IfElseFunctionProgram;
import genxyz.comm.impl.BooksimBrokerImpl;
import genxyz.comm.impl.MockSim;
import org.epochx.life.GenerationAdapter;
import org.epochx.life.Life;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.StatField;
import org.epochx.stats.Stats;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
//        //final IfElseFunctionProgram m = new IfElseFunctionProgram();
//        final CubicFunctionProgram m = new CubicFunctionProgram();
//        m.setOverallBestFitness(Double.MAX_VALUE);
//
//        Life.get().addGenerationListener(new GenerationAdapter() {
//            @Override
//            public void onGenerationEnd() {
//
//                //prints to console: Generation_Number Best_Fitness_Value Best_Gene
//                Stats.get().print(StatField.GEN_NUMBER, StatField.GEN_FITNESS_MIN, StatField.GEN_FITTEST_PROGRAM);
//                double currentGenBestFit = (double) Stats.get().getStat(StatField.GEN_FITNESS_MIN);
//                if (currentGenBestFit < m.getOverallBestFitness()) {
//                    m.setOverallBestGene((CandidateProgram) Stats.get().getStat(StatField.GEN_FITTEST_PROGRAM));
//                    m.setOverallBestFitness(currentGenBestFit);
//                }
//            }
//        });
//        m.setMaxInitialDepth(m.getOptimalMaxInitialDepth());
//        m.setPopulationSize(m.getOptimalPopulationSize());
//        m.setMaxDepth(m.getOptimalMaxDepth());
//        m.setNoGenerations(512);
//        m.setTerminationFitness(0.005);
//        //run gene breeding process for 4 times, from the start to select the best
//        m.setNoRuns(4);
//        m.run();
//
//        System.out.print("\n\n\n\n");
//        System.out.println("The best fitness we were able to achieve is: " + m.getOverallBestFitness());
//        System.out.println("The best gene we could breed is:\n" + m.getOverallBestGene());

        String booksimEndpoint = "tcp://192.168.59.103:9999";
        String gpEndpoint = "tcp://192.168.59.3:11112";

        RoutingAlgoBreeder m = new RoutingAlgoBreeder(4, booksimEndpoint, gpEndpoint);
        m.setMaxInitialDepth(6);
        m.setMaxDepth(10);
        m.setPopulationSize(100);
        m.setNoGenerations(32);
        m.run();

        Life.get().addGenerationListener(new GenerationAdapter() {
            @Override
            public void onGenerationEnd() {

                //prints to console: Generation_Number Best_Fitness_Value Best_Gene
                Stats.get().print(StatField.GEN_NUMBER, StatField.GEN_FITNESS_MIN, StatField.GEN_FITTEST_PROGRAM);
//                double currentGenBestFit = (double) Stats.get().getStat(StatField.GEN_FITNESS_MIN);
//                if (currentGenBestFit < m.getOverallBestFitness()) {
//                    m.setOverallBestGene((CandidateProgram) Stats.get().getStat(StatField.GEN_FITTEST_PROGRAM));
//                    m.setOverallBestFitness(currentGenBestFit);
//                }
            }
        });

    }
}
