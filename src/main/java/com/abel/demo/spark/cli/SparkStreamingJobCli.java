package com.abel.demo.spark.cli;

import com.abel.demo.spark.streaming.WordCountJobRunner;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Created by abel.chan on 17/6/17.
 */
public class SparkStreamingJobCli implements Serializable {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        String port = "9999";
        if (args != null && args.length == 2) {
            host = args[0];
            port = args[1];
        }

        WordCountJobRunner runner = new WordCountJobRunner();
        runner.sparkExecute(host, port);

    }

}
