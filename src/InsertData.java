import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class InsertData extends Configured implements Tool{

	public String Table_Name = "Twitter";
    @SuppressWarnings("deprecation")
	@Override
    public int run(String[] argv) throws IOException {
        Configuration conf = HBaseConfiguration.create();        
        @SuppressWarnings("resource")
		HBaseAdmin admin=new HBaseAdmin(conf);        
        
        boolean isExists = admin.tableExists(Table_Name);
        
        if(isExists == false) {
	        //create table with column family
	        HTableDescriptor htb=new HTableDescriptor(Table_Name);
	        HColumnDescriptor DateTimeFamily = new HColumnDescriptor("DateTime");
	        HColumnDescriptor TweetsFamily = new HColumnDescriptor("Tweets");
	        HColumnDescriptor InfoFamily = new HColumnDescriptor("Info");
	        
	        htb.addFamily(DateTimeFamily);
	        htb.addFamily(TweetsFamily);
	        htb.addFamily(InfoFamily);
	        admin.createTable(htb);
        }
        
        try {
    		@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader("Twitter.txt"));
    	    String line;
    	    
    	    int row_count=0;
    	    
    	    //iterate over every line of the input file
    	    while((line = br.readLine()) != null) {
    	    	
    	    	if(line.isEmpty())continue;
    	    	
    	    	row_count++;
    	    	
    	    	String[] lineArray = line.split("\t");
    	    	String date = lineArray[0];
    	    	String time = lineArray[1];
    	    	String tweet_text = lineArray[2];
    	    	String type = lineArray[3];
    	    	String media_type = lineArray[4];
    	    	String hashtags = lineArray[5];
    	    	String tweet_url = lineArray[6];
    	    	int retweets = Integer.parseInt(lineArray[7]);
    	    	
    	    	//initialize a put with row key as tweet_url
	            Put put = new Put(Bytes.toBytes(tweet_url));
	            
	            //add column data one after one
	            put.add(Bytes.toBytes("DateTime"), Bytes.toBytes("Date"), Bytes.toBytes(date));
	            put.add(Bytes.toBytes("DateTime"), Bytes.toBytes("Time"), Bytes.toBytes(time));
	            
	            put.add(Bytes.toBytes("Tweets"), Bytes.toBytes("Tweet_Text"), Bytes.toBytes(tweet_text));
	            put.add(Bytes.toBytes("Tweets"), Bytes.toBytes("Type"), Bytes.toBytes(type));
	            
	            put.add(Bytes.toBytes("Info"), Bytes.toBytes("Media_Type"), Bytes.toBytes(media_type));
	            put.add(Bytes.toBytes("Info"), Bytes.toBytes("Hashtags"), Bytes.toBytes(hashtags));
	            put.add(Bytes.toBytes("Info"), Bytes.toBytes("Retweets"), Bytes.toBytes(retweets));
	            
	            //add the put in the table
    	    	HTable hTable = new HTable(conf, Table_Name);
    	    	hTable.put(put);
    	    	hTable.close();    
	    	}
    	    System.out.println("Inserted " + row_count + " Inserted");
    	    
	    } catch (FileNotFoundException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    } 

      return 0;
   }
    
    public static void main(String[] argv) throws Exception {
        int ret = ToolRunner.run(new InsertData(), argv);
        System.exit(ret);
    }
}