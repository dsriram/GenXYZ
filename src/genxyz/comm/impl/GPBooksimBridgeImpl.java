package genxyz.comm.impl;

import genxyz.GPModule.IntegerVariable;
import genxyz.GPModule.RoutingAlgoBreeder;
import genxyz.comm.GPBooksimBridge;
import genxyz.comm.GPEndpoint;
import org.epochx.gp.representation.GPCandidateProgram;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class GPBooksimBridgeImpl implements GPBooksimBridge,GPEndpoint{

    private final ZMQ.Context c;
    private String endpoint;
    private double current_pgm_fitness;
    private Thread server_thread;
    private ZMQ.Socket s;
    private RoutingAlgoBreeder breeder;
    private GPCandidateProgram program;
    private boolean waitingForFitnessValue;

    public GPBooksimBridgeImpl(String endpoint) {
        this.endpoint = endpoint;
        c = ZMQ.context(4);
        current_pgm_fitness = Double.MAX_VALUE;
    }

    @Override
    public void setCandidateProgram(RoutingAlgoBreeder breeder,GPCandidateProgram program) {
        this.breeder = breeder;
        this.program = program;
    }

    @Override
    public String startBroker() {
        current_pgm_fitness = Double.MAX_VALUE;
        final GPEndpoint gpEndpoint = this;
        waitingForFitnessValue = true;
        server_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    ZMQ.Socket sock = c.socket(ZMQ.REP);
                    sock.bind(gpEndpoint.getEndpoint());
                    //4-element int array(nodes)=4*4,4-element int array(credits)=4*4,4 integers(cx, cy, dx, dy)=4*4=> 4*4+4*4+4*4 bytes
                    ByteBuffer buf = ByteBuffer.allocate(4 * 4 + 4 * 4 + 4 * 4);
                    ByteBuffer outBuf = ByteBuffer.allocate(4);
                    buf.order(ByteOrder.LITTLE_ENDIAN); //In an N-byte message, 0th byte is sent 1st, then 1st and so on
                    outBuf.order(ByteOrder.LITTLE_ENDIAN);
                    while (waitingForFitnessValue) {
                        sock.recvByteBuffer(buf, 0);
//                        System.out.println("Received");
                        buf.rewind();
                        IntBuffer ibuf = buf.asIntBuffer();
                        if (ibuf.get(0) == 0xCAFE) { //interrupt value => we are gna get the final fitness value now
                            sock.send("OK");
                            buf.clear();
                            ByteBuffer bufff = ByteBuffer.allocate(8);
                            bufff.order(ByteOrder.LITTLE_ENDIAN);
                            sock.recvByteBuffer(bufff, 0);
                            bufff.rewind();
                            DoubleBuffer dBuf = bufff.asDoubleBuffer();
                            double avgPacketLatency = dBuf.get();
                            sock.send("OK");
                            sock.close();
                            waitingForFitnessValue = false;
                            gpEndpoint.setWaitingForFitnessValue(false);
                            gpEndpoint.setFitness(avgPacketLatency);
                        } else {
                            int[] nodes = new int[4];
                            ibuf.get(nodes);
                            int[] credits = new int[4];
                            ibuf.get(credits);
                            int cx = ibuf.get();
                            int cy = ibuf.get();
                            int dx = ibuf.get();
                            int dy = ibuf.get();
                            int outNode = gpEndpoint.evaluate(nodes, credits, cx, cy, dx, dy);
//                            System.out.println("outnode :"+outNode);
                            if (outNode > 3) { //assuming 3 neighbours
                                outNode = 3; //clip the value to fit into possible outputs
                            }
                            if (outNode <0) {
                                outNode = 0;
                            }
                            buf.clear();
                            outBuf.putInt(outNode);
                            outBuf.rewind();
//                            System.out.println("Buf:"+outBuf.get()+" "+outBuf.get()+" "+outBuf.get()+" "+outBuf.get());
                            outBuf.rewind();
                            sock.sendByteBuffer(outBuf,0);
                            outBuf.clear();
                        }
                }
            }
        });
        server_thread.start();
        return endpoint;
    }

    @Override
    public double getFitness() {
        while (waitingForFitnessValue) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return current_pgm_fitness;
    }

    @Override
    public int evaluate(int[] nodes, int[] credits, int cx, int cy, int dx, int dy) {
        IntegerVariable[] available_nodes_var = breeder.available_node;
        for (int i = 0; i < available_nodes_var.length; i++) {
            available_nodes_var[i].setValue(nodes[i]);
        }

        IntegerVariable[] credits_var = breeder.credit;
        for (int i = 0; i < credits_var.length; i++) {
            credits_var[i].setValue(credits[i]);
        }

        int dists[] = getManhattanDistanceFromNeighbours(cx,cy,dx,dy);
        IntegerVariable[] hop_cost = breeder.hop_cost;
        for (int i = 0; i < hop_cost.length; i++) {
            hop_cost[i].setValue(dists[i]);
        }

        return (int) program.evaluate();
    }

    @Override
    public void setFitness(double fitness) {
        this.current_pgm_fitness = fitness;
    }

    @Override
    public void setWaitingForFitnessValue(boolean b) {

    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    //see wiki for manhattan dist explanation
    private int getManhattanDistanceFromNeighbours(int cx, int cy, int dx, int dy)[] {
        int[] manhattanDistance = new int[breeder.getNeighboursCount()]; //must be 4, for now
        int index = breeder.getNeighboursCount()-1;
        for (int i = -1; i < 2; i = i+2) { //i = -1 and then 1
            for (int j = -1; j < 2; j = j+2) { // j = -1 and then 1
                int x = cx+i;
                int y = cy+j;

                //warp around
                if(x<0)
                    x = cx+3; //assuming k=4, n=2
                if(y<0)
                    y = cy+3; //assuming k=4, n=2
                if(x%4 == 0)
                    x = cx-3;
                if(y%4 == 0)
                    y = cy-3;

                int dist_x = Math.abs(dx-x);
                int dist_y = Math.abs(dy-y);

                int dist = dist_x+dist_y;

                manhattanDistance[index] = dist;
                index--;
            }
        }

        return manhattanDistance;
    }


}
