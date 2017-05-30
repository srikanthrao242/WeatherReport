package com.weatherReport.WeatherReport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;

public class WeatherReportMapper extends Mapper<LongWritable,Text,Text,Text>{
	
	public void map(LongWritable keyOffSet, Text value, Context context){
		String[] strTokens =value.toString().split("\t");
		Date date = null;
		String currentTime = null;
		Date time = null;
		boolean isDateSatisfy = false;
		boolean isTimeSatisfy = false;
		Float minTemp = Float.MAX_VALUE;
		Float maxTemp = Float.MIN_VALUE;
		String minTempANDTime = null;
		String maxTempANDTime = null;
		Configuration conf = context.getConfiguration();
		Date timeMin = null;
		Date timeMax= null;
		Date dateMin=null,dateMax=null;
		Text temp = new Text();
		Text textDate = new Text();
		String datemin = conf.get("datemin");
		String datemax = conf.get("datemax");
		String timemin = conf.get("timemin");
		String timemax = conf.get("timemax");
		String currentDate = null;
		for(int count = 0;count < strTokens.length;count++){			
			if(count == 0){
				String strToken = strTokens[count];
				String []nameArr = strToken.split("_");
				try {
					dateMin = new SimpleDateFormat("dd-MM-yyyy").parse(datemin);
					dateMax = new SimpleDateFormat("dd-MM-yyyy").parse(datemax);
					date = new SimpleDateFormat("dd-MM-yyyy").parse(nameArr[1]);
					currentDate = nameArr[1];
					if((date.after(dateMin) && date.before(dateMax))||date.equals(dateMin)||date.equals(dateMax)){
						isDateSatisfy = true;	
						textDate.set(strToken);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}else if(count%2 == 1){
				currentTime = strTokens[count];
				try {
					timeMin = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:SSS'Z'").parse(currentDate+"T"+timemin+"Z");
					timeMax = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:SSS'Z'").parse(currentDate+"T"+timemax+"Z");
					time = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:SSS'Z'").parse(currentDate+"T"+currentTime+"Z");
					if(((time.after(timeMin) && time.before(timeMax))||time.equals(timeMin)||time.equals(timeMin)) && isDateSatisfy){
						isTimeSatisfy = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								
			}else{
				if(isTimeSatisfy){
					//isDateSatisfy = false;
					isTimeSatisfy = false;
					Float currentTemp = Float.parseFloat(strTokens[count]);
					if(minTemp > currentTemp){
						 minTemp = currentTemp;
						 minTempANDTime = minTemp + " AND " + currentTime;						 
					}
					if(maxTemp < currentTemp){
						maxTemp = currentTemp;						
						maxTempANDTime = maxTemp + " AND " + currentTime;
					}
				}				
			}
		}				
		try {
			if(maxTempANDTime != null){
				temp.set(maxTempANDTime);
				context.write(textDate, temp);
			}			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	    try {
	    	if(minTempANDTime != null){
	    		temp.set(minTempANDTime);
	    		context.write(textDate, temp);
	    	}			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
