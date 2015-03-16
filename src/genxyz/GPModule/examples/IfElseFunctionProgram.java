package genxyz.GPModule.examples;

import genxyz.GPModule.DoubleVariable;
import genxyz.GPModule.OptimalGPParameters;
import org.epochx.epox.IntegerERC;
import org.epochx.epox.Node;
import org.epochx.epox.lang.IfFunction;
import org.epochx.epox.math.GreaterThanFunction;
import org.epochx.epox.math.SubtractFunction;
import org.epochx.gp.model.GPModel;
import org.epochx.gp.representation.GPCandidateProgram;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.JavaRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IfElseFunctionProgram extends GPModel implements OptimalGPParameters {

    private DoubleVariable x;
    private IntegerERC a0, a1, a2, a3;

    // The double inputs/outputs that we will test solutions against.
    private double[] inputs;
    private double[] outputs;

    private double overallBestFitness;

    private CandidateProgram overallBestGene;

    public IfElseFunctionProgram() {

        x = new DoubleVariable("x");
        a0 = new IntegerERC(new JavaRandom(System.nanoTime()), 0, 400);
//        a1 = new DoubleERC(new JavaRandom(System.nanoTime()),0,6,64);
//        a2 = new DoubleERC(new JavaRandom(System.nanoTime()),0,6,64);
//        a3 = new DoubleERC(new JavaRandom(System.nanoTime()),0,6,64);

        List<Node> syntax = new ArrayList<>();

        // Functions.
        syntax.add(new IfFunction());
        syntax.add(new GreaterThanFunction());
        syntax.add(new SubtractFunction());
//        syntax.add(new AddFunction());
        // Terminals.
        syntax.add(x);
        syntax.add(a0);
        setSyntax(syntax);

        // Generate set of test inputs and corresponding correct output.
        inputs = new double[256];
        Random r = new Random();
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = r.nextInt(400);
        }
        outputs = generateOutputs(inputs);
        this.setMutationProbability(0.05);
    }

    @Override
    public Class<?> getReturnType() {
        return Double.class;
    }

    @Override
    public double getFitness(CandidateProgram p) {
        GPCandidateProgram program = (GPCandidateProgram) p;
        double score = 0;
        for (int i = 0; i < inputs.length; i++) {
            // Set the variables.
            x.setValue(inputs[i]);

            double result = (Double) program.evaluate();
            score += Math.abs(result - outputs[i]);
        }
        return score;
    }

    private double[] generateOutputs(double[] in) {
        double[] out = new double[in.length];

        for (int i = 0; i < in.length; i++) {
            double x = in[i];

            /*
             *  IF (x>100)
             *      return 10;
             *  ELSE
             *      return 20;
             */
            out[i] = (x > 200) ? 10 : 20;
        }
        return out;
    }

    @Override
    public int getOptimalMaxInitialDepth() {
        return 2;
    }

    @Override
    public int getOptimalMaxDepth() {
        return 3;
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
