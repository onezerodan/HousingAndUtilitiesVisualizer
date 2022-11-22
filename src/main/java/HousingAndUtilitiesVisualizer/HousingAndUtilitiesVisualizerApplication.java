package HousingAndUtilitiesVisualizer;

import HousingAndUtilitiesVisualizer.bot.TgBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



@SpringBootApplication
public class HousingAndUtilitiesVisualizerApplication  {

	Logger logger = LogManager.getLogger(HousingAndUtilitiesVisualizerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HousingAndUtilitiesVisualizerApplication.class, args);
	}


}
