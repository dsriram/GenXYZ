package genxyz.comm;


import java.io.IOException;

public interface BooksimBroker {

    public final String REQ_STRING = "START BOOKSIM";
    public final String REP_STRING1 = "BOOKSIM OK";
    public final String REP_STRING2 = "EP OK. BOOKSIM STARTED";

    public String getBooksimEndpoint();

    public void startBooksim(String gpEndpoint) throws IOException;

    public void stopBroker();

}
