import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;


public class GroupByType {
	public static String Table_Name = "Twitter";
	
	public static void main(String[] args) throws Throwable {
		Configuration conf = HBaseConfiguration.create();        
		@SuppressWarnings({ "deprecation", "resource" })
		HTable hTable = new HTable(conf, Table_Name);
		
		//define the filter
		SingleColumnValueFilter filter1 = new SingleColumnValueFilter(
				Bytes.toBytes("Tweets"), 
				Bytes.toBytes("Type"),
				CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("text")));
		
		SingleColumnValueFilter filter2 = new SingleColumnValueFilter(
				Bytes.toBytes("Tweets"), 
				Bytes.toBytes("Type"),
				CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("link")));
		
		Scan scan1 = new Scan();
		scan1.setFilter(filter1);
		
		Scan scan2 = new Scan();
		scan2.setFilter(filter2);
		
		//now we extract the result
		ResultScanner scanner1 = hTable.getScanner(scan1);
		ResultScanner scanner2 = hTable.getScanner(scan2); 
		
		int textNo = 0;
		for(Result result=scanner1.next(); result!=null; result=scanner1.next()) textNo++;
		int linkNo = 0;
		for(Result result=scanner2.next(); result!=null; result=scanner2.next()) linkNo++;

		System.out.println("Text: "+textNo);
		System.out.println("Link: "+linkNo);
	}
}
