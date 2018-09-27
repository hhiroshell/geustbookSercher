package jp.gr.java_conf.hhiroshell.vitess;

import java.util.ArrayList;
import java.util.List;

class MessagesTableDefinition implements TableDefinition {

    private static MessagesTableDefinition instance = null;

    static TableDefinition getInstance() {
        if (instance == null) {
            instance = new MessagesTableDefinition();
        }
        return instance;
    }

    // uninstanciable
    private MessagesTableDefinition() {}

    static final String TABLE_NAME = "messages";

    enum Column {
        COL_PAGE("page"),
        COL_TIME_CREATED_NS("time_created_ns"),
        COL_MESSAGE("message"),
        ;

        private final String label;

        Column(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }

    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumnLabel() {
        return Column.COL_PAGE.getLabel();
    }

    @Override
    public List<String> getAllColumnLabels() {
        Column[] columns = Column.values();
        List<String> labels = new ArrayList<>(columns.length);
        for (Column column : columns) {
            labels.add(column.getLabel());
        }
        return labels;
    }

}
