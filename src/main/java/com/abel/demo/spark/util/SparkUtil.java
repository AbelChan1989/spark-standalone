package com.abel.demo.spark.util;

import com.abel.demo.spark.cli.SparkJobCli;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by neo on 15-1-9.
 */
public class SparkUtil {
    public static final String PARAM_SPARK_MASTER_URL = "spark.master.url";
    public static final String SPARK_AKKA_TIMEOUT = "spark.akka.timeout";

    public static String masterUrl = "spark://localhost:7077";

    public static SparkConf getSparkConf(String appName, String[] jarpath, int coreNum) {
//        SparkConf sparkConf = new SparkConf()
//                .setMaster(masterUrl)
//                .setAppName(appName)
//                .setJars(JavaSparkContext.jarOfClass(jarClass))
//                .set(SPARK_AKKA_TIMEOUT, "600")
//                .set("spark.cores.max", coreNum + "")
//                .set("spark.default.parallelism", coreNum * 2 + "")
//                .set("spark.executor.memory", "128m")
//                .set("spark.speculation.multiplier", "1.5")
//                .set("spark.streaming.backpressure.enabled", "true")
////                .set("spark.executor.userClassPathFirst", "true")
//                .set("spark.executor.logs.rolling.strategy", "size")
//                .set("spark.executor.logs.rolling.maxRetainedFiles", "10")
//                .set("spark.executor.logs.rolling.size.maxBytes", "134217728"); // 128m
//
//        String sparkExecutorOpts = "";
//        try {
//            sparkExecutorOpts = sparkConf.get("spark.executor.extraJavaOptions");
//            if (sparkExecutorOpts == null) sparkExecutorOpts = "";
//        } catch (NoSuchElementException ignore) {
//        }
//        sparkConf.set("spark.executor.extraJavaOptions", sparkExecutorOpts + " -Dlog4j.configuration=file:/opt/package/spark/conf/log4j.simple.properties");
//        sparkExecutorOpts = "";
//        try {
//            sparkExecutorOpts = sparkConf.get("spark.driver.extraJavaOptions");
//            if (sparkExecutorOpts == null) sparkExecutorOpts = "";
//        } catch (NoSuchElementException ignore) {
//        }
//        sparkConf.set("spark.driver.extraJavaOptions", sparkExecutorOpts + " -XX:+UseConcMarkSweepGC");
//
//        if (confParams != null)
//            for (Map.Entry<String, String> e : confParams.entrySet())
//                sparkConf.set(e.getKey(), e.getValue());
//
//        String sparkHome = System.getenv("SPARK_HOME");
//        if (sparkHome != null) {
//            sparkConf.setSparkHome(sparkHome);
//        }
//
//        return sparkConf;
        SparkConf sparkConf = new SparkConf()
                .setMaster(masterUrl)
                .setAppName(appName)
                .setJars(jarpath)
                .set(SPARK_AKKA_TIMEOUT, "600")
                .set("spark.cores.max", String.valueOf(coreNum))
                .set("spark.default.parallelism", "1")
                .set("spark.executor.memory", "512m")
                .set("spark.driver.memory", "512m");

//        if (jarpath != null && jarpath.length > 1) {
//            sparkConf.setJars(jarpath);
//        }

        String sparkHome = System.getenv("SPARK_HOME");
        if (sparkHome != null) {
            sparkConf.setSparkHome(sparkHome);
        }
        return sparkConf;
    }

    public static JavaStreamingContext createJavaStreamingContext(String[] jarpath, boolean isLocalMode, String appName, Integer coreNum) {
        if (isLocalMode) {
            masterUrl = "local[*]";
        }
        return new JavaStreamingContext(getSparkConf(appName, jarpath, coreNum), Durations.seconds(1));
    }

    public static Set<String> getJarPaths(Class<?> cla) {
        Set<String> jarPaths = new HashSet<String>();
        URLClassLoader classLoader = (URLClassLoader) cla.getClassLoader();
        URL[] allUrls = classLoader.getURLs();
        for (URL url : allUrls) {
            if (url.getPath().endsWith("jar")) {
                jarPaths.add(url.getPath());
            }
        }

        //需要先package生成target目录下才支持直接运行
        try {

            File directory = new File("");// 参数为空
            String relativelyPath = directory.getCanonicalPath();
            System.out.println("[relativelyPath]cd :" + relativelyPath);

            String path = relativelyPath + (!relativelyPath.endsWith("target") ?
                    File.separator + "target" : "");
            System.out.println("[root path]" + path);
            File file = new File(path);
            if (file != null && file.exists() && file.isDirectory()) {
                for (File s : file.listFiles()) {
                    if (s.getAbsolutePath().contains("dependencies.jar")) {
                        System.out.println("[file]:" + s.getAbsolutePath());
                        jarPaths.add(s.getAbsolutePath());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jarPaths;
    }

//    public static JavaSparkContext createSparkContext(Class<?> jars, boolean isLocalMode, String appName, int coreNum, Map<String, String> confParams) {
//        //  masterUrl = "local[3]";
//        if (isLocalMode) {
//            masterUrl = "local[*]";
//        }
//
//        return new JavaSparkContext(getSparkConf(appName, jars, coreNum, confParams));
//    }


//    public static JavaSparkContext createSparkContext(Class<?> jars, boolean isLocalMode, String appName, Integer coreNum) {
//        return createSparkContext(JavaSparkContext.jarOfClass(jars), isLocalMode, appName, coreNum);
//    }


    public static JavaSparkContext createSparkContext(String[] jarpath, boolean isLocalMode, String appName, Integer coreNum) {
        //  masterUrl = "local[3]";
        if (isLocalMode) {
            masterUrl = "local[*]";
        }
//        SparkConf sparkConf = new SparkConf()
//                .setMaster(masterUrl)
//                .setAppName(appName)
//                .setJars(jarpath)
//                .set(SPARK_AKKA_TIMEOUT, "600")
//                .set("spark.cores.max", String.valueOf(coreNum))
//                .set("spark.default.parallelism", "1")
//                .set("spark.executor.memory", "512m")
//                .set("spark.driver.memory", "512m");
//
//        String sparkHome = System.getenv("SPARK_HOME");
//        if (sparkHome != null) {
//            sparkConf.setSparkHome(sparkHome);
//        }
        return new JavaSparkContext(getSparkConf(appName, jarpath, coreNum));
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
