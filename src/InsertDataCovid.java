import java.io.FileReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.Reader;

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

public class InsertDataCovid extends Configured implements Tool{

public String Table_Name = "CovidTweetTable";
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
       HColumnDescriptor UsersFamily = new HColumnDescriptor("Users");
       HColumnDescriptor TweetsFamily = new HColumnDescriptor("Tweets");
       HColumnDescriptor ExtraFamily = new HColumnDescriptor("Extra");
       
       htb.addFamily(UsersFamily);
       htb.addFamily(TweetsFamily);
       htb.addFamily(ExtraFamily);
       admin.createTable(htb);
        }
       
       
        try (Reader reader = new FileReader("/home/sg8rq/git/CS6304_HBase_Demo/Input File/covid19tweets.csv")){
    @SuppressWarnings("resource")
          
       int row_count=0;


       Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
       for (CSVRecord record : records) {
      String user_name = record.get("user_name");
           String user_loc = record.get("user_location");
           String user_des = record.get("user_description");
           String user_cre = record.get("user_created");
           String user_follo = record.get("user_followers");
           String user_frnds = record.get("user_friends");
           String user_fav = record.get("user_favourites");
           String user_ver = record.get("user_verified");
           String date = record.get("date");
           String txt = record.get("text");
           String hash = record.get("hashtags");
           String src = record.get("source");
           String retweet = record.get("is_retweet");

           row_count++;
           Put put = new Put(Bytes.toBytes(row_count));
           
           //add column data one after one
           		put.add(Bytes.toBytes("Users"), Bytes.toBytes("Name"), Bytes.toBytes(user_name));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UserLocation"), Bytes.toBytes(user_loc));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UserDescription"), Bytes.toBytes(user_des));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UserCreated"), Bytes.toBytes(user_cre));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UserFollowers"), Bytes.toBytes(Integer.parseInt(user_follo)));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UsersFriends"), Bytes.toBytes(user_frnds));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UsersFavourites"), Bytes.toBytes(user_fav));
                put.add(Bytes.toBytes("Users"), Bytes.toBytes("UsersVerified"), Bytes.toBytes(user_ver));
                //System.out.println("========Users Family Added successfully=======");
                put.add(Bytes.toBytes("Tweets"), Bytes.toBytes("Date"), Bytes.toBytes(date));
                put.add(Bytes.toBytes("Tweets"), Bytes.toBytes("Text"), Bytes.toBytes(txt));
                put.add(Bytes.toBytes("Tweets"), Bytes.toBytes("Hashtags"), Bytes.toBytes(hash));
                put.add(Bytes.toBytes("Tweets"), Bytes.toBytes("isTweet"), Bytes.toBytes(retweet));
               // System.out.println("========Tweets Family Added successfully=======");

   
                put.add(Bytes.toBytes("Extra"), Bytes.toBytes("Source"), Bytes.toBytes(src));


                
               // System.out.println("========Extra Family Added successfully=======");


           
           //add the put in the table
        HTable hTable = new HTable(conf, Table_Name);
        hTable.put(put);
        hTable.close();    
       }
       System.out.println("Inserted " + row_count);
    }
       
    catch (FileNotFoundException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }   catch (NumberFormatException e) {
        e.printStackTrace();
        System.out.println("CHECK");
        }
       

      return 0;
   }
   
    public static void main(String[] argv) throws Exception {
        int ret = ToolRunner.run(new InsertDataCovid(), argv);
        System.exit(ret);
    }
}