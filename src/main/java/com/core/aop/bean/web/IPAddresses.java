 package com.core.aop.bean.web;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.InetAddress;
 import java.net.NetworkInterface;
 import java.net.SocketException;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.util.StringUtils;
 
 public class IPAddresses
 {
   private static final Logger log = LoggerFactory.getLogger(IPAddresses.class);
   
   public static Set<InetAddress> resolveLocalAddresses()
   {
     Set<InetAddress> addrs = new HashSet();
     Enumeration<NetworkInterface> ns = null;
     try
     {
       ns = NetworkInterface.getNetworkInterfaces();
     }
     catch (SocketException localSocketException) {}
     Enumeration<InetAddress> is;
     for (; (ns != null) && (ns.hasMoreElements()); is.hasMoreElements())
     {
       NetworkInterface n = (NetworkInterface)ns.nextElement();
       is = n.getInetAddresses();
       continue;
       InetAddress i = (InetAddress)is.nextElement();
       if ((!i.isLoopbackAddress()) && (!i.isLinkLocalAddress()) && (!i.isMulticastAddress()) && 
         (!isSpecialIp(i.getHostAddress()))) {
         addrs.add(i);
       }
     }
     return addrs;
   }
   
   public static String resolveLocalIp()
   {
     Set<InetAddress> addrs = resolveLocalAddresses();
     Iterator localIterator = addrs.iterator();
     if (localIterator.hasNext())
     {
       InetAddress addr = (InetAddress)localIterator.next();
       return addr.getHostAddress();
     }
     return "";
   }
   
   public static Set<String> resolveLocalIps()
   {
     Set<InetAddress> addrs = resolveLocalAddresses();
     Set<String> ret = new HashSet();
     for (InetAddress addr : addrs) {
       ret.add(addr.getHostAddress());
     }
     return ret;
   }
   
   private static boolean isSpecialIp(String ip)
   {
     if (ip.contains(":")) {
       return true;
     }
     if (ip.startsWith("127.")) {
       return true;
     }
     if (ip.startsWith("169.254.")) {
       return true;
     }
     if (ip.equals("255.255.255.255")) {
       return true;
     }
     return false;
   }
   
   public static String getLocalHostName()
   {
     String hostname = System.getenv("HOSTNAME");
     if (StringUtils.isEmpty(hostname)) {
       try
       {
         Process pro = Runtime.getRuntime().exec("hostname");
         pro.waitFor();
         InputStream in = pro.getInputStream();
         BufferedReader read = new BufferedReader(new InputStreamReader(in));
         hostname = read.readLine();
       }
       catch (IOException e)
       {
         log.error("getLocalHostName IOException");
       }
       catch (InterruptedException e)
       {
         log.error("getLocalHostName InterruptedException");
       }
     }
     return hostname;
   }
   
   public static String hex2IP(String ip)
   {
     StringBuilder sb = new StringBuilder();
     for (String seg : ip.split("\\."))
     {
       String h = Integer.toHexString(Integer.parseInt(seg));
       if (h.length() == 1) {
         sb.append("0");
       }
       sb.append(h);
     }
     return sb.toString();
   }
 }