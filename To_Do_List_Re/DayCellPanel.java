package To_Do_List_Re;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

//하루 단위의 달력의 셀 UI 컴포넌트
//날짜를 하나씩 시각적으로 보여주는 셀
class DayCellPanel extends JPanel {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final Color TODAY_COLOR = Color.GREEN;

    private final LocalDate date;
    private final ScheduleManager manager;
    private final JPanel eventsPanel;

    public DayCellPanel(LocalDate date, ScheduleManager manager) {
        this.date = date;
        this.manager = manager;

        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setBackground(date.equals(LocalDate.now()) ? TODAY_COLOR : DEFAULT_COLOR);
        setLayout(new BorderLayout());

        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dayLabel.setForeground(Color.RED);
        }
        add(dayLabel, BorderLayout.NORTH);

        eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(eventsPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshEvents();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showAddEventDialog();
                }
            }
        });
    }

    private void refreshEvents() {
        eventsPanel.removeAll();
        List<ScheduleEvent> events = manager.getEventsByDate(date.atStartOfDay());
        events.sort(Comparator.comparing(ScheduleEvent::getStartTime));

        for (ScheduleEvent event : events) {
            JPanel eventRow = new JPanel(new BorderLayout());
            JLabel eventLabel = new JLabel(event.toString());
            JButton deleteButton = new JButton("X");
            deleteButton.setMargin(new Insets(0, 5, 0, 5));
            deleteButton.setFocusable(false);

            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "일정을 삭제하시겠습니까?", "삭제 확인", JOptionPane.OK_CANCEL_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    if (manager instanceof LhsScheduleManager lhs) {
                        lhs.removeEvent(event);
                        refreshEvents();
                    }
                }
            });

            eventRow.add(eventLabel, BorderLayout.CENTER);
            eventRow.add(deleteButton, BorderLayout.EAST);
            eventsPanel.add(eventRow);
        }

        eventsPanel.revalidate();
        eventsPanel.repaint();
    }

    private void showAddEventDialog() {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField startField = new JTextField("00:00");
        JTextField endField = new JTextField("00:00");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("제목:")); panel.add(titleField);
        panel.add(new JLabel("설명:")); panel.add(descField);
        panel.add(new JLabel("시작시간:")); panel.add(startField);
        panel.add(new JLabel("종료시간:")); panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "일정 추가", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalTime start = LocalTime.parse(startField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime end = LocalTime.parse(endField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                manager.addEvent(titleField.getText(), descField.getText(), date.atTime(start), date.atTime(end));
                refreshEvents();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "시간 형식 오류. HH:mm 형식을 사용하세요.");
            }
        }
    }
}