package com.panaceum.dao;

import com.google.gson.Gson;
import com.panaceum.model.Doctor;
import com.panaceum.model.Medicine;
import com.panaceum.model.User;
import com.panaceum.model.Prescription;
import com.panaceum.util.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

public class DoctorDao {

    private static DatabaseConnection connection = new DatabaseConnection();
    private UserDao userDao = new UserDao();

    public Response getAll(User user) {
        if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }

        List<Doctor> doctors = new ArrayList<>();
        Statement statement;
        ResultSet resultSet;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT doctorId, pesel, firstName, lastName FROM doctorView");

            while (resultSet.next()) {
                Doctor doctor = new Doctor();

                doctor.setId(resultSet.getInt("doctorId"));
                doctor.setPesel(resultSet.getString("pesel"));
                doctor.setFirstName(resultSet.getString("firstName"));
                doctor.setLastName(resultSet.getString("lastName"));

                doctors.add(doctor);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            connection.closeConnection();
            return Response.serverError().build();
        }
        connection.closeConnection();

        Gson gson = new Gson();
        return Response.ok(gson.toJson(doctors)).build();
    }

    public Response getById(User user, int id) {
        if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }

        Doctor doctor = new Doctor();
        Statement statement;
        ResultSet resultSet;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM doctorView WHERE doctorId = " + id);

            while (resultSet.next()) {
                doctor.setId(resultSet.getInt("doctorId"));
                doctor.setSpeciality(resultSet.getString("speciality"));
                doctor.setLicenceNumber(resultSet.getString("licenceNumber"));
                doctor.setPesel(resultSet.getString("pesel"));
                doctor.setFirstName(resultSet.getString("firstName"));
                doctor.setLastName(resultSet.getString("lastName"));
                doctor.setPhone(resultSet.getString("phone"));
                doctor.setEmail(resultSet.getString("email"));
                doctor.setAddressId(resultSet.getInt("addressId"));
                doctor.setCity(resultSet.getString("city"));
                doctor.setStreet(resultSet.getString("street"));
                doctor.setBuildingNumber(resultSet.getString("buildingNumber"));
                doctor.setFlatNumber(resultSet.getString("flatNumber"));
                doctor.setZipCode(resultSet.getString("zipCode"));
                doctor.setUserId(resultSet.getInt("userId"));
                doctor.setLogin(resultSet.getString("login"));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            connection.closeConnection();
            return Response.serverError().build();
        }
        connection.closeConnection();

        if (doctor.getId() == 0) {
            return Response.status(404).entity("No such doctor found").build();
        }

        Gson gson = new Gson();
        return Response.ok(gson.toJson(doctor)).build();
    }

    public Response getPrescriptions(User user, int doctorId) {
        if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }

        List<Prescription> prescriptions = new ArrayList<>();
        Statement statement;
        ResultSet resultSet;
        int cond = 0,
                i = 0;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            String[] sql = {"SELECT * FROM prescriptionView WHERE doctorId = " + doctorId,
                            "SELECT * FROM prescriptionExcerptView WHERE doctorId = " + doctorId};
            while ((cond == 0) || (i < 2)) {
                resultSet = statement.executeQuery(sql[i++]);

                while (resultSet.next()) {
                    cond++;
                    Prescription prescription = new Prescription();

                    prescription.setId(resultSet.getInt("prescriptionId"));
                    prescription.setDosage(resultSet.getString("dosage"));
                    prescription.setPrescriptionDate(resultSet.getString("prescriptionDate"));
                    prescription.setExpiryDate(resultSet.getString("expiryDate"));
                    prescription.setMedicineId(resultSet.getInt("medicineId"));
                    prescription.setMedicineName(resultSet.getString("medicineName"));
                    prescription.setActiveSubstance(resultSet.getString("activeSubstance"));
                    prescription.setTherapyPlanId(resultSet.getInt("therapyPlanId"));
                    prescription.setExcerptId(resultSet.getInt("excerptId"));
                    prescription.setDoctorid(resultSet.getInt("doctorid"));
                    prescription.setPatientId(resultSet.getInt("patientId"));
                    prescription.setPatientPesel(resultSet.getString("patientPesel"));
                    prescription.setPatientFirstName(resultSet.getString("patientFirstName"));
                    prescription.setPatientLastName(resultSet.getString("patientLastName"));

                    prescriptions.add(prescription);
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            connection.closeConnection();
            return Response.serverError().build();
        }
        connection.closeConnection();

        if (cond == 0) return Response.status(404).entity("No such doctor").build();

        Gson gson = new Gson();
        return Response.ok(gson.toJson(prescriptions)).build();
    }

    //tymczasowo, trzeba znaleźć lepsze rozwiązanie
    private Response getPrescriptionsFromExcerpt(User user, int doctorId) {
        /*if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }*/

        List<Prescription> prescriptions = new ArrayList<>();
        Statement statement;
        ResultSet resultSet;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM prescriptionExcerptView WHERE doctorId = " + doctorId);

            while (resultSet.next()) {
                System.err.println("to");
                Prescription prescription = new Prescription();

                prescription.setId(resultSet.getInt("prescriptionId"));
                prescription.setDosage(resultSet.getString("dosage"));
                prescription.setPrescriptionDate(resultSet.getString("prescriptionDate"));
                prescription.setExpiryDate(resultSet.getString("expiryDate"));
                prescription.setMedicineId(resultSet.getInt("medicineId"));
                prescription.setMedicineName(resultSet.getString("medicineName"));
                prescription.setActiveSubstance(resultSet.getString("activeSubstance"));
                prescription.setTherapyPlanId(resultSet.getInt("therapyPlanId"));
                prescription.setExcerptId(resultSet.getInt("excerptId"));
                prescription.setDoctorid(resultSet.getInt("doctorid"));
                prescription.setPatientId(resultSet.getInt("patientId"));
                prescription.setPatientPesel(resultSet.getString("patientPesel"));
                prescription.setPatientFirstName(resultSet.getString("patientFirstName"));
                prescription.setPatientLastName(resultSet.getString("patientLastName"));

                prescriptions.add(prescription);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            connection.closeConnection();
            return Response.serverError().build();
        }
        connection.closeConnection();

        try {
            if (prescriptions.get(0).getId() == 0) {
                return Response.status(404).entity("No prescriptions for doctor found").build();
            }
        } catch (IndexOutOfBoundsException e) {
            System.err.println("to");
            System.out.println(e.toString());
            connection.closeConnection();
            return Response.status(404).entity("No prescriptions for doctor found").build();
        }

        Gson gson = new Gson();
        return Response.ok(gson.toJson(prescriptions)).build();
    }

    public Response add(User user, Doctor doctor) {
        if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }
        if (!userDao.checkPrivileges(user.getLogin()).equals("admin")) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }

        Statement statement;
        ResultSet resultSet;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT addDoctor('" + doctor.getSpeciality() + "', '" + doctor.getLicenceNumber()
                    + "', '" + doctor.getPesel() + "', '" + doctor.getFirstName() + "', '" + doctor.getLastName()
                    + "', '" + doctor.getPhone() + "', '" + doctor.getEmail() + "', '" + doctor.getCity()
                    + "', '" + doctor.getStreet() + "', '" + doctor.getBuildingNumber() + "', '" + doctor.getFlatNumber()
                    + "', '" + doctor.getZipCode() + "', '" + doctor.getLogin() + "')");

            while (resultSet.next()) {
                String result = resultSet.getString(1);
                doctor.setId(Integer.parseInt(result.substring(1, result.indexOf(","))));
                doctor.setPassword(result.substring(result.indexOf(",") + 1, result.indexOf(")")));
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            connection.closeConnection();

            if (ex.toString().contains("login")) {
                return Response.status(406).entity("Login already exist in DB").build();
            }
            return Response.serverError().build();
        }

        connection.closeConnection();

        if (doctor.getId() == 0) {
            return Response.status(406).entity("Doctor already exist in DB").build();
        }

        return Response.ok("{\"doctorId\":" + doctor.getId()
                + ", \"login\":\"" + doctor.getLogin() + "\", \"password\":\"" + doctor.getPassword() + "\"}").build();
    }

    public Response update(User user, Doctor doctor) {
        if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }

        Statement statement;
        ResultSet resultSet;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT updateDoctor('" + doctor.getLogin() + "', '" + doctor.getPhone() + "', '" + doctor.getEmail() + "', '" + doctor.getCity()
                    + "', '" + doctor.getStreet() + "', '" + doctor.getBuildingNumber() + "', '" + doctor.getFlatNumber()
                    + "', '" + doctor.getZipCode() + "')");
        } catch (SQLException ex) {
            System.out.println(ex.toString());
            connection.closeConnection();
            return Response.serverError().build();
        }

        connection.closeConnection();
        return Response.ok().build();
    }

    public Response delete(User user, int id) {
        if (!userDao.validate(user)) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }
        if (!userDao.checkPrivileges(user.getLogin()).equals("admin")) {
            return Response.status(403).entity("User doesn't have necessary permissions").build();
        }

        Statement statement;

        try {
            connection.establishConnection();
            statement = connection.getConnection().createStatement();
            statement.executeQuery("SELECT deleteDoctor(" + id + ")");
        } catch (SQLException ex) {
            if (!ex.toString().contains("Zapytanie nie zwróciło żadnych wyników")) {
                System.out.println(ex.toString());
                connection.closeConnection();
                return Response.serverError().build();
            }
        }

        connection.closeConnection();
        return Response.ok().build();
    }

}
