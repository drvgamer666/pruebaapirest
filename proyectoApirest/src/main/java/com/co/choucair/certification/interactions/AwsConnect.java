package com.co.choucair.certification.interactions;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class AwsConnect {
    private static AWSCredentials getSessionCredentials()  {
        /*return new BasicSessionCredentials(
                System.getenv("accessKey"),
                System.getenv("secretKey"),
                System.getenv("sessionToken")
        );*/
        return new BasicAWSCredentials(
                System.getenv("clientid"),
                System.getenv("secretid")
        );
    }
    public static boolean checkIfFileExists(String bucket, String fileName){
        boolean existsJson = false;
        try {
            AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(getSessionCredentials()))
                    .build();
            existsJson = doesItExists(s3,bucket,fileName);
        } catch (Exception e) {
            throw e;
        }
        return existsJson;
    }

    public static boolean doesItExists(AmazonS3 s3,String bucket,String fileName){
        boolean doesItExists = false;
        try {
            doesItExists = s3.doesObjectExist(bucket, fileName); //s3.doesBucketExistV2(bucket);
        } catch (Exception error) {
            boolean isUploadedFile = false;
            throw new IllegalStateException(error);
        }
        return doesItExists;
    }
    public static void wait (int Millisecond){
        try {

            Thread.sleep(Millisecond);

        }catch(Exception e) {
            System.out.println(e);
        }

    }



    public static String downloadFileS3(String bucket, String fileName) throws IOException, InterruptedException {
        String resS3 ="src/test/java/evaluation/result.json";
        try {
            AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(getSessionCredentials()))
                    .build();
            S3Object o = s3.getObject(bucket, fileName);
            S3ObjectInputStream s3is = o.getObjectContent();

            //ObjectMapper mapper = new ObjectMapper();
            //String jsonstring = mapper.writeValueAsString(o);
            //return jsonstring;

            //System.out.println("Resultado:" + s3is.read());
            StringBuilder sb = new StringBuilder();
            //FileOutputStream fos = new FileOutputStream(new File(resS3));
            byte[] read_buf = new byte[1024];
            int read_len = -1;
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(s3is));
            while ((line = br.readLine()) != null) {
                //System.out.println("Resultado len:" + read_len);
                //fos.write(read_buf,0,read_len);
                sb.append(line);
            }
            s3is.close();

            String textjson = sb.toString();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JSONObject json = new JSONObject(textjson);
            System.out.println("_________________>"+textjson.toString());
            return textjson;
            //fos.close();

        } catch (Exception e) {
            throw e;
        }
    }
    public static String filesInS3(String bucket){
        String list="";
        try {
            AmazonS3 s3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new AWSStaticCredentialsProvider(getSessionCredentials()))
                    .build();
            //existsJson = doesItExists(s3,bucket,fileName);
            ListObjectsV2Result result = s3.listObjectsV2(bucket);
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            //list = objects.toString();
            for (S3ObjectSummary os : objects) {
                list = list + ", " + os.getKey();
                System.out.println("* " + os.getKey());
            }
        } catch (Exception e) {
            throw e;
        }
        return list;
    }

}
