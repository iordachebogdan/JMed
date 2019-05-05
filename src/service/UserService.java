package service;

import model.*;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private static UserService instance = null;
    private int currentId;
    private Map<String, User> users;
    private Map<Integer, User> usersById;

    //singleton pattern
    private UserService() {
        List<User> userList = DbService.getInstance().getUsers();
        this.users = new TreeMap<>();
        this.usersById = new TreeMap<>();
        for (User user : userList) {
            this.users.put(user.getUsername(), user);
            this.usersById.put(user.getId(), user);
        }
        if (this.usersById.isEmpty())
            this.currentId = 0;
        else
            this.currentId = Collections.max(this.usersById.keySet());
    }

    public static UserService getInstance() {
        if (instance == null)
            instance = new UserService();
        return instance;
    }

    private void save() {
        DbService.getInstance()
                .updateUsers(new ArrayList<>(users.values()));
    }

    public int registerUser(String username, String password, UserDetails userDetails, UserTypeEnum type) {
        if (users.containsKey(username))
            throw new IllegalArgumentException("Username already exists");
        currentId++;
        if (type == UserTypeEnum.MEDIC)
            users.put(username, new Medic(currentId, username, AuthenticationService.hashFunction(password),
                    userDetails));
        else
            users.put(username, new Patient(currentId, username, AuthenticationService.hashFunction(password),
                    userDetails));
        usersById.put(currentId, users.get(username));
        save();
        return currentId;
    }

    public User getUserById(int id) {
        return usersById.getOrDefault(id, null);
    }

    public User getUserByUsername(String username) {
        return users.getOrDefault(username, null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Medic> getMedics() {
        return users.values().stream()
                .filter(u -> u instanceof Medic)
                .map(u -> (Medic)u)
                .collect(Collectors.toList());
    }

    public List<Patient> getPatients() {
        return users.values().stream()
                .filter(u -> u instanceof Patient)
                .map(u -> (Patient)u)
                .collect(Collectors.toList());
    }

    public void addSpecializationToMedic(int medicId, Specialization specialization) {
        Medic medic = (Medic)getUserById(medicId);
        if (!medic.getSpecializations().contains(specialization))
            medic.getSpecializations().add(specialization);
        save();
    }
}
