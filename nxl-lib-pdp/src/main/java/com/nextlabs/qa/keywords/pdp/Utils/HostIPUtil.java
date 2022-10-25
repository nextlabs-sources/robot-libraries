package com.nextlabs.qa.keywords.pdp.Utils;

import com.bluejungle.destiny.agent.pdpapi.IPDPHost;
import com.google.common.net.InetAddresses;
import com.nextlabs.qa.keywords.pdp.beans.PDPRequest;

import java.net.InetAddress;
import java.util.concurrent.*;

/**
 * Created by sduan on 15/12/2015.
 */
public class HostIPUtil {

    public static int getIPFromHost(IPDPHost host, int timeoutMilliSeconds) throws Exception {
        if(host == null) {
            throw new IllegalArgumentException("the host should not be null");
        }
        String ipString = host.getValue(PDPRequest.ATTR_HOST_IP);
        String hostString = host.getValue(PDPRequest.ATTR_HOST_HOSTNAME);

        if(ipString != null) {
            return Integer.parseInt(ipString);
        } else if(hostString != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Integer> future = executor.submit(new DoResolve(hostString));
            try {
                Integer ip = future.get(timeoutMilliSeconds, TimeUnit.MILLISECONDS);
                return ip;
            } catch (InterruptedException|ExecutionException|TimeoutException e) {
                throw new IllegalStateException("Can't resolve hostname " + hostString);
            }
        } else {
            throw new IllegalArgumentException("the host doesn't contain any host info (ip or hostname)");
        }
    }

    public static int getIPFromHost(IPDPHost host) throws Exception {
        return getIPFromHost(host, 500);
    }
}

class DoResolve implements Callable<Integer> {

    private final String hostname;

    public DoResolve(String hostname) {
        this.hostname = hostname;
    }
    @Override
    public Integer call() throws Exception {
        InetAddress inetAddress = InetAddress.getByName(this.hostname);
        return InetAddresses.coerceToInteger(inetAddress);
    }
}