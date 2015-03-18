package genxyz.comm;

import genxyz.comm.impl.BooksimBrokerImpl;

public class BooksimBrokerFactory {

    private static BooksimBrokerImpl impl;

    public static BooksimBroker getBooksimBroker(String booksimEP) {
        if (impl == null) {
            impl = new BooksimBrokerImpl(booksimEP);
        }
        return impl;
    }

}
