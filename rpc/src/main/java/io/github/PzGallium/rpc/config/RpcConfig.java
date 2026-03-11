package io.github.PzGallium.rpc.config;

/**
 * RPC framework configuration. Supports system properties and environment variables for different environments.
 * <ul>
 *   <li>Zookeeper: -Drpc.zk.address=host:port or env RPC_ZK_ADDRESS, default localhost:2181</li>
 *   <li>Server port: -Drpc.server.port=9000 or env RPC_SERVER_PORT, default 9000</li>
 * </ul>
 */
public final class RpcConfig {

    private static final String DEFAULT_ZK_ADDRESS = "localhost:2181";
    private static final int DEFAULT_SERVER_PORT = 9000;

    public static String getZkAddress() {
        String v = System.getProperty("rpc.zk.address");
        if (v != null && !v.isEmpty()) {
            return v;
        }
        v = System.getenv("RPC_ZK_ADDRESS");
        return (v != null && !v.isEmpty()) ? v : DEFAULT_ZK_ADDRESS;
    }

    public static int getServerPort() {
        String v = System.getProperty("rpc.server.port");
        if (v != null && !v.isEmpty()) {
            try {
                return Integer.parseInt(v.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        v = System.getenv("RPC_SERVER_PORT");
        if (v != null && !v.isEmpty()) {
            try {
                return Integer.parseInt(v.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_SERVER_PORT;
    }

    private RpcConfig() {
    }
}
