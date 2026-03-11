package io.github.PzGallium.rpc.config;

/**
 * RPC 框架配置，支持系统属性与环境变量，便于不同环境使用。
 * <ul>
 *   <li>Zookeeper: -Drpc.zk.address=host:port 或环境变量 RPC_ZK_ADDRESS，默认 localhost:2181</li>
 *   <li>服务端端口: -Drpc.server.port=9000 或环境变量 RPC_SERVER_PORT，默认 9000</li>
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
