package service;

import model.Case;
import model.Medic;
import model.Patient;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbService {
    private static final String path = "resources/db";

    private static DbService instance = null;
    private List<User> users;
    private List<Case> cases;
    private Map<Patient, Medic> requests;

    //singleton pattern
    @SuppressWarnings("unchecked")
    private DbService() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            this.users = (List<User>)in.readObject();
            this.cases = (List<Case>)in.readObject();
            this.requests = (Map<Patient, Medic>)in.readObject();
        } catch (IOException e) {
            this.users = new ArrayList<>();
            this.cases = new ArrayList<>();
            this.requests = new HashMap<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void store() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(users);
            out.writeObject(cases);
            out.writeObject(requests);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DbService getInstance() {
        if (instance == null)
            instance = new DbService();
        return instance;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Case> getCases() {
        return cases;
    }

    public Map<Patient, Medic> getRequests() {
        return requests;
    }

    public void updateUsers(List<User> users) {
        this.users = users;
        store();
    }

    public void updateCases(List<Case> cases) {
        this.cases = cases;
        store();
    }

    public void updateRequests(Map<Patient, Medic> requests) {
        this.requests = requests;
        store();
    }
}
