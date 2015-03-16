package genxyz.GPModule.examples;

import genxyz.GPModule.DoubleVariable;
import genxyz.GPModule.OptimalGPParameters;
import org.epochx.epox.DoubleERC;
import org.epochx.epox.Node;
import org.epochx.epox.math.AddFunction;
import org.epochx.epox.math.MultiplyFunction;
import org.epochx.gp.model.GPModel;
import org.epochx.gp.representation.GPCandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.JavaRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CubicFunctionProgram extends GPModel implements OptimalGPParameters {
    private DoubleVariable x;
    private DoubleERC a0;

    // The double inputs/outputs that we will test solutions against.
    private double[] inputs;
    private double[] outputs;

    private double overallBestFitness;

    private CandidateProgram overallBestGene;

    public CubicFunctionProgram() {

        x = new DoubleVariable("x");

        //Double type, ergodic random variable in the range 0 to 10, of precision 64
        a0 = new DoubleERC(new JavaRandom(System.nanoTime()), 0, 10, 64);

        List<Node> syntax = new ArrayList<>();

        // Functions.
        syntax.add(new AddFunction());

        syntax.add(new MultiplyFunction());

        // Terminals.
        syntax.add(x);
        syntax.add(a0);
        setSyntax(syntax);

        // Generate set of test inputs and corresponding correct output.
        inputs = new double[256];
        Random r = new Random();
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = 100 * r.nextDouble();
        }
        outputs = generateOutputs(inputs);
    }

    @Override
    public Class<?> getReturnType() {
        return Double.class;
    }

    @Override
    public double getFitness(CandidateProgram p) {
        GPCandidateProgram program = (GPCandidateProgram) p;

        //lesser the score fitter the program
        double score = 0;
        for (int i = 0; i < inputs.length; i++) {
            // Set the variables.
            x.setValue(inputs[i]);

            double result = (Double) program.evaluate();
            score += Math.abs(result - outputs[i]);
        }
        return score / inputs.length;
    }

    private double[] generateOutputs(double[] in) {
        double[] out = new double[in.length];

        for (int i = 0; i < in.length; i++) {
            double x = in[i];

            // f(x) = x^3 + x^2 + x + 1
            out[i] = x * x * x + x * x + x + 1;
        }
        return out;
    }

    @Override
    public int getOptimalMaxInitialDepth() {
        return 2;
    }

    @Override
    public int getOptimalMaxDepth() {
        return 4;
    }

    @Override
    public int getOptimalPopulationSize() {
        return 1000;
    }

    public double getOverallBestFitness() {
        return overallBestFitness;
    }

    public void setOverallBestFitness(double overallBestFitness) {
        this.overallBestFitness = overallBestFitness;
    }

    public CandidateProgram getOverallBestGene() {
        return overallBestGene;
    }

    public void setOverallBestGene(CandidateProgram overallBestGene) {
        this.overallBestGene = overallBestGene;
    }
}
