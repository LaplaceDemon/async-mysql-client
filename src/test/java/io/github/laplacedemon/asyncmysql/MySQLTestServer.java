package io.github.laplacedemon.asyncmysql;

import org.h2.tools.Server;

import java.sql.SQLException;

public class MySQLTestServer {
    private Server server;

    public void init() throws SQLException {
        String[] args = new String[0];
        server = Server.createTcpServer(args).start();
    }

    public void start() throws SQLException {
        server.start();
    }

    public void stop() {
        server.stop();
    }

}
