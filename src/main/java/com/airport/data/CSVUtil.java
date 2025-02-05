package com.airport.data;

import com.airport.domain.model.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class CSVUtil {
    private static final String filePath = "data/reservations.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String HEADER = "flightNumber,departureDate,ticketPrice,passengerName,passportNumber,aircraftModel,aircraftType";

    public static void saveReservationsToCSV(HashMap<String, ArrayList<Passenger>> reservations, HashMap<String, Flight> flights) {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) { // Overwrite mode
            // Write the header unconditionally
            writer.append(HEADER);
            writer.newLine();
            for (Map.Entry<String, ArrayList<Passenger>> entry : reservations.entrySet()) {
                String flightNumber = entry.getKey();
                ArrayList<Passenger> passengers = entry.getValue();
                Flight flight = flights.get(flightNumber); // Get the flight object associated with the flight number
                if (flight != null) {
                    for (Passenger passenger : passengers) {
                        String line = String.format("%s,%s,%s,%s,%s,%s,%s",
                                flightNumber.trim(),
                                flight.getDepartureDate().format(DATE_FORMAT).trim(),
                                flight.getTicketPrice().toString().trim(),
                                passenger.getName().trim(),
                                passenger.getPassportNumber().trim(),
                                flight.getAircraft().getModel().trim(),
                                flight.getAircraft().getClass().getSimpleName().trim());
                        writer.append(line);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public static HashMap<String, ArrayList<Passenger>> loadReservationsFromCSV(HashMap<String, Flight> flights) {
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
                Passenger passenger = new Passenger(values[3].trim(), values[4].trim());  // name, passport number
                String aircraftModel = values[5].trim();
                String aircraftType = values[6].trim();

                // Create Aircraft
                Aircraft aircraft;
                if (aircraftType.equalsIgnoreCase("CommercialAircraft")) {
                    aircraft = new CommercialAircraft(aircraftModel, 0, 0, "");
                } else if (aircraftType.equalsIgnoreCase("PrivateJet")) {
                    aircraft = new PrivateJet(aircraftModel, 0, 0, false, 0);
                } else {
                    aircraft = new Aircraft(aircraftModel, 0, 0);
                }

                // Create Flight if not already in flights map
                Flight flight = flights.get(flightNumber);
                if (flight == null) {
                    flight = new Flight(flightNumber, departureDate, ticketPrice, aircraft);
                    flights.put(flightNumber, flight);
                }

                // Add Passenger to Reservations
                reservations.computeIfAbsent(flightNumber, k -> new ArrayList<>()).add(passenger);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return reservations;
    }
}