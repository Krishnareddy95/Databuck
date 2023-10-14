package com.databuck.config;

public class S3Constants {
	public static String S3ACCESSKEY;
	public static String S3SECRETKEY;	
	public static String S3REGION;
	public static String S3BUCKET;
	public static boolean isS3PropEnabled = false;
		
	public static void initiallizeS3Constants(String accessKey, String secretKey, String s3Region, String s3Bucket){
		S3ACCESSKEY = accessKey;
		S3SECRETKEY = secretKey;
		S3REGION = s3Region;
		S3BUCKET = s3Bucket;
		isS3PropEnabled = true;
	}
}
