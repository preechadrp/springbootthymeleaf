package com.example.springbootthymeleaf.config;

import org.springframework.stereotype.Component;

import com.example.springbootthymeleaf.BroadcastWebSocketHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InitConfig {

	private Thread myThread;
	private boolean startThread = true;
	
	@PostConstruct
	public void init() {
		log.info("init");
		myThread = new Thread(()->{
			int count = 0;
			while (startThread) {
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
				String message = "Server Broadcast #" + (++count) + " @ " + System.currentTimeMillis();
				BroadcastWebSocketHandler.broadcast(message);
			}
		});
		myThread.start();
	}
	
	@PreDestroy
	public void stop() {
		log.info("stop");
		if (myThread != null && myThread.isAlive()) {
			startThread = false;
			myThread.interrupt();
		}
	}
}
