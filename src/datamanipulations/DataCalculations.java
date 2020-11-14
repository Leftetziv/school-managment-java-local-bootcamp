/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamanipulations;

import dataentry.RemoveDuplicates;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
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
        Scanner sc = new Scanner(System.in);
        String answer;
        int courseCounter = 1;

        System.out.println("Enter the number of the course below to show its " + element + ", or q to exit");
        for (Course c : courses) {
            System.out.println(courseCounter + " - " + c);
            courseCounter++;
        }

        do {
            answer = sc.nextLine();

            if ("q".equalsIgnoreCase(answer)) {
                break;
            }

            try {
                if (Integer.parseInt(answer) > 0 && Integer.parseInt(answer) < courseCounter) {
                    if ("students".equals(element)) {
                        courses.get(Integer.parseInt(answer) - 1).
                                getStudents().stream().
                                forEach(System.out::println);
                        System.out.println("");
                    } else if ("trainers".equals(element)) {
                        courses.get(Integer.parseInt(answer) - 1).
                                getTrainers().stream().
                                forEach(System.out::println);
                        System.out.println("");
                    } else if ("assignments".equals(element)) {
                        List<Assignment> assignments = new ArrayList<>();
                        courses.get(Integer.parseInt(answer) - 1).
                                getStudents().stream().
                                forEach(i -> i.getAssignments().
                                forEach(assignments::add));

                        List<Assignment> individualAssignments = new ArrayList<>();
                        List<Assignment> teamAssignments = new ArrayList<>();

                        individualAssignments = assignments.stream().
                                filter(i -> !i.isTeamAssignment()).
                                collect(Collectors.toList());
                        teamAssignments = assignments.stream().
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
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a course number, or q to exit");
                continue;
            }
            System.out.println("Enter the number of the course above to show its " + element + ", or q to exit");
        } while (!"q".equalsIgnoreCase(answer));
    }

    public static ArrayList<Student> getMultiCourseStudents(ArrayList<Course> courses) {
        return getStudentList(courses, true);
    }

    public static ArrayList<Student> getSingleCourseStudents(ArrayList<Course> courses) {
        return getStudentList(courses, false);
    }

    private static ArrayList<Student> getStudentList(ArrayList<Course> courses, boolean duplicate) {
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

        return (duplicate ? duplicateStudents : distinctStudents);
    }

    public static void printAssignmentsPerStudent(ArrayList<Student> students, ArrayList<Course> courses) {
        Scanner sc = new Scanner(System.in);
        String answer;
        int studentCounter = 1;

        System.out.println("Enter the number of the Student below to show his/her assignments, or q to exit");
        for (Student c : students) {
            System.out.println(studentCounter + " - " + c);
            studentCounter++;
        }

        do {
            answer = sc.nextLine();
            if ("q".equalsIgnoreCase(answer)) {
                break;
            }
            try {
                if (Integer.parseInt(answer) > 0 && Integer.parseInt(answer) < studentCounter) {

                    Student student = students.get(Integer.parseInt(answer) - 1);
                    if (!getMultiCourseStudents(courses).contains(student)) {
                        System.out.println("Showing student's assginment:");
                        student.getAssignments().stream().forEach(Assignment::toStringStudent);
                        System.out.println();
                    } else {
                        System.out.println("Showing student's assginment (multiple course student):");
                        for (Course c : courses) {
                            int studentIndex = c.getStudents().indexOf(student);
                            if (studentIndex != -1) {
                                System.out.println(c.toString() + ":");
                                c.getStudents().get(studentIndex).getAssignments().stream().forEach(Assignment::toStringStudent);
                                System.out.println();
                            }
                        }
                        System.out.println();
                    }
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a student number, or q to exit");
                continue;
            }
            System.out.println("Enter the number of the student above to show its assignments, or q to exit");
        } while (!"q".equalsIgnoreCase(answer));

    }

    public static void printAssignmentsToSubmitPerWeek(ArrayList<Course> courses) {
        Scanner sc = new Scanner(System.in);
        String answer;
        LocalDate date;
        LocalDateTime startDate;
        LocalDateTime endDate;

        do {
            System.out.println("Input date to find the assignments that are due the specific week of your date (YYYY-MM-DD), or q to exit");
            answer = sc.nextLine();
            if ("q".equalsIgnoreCase(answer)) {
                break;
            }

            try {
                date = LocalDate.parse(answer);

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

                break;
            } catch (Exception e) {
                System.out.println("Date must be in format YYYY-MM-DD");
                continue;
            }

        } while (!"q".equalsIgnoreCase(answer));

    }

}
