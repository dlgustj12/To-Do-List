package To_Do_List_Re;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

//실제 일정 데이터를 Map에 저장/관리하는 ScheduleManager 구현체
//일정 저장/조회/삭제 로직 담당
class LhsScheduleManager implements ScheduleManager {
    private final Map<LocalDate, List<ScheduleEvent>> eventMap = new HashMap<>();

    @Override
    public void addEvent(String title, String description, LocalDateTime start, LocalDateTime end) {
        ScheduleEvent event = new ScheduleEvent(title, description, start, end);
        eventMap.computeIfAbsent(start.toLocalDate(), k -> new ArrayList<>()).add(event);
    }

    @Override
    public List<ScheduleEvent> getEventsByDate(LocalDateTime date) {
        return new ArrayList<>(eventMap.getOrDefault(date.toLocalDate(), Collections.emptyList()));
    }

    public void removeEvent(ScheduleEvent event) {
        LocalDate date = event.getStartTime().toLocalDate();
        List<ScheduleEvent> events = eventMap.get(date);
        if (events != null && events.remove(event) && events.isEmpty()) {
            eventMap.remove(date);
        }
    }
}


