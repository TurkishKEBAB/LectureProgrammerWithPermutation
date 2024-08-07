package Lecture;

/*
 * @author of this code Yigit Okur (23Soft1040)
 * github.com/TurkishKEBAB
 */

package Lecture;

/*
 * @author of this code Yigit Okur (23Soft1040)
 * github.com/TurkishKEBAB
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class CourseSchedule extends JFrame {
   private final List<Course> courses;
   private final List<Course> selectedCourses;
   private int totalCredits;
   private final JPanel schedulePanel;
   private final String[] courseTimes = {
           "08:30 - 09:20", "09:30 - 10:20", "11:30 - 12:20", "12:30 - 13:20", "13:30 - 14:20",
           "14:30 - 15:20", "15:30 - 16:20", "16:30 - 17:20", "17:30 - 18:20", "18:30 - 19:20",
           "19:30 - 20:20"
   };
   private final String[] days = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};

   public CourseSchedule() {
      String username = JOptionPane.showInputDialog(null, "Size verilen kullanıcı adını giriniz: ");
      String password = JOptionPane.showInputDialog(null, "Size verilen şifreyi giriniz:");
      if (!"0".equals(username) || !"0".equals(password)) {
         JOptionPane.showMessageDialog(null, "Verilen ifadeler yanlıştır. Çıkış yapılıyor");
         System.exit(0);
      }

      setTitle("Işık Üniversitesi Ders Programı Oluşturucu Beta_v1");
      setSize(1000, 600);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);

      courses = new ArrayList<>();
      selectedCourses = new ArrayList<>();
      totalCredits = 0;

      JLabel label = new JLabel("Lütfen almak istediğiniz dersleri seçin:");
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(0, 1));
      readCourses();
      courses.sort(Comparator.comparing(Course::getName));
      listCourses(panel);

      JScrollPane scrollPane = new JScrollPane(panel);
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      JButton button = new JButton("Tamam");
      JPanel buttonPanel = new JPanel();

      JButton searchButton = new JButton("Ara");
      searchButton.addActionListener(new SearchActionListener(panel));

      buttonPanel.add(searchButton);

      JButton resetButton = new JButton("Ders Seçimini Sıfırla");
      resetButton.addActionListener(new ResetActionListener(panel));

      buttonPanel.add(resetButton);

      schedulePanel = new JPanel(new GridLayout(days.length, courseTimes.length));
      JScrollPane scheduleScrollPane = new JScrollPane(schedulePanel);
      scheduleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scheduleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      add(label, BorderLayout.NORTH);
      add(scrollPane, BorderLayout.WEST);
      add(scheduleScrollPane, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
      add(button, BorderLayout.EAST);

      button.addActionListener(e -> saveFinalState());

      setVisible(true);
      BufferedImage iconImage = null;

      try {
         URL iconUrl = new URL("https://github.com/TurkishKEBAB/DersProgram-Olusturucu/blob/main/Designer.png?raw=true");
         iconImage = ImageIO.read(iconUrl);
      } catch (IOException ex) {
         ex.printStackTrace();
      }

      if (iconImage != null) {
         setIconImage(iconImage);
      }
   }

   private void listCourses(JPanel panel) {
      for (Course course : courses) {
         if (course.getCredit() != null && (course.getCredit() < 0 || course.getCredit() > 10)) {
            continue;
         }

         String courseCode = course.getCourseCode();
         JCheckBox checkBox = new JCheckBox(courseCode + " - " + course.getName());
         checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
               selectedCourses.add(course);
               if (course.getCredit() != null) {
                  totalCredits += course.getCredit();
               }
            } else {
               selectedCourses.remove(course);
               if (course.getCredit() != null) {
                  totalCredits -= course.getCredit();
               }
            }
            updateSchedule();
         });
         checkBox.setToolTipText("Öğretmen: " + course.getTeacher() + ", Sınıf: " + course.getClassroom() + ", Açıklama: " + course.getDescription());
         panel.add(checkBox);
      }
   }

   private void updateSchedule() {
      schedulePanel.removeAll();
      schedulePanel.setLayout(new GridLayout(days.length, courseTimes.length));
      Map<String, Map<String, List<Course>>> courseMap = new HashMap<>();
      for (String day : days) {
         courseMap.put(day, new HashMap<>());
         for (String time : courseTimes) {
            courseMap.get(day).put(time, new ArrayList<>());
         }
      }

      for (Course course : selectedCourses) {
         if (courseMap.containsKey(course.getDay()) && courseMap.get(course.getDay()).containsKey(course.getTime())) {
            courseMap.get(course.getDay()).get(course.getTime()).add(course);
         }
      }

      for (String day : days) {
         for (String time : courseTimes) {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panel.setPreferredSize(new Dimension(100, 50));
            panel.setLayout(new GridBagLayout());
            List<Course> courseList = courseMap.get(day).get(time);
            if (courseList.isEmpty()) {
               JLabel label = new JLabel();
               panel.add(label);
            } else {
               JLabel label = new JLabel();
               StringBuilder sb = new StringBuilder();
               for (Course course : courseList) {
                  sb.append(course.getName()).append(", ");
               }
               sb.delete(sb.length() - 2, sb.length());
               label.setText(sb.toString());
               panel.add(label, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
            }
            schedulePanel.add(panel);
         }
      }

      schedulePanel.revalidate();
      schedulePanel.repaint();
   }

   private void readCourses() {
      if (!isInternetAvailable()) {
         JOptionPane.showMessageDialog(null, "İnternet bağlantınız yok. Çıkış yapılıyor.");
         System.exit(0);
      }

      try {
         URL url = new URL("https://raw.githubusercontent.com/TurkishKEBAB/DersProgram-Olusturucu/main/Dersler.txt");
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("GET");
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
         String line;
         while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
               String[] parts = line.split(",");
               if (parts.length == 8) {
                  String courseCode = parts[0];
                  String name = parts[1];
                  Integer credit = Integer.parseInt(parts[2]);
                  String teacher = parts[3];
                  String classroom = parts[4];
                  String description = parts[5];
                  String day = parts[6];
                  String time = parts[7];
                  courses.add(new Course(courseCode, name, credit, teacher, classroom, description, day, time));
               }
            }
         }
         reader.close();
      } catch (IOException ex) {
         JOptionPane.showMessageDialog(null, "Ders verileri okunamadı. Çıkış yapılıyor.");
         ex.printStackTrace();
         System.exit(0);
      }
   }

   private boolean isInternetAvailable() {
      try (Socket socket = new Socket()) {
         socket.connect(new InetSocketAddress("google.com", 80), 3000);
         return true;
      } catch (IOException e) {
         return false;
      }
   }

   private void saveFinalState() {
      try (PrintWriter writer = new PrintWriter(new FileWriter("CourseSchedule.txt"))) {
         writer.println("Seçilen Dersler:");
         for (Course course : selectedCourses) {
            writer.printf("%s - %s (%d Kredi)\n", course.getCourseCode(), course.getName(), course.getCredit());
         }
         writer.printf("Toplam Kredi: %d\n", totalCredits);
         writer.println("\nDers Programı:");
         for (Component component : schedulePanel.getComponents()) {
            JPanel panel = (JPanel) component;
            JLabel label = (JLabel) panel.getComponent(0);
            writer.println(label.getText());
         }
      } catch (IOException e) {
         JOptionPane.showMessageDialog(null, "Ders programı kaydedilirken bir hata oluştu.");
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(CourseSchedule::new);
   }

   private class SearchActionListener implements ActionListener {
      private final JPanel panel;

      public SearchActionListener(JPanel panel) {
         this.panel = panel;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         String searchText = JOptionPane.showInputDialog(null, "Aramak istediğiniz dersin kodunu veya adını girin:");
         Component[] components = panel.getComponents();
         for (Component component : components) {
            if (component instanceof JCheckBox) {
               JCheckBox checkBox = (JCheckBox) component;
               if (searchText != null && !searchText.isEmpty()) {
                  checkBox.setVisible(checkBox.getText().toLowerCase().contains(searchText.toLowerCase()));
               } else {
                  checkBox.setVisible(true);
               }
            }
         }
      }
   }

   private class ResetActionListener implements ActionListener {
      private final JPanel panel;

      public ResetActionListener(JPanel panel) {
         this.panel = panel;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         selectedCourses.clear();
         totalCredits = 0;
         Component[] components = panel.getComponents();
         for (Component component : components) {
            if (component instanceof JCheckBox checkBox) {
               checkBox.setSelected(false);
            }
         }
         updateSchedule();
      }
   }
}

