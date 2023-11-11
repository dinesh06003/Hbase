import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hbase.util.Bytes;
public class UsersByLocationCounter {

    public static Map<String, Integer> countUsersByLocation(String tableName, String familyName, String qualifierName) throws IOException {
        Map<String, Integer> countMap = new HashMap<>();

        try (Connection connection = ConnectionFactory.createConnection(HBaseConfiguration.create())) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifierName));

            ResultScanner scanner = table.getScanner(scan);
            for (Result result : scanner) {
                byte[] locBytes = result.getValue(Bytes.toBytes(familyName), Bytes.toBytes(qualifierName));
                if (locBytes != null) {
                    String loc = Bytes.toString(locBytes);
                    countMap.put(loc, countMap.getOrDefault(loc, 0) + 1);
                }
            }
        }

        return countMap;
    }

    public static void main(String[] args) {
        String tableName = "CovidTweetTable"; 
        String familyName = "Users";
        String qualifierName = "UserLocation";

        try {
            Map<String, Integer> countMap = countUsersByLocation(tableName, familyName, qualifierName);
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                System.out.println("Location: " + entry.getKey() + ", User Count: " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}