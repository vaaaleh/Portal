package me.joeleoli.portal.independent.file;

import lombok.Getter;

import java.io.*;
import java.util.Properties;

@Getter
public class Config {

    private String[] hubs;
    private String[] queues;
    private String redisHost;
    private int redisPort;
    private String redisPassword;

    public Config() {
        File file = new File("config.properties");

        if (!file.exists()) {
            try {
                file.createNewFile();

                FileOutputStream output = new FileOutputStream(file);
                output.write("hubs=hub-01\n".getBytes());
                output.write("queues=test1,test2,test3\n".getBytes());
                output.write("redis-host=127.0.0.1\n".getBytes());
                output.write("redis-port=6379\n".getBytes());
                output.write("redis-password=dev\n".getBytes());
                output.flush();
                output.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");

            prop.load(input);

            this.hubs = ((String) prop.getOrDefault("hubs", "")).split(",");
            this.queues = ((String) prop.getOrDefault("queues", "")).split(",");
            this.redisHost = ((String) prop.getOrDefault("redis-host", "127.0.0.1"));
            this.redisPort = Integer.valueOf((String) prop.getOrDefault("redis-port", "6379"));
            this.redisPassword = ((String) prop.getOrDefault("redis-password", ""));
        }
        catch (IOException io) {
            io.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
