package jp.gr.java_conf.hhiroshell.vitess;

import com.google.common.primitives.UnsignedLong;
import io.vitess.client.cursor.Cursor;
import io.vitess.client.cursor.Row;

import java.util.Arrays;
import java.util.Iterator;

public class Main {

    private static final String USAGE =
            "usage: java -jar guestbookSearcher-0.1.jar -h <host:port> -k <search string> [-p]" + "\n" +
            "   -h: host and port for vtgate" + "\n" +
            "   -k: search string" + "\n" +
            "   -p: print search result or not";

    private static boolean print_result = false;

    private static String hostport;

    private static String keyword;

    public static void main(String[] args) {
        readOptions(args);

        try (GuestbookSearcher searcher = new GuestbookSearcher(hostport);
             Cursor cursor = searcher.search(keyword)) {
            if (print_result) {
                Row row;
                while ((row = cursor.next()) != null) {
                    UnsignedLong page = row.getULong("page");
                    UnsignedLong timeCreated = row.getULong("time_created_ns");
                    byte[] message = row.getBytes("message");
                    System.out.format("(%s, %s, %s)\n", page, timeCreated, new String(message));
                }
            }
        } catch (Exception e) {
            System.out.println("Vitess Java client failed.");
            System.out.println("Error Details:");
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static void readOptions(String[] args) {
        Iterator<String> iterator = Arrays.asList(args).iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();
            if ("-p".equals(arg)) {
                print_result = true;
            } else if ("-h".equals(arg)) {
                hostport = iterator.next();
            } else if ("-k".equals(arg)) {
                keyword = iterator.next();
            }
        }
        if (hostport == null || hostport.isEmpty() || keyword == null || keyword.isEmpty()) {
            System.out.println(USAGE);
            System.exit(1);
        }
    }

}
