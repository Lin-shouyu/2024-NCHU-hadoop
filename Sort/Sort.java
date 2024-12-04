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
        
public class Sort {
        
    public static class Map extends Mapper<LongWritable, Text, IntWritable, NullWritable> {
        // 修改：將輸出值類型改為 NullWritable
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                int token = Integer.parseInt(tokenizer.nextToken());
                context.write(new IntWritable(token), NullWritable.get());  // 修改：輸出空值
            }
        }
    } 
        
    public static class Reduce extends Reducer<IntWritable, NullWritable, IntWritable, NullWritable> {
        // 修改：將值類型改為 NullWritable
        public void reduce(IntWritable key, Iterable<NullWritable> values, Context context) 
          throws IOException, InterruptedException {
            for (NullWritable value : values) {
                context.write(key, NullWritable.get());
            }
        }
    }
        
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        Job job = new Job(conf, "sort");
        //reduce key - value 變數型態
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(NullWritable.class);  // 修改：設置輸出值類型為 NullWritable
        
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setJarByClass(Sort.class);
        
        //input讀取型態
        job.setInputFormatClass(TextInputFormat.class);
        //最終寫出型態
        job.setOutputFormatClass(TextOutputFormat.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        job.waitForCompletion(true);
    }
}