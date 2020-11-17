/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import view.dataentry.ReadFromUserUtilities;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
                System.out.println("");
            } else if ("trainers".equals(element)) {
                courses.get(answerInt - 1).
                        getTrainers().stream().
                        forEach(System.out::println);
                System.out.println("");
            } else if ("assignments".equals(element)) {
                List<Assignment> allAssignments = new ArrayList<>();
                courses.get(answerInt - 1).
                        getStudents().stream().
                        forEach(i -> i.getAssignments().
                        forEach(allAssignments::add));

                List<Assignment> individualAssignments;
                List<Assignment> teamAssignments;

                individualAssignments = allAssignments.stream().
                        filter(i -> !i.isTeamAssignment()).
                        collect(Collectors.toList());
                teamAssignments = allAssignments.stream().
                        filter(i -> i.isTeamAssignment()).
                        collect(Collectors.toList());

                System.out.println("Individual assignments:");
                individualAssignments.stream().forEach(Assignment::toStringStudent);
                System.out.println("Team assignments:");
                for (Assignment ass : teamAssignments) {
                    ass.toStringStudent();                              //TODO REMOVE DUPLICATE TEAM ASSIGNMENTs TO ONLY SHOW ONCE
                }

                System.out.println("");
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
            
            student.getAssignments().stream().forEach(Assignment::toStringStudent);
            
//            if (!getMultiCourseStudents(courses).contains(student)) {
//                System.out.println("Showing student's assginment:");
//                student.getAssignments().stream().forEach(Assignment::toStringStudent);
//                System.out.println();
//            } else {
//                System.out.println("Showing student's assginment (multiple course student):");
//                for (Course c : courses) {
//                    int studentIndex = c.getStudents().indexOf(student);
//                    if (studentIndex != -1) {
//                        System.out.println(c.toString() + ":");
//                        c.getStudents().get(studentIndex).getAssignments().stream().forEach(Assignment::toStringStudent);
//                        System.out.println();
//                    }
//                }
//                System.out.println();
//            }
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

//        
    }

}