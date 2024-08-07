package Lecture;

/*
 * @author of this code Yigit Okur (23Soft1040)
 * github.com/TurkishKEBAB
 */


public class Course {
   private final String courseCode;
   private final String name;
   private final Integer credit;
   private final String teacher;
   private final String classroom;
   private final String description;
   private final String day;
   private final String time;

   public Course(String courseCode, String name, Integer credit, String teacher, String classroom, String description, String day, String time) {
      this.courseCode = courseCode;
      this.name = name;
      this.credit = credit;
      this.teacher = teacher;
      this.classroom = classroom;
      this.description = description;
      this.day = day;
      this.time = time;
   }

   public String getCourseCode() {
      return courseCode;
   }

   public String getName() {
      return name;
   }

   public Integer getCredit() {
      return credit;
   }

   public String getTeacher() {
      return teacher;
   }

   public String getClassroom() {
      return classroom;
   }

   public String getDescription() {
      return description;
   }

   public String getDay() {
      return day;
   }

   public String getTime() {
      return time;
   }
}
