package com.abel.demo.spark.util;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by neo on 15-1-9.
 */
public class SparkUtil {
    public static final String PARAM_SPARK_MASTER_URL = "spark.master.url";
    public static final String SPARK_AKKA_TIMEOUT = "spark.akka.timeout";

    public static String masterUrl = "spark://localhost:7077";

    public static JavaSparkContext createSparkContext(Class<?> jars, boolean isLocalMode, String appName, int coreNum, Map<String, String> confParams) {
        //  masterUrl = "local[3]";
        if (isLocalMode) {
            masterUrl = "local[*]";
        }
        SparkConf sparkConf = new SparkConf()
                .setMaster(masterUrl)
                .setAppName(appName)
                .setJars(JavaSparkContext.jarOfClass(jars))
                .set(SPARK_AKKA_TIMEOUT, "600")
                .set("spark.cores.max", coreNum + "")
                .set("spark.default.parallelism", coreNum * 2 + "")
                .set("spark.executor.memory", "128m")
                .set("spark.speculation.multiplier", "1.5")
                .set("spark.streaming.backpressure.enabled", "true")
//                .set("spark.executor.userClassPathFirst", "true")
                .set("spark.executor.logs.rolling.strategy", "size")
                .set("spark.executor.logs.rolling.maxRetainedFiles", "10")
                .set("spark.executor.logs.rolling.size.maxBytes", "134217728") // 128m
                ;
        String sparkExecutorOpts = "";
        try {
            sparkExecutorOpts = sparkConf.get("spark.executor.extraJavaOptions");
            if (sparkExecutorOpts == null) sparkExecutorOpts = "";
        } catch (NoSuchElementException ignore) {
        }
        sparkConf.set("spark.executor.extraJavaOptions", sparkExecutorOpts + " -Dlog4j.configuration=file:/opt/package/spark/conf/log4j.simple.properties");
        sparkExecutorOpts = "";
        try {
            sparkExecutorOpts = sparkConf.get("spark.driver.extraJavaOptions");
            if (sparkExecutorOpts == null) sparkExecutorOpts = "";
        } catch (NoSuchElementException ignore) {
        }
        sparkConf.set("spark.driver.extraJavaOptions", sparkExecutorOpts + " -XX:+UseConcMarkSweepGC");

        if (confParams != null)
            for (Map.Entry<String, String> e : confParams.entrySet())
                sparkConf.set(e.getKey(), e.getValue());

        String sparkHome = System.getenv("SPARK_HOME");
        if (sparkHome != null) {
            sparkConf.setSparkHome(sparkHome);
        }
        return new JavaSparkContext(sparkConf);
    }


    public static JavaSparkContext createSparkContext(Class<?> jars, boolean isLocalMode, String appName, Integer coreNum) {
        return createSparkContext(JavaSparkContext.jarOfClass(jars), isLocalMode, appName, coreNum);
    }


    public static JavaSparkContext createSparkContext(String[] jarpath, boolean isLocalMode, String appName, Integer coreNum) {
        //  masterUrl = "local[3]";
        if (isLocalMode) {
            masterUrl = "local[*]";
        }
        SparkConf sparkConf = new SparkConf()
                .setMaster(masterUrl)
                .setAppName(appName)
                .setJars(jarpath)
                .set(SPARK_AKKA_TIMEOUT, "600")
                .set("spark.cores.max", String.valueOf(coreNum))
                .set("spark.default.parallelism", "1")
                .set("spark.executor.memory", "512m")
                .set("spark.driver.memory", "512m");
//                .set("spark.speculation.multiplier", "1.5");

        String sparkHome = System.getenv("SPARK_HOME");
        if (sparkHome != null) {
            sparkConf.setSparkHome(sparkHome);
        }
        return new JavaSparkContext(sparkConf);
    }

    /**
     * <li>功能描述：获取spark core的大小
     *
     * @return long
     * @author Administrator
     */
    public static int getSparkCore(Date beginDate, Date endDate) {
        long day = 0;
        try {
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
            return (int) ((day / 5 + 1) * 10 > 80 ? 80 : (day / 5 + 1) * 10);
            //System.out.println("相隔的天数="+day);
        } catch (Exception e) {
        }
        return 10;
    }

    /**
     * <li>功能描述：获取spark core的大小
     *
     * @return long
     * @author Administrator
     */
    public static int getSparkCore(long beginDate, long endDate) {
        long day = 0;

        try {
            day = (endDate - beginDate) / (24 * 60 * 60 * 1000);
            return (int) ((day / 5 + 1) * 10 > 80 ? 80 : (day / 5 + 1) * 10);
            //System.out.println("相隔的天数="+day);
        } catch (Exception e) {
        }
        return 10;
    }


}
