package To_Do_List;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// 5. 전체 달력 UI (한 달의 날짜별 cell 관리)
//사용자와 상호작용하는 메인 UI
public class CalendarGUI extends JFrame {

    private JPanel calendarPanel; // 달력(날짜 셀) 패널
    private JLabel monthLabel; // 현재 월 표시라벨
    private LocalDate currentDate; // 현재 달력에서 보고있는 날짜
    private LhsScheduleManager scheduleManager; //할일 데이터 관리하는 객체
    private final Map<LocalDate, DayCellPanel> dayCellPanelMap = new HashMap<>();
    // LocalDate(날짜)-> key, DayCellPanel 객체-> value
    // dayCellPanelMap : 특정 날짜에 해당하는 DayCellPanel 객체를 빠르게 찾기 위한 맵.

    public CalendarGUI() {
        setTitle("Calendar_ToDoList_Of_LHS");
        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentDate = LocalDate.now();
        scheduleManager = new LhsScheduleManager();

        // 상단 패널 (월 변경)
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);

        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });
        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        calendarPanel = new JPanel(new GridLayout(0, 7, 3, 3));
        add(topPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    // 달력 갱신
    private void updateCalendar() {
        calendarPanel.removeAll();
        dayCellPanelMap.clear();

        // 월 레이블
        YearMonth yearMonth = YearMonth.of(currentDate.getYear(), currentDate.getMonth());
        monthLabel.setText(currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.KOREAN) + " " + currentDate.getYear());

        // 요일
        DayOfWeek[] koreanOrder = {
                //일월화수목금토 로
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY
        };
        for (DayOfWeek dow : koreanOrder) {
            JLabel lbl = new JLabel(dow.getDisplayName(TextStyle.SHORT, Locale.KOREAN), SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD,25f));

            calendarPanel.add(lbl);
        }

        // 시작 요일 및 총 일 수
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int startDay = firstDayOfMonth.getDayOfWeek().getValue() % 7; // 0(일)-6(토)
        int daysInMonth = yearMonth.lengthOfMonth();

        // 앞 빈칸
        for (int i = 0; i < startDay; i++) calendarPanel.add(new JLabel(""));

        // 날짜 셀
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate thisDate = yearMonth.atDay(day);
            DayCellPanel panel = new DayCellPanel(thisDate, scheduleManager, this::refreshAllPanels);
            dayCellPanelMap.put(thisDate, panel);
            calendarPanel.add(panel);
        }

        calendarPanel.revalidate(); //배치(레이아웃) 갱신
        calendarPanel.repaint(); //화면(그래픽) 갱신
    }

    // 전체 날짜 셀 일정 새로고침
    private void refreshAllPanels() {
        for (DayCellPanel panel : dayCellPanelMap.values()) {
            panel.refreshEventList();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame obj = new CalendarGUI();
            obj.setVisible(true);
        });
    }
}

