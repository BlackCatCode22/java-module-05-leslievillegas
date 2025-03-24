import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZooApp {
    public static void main(String[] args) {
        System.out.println("Welcome to my Zoo Program!");

        // List to store animals and map to count species
        List<Animal> animals = new ArrayList<>();
        Map<String, Integer> speciesCount = new HashMap<>();

        // File path for animal names and arriving animals
        String filePath = "animalNames.txt";
        AnimalNameListsWrapper animalLists = Utilities.createAnimalNameLists(filePath);

        // Lists of animal names
        LinkedList<String> listOfHyenaNames = animalLists.getHyenaNameList();
        LinkedList<String> listOfLionNames = animalLists.getLionNameList();
        LinkedList<String> listOfTigerNames = animalLists.getTigerNameList();
        LinkedList<String> listOfBearNames = animalLists.getBearNameList();

        // Print the animal names to the console
        printAnimalNames("Hyena", listOfHyenaNames);
        printAnimalNames("Lion", listOfLionNames);
        printAnimalNames("Tiger", listOfTigerNames);
        printAnimalNames("Bear", listOfBearNames);

        // Process arriving animals
        processArrivingAnimals("arrivingAnimals.txt", listOfHyenaNames, listOfLionNames, listOfTigerNames, listOfBearNames, animals, speciesCount);

        // Write the report to a file
        writeReport("newAnimals.txt", animals, speciesCount);
    }

    // Method to print animal names
    private static void printAnimalNames(String species, List<String> names) {
        System.out.println("\n" + species + " Names:");
        for (String name : names) {
            System.out.println(name);
        }
    }

    // Method to process the arriving animals from the file
    private static void processArrivingAnimals(String filePath, LinkedList<String> listOfHyenaNames, LinkedList<String> listOfLionNames, LinkedList<String> listOfTigerNames, LinkedList<String> listOfBearNames, List<Animal> animals, Map<String, Integer> speciesCount) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Regular expression to match the pattern "X year old Y species"
            Pattern pattern = Pattern.compile("(\\d+) year old (male|female) (\\w+)");

            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                // Match the line with the regex
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // Extract age and species
                    int age = Integer.parseInt(matcher.group(1));  // Age is in the first captured group
                    String species = matcher.group(3);  // Species is in the third captured group

                    // Get the name based on the species
                    String name = getNameForSpecies(species, listOfHyenaNames, listOfLionNames, listOfTigerNames, listOfBearNames);

                    // Create the animal object based on species
                    Animal animal = createAnimal(species, name, age);

                    if (animal != null) {
                        animals.add(animal);
                        speciesCount.put(species, speciesCount.getOrDefault(species, 0) + 1);
                    }
                } else {
                    System.err.println("Invalid line format: " + line); // Handle the case when the line doesn't match the pattern
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading arriving animals file: " + e.getMessage());
        }
    }

    // Method to get the name based on the species
    private static String getNameForSpecies(String species, LinkedList<String> hyenaNames, LinkedList<String> lionNames, LinkedList<String> tigerNames, LinkedList<String> bearNames) {
        return switch (species) {
            case "Hyena" -> !hyenaNames.isEmpty() ? hyenaNames.remove(0) : "Unnamed Hyena";
            case "Lion" -> !lionNames.isEmpty() ? lionNames.remove(0) : "Unnamed Lion";
            case "Tiger" -> !tigerNames.isEmpty() ? tigerNames.remove(0) : "Unnamed Tiger";
            case "Bear" -> !bearNames.isEmpty() ? bearNames.remove(0) : "Unnamed Bear";
            default -> "Unknown";
        };
    }

    // Method to create an animal based on species, name, and age
    private static Animal createAnimal(String species, String name, int age) {
        return switch (species) {
            case "Hyena" -> new Hyena(name, age);
            case "Lion" -> new Lion(name, age);
            case "Tiger" -> new Tiger(name, age);
            case "Bear" -> new Bear(name, age);
            default -> null;
        };
    }

    // Method to write the report to a file
    private static void writeReport(String fileName, List<Animal> animals, Map<String, Integer> speciesCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String species : speciesCount.keySet()) {
                writer.write(species + " (Total: " + speciesCount.get(species) + ")\n");
                for (Animal animal : animals) {
                    if (animal.getSpecies().equals(species)) {
                        writer.write("  - " + animal.getName() + ", Age: " + animal.getAge() + "\n");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Animal {
    private final String name;
    private final int age;
    private final String species;

    public Animal(String name, int age, String species) {
        this.name = name;
        this.age = age;
        this.species = species;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getSpecies() { return species; }
}

class Hyena extends Animal {
    public Hyena(String name, int age) { super(name, age, "Hyena"); }
}

class Lion extends Animal {
    public Lion(String name, int age) { super(name, age, "Lion"); }
}

class Tiger extends Animal {
    public Tiger(String name, int age) { super(name, age, "Tiger"); }
}

class Bear extends Animal {
    public Bear(String name, int age) { super(name, age, "Bear"); }
}

class AnimalNameListsWrapper {
    private final LinkedList<String> hyenaNameList = new LinkedList<>();
    private final LinkedList<String> lionNameList = new LinkedList<>();
    private final LinkedList<String> tigerNameList = new LinkedList<>();
    private final LinkedList<String> bearNameList = new LinkedList<>();

    public LinkedList<String> getHyenaNameList() { return hyenaNameList; }
    public LinkedList<String> getLionNameList() { return lionNameList; }
    public LinkedList<String> getTigerNameList() { return tigerNameList; }
    public LinkedList<String> getBearNameList() { return bearNameList; }
}

class Utilities {
    public static AnimalNameListsWrapper createAnimalNameLists(String filePath) {
        AnimalNameListsWrapper wrapper = new AnimalNameListsWrapper();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSpecies = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.endsWith("Names:")) {
                    currentSpecies = line.split(" ")[0];
                } else if (!line.isEmpty()) {

                    String[] names = line.split(",");
                    for (String name : names) {
                        name = name.trim();
                        switch (currentSpecies) {
                            case "Hyena" -> wrapper.getHyenaNameList().add(name);
                            case "Lion" -> wrapper.getLionNameList().add(name);
                            case "Tiger" -> wrapper.getTigerNameList().add(name);
                            case "Bear" -> wrapper.getBearNameList().add(name);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wrapper;
    }
}

