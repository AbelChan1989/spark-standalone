package com.abel.demo.spark.streaming;

import com.abel.demo.spark.util.SparkUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Serializable;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by abel.chan on 17/6/18.
 */
public class KafkaWordCountJobRunner implements Serializable {

    private static final Pattern SPACE = Pattern.compile(" ");

    public void sparkExecute() {
        //设置匹配模式，以空格分隔
        final Pattern SPACE = Pattern.compile(" ");
        //接收数据的地址和端口
        String zkQuorum = "localhost:2181";
        //话题所在的组
        String group = "1";
        //话题名称以“，”分隔
        String topics = "top1,top2";
        //每个话题的分片数
        int numThreads = 2;

        Set<String> jarPaths = SparkUtil.getJarPaths(this.getClass());
        boolean isLocalMode = true;
        //spark kafka 必须有2个core以上，一个接收，一个处理
        JavaStreamingContext jssc = SparkUtil.createJavaStreamingContext(jarPaths.toArray(new String[]{}), isLocalMode, KafkaWordCountJobRunner.class.getSimpleName(), 2);

//        SparkConf sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local[2]");
//        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(10000));


        //        jssc.checkpoint("checkpoint"); //设置检查点
        //存放话题跟分片的映射关系
        Map<String, Integer> topicmap = new HashMap<>();
        String[] topicsArr = topics.split(",");
        int n = topicsArr.length;
        for (int i = 0; i < n; i++) {
            topicmap.put(topicsArr[i], numThreads);
        }
        //从Kafka中获取数据转换成RDD
        JavaPairReceiverInputDStream<String, String> lines = KafkaUtils.createStream(jssc, zkQuorum, group, topicmap);
        //从话题中过滤所需数据
        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<Tuple2<String, String>, String>() {

            @Override
            public Iterable<String> call(Tuple2<String, String> arg0)
                    throws Exception {
                return Arrays.asList(SPACE.split(arg0._2));
            }
        });
        //对其中的单词进行统计
        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
                new PairFunction<String, String, Integer>() {
                    @Override
                    public Tuple2<String, Integer> call(String s) {
                        return new Tuple2<String, Integer>(s, 1);
                    }
                }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i1, Integer i2) {
                return i1 + i2;
            }
        });
        //打印结果
        wordCounts.print();
        wordCounts.dstream().saveAsTextFiles("hdfs://127.0.0.1:9000/sparkStream/wordCount/", "spark");
        jssc.start();
        jssc.awaitTermination();
    }
}
