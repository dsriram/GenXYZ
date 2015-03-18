package genxyz.comm;

public interface GPEndpoint {

    public int evaluate(int[] nodes, int[] credits, int cx, int cy, int dx, int dy);

    public void setFitness(double fitness);

    public void setWaitingForFitnessValue(boolean b);

    public String getEndpoint();
}
