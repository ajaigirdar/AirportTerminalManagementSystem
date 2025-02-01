package com.airport.data;

import com.airport.domain.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSVUtil {
    private static final String filePath = "data/reservations.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String HEADER = "flightNumber,departureDate,ticketPrice,passengerName,passportNumber,aircraftModel,aircraftType";

    public static void saveReservationsToCSV(HashMap<String, ArrayList<Passenger>> reservations, HashMap<String,
            Flight> flights) {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {

            if (!file.exists() || file.length() == 0) {
                writer.append(HEADER);
                writer.newLine();
            }

            for (Map.Entry<String, ArrayList<Passenger>> entry : reservations.entrySet()) {
                String flightNumber = entry.getKey();
                ArrayList<Passenger> passengers = entry.getValue();

                Flight flight = flights.get(flightNumber); // get the flight object associated with the flight number

                if (flight != null){
                    for (Passenger passenger : passengers) {
                        String line = String.format("%s,%s,%s,%s,%s,%s,%s\n",
                                flightNumber.trim(),
                                flight.getDepartureDate().format(DATE_FORMAT).trim(),
                                flight.getTicketPrice().toString().trim(),
                                passenger.getName().trim(),
                                passenger.getPassportNumber().trim(),
                                flight.getAircraft().getModel().trim(),
                                flight.getAircraft().getClass().getSimpleName().trim());
                        writer.append(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());;
        }
    }

    public static HashMap<String, ArrayList<Passenger>> loadReservationsFromCSV() {
        HashMap<String, ArrayList<Passenger>> reservations = new HashMap<>();
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            System.err.println("No reservations found.");
            return reservations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length < 7) continue; // skips any lines with < 7 columns

                String flightNumber = values[0].trim();
                LocalDate departureDate = LocalDate.parse(values[1].trim(), DATE_FORMAT);
                BigDecimal ticketPrice = new BigDecimal(values[2].trim());
                Passenger passenger = new Passenger(values[3].trim(),   // name
                                                    values[4].trim());  // passport number

                Aircraft aircraft = values[6].trim().equalsIgnoreCase("Commercial")             // aircraft type
                        ? new CommercialAircraft(values[5].trim(), 0, 0, "")  // aircraft model
                        : new PrivateJet(values[5].trim(),0,0,false,0);

                reservations.computeIfAbsent(flightNumber, k -> new ArrayList<>()).add(passenger);      // to avoid NPE
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV." + e.getMessage());;
        }
        return reservations;
    }
}