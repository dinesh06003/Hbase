import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UsersByCreationYearCounter {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Table table = new HTable(conf, "CovidTweetTable");

        // Create a scan object to scan the table
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("Users"), Bytes.toBytes("UserCreated"));

        ResultScanner scanner = table.getScanner(scan);

        Map<String, Integer> userCreationYearCount = new HashMap<>();

        for (Result result : scanner) {
            byte[] userCreationValue = result.getValue(Bytes.toBytes("Users"), Bytes.toBytes("UserCreated"));
            //System.out.println(Bytes.toString(userCreationValue));
            if (userCreationValue != null) {
                String userCreationDate = Bytes.toString(userCreationValue);
                //System.out.println(userCreationDate);
                String userCreationYear = userCreationDate.substring(0, 4);

                userCreationYearCount.put(userCreationYear, userCreationYearCount.getOrDefault(userCreationYear, 0) + 1);
            }
        }

        scanner.close();

        // Print the user count for each year
        for (Map.Entry<String, Integer> entry : userCreationYearCount.entrySet()) {
            System.out.println("Year: " + entry.getKey() + ", User Count: " + entry.getValue());
        }

        table.close();
    }
}
