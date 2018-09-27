package jp.gr.java_conf.hhiroshell.vitess;

import java.util.List;

/**
 * @author hhiroshell
 *
 */
interface TableDefinition {

    /**
     * @return
     */
    String getTableName();

    /**
     * @return
     */
    String getIdColumnLabel();

    /**
     * @return
     */
    List<String> getAllColumnLabels();

}