package com.gmail.dimabah;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class DatabaseManager {
    private EntityManager em;
    private Scanner sc = new Scanner(System.in).useLocale(Locale.ENGLISH);

    public DatabaseManager() {
    }

    public void showInterface() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("Apartments");
            em = emf.createEntityManager();
            try {
                do {
                    System.out.println("*".repeat(50));
                    System.out.print("""
                            Enter item number:
                              1: add apartment
                              2: add random apartments
                              3: view all apartments
                              4: find apartments
                              5: exit
                            ->\s""");
                } while (processInputDataFromInterface());
            } finally {
                em.close();
                emf.close();
                sc.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean processInputDataFromInterface() {
        String choice = sc.nextLine();
        boolean result = true;
        switch (choice) {
            case "1" -> result = addApartment(false);
            case "2" -> result = addApartment(true);
            case "3" -> viewApartments();
            case "4" -> showSearchInterface();
            case "5" -> {
                return false;
            }
            default -> result = false;
        }
        if (!result) {
            System.out.println("Incorrect entered data, try again");
        }
        return true;
    }

    private boolean addApartment(boolean random) {
        try {
            Apartment[] apartments;
            if (!random) {
                apartments = requestDataToAdd();
            } else {
                apartments = requestRandomDataToAdd();
            }
            em.getTransaction().begin();
            try {
                for (var i : apartments) {
                    em.persist(i);
                }
                em.getTransaction().commit();
            } catch (Exception ex) {
                em.getTransaction().rollback();
                ex.printStackTrace();
                System.out.println("Error, problem with database");
            }
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    private Apartment[] requestDataToAdd() {
        System.out.print("Enter city/town/village in which the apartment is located: ");
        String city = sc.nextLine();
        System.out.print("Enter the district in which the apartment is located: ");
        String district = sc.nextLine();
        System.out.print("Enter the address of the apartment: ");
        String address = sc.nextLine();
        System.out.print("Enter the area of the apartment: ");
        double area = Double.parseDouble(sc.nextLine());
        System.out.print("Enter number of rooms: ");
        int room = Integer.parseInt(sc.nextLine());
        System.out.print("Enter the price of the apartment: ");
        double price = Double.parseDouble(sc.nextLine());

        return new Apartment[]{new Apartment(city, district, address, area, room, price)};
    }

    private Apartment[] requestRandomDataToAdd() {
        System.out.print("Enter the number of apartments to be generated: ");
        int number = Integer.parseInt(sc.nextLine());
        Apartment[] apartments = new Apartment[number];
        for (int i = 0; i < apartments.length; i++) {
            apartments[i] = generateApartment();
        }
        return apartments;
    }

    private Apartment generateApartment() {
        Random random = new Random();
        String[] cities = {"Kyiv", "Kharkiv"};
        String[][] districts = {
                {"Darnytskyi", "Desnianskyi", "Dniprovskyi",
                        "Dniprovskyi", "Dniprovskyi", "Pecherskyi", "Podilskyi",
                        "Shevchenkivskyi", "Solomianskyi", "Sviatoshynskyi"},
                {"Shevchenkivskyi", "Novobavarskyi", "Kyivskyi",
                        "Slobidskyi", "Kholodnohirskyi", "Saltivskyi",
                        "Nemyshlianskyi", "Industrialnyi", "Osnovianskyi"}};
        String[] streets = {"Lesya Ukrainka Street", "Bogdan Khmelnitsky Street", "Oles Honchar Street"};

        int temp = random.nextInt(cities.length);
        String city = cities[temp];
        String district = districts[temp][random.nextInt(districts[temp].length)];
        String address = streets[random.nextInt(streets.length)];
        int room = random.nextInt(1, 5);
        double area = random.nextDouble(room * 18 + 18, room * 18 + 26);
        area = Math.ceil(area);
        double price = 50000 * area / (temp + 1);

        return new Apartment(city, district, address, area, room, price);
    }

    private void viewApartments() {
        Query query = em.createQuery("SELECT a FROM Apartment a", Apartment.class);
        viewApartments(query);
    }

    private void viewApartments(Query query) {
        List<Apartment> list = (List<Apartment>) query.getResultList();
        if (list.size() == 0) {
            System.out.println("There are no such apartments in the database");
        } else {
            System.out.println("-".repeat(150));
            System.out.println(list.get(0).getHeader());
            System.out.println("-".repeat(150));
            for (var a : list) {
                System.out.println(a.getFormattedObject());
            }
            System.out.println("-".repeat(150));
        }
    }

    private void showSearchInterface() {
        do {
            System.out.println("?".repeat(50));
            System.out.print("""
                    Enter item number:
                      1: search by city/town/village
                      2: search by district
                      3: search by address
                      4: search by area
                      5: search by number of rooms
                      6: search by price
                      7: search by multiple parameters
                      8: return
                    ->\s""");
        } while (processInputDataForSearch());
    }

    private boolean processInputDataForSearch() {
        String choice = sc.nextLine();
        String result = switch (choice) {
            case "1" -> searchByName("city");
            case "2" -> searchByName("district");
            case "3" -> searchByName("address");
            case "4" -> searchByValue("area");
            case "5" -> searchByValue("room");
            case "6" -> searchByValue("price");
            case "7" -> searchByParameters();
            case "8" -> "return";
            default -> null;
        };
        if ("return".equals(result)) {
            return false;
        }
        if (result == null) {
            System.out.println("Incorrect entered data, try again");
        } else if (!result.equals("")) {
            Query query = em.createQuery("SELECT x FROM Apartment x WHERE" + result);
            viewApartments(query);
        }
        return true;
    }

    private String searchByParameters() {
        StringBuilder sb = new StringBuilder();
        String result;
        System.out.println("Enter value or nothing to skip parameter");
        for (var field : Apartment.class.getDeclaredFields()) {
            result = createStringForSearchByParameters(field);
            if (result == null) {
                return null;
            }
            if(sb.length()!=0 && !result.equals("")){
                sb.append(" AND");
            }
            sb.append(result);
        }
        return sb.toString();
    }

    private String createStringForSearchByParameters(Field field) {
        if (!field.isAnnotationPresent(Id.class)) {
            System.out.println("Search by "+field.getName());
            if (field.getType().getSimpleName().equals("String")) {
                return searchByName(field.getName());
            } else {
                return searchByValue(field.getName());
            }
        }
        return "";
    }

    private String searchByName(String name) {
        System.out.print("Enter the full name or part of it: ");
        String result = sc.nextLine();
        if (result.equals("")) {
            return "";
        }
        result = " x." + name + " LIKE '%" + result + "%'";
        return result;
    }

    private String searchByValue(String name) {
        System.out.print("Enter the required value or use symbols '<', '>' or '-' (e.g. '<4', '>1', '2-3'): ");
        String result = sc.nextLine().replaceAll(" ", "");
        if (result.equals("")) {
            return "";
        }
        String startValue;
        String lastValue;
        if (result.matches("[<>][0-9]+[.]?[0-9]*")) {
            result = " x." + name + " " + result;
        } else if (result.matches("[0-9]+[.]?[0-9]*-[0-9]+[.]?[0-9]*")) {
            startValue = result.substring(0, result.indexOf('-'));
            lastValue = result.substring(result.indexOf('-') + 1);
            result = " x." + name + " BETWEEN " + startValue + " AND " + lastValue;
        } else if (result.matches("[0-9]+[.]?[0-9]*")) {
            result = " x." + name + " = " + result;
        } else {
            return null;
        }

        return result;
    }
}
