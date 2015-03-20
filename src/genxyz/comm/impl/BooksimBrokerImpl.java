package genxyz.comm.impl;

import genxyz.comm.BooksimBroker;
import org.zeromq.ZMQ;

import java.io.IOException;

public class BooksimBrokerImpl implements BooksimBroker {

    private String booksimEP, gpEndPoint;
    private ZMQ.Context zcontext;

    public BooksimBrokerImpl(String booksimEP) {
        zcontext = ZMQ.context(4);
        this.booksimEP = booksimEP;
    }

    @Override
    public String getBooksimEndpoint() {
        return null;
    }

    @Override
    public void startBooksim(String gpEndpoint) throws IOException {
        this.gpEndPoint = gpEndpoint;
        ZMQ.Socket s = zcontext.socket(ZMQ.REQ);
        s.connect(booksimEP);
        s.setSendTimeOut(2000);
        s.setReceiveTimeOut(2000);
        s.send(BooksimBroker.REQ_STRING);
        String reply = s.recvStr();
        if (reply != null && reply.contentEquals(BooksimBroker.REP_STRING1))
            System.out.println("Connected to Booksim backend");
        else {
            s.close();
            throw new IOException("Unable to connect to Booksim backend at " + booksimEP);
        }
        s.send(gpEndpoint);
        reply = s.recvStr();
//        System.out.println(reply);
//        ByteBuffer buf = ByteBuffer.allocate(16);
//        s.recvByteBuffer(buf,0);
//        buf.rewind();
//        buf.rewind();
//        buf.order(ByteOrder.LITTLE_ENDIAN);
//        IntBuffer ibuf = buf.asIntBuffer();
//        System.out.println(ibuf.get()+" "+ibuf.get()+" "+ibuf.get()+" ");
        if (reply != null && reply.contentEquals(BooksimBroker.REP_STRING2))
            System.out.println("Started booksim");
        else {
            s.close();
            throw new IOException("Unable to start booksim. Did not receive REP_STRING2");
        }
        s.close();
    }

    @Override
    public void stopBroker() {
        zcontext.close();
    }
}
