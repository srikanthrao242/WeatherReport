package com.weatherReport.WeatherReport;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CalculateTempWithinTimePeriod {
	public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{
		Configuration conf = new Configuration();
		conf.set("timemin", args[0]);
		conf.set("timemax", args[1]);
		conf.set("datemin", args[2]);
		conf.set("datemax", args[3]);
		Job job = Job.getInstance(conf, "Wheather Report");
		job.setJarByClass(CalculateTempWithinTimePeriod.class);
		
		job.setMapperClass(WeatherReportMapper.class);
		job.setReducerClass(WeatherReportReducer.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		Path pathInput = new Path(args[4]);
		Path pathOutput = new Path(args[5]);
		
		FileInputFormat.addInputPath(job, pathInput);
		FileOutputFormat.setOutputPath(job,pathOutput);
		
		try{
			System.exit(job.waitForCompletion(true) ? 0:1);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
