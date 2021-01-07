package rvm.dz.dz6test;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class Dz6TestApplication/* implements CommandLineRunner*/ {

	public static void main(String[] args) {
		SpringApplication.run(Dz6TestApplication.class, args);
	}

	@Autowired
	private KafkaTemplate<String, String> template;

	private final CountDownLatch latch = new CountDownLatch(3);

	public void run(String... args) throws Exception {
		this.template.send("myTopic", "foo1");
		this.template.send("myTopic", "foo2");
		this.template.send("myTopic", "foo3");
//		latch.await(60, TimeUnit.SECONDS);
		log.info("All received");
	}

	@KafkaListener(topics = "myTopic")
	public void listen(ConsumerRecord<?, ?> cr) throws Exception {
		log.info(cr.toString());
//		latch.countDown();
	}


}
