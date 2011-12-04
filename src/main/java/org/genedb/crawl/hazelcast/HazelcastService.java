package org.genedb.crawl.hazelcast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Instance;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * A utility to startup independent Hazelcast instances, whose only purpose is to help separate crawl instances
 * by sharing the caching load. It starts up a lightweight http server, which will listen for status or stop 
 * requests. 
 * 
 * It can be controlled from the command line using the crawl bash script.
 */
public class HazelcastService {

    
    public enum HazelcastServiceCommand {
        start, 
        stop, 
        status;
    }
    
    private static Logger             logger      = Logger.getLogger(HazelcastService.class);
    
    @Option(name = "-h", aliases = { "--help" }, usage = "Print help")
    public boolean           help;
    
    @Option(name = "-x", aliases = { "--hazelcast" }, usage = "A hazelcast xml file", required = true)
    public File              hazelcastFile;
    
    @Option(name = "-p", aliases = { "--port" }, usage = "The port to listen for commands on", required = false)
    public int               port        = 9999;
    
    @Option(name = "-c", aliases = { "--context" }, usage = "The server base context path (default is '/hazelcast/'), don't forget the slashes!", required = false)
    public String            baseContext = "/hazelcast/";
    
    @Option(name = "-t", aliases = { "--test" }, usage = "Add some hard-coded test values to this instance", required = false)
    public boolean           testValues  = false;
    
    @Option(name = "-l", aliases = { "--log" }, usage = "The path to a log4j property file.", required = false)
    public File              logPath;
    
    @Argument(index = 0, usage = "Can be either start|status|stop.", required = true)
    public HazelcastServiceCommand   command;
    
    private final String      host        = "localhost";
    
    private HazelcastInstance hz;
    private HazelcastMonitor  monitor;
    private HttpServer        server;
    
    private void run() throws IOException {
        
        switch (command) {
            case start:
                this.start();
                break;
            case stop:
            case status:
                request(url(command));
                break;
        }

    }

    private void request(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        try {
            int code = connection.getResponseCode();
            System.out.println("Response Code: " + code);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        } catch (ConnectException ce) {
            System.out.println("Connection refused. Service not running on port " + port);
        } finally {
            connection.disconnect();
        }
    }

    private void start() throws IOException {

        if (logPath != null) {
            PropertyConfigurator.configure(logPath.getAbsolutePath());
        }

        if (!hazelcastFile.isFile()) {
            throw new RuntimeException("Could not find hazelcast configuration file : " + hazelcastFile);
        }

        /*
         * Start a lightweight http server, make sure it can only be accessed
         * from the machine it's running on (don't want to allow remote
         * shutdown).
         */
        server = HttpServer.create(new InetSocketAddress(host, port), port);
        server.createContext(baseContext + HazelcastServiceCommand.stop, new ShutdownHandler());
        server.createContext(baseContext + HazelcastServiceCommand.status, new StatusHandler());
        server.setExecutor(null);
        server.start();

        // Config config = new Config();
        // config.setConfigurationFile(hazelcastFile);
        // config.setConfigurationUrl(hazelcastFile.toURI().toURL());
        // hz = Hazelcast.newHazelcastInstance(config);

        /*
         * Creating a Config object (as commented out above) and setting the
         * file using setConfigurationFile doesn't seem to work at all. The
         * system property approach does.
         */
        String path = hazelcastFile.getAbsolutePath();
        logger.info("Setting configuration file " + path);
        System.setProperty("hazelcast.config", path);

        hz = Hazelcast.getDefaultInstance();

        logger.info(hz.getConfig().getConfigurationFile());
        logger.info(hz.getConfig().getGroupConfig().getName());

        monitor = new HazelcastMonitor();
        hz.addInstanceListener(monitor);

        if (testValues) {
            hz.getMap("tst").put("1", "1");
            hz.getMap("tst").put("2", "2");
            hz.getMap("tst").put("3", "3");

            logger.info(hz.getMap("tst").getId());
            logger.info(hz.getMap("tst").size());
        }

        System.out.println("-- HAZELCAST SERVICE WRAPPER READY --");
        System.out.println(String.format("To check status, request: '%s'.", url(HazelcastServiceCommand.status)));
        System.out.println(String.format("To shut it down, request: '%s'.", url(HazelcastServiceCommand.stop)));

    }

    private URL url(HazelcastServiceCommand command) throws MalformedURLException {
        return new URL(String.format("http://%s:%s%s%s", host, port, baseContext, command));
    }

    private class StatusHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            StringBuffer buffer = new StringBuffer();

            buffer.append("This is the info : \n");

            for (Instance instance : hz.getInstances()) {
                buffer.append(HazelcastMonitor.stats(HazelcastMonitor.getId(instance)) + "\n");
            }

            String response = buffer.toString();

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class ShutdownHandler implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {

            Hazelcast.shutdownAll();

            String response = "Shutting down";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

            System.exit(-1);

        }

    }

    public static void main(String[] args) throws IOException {

        HazelcastService serviceWrapper = new HazelcastService();

        CmdLineParser parser = new CmdLineParser(serviceWrapper);
        try {

            parser.parseArgument(args);

            if (serviceWrapper.help) {
                parser.setUsageWidth(80);
                parser.printUsage(System.out);
                System.exit(1);
            }

            serviceWrapper.run();

        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            parser.setUsageWidth(80);
            parser.printUsage(System.out);
            System.exit(1);
        }

    }

}
