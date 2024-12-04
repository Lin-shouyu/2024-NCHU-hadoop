import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
        
public class SortWordCount {
        
    public static class CountMap extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                context.write(new Text(token), new IntWritable(1));
            }
        }
    } 
        
    public static class CountReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) 
          throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }

    public static class Map extends Mapper<LongWritable, Text, IntWritable, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            if (tokenizer.countTokens() >= 2) {  // 確保有足夠的token
                String word = tokenizer.nextToken();
                int count = Integer.parseInt(tokenizer.nextToken());
                context.write(new IntWritable(count), new Text(word));
            }
        }
    } 
        
    public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
        public void reduce(IntWritable key, Iterable<Text> values, Context context) 
          throws IOException, InterruptedException {
            for (Text value : values) {
                context.write(key, value);
            }
        }
    }
        
    public static void main(String[] args) throws Exception {
        // 第一個 Job：計算字數
        Configuration conf1 = new Configuration();
        Job job1 = new Job(conf1, "wordcount");
    
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        
        job1.setMapperClass(CountMap.class);
        job1.setReducerClass(CountReduce.class);
        job1.setJarByClass(Sort.class);
        
        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);
        
        // 創建臨時輸出路徑
        Path tempDir = new Path("temp-output");
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, tempDir);
        
        // 執行第一個 Job
        job1.waitForCompletion(true);

        // 第二個 Job：排序
        Configuration conf2 = new Configuration();
        Job job2 = new Job(conf2, "sort");
    
        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(Text.class);
        
        job2.setMapperClass(Map.class);
        job2.setReducerClass(Reduce.class);
        job2.setJarByClass(Sort.class);
        
        job2.setInputFormatClass(TextInputFormat.class);
        job2.setOutputFormatClass(TextOutputFormat.class);
        
        FileInputFormat.addInputPath(job2, tempDir);
        FileOutputFormat.setOutputPath(job2, new Path(args[1]));
        
        // 執行第二個 Job
        job2.waitForCompletion(true);
        
        // 刪除臨時目錄
        org.apache.hadoop.fs.FileSystem.get(conf1).delete(tempDir, true);
    }
}