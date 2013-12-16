package org.teiath.service.aggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

@Component
public class AggregatorTask {
	@Autowired
	private AggregatorService aggregatorService;

	@Scheduled(cron = "${cron.interval}")
	public void run() {
		System.out.println("Aggregator started");
		try {
			Timestamp timestamp = new Timestamp(new Date().getTime());

			Random generator = new Random(System.currentTimeMillis());

			long millis = generator.nextInt(1000) * 10;
			System.out.println(millis);
			Thread.sleep(millis);

			aggregatorService.run(timestamp);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Aggregator ended");
	}


}
