package To_Do_List;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleManager {
    void addEvent(String title, String description, LocalDateTime start, LocalDateTime end);
    List<ScheduleEvent> getEventsByDate(LocalDateTime date);
}
