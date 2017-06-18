package com.abel.demo.spark.streaming;

import com.abel.demo.spark.util.SparkUtil;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Serializable;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by abel.chan on 17/6/18.
 */
public class WordCountJobRunner implements Serializable{

    private static final Pattern SPACE = Pattern.compile(" ");

    public void sparkExecute(String host, String port) {

        Set<String> jarPaths = SparkUtil.getJarPaths(this.getClass());

        boolean isLocalMode = false;
        JavaStreamingContext ssc = SparkUtil.createJavaStreamingContext(jarPaths.toArray(new String[]{}), isLocalMode, WordCountJobRunner.class.getSimpleName(), 2);

        JavaReceiverInputDStream<String> lines = ssc.socketTextStream(
                host, Integer.parseInt(port), StorageLevels.MEMORY_AND_DISK_SER);

        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(String x) {
                System.out.println("[value]:" + x);
                return Arrays.asList(SPACE.split(x));
            }
        });

        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
                new PairFunction<String, String, Integer>() {
                    @Override
                    public Tuple2<String, Integer> call(String s) {
                        System.out.println("[word]:" + s);
                        return new Tuple2<>(s, 1);
                    }
                }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });

        wordCounts.print();
        wordCounts.dstream().saveAsTextFiles("hdfs://127.0.0.1:9000/sparkStream/wordCount/", "spark");

        ssc.start();
        ssc.awaitTermination();
    }
}
