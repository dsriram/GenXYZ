package genxyz.comm;


import genxyz.comm.impl.GPBooksimBridgeImpl;

public class GPBooksimBridgeFactory {

    private static GPBooksimBridgeImpl impl;

    public static GPBooksimBridge getGPBooksimBridge(String endpoint) {
        if (impl == null) {
            impl = new GPBooksimBridgeImpl(endpoint);
        }
        return impl;
    }
}
