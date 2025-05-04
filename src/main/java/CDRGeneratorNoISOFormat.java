import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CDRGeneratorNoISOFormat {

    private static final Random random = new Random();

    // указываем в формате не ISO8601
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        // Генерируем всего 10 записей
        try (FileWriter writer = new FileWriter("cdr_no_ISO8601_format.txt")) {
            for (int i = 0; i < 10; i++) {
                generateCallData(writer, 2, 48);
            }

            System.out.println("Генерация тестовых данных с датой не в формате ISO8601 завершена. Записи сохранены в cdr_no_ISO8601_format.txt.");
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }


    private static void generateCallData(FileWriter writer, int minDuration, int maxDuration) throws IOException {
        // Массивы номеров абонентов из БД BRT
        String[] outcomingNum = {"79001112233", "79002223344", "79003334455", "79004445566", "79005556677"};
        String[] incomingNum = {"79001112233", "79002223344", "79003334455", "79004445566", "79005556677"};

        String outcomingMsisdn, incomingMsisdn;
        do {
            //выбираем рандомный номер из массива в пределах его длины
            outcomingMsisdn = outcomingNum[random.nextInt(outcomingNum.length)];
            incomingMsisdn = incomingNum[random.nextInt(incomingNum.length)];
        }
        // проверяем, что номера не совпадают, чтобы не было случая, когда абонент звонит сам себе
        while (outcomingMsisdn.equals(incomingMsisdn));

        // Случайная длительность звонка в минутах
        int duration = random.nextInt((maxDuration - minDuration) + 1) + minDuration;

        // Случайное время начала звонка (в пределах последнего месяца)
        LocalDateTime startTime = LocalDateTime.now().minusDays(random.nextInt(30)).minusMinutes(random.nextInt(60 * 24));
        LocalDateTime endTime = startTime.plusMinutes(duration);

        // Форматируем время
        String startTimeStr = startTime.format(formatter);
        String endTimeStr = endTime.format(formatter);

        // Записываем данные в файл (зависит от реализации, тут запись по структуре JSON)
        writer.write(String.format("{\n" +
                "\"outcoming_msisdn\": \"%s\",\n" +
                "\"incoming_msisdn\": \"%s\",\n" +
                "\"start_time\": \"%s\",\n" +
                "\"end_time\": \"%s\"\n" +
                "},\n", outcomingMsisdn, incomingMsisdn, startTimeStr, endTimeStr));
    }
}
