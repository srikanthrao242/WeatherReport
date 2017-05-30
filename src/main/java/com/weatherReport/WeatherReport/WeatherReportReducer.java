package com.weatherReport.WeatherReport;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class WeatherReportReducer extends Reducer<Text, Text, Text, Text>{
	MultipleOutputs<Text, Text> multiOp;
	public void setup(Context context){
		multiOp = new MultipleOutputs<Text, Text>(context);
	}
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
		int count = 0;
		String reducerInputStr[] = null;
		String temp1="",temp2= "";
		String time1 = "",time2="";
		Text result = new Text();
		for(Text val:values){
			if(count == 0){
				reducerInputStr = val.toString().split("AND");
				temp1 = reducerInputStr[0];
				time1 = reducerInputStr[1];
			}else{
				reducerInputStr = val.toString().split("AND");
				temp2 = reducerInputStr[0];
				time2 = reducerInputStr[1];
			}
			count++;
		}
		if(Float.parseFloat(temp1) > Float.parseFloat(temp2)){
			result = new Text("Time: " + time2 + " MinTemp: " + temp2 + "\t"
				     + "Time: " + time1 + " MaxTemp: " + temp1);
		}else{
			result = new Text("Time: " + time1 + " MinTemp: " + temp1 + "\t"
				     + "Time: " + time2 + " MaxTemp: " + temp2);
		}
		context.write( key, result);
	}
	 @Override
	 public void cleanup(Context context) throws IOException,
	   InterruptedException {
		 multiOp.close();
	 }
}
