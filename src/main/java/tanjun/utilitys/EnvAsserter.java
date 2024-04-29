package tanjun.utilitys;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EnvAsserter {
    public static boolean assertEnv() throws IOException {
        try{
            Dotenv dotenv = Dotenv.load();
        } catch (Exception e){
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.close();
        }
        Dotenv dotenv = Dotenv.load();
        if (dotenv.get("BotToken") == null){
            System.out.println("Bot token not set. Please go to https://discord.com/developers/applications and " +
                    "generate a new Token for your Bot and Enter it: ");
            Scanner in = new Scanner(System.in);
            String token = in.nextLine();
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("BotToken=");
            writer.append(token);
            writer.append('\n');
            writer.close();
        }

        if (dotenv.get("TenorApiKey") == null){
            System.out.println("Tenor API key not set. Please go to https://developers.google.com/tenor/guides/quickstart" +
                    " and generate a follow the instructions to generate a Tenor API key and Enter it: ");
            Scanner in = new Scanner(System.in);
            String tenorApiKey = in.nextLine();
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("TenorApiKey=");
            writer.append(tenorApiKey);
            writer.append('\n');
            writer.close();
        }

        if (dotenv.get("DefaultLocale") == null){
            System.out.println("The Default Localisation for Tanjun.java was not found. Please enter what the main" +
                    " Language of the Bot will be. Currently Supported: 'de', 'en': ");
            Scanner in = new Scanner(System.in);
            String defaultLocale = in.nextLine();
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("DefaultLocale=");
            writer.append(defaultLocale);
            writer.append('\n');
            writer.close();
        }

        if (dotenv.get("DatabaseUrl") == null){
            System.out.println("Your Database Url is not set. Please enter the URL to connect to your Database. If you" +
                    " dont know what to enter here, please refer to " +
                    "https://www.javatpoint.com/example-to-connect-to-the-mysql-database: ");
            Scanner in = new Scanner(System.in);
            String databaseUrl = in.nextLine();
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("DatabaseUrl=");
            writer.append(databaseUrl);
            writer.append('\n');
            writer.close();
        }

        if (dotenv.get("DatabaseUsername") == null){
            System.out.println("The Username from your Database User is not set. Please enter it: ");
            Scanner in = new Scanner(System.in);
            String databaseUsername = in.nextLine();
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("DatabaseUsername=");
            writer.append(databaseUsername);
            writer.append('\n');
            writer.close();
        }

        if (dotenv.get("DatabasePassword") == null){
            System.out.println("The Password from your Database User is not set. Please enter it: ");
            Scanner in = new Scanner(System.in);
            String databasePassword = in.nextLine();
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("DatabasePassword=");
            writer.append(databasePassword);
            writer.append('\n');
            writer.close();
        }

        if (dotenv.get("MaxLogFileSize") == null){
            System.out.println("The max file size for the log file is not set. Please enter the file size in Bytes" +
                    " (e.g. 1048576 = 1MB (1024*1024): ");
            Scanner in = new Scanner(System.in);
            int maxFileSize = in.nextInt();
            System.out.println(maxFileSize);
            BufferedWriter writer = new BufferedWriter(new FileWriter(".env", true));
            writer.append("MaxLogFileSize=");
            writer.append(String.valueOf(maxFileSize));
            writer.append('\n');
            writer.close();
        }

        return true;
    }
}