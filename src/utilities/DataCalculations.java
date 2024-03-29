/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.*;

/**
 *
 * @author Leyteris
 */
public class DataCalculations {

    public static void printStudentsPerCourse(ArrayList<Course> courses) {
        printSubElementPerCourse(courses, "students");
    }

    public static void printTrainersPerCourse(ArrayList<Course> courses) {
        printSubElementPerCourse(courses, "trainers");
    }

    public static void printAssignmentsPerCourse(ArrayList<Course> courses) {
        printSubElementPerCourse(courses, "assignments");
    }

    private static void printSubElementPerCourse(ArrayList<Course> courses, String element) {
        int answerInt;
        int courseCounter = 1;

        System.out.println("Enter the number of the course below to show its " + element + ", or q to exit");
        for (Course c : courses) {
            System.out.println(courseCounter + " - " + c);
            courseCounter++;
        }

        answerInt = ReadFromUserUtilities.readNumberOrQuit(1, courseCounter - 1);

        if (answerInt != -1) {
            if ("students".equals(element)) {
                courses.get(answerInt - 1).
                        getStudents().stream().
                        forEach(System.out::println);
                System.out.println();
            } else if ("trainers".equals(element)) {
                courses.get(answerInt - 1).
                        getTrainers().stream().
                        forEach(System.out::println);
                System.out.println();
            } else if ("assignments".equals(element)) {
                do {
                    long courseId = courses.get(answerInt - 1).getId();

                    List<Assignment> allAssignments = new ArrayList<>();
                    courses.get(answerInt - 1).
                            getStudents().stream().
                            forEach(i -> i.getAssignments().stream().
                            filter(k -> k.getCourseId() == courseId).
                            forEach(allAssignments::add));

                    Set<Assignment> individualAssignments = allAssignments.stream().
                            filter(i -> !i.isTeamAssignment()).
                            collect(Collectors.toSet());
                    System.out.println("Individual assignments:");
                    individualAssignments.stream().forEach(i -> System.out.println(i));

                    Set<Assignment> teamAssignments = allAssignments.stream().
                            filter(i -> i.isTeamAssignment()).
                            collect(Collectors.toSet());
                    System.out.println("Team assignments:");
                    teamAssignments.stream().forEach(i -> System.out.println(i));

                    System.out.println();
                    System.out.println("Do you want to submit a student's assignment? (yes/no)");

                    if (ReadFromUserUtilities.readYesOrNo()) {
                        submitAssignment(individualAssignments, teamAssignments);
                    } else {
                        break;
                    }
                } while (true);
            }
        }
    }

    private static void submitAssignment(Set<Assignment> individualAssignments, Set<Assignment> teamAssignments) {
        int assCounter = 1;
        List<Assignment> notSubmittedIndividualAssignments;
        List<Assignment> notSubmittedTeamAssignments;

        System.out.println("Individual assignments:");

        notSubmittedIndividualAssignments = individualAssignments.stream().filter(i -> i.getSubDateTime() == null).collect(Collectors.toList());
        notSubmittedTeamAssignments = teamAssignments.stream().filter(i -> i.getSubDateTime() == null).collect(Collectors.toList());

        for (Assignment ass : notSubmittedIndividualAssignments) {
            System.out.println(assCounter + " - " + ass);
            assCounter++;
        }
        for (Assignment ass : notSubmittedTeamAssignments) {
            System.out.println(assCounter + " - " + ass);
            assCounter++;
        }

        int answer = ReadFromUserUtilities.readNumberOrQuit(1, assCounter - 1);
        int mark;
        if (answer != -1) {
            answer = answer - 1;
            if (answer < notSubmittedIndividualAssignments.size()) {
                System.out.println("Enter assignment submission date and time: (YYYY-MM-DD HH:MM)");
                notSubmittedIndividualAssignments.get(answer).setSubDateTime(ReadFromUserUtilities.readDateTime());

                do {
                    System.out.println("Enter oral grade (less than " + notSubmittedIndividualAssignments.get(answer).getMaxOralMark() + ")");
                    mark = ReadFromUserUtilities.readInt();
                    notSubmittedIndividualAssignments.get(answer).setOralMark(mark);
                } while (mark < 0 || mark > notSubmittedIndividualAssignments.get(answer).getMaxOralMark());

                do {
                    System.out.println("Enter total grade (less than " + notSubmittedIndividualAssignments.get(answer).getMaxTotalMark() + ")");
                    mark = ReadFromUserUtilities.readInt();
                    notSubmittedIndividualAssignments.get(answer).setTotalMark(mark);
                } while (mark < 0 || mark > notSubmittedIndividualAssignments.get(answer).getMaxTotalMark());
            } else {
                answer = answer - notSubmittedIndividualAssignments.size();
                System.out.println("Enter assignment submission date and time: (YYYY-MM-DD HH:MM)");
                notSubmittedTeamAssignments.get(answer).setSubDateTime(ReadFromUserUtilities.readDateTime());

                do {
                    System.out.println("Enter oral grade (less than " + notSubmittedTeamAssignments.get(answer).getMaxOralMark() + ")");
                    mark = ReadFromUserUtilities.readInt();
                    notSubmittedTeamAssignments.get(answer).setOralMark(mark);
                } while (mark < 0 || mark > notSubmittedTeamAssignments.get(answer).getMaxOralMark());

                do {
                    System.out.println("Enter total grade (less than " + notSubmittedTeamAssignments.get(answer).getMaxTotalMark() + ")");
                    mark = ReadFromUserUtilities.readInt();
                    notSubmittedTeamAssignments.get(answer).setTotalMark(mark);
                } while (mark < 0 || mark > notSubmittedTeamAssignments.get(answer).getMaxTotalMark());
            }
        }
    }

    public static ArrayList<Student> getMultiCourseStudents(ArrayList<Course> courses) {
        return getStudentList(courses, true);
    }

    public static ArrayList<Student> getSingleCourseStudents(ArrayList<Course> courses) {
        return getStudentList(courses, false);
    }

    private static ArrayList<Student> getStudentList(ArrayList<Course> courses, boolean duplicateStudent) {
        ArrayList<Student> mixedStudents = new ArrayList<>();
        ArrayList<Student> distinctStudents = new ArrayList<>();
        ArrayList<Student> duplicateStudents = new ArrayList<>();

        for (Course c : courses) {
            mixedStudents.addAll(c.getStudents());
        }

        for (Student t : mixedStudents) {
            if (!distinctStudents.contains(t)) {
                distinctStudents.add(t);
            } else {
                if (!duplicateStudents.contains(t)) {
                    duplicateStudents.add(t);
                }
            }
        }

        return (duplicateStudent ? duplicateStudents : distinctStudents);
    }

    public static void printAssignmentsPerStudent(ArrayList<Student> students, ArrayList<Course> courses) {
        int answerInt;
        int studentCounter = 1;

        System.out.println("Enter the number of the Student below to show his/her assignments, or q to exit");
        for (Student c : students) {
            System.out.println(studentCounter + " - " + c);
            studentCounter++;
        }

        answerInt = ReadFromUserUtilities.readNumberOrQuit(1, studentCounter - 1);

        if (answerInt != -1) {
            Student student = students.get(answerInt - 1);

            student.getAssignments().stream().forEach(i -> System.out.println(i));
        }
    }

    public static void printAssignmentsToSubmitPerWeek(ArrayList<Course> courses) {
        LocalDate date;
        LocalDateTime startDate;
        LocalDateTime endDate;

        System.out.println("Input date to find the assignments that are due the specific week of your date (YYYY-MM-DD), or q to exit");

        date = ReadFromUserUtilities.readDateOrQuit();

        if (!date.equals(LocalDate.parse("0001-01-01"))) {
            while (date.getDayOfWeek() != DayOfWeek.MONDAY) {
                date = date.minusDays(1);
            }

            startDate = date.atStartOfDay();
            endDate = startDate.plusWeeks(1).plusHours(24);

            System.out.println("Students that still have assignments to submit:");
            for (Course c : courses) {
                for (Student s : c.getStudents()) {
                    for (Assignment ass : s.getAssignments()) {
                        if (ass.getDueDateTime().isAfter(startDate) && ass.getDueDateTime().isBefore(endDate) && ass.getSubDateTime() == null) {
                            System.out.println(s.toString());
                        }
                    }
                }
            }
        }
    }
    
}
