package genxyz.comm.impl;


import genxyz.comm.BooksimBroker;
import org.zeromq.ZMQ;

public class MockSim {
    private ZMQ.Context c;
    private String endpoint;
    private Thread server_thread;
    final ZMQ.Socket s;

    public MockSim(String endpoint) {
        this.endpoint = endpoint;
        c = ZMQ.context(1);
        s = c.socket(ZMQ.REP);
    }

    public void startSimBackend() {
        s.bind(endpoint);
        server_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String req1 = s.recvStr();
                    if (req1.contentEquals(BooksimBroker.REQ_STRING)) {
                        s.send(BooksimBroker.REP_STRING1);
                    } else {
                        continue;
                    }
                    String req2 = s.recvStr();
                    System.out.println("MockSim: Received EP " + req2 + "from broker");
                    s.send(BooksimBroker.REP_STRING2);
                }
            }
        });
        server_thread.start();
    }

    public void stopSimBackend() {
        server_thread.stop();
        s.close();
        c.close();
    }
}
