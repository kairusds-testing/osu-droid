package ru.nsu.ccfit.zuev.osu.online;

import com.edlplan.framework.utils.functionality.SmartIterator;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Dns;

public class IPv4DNSSelector implements Dns {

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        List<InetAddress> list = new ArrayList<InetAddress>();

        try {
            list = SmartIterator.wrap(SYSTEM.lookup(hostname).iterator())
                .applyFilter(host -> host instanceof Inet4Address)
                .collectAllAsList();
        }catch(NullPointerException e) {
            UnknownHostException unknownHostException =
                new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }

        return list;
    }

}