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
			while (startThread) {
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				BroadcastWebSocketHandler.broadcast("ทดสอบข้อความ");
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
