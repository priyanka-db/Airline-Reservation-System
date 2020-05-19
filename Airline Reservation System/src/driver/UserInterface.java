package driver;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import airport.Airport;
import flight.*;
import system.FlightController;
import utils.Saps;

/**
 * This class is the interface between user and system,
 * it takes and processes user input for searching flight,
 * and processes and outputs system returned information to user.
 */

public class UserInterface {
    /**
     * @param depAirport is the string value for departure airport
     * @param arrAirport is the string value for arrival airport
     * @param depTime is the string value for departure time
     * @param arrTime is the string value for arrival time
     * @param seatClass is the string value for seating class
     * @param tripType is the string value for trip type
     * @param sortParam is the string value for sorting
     * @param returnTime is the string value for return time
     * @param flightNumber is the int value for reserving flights
     * @param returnFlightNumber is the int value for reserving return flights
     * @param filterType is the int value for filters
     */
    String depAirport;
    String arrAirport;
    String depTime;
    String arrTime;
    String seatClass;
    String tripType;
    String sortParam;
    String returnTime;
    int flightNumber;
    int returnFlightNumber;
    int filterType;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");

    // flights are separated into res and view because when filter flight list,
    // the flight reserve number (index) will change
    ArrayList<ArrayList<Flight>> resFlight;
    ArrayList<ArrayList<Flight>> resReturnFlight;
    ArrayList<ArrayList<Flight>> viewFlight;
    ArrayList<ArrayList<Flight>> viewReturnFlight;
    FlightController controller;
    ArrayList<ArrayList<Flight>> selectedFlight;
    ArrayList<ArrayList<Flight>> selectedRetFlight;

    Map<String, Airport> airportMap;

    /**
     * Default constructor
     */
    public UserInterface() {
        controller = new FlightController();
        // default parameters
        depAirport = "";
        arrAirport = "";
        depTime = "";
        arrTime = "";
        seatClass = "Coach";
        tripType = "one-way";
        sortParam = "travelTime";
        returnTime = "";
        filterType = 3;
        resFlight = new ArrayList();
        resReturnFlight = new ArrayList();
        viewFlight = new ArrayList();
        viewReturnFlight = new ArrayList();
        selectedFlight = new ArrayList();
        selectedRetFlight = new ArrayList();

        controller.setAirportMap();
        airportMap = controller.getAirportMap();
    }

    /**
     * List of parameters that required user to input
     */
    public void mainMenu() {
        System.out.println("1. Departure Airport: " + depAirport);
        System.out.println("2. Arrival Airport: " + arrAirport);
        System.out.println("3. Departure Date: " + depTime);
        System.out.println("4. Arrival Date: " + arrTime);
        System.out.println("5. Seating Class (Coach, FirstClass): " + seatClass);
        System.out.println("6. Trip Type (one-way, round-trip): " + tripType);
        System.out.println("7. Return Date (round-trip only): " + returnTime);
        System.out.println("8. Sort By (depTime, arrTime, totalPrice, travelTime): " + sortParam);
        System.out.println("9. Filter (0. Non Stop, 1. One Stop, 2. Two Stops, 3. List All)");
        System.out.println("10. Select Flight (0. Select Departure Flight 1. Select Return Flight (round-trip only))");
        System.out.println("11. Search Flight");
        System.out.println("12. Reserve Flight");
        System.out.println("13. Show Flight List Again");

        // Use Scanner to read user input
        Scanner scan = new Scanner(System.in);
        if(scan.hasNext()) {
            try {
                int selectMenu = scan.nextInt();
                readUserInput(selectMenu);
                // handle user not typing integer when selecting
            } catch (Exception e){
                System.out.println("===Please enter integer only.===");
                System.out.println();
                mainMenu();
            }

        }
    }

    /**
     * Read and process user inputs
     */
    public void readUserInput(int selectMenu) {
        Scanner scan = new Scanner(System.in);
        String param;

        if(selectMenu<11 && scan.hasNext()) {
            param = scan.next();
            // if user type 11, 12, 13 then assign empty string
        } else {
            param = "";
        }

        switch(selectMenu) {
            case 1:
                // convert string to upper case no matter user input lower or upper case
                // check if code does exist
                if (isValidCode(param)) {
                    depAirport = param.toUpperCase();
                    System.out.println(depAirport);
                    System.out.println();
                    break;
                } else {
                    System.out.println("===Please enter a valid airport.===");
                    System.out.println();
                    break;
                }


            case 2:
                // convert string to upper case no matter user input lower or upper case
                // check if code does exist
                if (isValidCode(param)) {
                    arrAirport = param.toUpperCase();
                    System.out.println(arrAirport);
                    System.out.println();
                    break;
                } else {
                    System.out.println("===Please enter a valid airport.===");
                    System.out.println();
                    break;
                }

            case 3:
                // if user search by departure time, then set arrival time to empty string
                // check if date is valid
                if (isValidDate(param)) {
                    depTime = param;
                    arrTime = "";
                    System.out.println(depTime);
                    System.out.println();
                    break;
                } else {
                    System.out.println("===Please enter a valid date.===");
                    System.out.println();
                    break;
                }


            case 4:
                // if user search by arrival time, then set departure time to empty string
                arrTime = param;
                depTime = "";
                System.out.println(arrTime);
                System.out.println();
                break;

            case 5:
                // user input for seatClass
                seatClass = param;
                System.out.println(seatClass);
                System.out.println();
                break;

            case 6:
                // user input for trip type
                tripType = param;
                System.out.println(tripType);
                System.out.println();
                break;

            case 7:
                // user input for return date
                // check if date is valid
                if (isValidDate(param)) {
                    returnTime = param;
                } else {
                    System.out.println("===Please enter a valid date.===");
                    System.out.println();
                    break;
                }
                LocalDate ret = LocalDate.parse(returnTime, formatter);
                if(!depTime.isEmpty()) {
                    LocalDate dep = LocalDate.parse(depTime, formatter);
                    // handle edge case: return date cannot be earlier than departure date
                    if(dep.isAfter(ret)){
                        returnTime = "";
                        System.out.println("===Return date must be later than departure date.===");
                        System.out.println();
                        break;
                    }
                } else if(!arrTime.isEmpty()) {
                    LocalDate arr = LocalDate.parse(arrTime, formatter);
                    // handle edge case: return date cannot be earlier than departure date
                    if(arr.isAfter(ret)){
                        returnTime = "";
                        System.out.println("===Return date must be later than departure date.===");
                        System.out.println();
                        break;
                    }
                }
                System.out.println(returnTime);
                System.out.println();
                break;

            case 8:
                // user input for sort param
                sortParam = param;
                System.out.println(sortParam);
                System.out.println();
                break;

            case 9:
                // user input for filter param
                filterType = Integer.valueOf(param);
                System.out.println(filterType);
                System.out.println();
                break;

            case 10:
                // user input for flight selection after defining the departure/return choice
                // depOrReturn is for user to input departing's or returning's flight reserve number
                int depOrReturn = Integer.valueOf(param);
                switch (depOrReturn){
                    // reserve departure flight first
                    case 0:
                        flightNumber = scan.nextInt();
//                        buildViewFlightList();
                        selectedFlight.clear();
                        selectedFlight.add(resFlight.get(flightNumber));
                        System.out.println("Selected Departure Flight Number: " + flightNumber);
                        printSelectedList(selectedFlight, viewFlight, "");

                        // if user enters departure flight reserve number and previously select round-trip
                        // then print return flight list for them to choose return flight
                        if (tripType.equalsIgnoreCase("round-trip")) {
                            if(viewReturnFlight.isEmpty()) {
                                System.out.println("No valid return flight, please choose another return date.");
                                break;
                            }
                            System.out.println("Return flight list");
                            printFlightList(resReturnFlight, viewReturnFlight, "Returning");
                            System.out.println("Please select return flight:");
                            returnFlightNumber = scan.nextInt();
                            selectedRetFlight.clear();
                            selectedRetFlight.add(resReturnFlight.get(returnFlightNumber));
                            System.out.println("Selected Departure Flight Number: " + flightNumber);
                            printSelectedList(selectedFlight, viewFlight, "");
                            System.out.println("Selected Return Flight Number: " + returnFlightNumber);
                            printSelectedList(selectedRetFlight, viewFlight, "");
                        }
                        break;
                    // reserve return flight first
                    case 1:
                        // if user enters return flight reserve number first instead of departing flight first
                        if(flightNumber==-1) {
                            System.out.println("Please select departing flight first.");
                            break;
                        }
                        returnFlightNumber = scan.nextInt();
                        selectedRetFlight.clear();
                        selectedRetFlight.add(resReturnFlight.get(returnFlightNumber));
                        System.out.println("Selected Departure Flight Number: " + flightNumber);
                        printSelectedList(selectedFlight, viewFlight, "");
                        System.out.println("Selected Return Flight Number: " + returnFlightNumber);
                        printSelectedList(selectedRetFlight, viewFlight, "");
                        break;
                }
                // display a message for user to confirm flight selection before the reserve step
                if (tripType.equalsIgnoreCase("one-way")) {
                    if (!selectedFlight.isEmpty()) {
                        System.out.println("Please press 1 to book selected flights, press 0 to discard");
                        // if confirmed, execute case 12
                        if (scan.nextInt() == 1) {
                            readUserInput(12);
                        }
                    }
                } else if (tripType.equalsIgnoreCase("round-trip")) {
                    if (!selectedRetFlight.isEmpty() && !selectedFlight.isEmpty()) {
                        System.out.println("Please press 1 to book selected flights, press 0 to discard");
                        // if confirmed, execute case 12
                        if (scan.nextInt() == 1) {
                            readUserInput(12);
                        }
                    }
                }
                break;

            case 11:
                // default value
                // if user search again, reset flight number and return flight number
                flightNumber = -1;
                returnFlightNumber = -1;

                // check if departureAirport and arrivalAirport are empty
                if (depAirport.isEmpty() || arrAirport.isEmpty()) {
                    System.out.println("===Please enter departure airport AND arrival airport===");
                    System.out.println();
                    break;
                    // check if returnTime is empty when user input round-trip
                } else if(tripType.equalsIgnoreCase("round-trip") && returnTime.isEmpty()){
                    System.out.println("===Please enter a RETURN date.===");
                    System.out.println();
                    break;
                } else {
                    // if user search by departure time
                    if (!depTime.isEmpty()) {
                        resFlight = controller.searchDepTimeFlight(depAirport, depTime, arrAirport, seatClass, depTime);
                        String nextDay= LocalDate.parse(depTime, formatter).plusDays(1).format(formatter);
                        resFlight.addAll(controller.searchDepTimeFlight(depAirport, nextDay, arrAirport, seatClass, depTime));
                        // if user search by arrival time
                    } else {
                        resFlight = controller.searchArrTimeFlight(depAirport, arrTime, arrAirport, seatClass, arrTime);
                        String nextDay= LocalDate.parse(arrTime, formatter).plusDays(1).format(formatter);
                        resFlight.addAll(controller.searchArrTimeFlight(depAirport, nextDay, arrAirport, seatClass, arrTime));
                    }
                    // apply sorter
                    controller.sortByParam(sortParam, resFlight, seatClass);
                    printFlightList(resFlight, viewFlight, "Departing");
                    System.out.println("===================================END========================================");
                }

                // if trip type is round-trip, search return flight
                if (tripType.equalsIgnoreCase("round-trip")){
                    // if user search by departure time
                    if (!depTime.isEmpty()) {
                        resReturnFlight = controller.searchDepTimeFlight(arrAirport, returnTime, depAirport, seatClass, returnTime);
                        String nextDay= LocalDate.parse(returnTime, formatter).plusDays(1).format(formatter);
                        resFlight.addAll(controller.searchDepTimeFlight(depAirport, nextDay, arrAirport, seatClass, returnTime));
                        // if user search by arrival time
                    } else {
                        resReturnFlight = controller.searchArrTimeFlight(arrAirport, returnTime, depAirport, seatClass, returnTime);
                        String nextDay= LocalDate.parse(returnTime, formatter).plusDays(1).format(formatter);
                        resFlight.addAll(controller.searchArrTimeFlight(depAirport, nextDay, arrAirport, seatClass, returnTime));
                    }
                    // apply sorter
                    controller.sortByParam(sortParam, resReturnFlight, seatClass);
                    System.out.println("Return Flight List");
                    printFlightList(resReturnFlight, viewReturnFlight, "Returning");
                    System.out.println("===================================END========================================");

                }
                break;

            case 12:
                // reserve flight(s)
                controller.reserveFlight(viewFlight.get(flightNumber), seatClass);
                System.out.println("Reserved Departure Flight:");
                // print out reservation summary
                printSelectedList(selectedFlight, viewFlight, "");
                if(tripType.equalsIgnoreCase("round-trip")){
                    controller.reserveFlight(viewReturnFlight.get(returnFlightNumber), seatClass);
                    // print out reservation summary
                    System.out.println("Reserved Return Flight:");
                    printSelectedList(selectedRetFlight, viewFlight, "");
                }
                resetToDefault();
                break;

            case 13:
                // show flight list again
                printFlightList(resFlight, viewFlight, "Departing");
                System.out.println("===================================END========================================");
                if(resReturnFlight.size()>0){
                    System.out.println("Return Flight List:");
                    printFlightList(resReturnFlight, viewReturnFlight, "Returning");
                    System.out.println("===================================END========================================");
                }
                break;
        }
        mainMenu();
    }

    /**
     * Print flight searching result
     */
    public void printFlightList(ArrayList<ArrayList<Flight>> flight, ArrayList<ArrayList<Flight>> viewFlight, String departOrReturn) {
        buildViewFlightList();
        if (flight.size() == 0) {
            System.out.println("No " + departOrReturn + " " + seatClass + " flights available.");
            return;
        } else {
            for (int i=0; i<viewFlight.size(); i++) {
                ArrayList<Flight> flightList = viewFlight.get(i);
                ArrayList<String> info = FlightController.getInfo(flightList, seatClass);
                System.out.println("Flight Reserve Number: " + i);
                System.out.println("Departure:"+info.get(0)+
                        ", Arrival:"+info.get(1)+
                        ", Duration:"+info.get(2)+"min"+
                        ", Price:"+"$"+info.get(3));
                for (Flight f : flightList) {
                    System.out.println(f.toLocalString());
//                    System.out.println("GMT: " + f.toString());
                }
                System.out.println();
            }
        }
    }

    /**
     * print selected flight
     */
    public void printSelectedList(ArrayList<ArrayList<Flight>> flight, ArrayList<ArrayList<Flight>> viewFlight, String departOrReturn) {
        buildViewFlightList();
        for (int i=0; i<flight.size(); i++) {
            ArrayList<Flight> flightList = flight.get(i);
            ArrayList<String> info = FlightController.getInfo(flightList, seatClass);
            System.out.println("Departure:"+info.get(0)+
                    ", Arrival:"+info.get(1)+
                    ", Duration:"+info.get(2)+
                    ", Price:"+"$"+info.get(3));
            for (Flight f : flightList) {
                System.out.println(f.toLocalString());
            }
            System.out.println();
        }

    }

    /**
     * Build user view's flight
     */
    public void buildViewFlightList(){
        // clear the previous flight list because we have to show correct filter, valid return flight
        viewFlight.clear();
        viewReturnFlight.clear();
        if(resFlight.size()==0) {
            //System.out.println("No resFlight has been built");
            return;
        }
        // if user select one-way
        for(int i=0;i<resFlight.size();i++){
            ArrayList<Flight> flightList = resFlight.get(i);
            int n = flightList.size();
            // if filter = non stop but flight list not having 1 element only then skip
            // if filter = one stop but flight list not having 2 elements only then skip
            // if filter = two stop but flight list not having 3 elements only then skip
            if((filterType==0 && n!=1) || (filterType==1 && n!=2) || (filterType==2 && n!=3)) continue;
            viewFlight.add(flightList);
        }
        if(!tripType.equalsIgnoreCase("round-trip") || resReturnFlight.size()==0) return;
        // if user select round-trip
        for(int i=0; i<resReturnFlight.size(); i++){
            ArrayList<Flight> flightList = resReturnFlight.get(i);
            int n = flightList.size();
            if((filterType==0 && n!=1) || (filterType==1 && n!=2) || (filterType==2 && n!=3)) continue;
            // if user select departing flight (default flightNumber is -1 which means not select)
            if(flightNumber!=-1){
                // viewFlight.get(flightNumber) means get the flight reserve number
                // .get(viewFlight.get(flightNumber).size()-1) means get the last element
                Flight departing = viewFlight.get(flightNumber).get(viewFlight.get(flightNumber).size()-1);
                Flight returning = flightList.get(0);
                // if departing flight + min layover time is already pass the returning flight time (which means we cannot catch up) then skip
                if(departing.arrivalTime().plusMinutes(Saps.MIN_LAYOVER_TIME).isAfter(returning.departureTime())) continue;
            }
            viewReturnFlight.add(flightList);
        }
    }

    /**
     * Check user input airport code
     *
     * @param param is user input airport code
     *
     * @return true is the code is in the database
     */
    public boolean isValidCode(String param) {
        return airportMap.containsKey(param.toUpperCase());
    }

    /**
     * Check user input departure/return dates
     *
     * @param param is user input date
     *
     * @return true is the date is in the database
     */
    public boolean isValidDate(String param) {
        LocalDate date = LocalDate.parse(param, formatter);
        if (date.getYear()==Saps.DEFAULT_YEAR
                && date.getMonthValue()==Saps.DEFAULT_MONTH
                && date.getDayOfMonth()>=Saps.MIN_DATE
                && date.getDayOfMonth()<=Saps.MAX_DATE ) {
            return true;
        }
        return false;
    }

    /**
     * Reset param to default value after reserving flight
     */
    public void resetToDefault() {
        this.depAirport = "";
        this.arrAirport = "";
        this.depTime = "";
        this.arrTime = "";
        this.seatClass = "Coach";
        this.tripType = "one-way";
        this.sortParam = "travelTime";
        this.returnTime = "";
        this.filterType = 3;
    }
}