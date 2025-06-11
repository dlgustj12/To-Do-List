package To_Do_List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

//하루 단위의 달력의 셀 UI 컴포넌트
//날짜를 하나씩 시각적으로 보여주는 셀
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

        //토일은 휴일이니께~~
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            dayButton.setForeground(Color.RED);
        } else {
            dayButton.setForeground(Color.BLACK);
        }

        // 날짜 에 마우스 올릴 때 배경색 살짝 바뀌는 효과!!
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

        // 스크롤 생성
        JScrollPane scrollPane = new JScrollPane(scheduleListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15); // 스크롤 속도 조정

        add(scrollPane, BorderLayout.CENTER);

        refreshEventList();
    }

    public void refreshEventList() {
        scheduleListPanel.removeAll();
        List<ScheduleEvent> events = scheduleManager.getEventsByDate(date.atStartOfDay());

        //일정 반복
        for (ScheduleEvent event : events) {
            JPanel eventPanel = new JPanel(new BorderLayout());
            eventPanel.setBackground(new Color(250,250,250));

            //셀에 직접 표시되는 텍스트
            String labelText = "[" + event.getStartTime().toLocalTime() + " - " +
                    event.getEndTime().toLocalTime() + "] " + event.getTitle();
            JLabel eventLabel = new JLabel(labelText);
            eventLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));

            JButton delBtn = new JButton("X");

            delBtn.setMargin(new Insets(0,0,0,0));
            delBtn.setFont(new Font("맑은 고딕", Font.BOLD, 10));
            delBtn.setForeground(Color.RED);

            // 삭제 버튼 클릭 시 MessageBox → OK 누르면 일정 삭제
            delBtn.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                        this,
                        "해당 일정을 삭제하시겠습니까?\n" +
                                labelText + "\n" +
                                "내용: " + event.getDescription(),
                        "할일 삭제",
                        JOptionPane.OK_CANCEL_OPTION
                );
                if (result == JOptionPane.OK_OPTION) {
                    scheduleManager.deleteEvent(event);
                    refreshEventList();
                    onScheduleChanged.run();
                }
            });

            eventPanel.add(eventLabel, BorderLayout.CENTER);
            eventPanel.add(delBtn, BorderLayout.EAST);

            scheduleListPanel.add(eventPanel);
        }
        scheduleListPanel.revalidate();
        scheduleListPanel.repaint();
    }

    private void showAddEventDialog() {
        List<ScheduleEvent> events = scheduleManager.getEventsByDate(date.atStartOfDay());

       /* // 1. 일정 개수 제한
        if (events.size() >= 5) {
            JOptionPane.showMessageDialog(this, "일정이 꽉찼습니다!!");
            return;
        }*/

        JTextField titleField = new JTextField();
        JTextField contentField = new JTextField();
        JTextField startField = new JTextField("00:00");
        JTextField endField = new JTextField("00:00");

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("제목 :"));
        panel.add(titleField);
        panel.add(new JLabel("내용 :"));
        panel.add(contentField);
        panel.add(new JLabel("시작시간 :"));
        panel.add(startField);
        panel.add(new JLabel("종료시간 :"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                date + " 할일 추가", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String content = contentField.getText();
            String startText = startField.getText();
            String endText = endField.getText();

            //예외처리 : 제목이나 내용 공백
            if (title.trim().isEmpty() || content.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "제목과 내용을 모두 입력하세요.");
                return;
            }

            //예외처리 : 시간 형식 [--:--]이 아닐때
            if (!startText.matches("\\d{2}:\\d{2}") || !endText.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this, "시간 형식은 [HH:mm] 입니다.");
                return;
            }

            //예외처리 : HH가 24이상 이거나 mm이 60이상일떄
            int startH = Integer.parseInt(startText.substring(0, 2));
            int startM = Integer.parseInt(startText.substring(3, 5));
            int endH = Integer.parseInt(endText.substring(0, 2));
            int endM = Integer.parseInt(endText.substring(3, 5));

            if (startH < 0 || startH > 23 || startM < 0 || startM > 59
                    || endH < 0 || endH > 23 || endM < 0 || endM > 59) {
                JOptionPane.showMessageDialog(this, "시간은 00~23, 분은 00~59 사이여야 합니다.");
                return;
            }

            try {
                LocalTime startTime = LocalTime.parse(startText);
                LocalTime endTime = LocalTime.parse(endText);

                //예외처리 : 시작시간 > 종료시간 일떄
                if (!startTime.isBefore(endTime)) {
                    JOptionPane.showMessageDialog(this, "시작 시간은 종료 시간보다 빨라야 합니다.");
                    return;
                }

                //예외처리 : 다른 일정이랑 시간 중복됐을때
                boolean overlap = false;
                for (ScheduleEvent event : events) {
                    LocalTime existStart = event.getStartTime().toLocalTime();
                    LocalTime existEnd = event.getEndTime().toLocalTime();
                    if (startTime.isBefore(existEnd) && endTime.isAfter(existStart)) {
                        overlap = true;
                        break;
                    }
                }
                if (overlap) {
                    JOptionPane.showMessageDialog(this, "이미 같은 시간대에 일정이 존재합니다.");
                    return;
                }

                //일정 등록
                scheduleManager.addEvent(title, content, LocalDateTime.of(date, startTime), LocalDateTime.of(date, endTime));
                refreshEventList();
                onScheduleChanged.run();

            } catch (Exception ex) {
                // Last 거름망(예기치 못한 오류)
                JOptionPane.showMessageDialog(this, "입력오류!!");
            }
        }
    }
}
