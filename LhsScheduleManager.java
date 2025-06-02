package To_Do_List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

class LhsScheduleManager implements ScheduleManager {
    private final Map<LocalDate, List<ScheduleEvent>> eventMap = new HashMap<>();

    @Override
    public void addEvent(String title, String description, LocalDateTime start, LocalDateTime end) {
        // title만 내용으로 사용, description은 빈 문자열로 저장
        if (title == null) title = "";
        if (description == null) description = "";
        LocalDate date = start.toLocalDate();
        eventMap.putIfAbsent(date, new ArrayList<>());
        eventMap.get(date).add(new ScheduleEvent(title, description, start, end));
    }

    @Override
    public List<ScheduleEvent> getEventsByDate(LocalDateTime date) {
        LocalDate d = date.toLocalDate();
        return new ArrayList<>(eventMap.getOrDefault(d, new ArrayList<>()));
    }

    public void removeEvent(ScheduleEvent event) {
        LocalDate date = event.getStartTime().toLocalDate();
        if (eventMap.containsKey(date)) {
            eventMap.get(date).remove(event);
            if (eventMap.get(date).isEmpty()) eventMap.remove(date);
        }
    }
}
