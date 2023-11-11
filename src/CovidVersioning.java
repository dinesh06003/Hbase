import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;


public class CovidVersioning {

public static String Table_Name = "CovidTweetTable";
	
	@SuppressWarnings("deprecation")
	public static void main(String[] argv) throws Exception {
		Configuration conf = HBaseConfiguration.create();        
		@SuppressWarnings({ "resource" })
		HTable hTable = new HTable(conf, Table_Name);
		
		String row_key = "88";
		//initialize a put with row key as tweet_url
		Put put = new Put(Bytes.toBytes(row_key));

		//insert additional data
		put.add(Bytes.toBytes("Users"), Bytes.toBytes("UserLocation"), Bytes.toBytes("hyd"));
		put.add(Bytes.toBytes("Users"), Bytes.toBytes("UserLocation"), Bytes.toBytes("sec"));
		hTable.put(put);

		//initialize a ge with row key as tweet_url
		Get get = new Get(Bytes.toBytes(row_key));
		get.setMaxVersions(3);

		Result result = hTable.get(get);
		//byte[] b = result.getValue(Bytes.toBytes("Tweets"), Bytes.toBytes("Type"));
		//System.out.println(new String(b));
		List<KeyValue> allResult = result.getColumn(Bytes.toBytes("Users"), Bytes.toBytes("UserLocation"));
		for(KeyValue kv: allResult) {
			System.out.println(new String(kv.getValue()));
		}
	}
}
