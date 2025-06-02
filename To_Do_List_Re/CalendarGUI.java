package To_Do_List_Re;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

// 5. 전체 달력 UI (한 달의 날짜별 cell 관리)(Swing 기반)
// 사용자와 상호작용하는 메인 UI
public class CalendarGUI {
    private final JFrame frame;
    private final JPanel calendarPanel;
    private final JLabel monthLabel;
    private final ScheduleManager manager;
    private LocalDate currentDate;

    public CalendarGUI(ScheduleManager manager) {
        this.manager = manager;
        this.currentDate = LocalDate.now();

        frame = new JFrame("일정 관리");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 900);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        calendarPanel = new JPanel(new GridLayout(0, 7));
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(calendarPanel, BorderLayout.CENTER);

        prevButton.addActionListener(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });

        nextButton.addActionListener(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });

        updateCalendar();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateCalendar() {
        calendarPanel.removeAll();
        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int startDay = firstDay.getDayOfWeek().getValue() % 7;
        int lengthOfMonth = currentDate.lengthOfMonth();

        monthLabel.setText(currentDate.getMonth() + " " + currentDate.getYear());

        for (int i = 0; i < startDay; i++) {
            calendarPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= lengthOfMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            DayCellPanel cell = new DayCellPanel(date, manager);
            calendarPanel.add(cell);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarGUI(new LhsScheduleManager()));
    }
}


