import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CDRGeneratorPositive {

    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    // или DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss"), если в реализации принято решение не отображать часовой пояс

    public static void main(String[] args) {
        // Генерируем всего 10 записей
        try (FileWriter writer = new FileWriter("cdr_positive.txt")) {
            // Длительность звонка от 2 до 48 минут (3 записи)
            for (int i = 0; i < 3; i++) {
                generateCallData(writer, 2, 48);
            }

            // Длительность звонка от 52 до 60 минут (3 записи)
            for (int i = 0; i < 3; i++) {
                generateCallData(writer, 52, 60);
            }

            // Длительность звонка 1 минута (1 запись)
            generateCallData(writer, 1, 1);

            // Длительность звонка 50 минут (1 запись)
            generateCallData(writer, 50, 50);

            // Длительность звонка 51 минута (1 запись)
            generateCallData(writer, 51, 51);

            // Длительность звонка 49 минут (1 запись)
            generateCallData(writer, 49, 49);

            System.out.println("Генерация позитивных тестовых данных завершена. Записи сохранены в cdr_positive.txt.");
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

        // Форматируем время в ISO 8601
        String startTimeStr = startTime.atZone(ZoneOffset.UTC).format(formatter);
        String endTimeStr = endTime.atZone(ZoneOffset.UTC).format(formatter);

        // Записываем данные в файл (зависит от реализации, тут запись по структуре JSON)
        writer.write(String.format("{\n" +
                "\"outcoming_msisdn\": \"%s\",\n" +
                "\"incoming_msisdn\": \"%s\",\n" +
                "\"start_time\": \"%s\",\n" +
                "\"end_time\": \"%s\"\n" +
                "},\n", outcomingMsisdn, incomingMsisdn, startTimeStr, endTimeStr));
    }
}

