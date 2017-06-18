package com.abel.demo.spark.cli;

import com.abel.demo.spark.streaming.KafkaWordCountJobRunner;
import com.abel.demo.spark.streaming.WordCountJobRunner;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Created by abel.chan on 17/6/17.
 */
public class SparkStreamingKafkaJobCli implements Serializable {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {

        KafkaWordCountJobRunner runner = new KafkaWordCountJobRunner();
        runner.sparkExecute();

    }

}
