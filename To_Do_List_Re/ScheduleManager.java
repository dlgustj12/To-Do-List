package To_Do_List_Re;

import java.time.LocalDateTime;
import java.util.List;

//수정 불가
public interface ScheduleManager {
    void addEvent(String title, String description, LocalDateTime start, LocalDateTime end);
    List<ScheduleEvent> getEventsByDate(LocalDateTime date);
}
