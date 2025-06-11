package To_Do_List;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

//실제 일정 데이터를 Map에 저장/관리하는 ScheduleManager 구현체
//일정 저장/조회/삭제 로직 담당
class LhsScheduleManager implements ScheduleManager {
    //날짜별로 이벤트 목록 저장하는 해시맵
    private final Map<LocalDate, List<ScheduleEvent>> eventMap = new HashMap<>();

    //일정 추가(인터페이스꺼 구현)
    @Override
    public void addEvent(String title, String description, LocalDateTime start, LocalDateTime end) {
        //null이면 빈 문자열로 대체
        if (title == null) title = "";
        if (description == null) description = "";

        //LocalDate : 날짜 클래스임
        LocalDate date = start.toLocalDate(); // date <- 이벤트가 시작하는 날짜 객체가 됨

        // Map에 key가 date인게 없을때만 새 리스트 추가
        if (!eventMap.containsKey(date)) {
            eventMap.put(date, new ArrayList<>());
        }
        //eventMap.putIfAbsent(date, new ArrayList<>()); <- 위의 예외처리 사실은 한줄로 처리 가능..!

        //eventMap에서 해당 날짜(date)에 해당하는 이벤트 리스트를 가져와서,
        //그 리스트에 새로운 이벤트(ScheduleEvent 객체)를 추가
        eventMap.get(date).add(new ScheduleEvent(title, description, start, end));
    }

    //삭제 이벤트(인터페이스꺼 구현)
    @Override
    public void deleteEvent(ScheduleEvent event) {
        if (event == null) return;

        LocalDate date = event.getStartTime().toLocalDate();

        if (eventMap.containsKey(date)) {
            eventMap.get(date).remove(event);

            if (eventMap.get(date).isEmpty()) {
                eventMap.remove(date);
            }
        }
    }


    //날짜 일정 조회
    @Override
    public List<ScheduleEvent> getEventsByDate(LocalDateTime date) {
        LocalDate d = date.toLocalDate();
        // 날짜에 해당하는 이벤트 리스트 반환 (없으면 빈 리스트)
        return new ArrayList<>(eventMap.getOrDefault(d, new ArrayList<>()));
    }
}
