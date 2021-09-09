package ru.nsu.ccfit.zuev.osu.online;

import com.edlplan.framework.utils.functionality.SmartIterator;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Dns;

public class IPv4DNSSelector implements Dns {

    @Override
    public List<InetAddress> lookup(String hostname) {
        return SmartIterator.wrap(SYSTEM.lookup(hostname)).iterator())
            .applyFilter(host -> host instanceof Inet4Address)
            .collectAllAsList();
    }

}