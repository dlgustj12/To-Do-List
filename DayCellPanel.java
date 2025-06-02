package To_Do_List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

class DayCellPanel extends JPanel {
    private final LocalDate date; // 이 패널이 나타내는 날짜
    private final LhsScheduleManager scheduleManager; // 일정 추가/삭제/조회 객체
    private final Runnable onScheduleChanged;
    private final JButton dayButton; // 날짜 숫자 버튼
    private final JPanel scheduleListPanel;

    public DayCellPanel(LocalDate date, LhsScheduleManager scheduleManager, Runnable onScheduleChanged) {
        this.date = date;
        this.scheduleManager = scheduleManager;
        this.onScheduleChanged = onScheduleChanged;
        setLayout(new BorderLayout(0, 2));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setBackground(Color.WHITE);

        // 날짜 숫자 버튼 (위)
        dayButton = new JButton(String.valueOf(date.getDayOfMonth()));
        dayButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        dayButton.setFocusPainted(true); // 포커스 테두리 표시
        dayButton.setContentAreaFilled(true); // 배경 채우기
        dayButton.setOpaque(true); // 불투명하게
        dayButton.setBackground(new Color(235, 240, 255)); // 연한 파란 배경
        dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dayButton.setMargin(new Insets(3, 3, 3, 3));

        // 입체감 있는 테두리
        dayButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // 요일에 따라 글자색 변경
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            dayButton.setForeground(Color.RED);
        } else {
            dayButton.setForeground(Color.BLACK);
        }

        // 마우스 올릴 때 배경색 효과
        dayButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                dayButton.setBackground(new Color(180, 200, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                dayButton.setBackground(new Color(235, 240, 255));
            }
        });

        // 클릭 시 일정 추가 다이얼로그
        dayButton.addActionListener(e -> showAddEventDialog());

        add(dayButton, BorderLayout.NORTH);

        // 일정 공간 (아래)
        scheduleListPanel = new JPanel();
        scheduleListPanel.setLayout(new BoxLayout(scheduleListPanel, BoxLayout.Y_AXIS));
        scheduleListPanel.setBackground(new Color(250,250,250));
        add(scheduleListPanel, BorderLayout.CENTER);

        refreshEventList();
    }

    private void showAddEventDialog() {
        JTextField titleField = new JTextField();
        JTextField contentField = new JTextField();
        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("10:00");
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("제목 :"));
        panel.add(titleField);
        panel.add(new JLabel("내용 :"));
        panel.add(contentField);
        panel.add(new JLabel("시작시간 :"));
        panel.add(startField);
        panel.add(new JLabel("종료시간 :"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, date + " 할일 추가", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText();
                String content = contentField.getText();
                LocalTime startTime = LocalTime.parse(startField.getText());
                LocalTime endTime = LocalTime.parse(endField.getText());
                scheduleManager.addEvent(title, content, LocalDateTime.of(date, startTime), LocalDateTime.of(date, endTime));
                refreshEventList();
                onScheduleChanged.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 오류: " + ex.getMessage());
            }
        }
    }

    private void showDeleteDialog(ScheduleEvent event) {
        int result = JOptionPane.showConfirmDialog(this,
                "정말로 삭제하시겠습니까?\n" + event.toString(), "할일 삭제", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            scheduleManager.removeEvent(event);
            refreshEventList();
            onScheduleChanged.run();
        }
    }

    public void refreshEventList() {
        scheduleListPanel.removeAll();
        List<ScheduleEvent> events = scheduleManager.getEventsByDate(date.atStartOfDay());
        for (ScheduleEvent event : events) {
            JPanel eventPanel = new JPanel(new BorderLayout());
            eventPanel.setBackground(new Color(250,250,250));

            JLabel eventLabel = new JLabel(event.toString());
            eventLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
            JButton delBtn = new JButton("X");
            delBtn.setMargin(new Insets(0,0,0,0));
            delBtn.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            delBtn.setForeground(Color.RED);
            delBtn.addActionListener(e -> showDeleteDialog(event));
            eventPanel.add(eventLabel, BorderLayout.CENTER);
            eventPanel.add(delBtn, BorderLayout.EAST);
            scheduleListPanel.add(eventPanel);
        }
        scheduleListPanel.revalidate();
        scheduleListPanel.repaint();
    }
}