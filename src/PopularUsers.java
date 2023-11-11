import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PopularUsers {

    public static void main(String[] argv) throws Exception {
    	Configuration conf = HBaseConfiguration.create();
        TableName tableName = TableName.valueOf("CovidTweetTable");
  		/*Table table =  connection.getTable(tableName);*/
    	Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create());
       /* Table table = connection.getTable(TableName.valueOf(tableName));*/
    	Table table =  connection.getTable(tableName);

        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes("Extra"));
        scan.addColumn(Bytes.toBytes("Extra"), Bytes.toBytes("UserFollowers"));
        scan.setCaching(100);

        try (ResultScanner scanner = ((org.apache.hadoop.hbase.client.Table) table).getScanner(scan)) {
            for (Result result : scanner) {
                byte[] valueBytes = result.getValue(Bytes.toBytes("Extra"), Bytes.toBytes("UserFollowers"));
                if (valueBytes != null) {
                    int userFollowers = Bytes.toInt(valueBytes);
                    if (userFollowers > 999999) {
                        byte[] rowKey = result.getRow();
                        String userName = Bytes.toString(rowKey);
                        System.out.println("User: " + userName + ", UserFollowers: " + userFollowers);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ((Connection) table).close();
            connection.close();
        }
    }
}
