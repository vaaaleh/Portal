package me.joeleoli.portal.independent;

import me.joeleoli.portal.independent.file.Config;
import me.joeleoli.portal.independent.jedis.PortalSubscriptionHandler;
import me.joeleoli.portal.independent.log.Logger;
import me.joeleoli.portal.independent.thread.BroadcastThread;
import me.joeleoli.portal.independent.thread.QueueThread;
import me.joeleoli.portal.shared.jedis.JedisChannel;
import me.joeleoli.portal.shared.jedis.JedisPublisher;
import me.joeleoli.portal.shared.jedis.JedisSettings;
import me.joeleoli.portal.shared.jedis.JedisSubscriber;
import me.joeleoli.portal.shared.queue.Queue;

import lombok.Getter;

@Getter
public class Portal {

    @Getter
    private static Portal instance;

    private Config config;

    private JedisSubscriber subscriber;
    private JedisPublisher publisher;

    private Portal() {
        this.config = new Config();

        for (String name : this.config.getQueues()) {
            Queue.getQueues().add(new Queue(name));

            Logger.print("Loaded queue `" + name + "` from config");
        }

        JedisSettings settings = new JedisSettings(
                this.config.getRedisHost(),
                this.config.getRedisPort(),
                this.config.getRedisPassword().equals("") ? null : this.config.getRedisPassword()
        );

        this.subscriber = new JedisSubscriber(JedisChannel.INDEPENDENT, settings, new PortalSubscriptionHandler());
        this.publisher = new JedisPublisher(settings);
        this.publisher.start();

        new QueueThread().start();
        new BroadcastThread().start();

        Logger.print("Portal is now running...");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!settings.getJedisPool().isClosed()) {
                    settings.getJedisPool().close();
                }
            }
        });
    }

    public static void main(String[] args) {
        instance = new Portal();
    }

}
