package To_Do_List;

import java.time.LocalDateTime;

//수정 불가
public class ScheduleEvent {
    private String title; //제목
    private String description; //내용
    private LocalDateTime startTime; //시작 시간
    private LocalDateTime endTime; //종료 시간

    //생성자
    public ScheduleEvent(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //접근자들
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    @Override
    public String toString() {
        return "[" + startTime.toLocalTime() + " - " + endTime.toLocalTime() + "] " + title;
    }
}
