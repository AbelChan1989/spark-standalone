package com.abel.demo.spark.cli;

import com.abel.demo.spark.util.SparkUtil;
import com.google.common.collect.Lists;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by abel.chan on 17/6/13.
 */
public class SparkJobCli implements Serializable {

    public static void main(String[] args) {
        SparkJobCli cli = new SparkJobCli();
        cli.sparkExecute();
    }

    private void sparkExecute() {

        Set<String> jarPaths = SparkUtil.getJarPaths(this.getClass());

        JavaSparkContext jsc = null;

        try {
            List<String> data = new ArrayList<String>();
            data.add("i love you");
            data.add("you love me");

            boolean isLocalMode = false;//当等于true时，支持本地调试。

            jsc = SparkUtil.createSparkContext(jarPaths.toArray(new String[]{}), isLocalMode, SparkJobCli.class.getSimpleName(), 1);
            System.out.println("[result]:" +
                    jsc.parallelize(data).repartition(1).mapPartitions(new FlatMapFunction<Iterator<String>, String>() {
                        public Iterable<String> call(Iterator<String> values) throws Exception {

                            List<String> res = Lists.newArrayList();
                            while (values.hasNext()) {
                                String next = values.next();
                                res.addAll(Arrays.asList(next.split(" ")));
                            }
                            return res;
                        }
                    }).mapToPair(new PairFunction<String, String, Integer>() {
                        public Tuple2<String, Integer> call(String s) throws Exception {
                            return new Tuple2<String, Integer>(s, 1);
                        }
                    }).reduceByKey(new Function2<Integer, Integer, Integer>() {
                        public Integer call(Integer v1, Integer v2) throws Exception {
                            return v1 + v2;
                        }
                    }).collect());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jsc != null) {
                jsc.close();
            }
        }
    }
}
