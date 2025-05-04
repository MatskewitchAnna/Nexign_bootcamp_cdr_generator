import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CDRGeneratorNoIncomingMsisdn {

    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public static void main(String[] args) {
        // Генерируем всего 10 записей
        try (FileWriter writer = new FileWriter("cdr_no_incoming_msisdn.txt")) {
            for (int i = 0; i < 10; i++) {
                generateCallData(writer, 1, 60);
            }

            System.out.println("Генерация тестовых данных с отсутствующим номером вызываемого абонента завершена. Записи сохранены в cdr_no_incoming_msisdn.txt.");

        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }


    private static void generateCallData(FileWriter writer, int minDuration, int maxDuration) throws IOException {
        // Массивы невалидных номеров абонентов
        String[] outcomingNum = {"79001112233", "79002223344", "79003334455", "79004445566", "79005556677"};
        String[] incomingNum = {""};

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
