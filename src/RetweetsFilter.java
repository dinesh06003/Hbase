import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;



public class RetweetsFilter {

	public static String Table_Name = "Twitter";
	
	public static void main(String[] argv) throws Exception {
		Configuration conf = HBaseConfiguration.create();        
		@SuppressWarnings({ "deprecation", "resource" })
		HTable hTable = new HTable(conf, Table_Name);
		
		//define the filter
		SingleColumnValueFilter filter = new SingleColumnValueFilter(
				Bytes.toBytes("Info"), 
				Bytes.toBytes("Retweets"),
				CompareOp.LESS_OR_EQUAL,
				new BinaryComparator(Bytes.toBytes(821)));//we want tweets which retweet number is less than or equal to 821
		
		Scan scan = new Scan();
		scan.setFilter(filter);
		
		//now we extract the result
		ResultScanner scanner = hTable.getScanner(scan);
		for(Result result=scanner.next(); result!=null; result=scanner.next()) {
			
			int retweets=Bytes.toInt(result.getValue(
					Bytes.toBytes("Info"),
					Bytes.toBytes("Retweets")));
			
			String tweet_text=new String(result.getValue(
					Bytes.toBytes("Tweets"),
					Bytes.toBytes("Tweet_Text")));
			System.out.println("Retweets:"+retweets + "|||Tweet_Text:"+tweet_text);
		}
    }
}
