package jp.gr.java_conf.hhiroshell.vitess;

import com.google.common.collect.ImmutableMap;
import io.vitess.client.Context;
import io.vitess.client.RpcClient;
import io.vitess.client.VTGateBlockingConnection;
import io.vitess.client.VTSession;
import io.vitess.client.cursor.Cursor;
import io.vitess.client.grpc.GrpcClientFactory;
import io.vitess.proto.Query;
import org.joda.time.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

class GuestbookSearcher implements AutoCloseable {

    private RpcClient client;

    private VTGateBlockingConnection connection;

    private static final String PLACE_HOLDER_LIKE_OPERATOR = "operator";

    GuestbookSearcher(String hostport) {
        client = new GrpcClientFactory().create(
                Context.getDefault().withDeadlineAfter(Duration.standardDays(365)), hostport);
        connection = new VTGateBlockingConnection(client);
    }

    @Override
    public void close() {
        try {
            client.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Cursor search(String keyword) throws SQLException {
        Map<String, Object> bindVars = new ImmutableMap.Builder<String, Object>()
                        .put(PLACE_HOLDER_LIKE_OPERATOR, "%" + keyword + "%")
                        .build();
        Context context = Context.getDefault().withDeadlineAfter(Duration.standardDays(365));
        VTSession session = new VTSession("@master", Query.ExecuteOptions.getDefaultInstance());
        long begin = System.currentTimeMillis();
        Cursor cursor = connection.execute(context, buildQuery(), bindVars, session);
        long end = System.currentTimeMillis();
        System.out.println("TIME ELAPSED: " + (end - begin) + " [ms]");
        return cursor;
    }

    private String buildQuery() {
        return (new StringBuilder())
                .append("SELECT * FROM ")
                .append(MessagesTableDefinition.getInstance().getTableName())
                .append(" WHERE ")
                .append(MessagesTableDefinition.Column.COL_MESSAGE.getLabel())
                .append(" LIKE ")
                .append(":")
                .append(PLACE_HOLDER_LIKE_OPERATOR)
                .toString();
    }

}
