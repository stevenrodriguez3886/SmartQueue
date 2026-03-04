package smartqueue;

/**
 * A simple data transfer object representing a single row in a wait-time report.
 *
 * The frontend staff dashboard requests this data for a given date range and then
 * displays or exports it. The report entry includes the scheduled appointment date
 * and hour, the number of customers ahead (waitCount), and a rough wait estimate in
 * minutes (waitMinutes) computed from the current appointment duration.
 */
public record WaitReportEntry(
        String date,
        int hour,
        int waitCount,
        int waitMinutes
) {
}
