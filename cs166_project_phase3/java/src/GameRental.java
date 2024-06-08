/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.time.*;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: updateProfile(esql, authorisedUser); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewTrackingInfo(esql); break;
                   case 9: updateTrackingInfo(esql); break;
                   case 10: updateCatalog(esql, authorisedUser); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(GameRental esql){
      try{
         System.out.println(
                 "\n\n*******************************************************\n" +
                         "              Create New User      	               \n" +
                         "*******************************************************\n");
         // Username Selection
         System.out.println("Username Selection");
         System.out.println("Usernames should be no longer than 50 characters.");
         System.out.println("Please enter your username: ");
         String user = in.readLine();

         // validate username
         while (true) { // loop until username has not been taken
            if (user.length() <= 50) { // username <= 50 characters
               String availableUser = "SELECT EXISTS (Select 1 FROM Users WHERE login = '" + user + "' LIMIT 1)";
               List<List<String>> userResult = esql.executeQueryAndReturnResult(availableUser);
               boolean userTaken = userResult.get(0).contains("t");
               if (!userTaken) { // username is available to register
                  break;
               }
            }
            System.out.println("Invalid Username");
            System.out.println("Please enter you username: ");
            user = in.readLine();
         }

         // Password Selection
         System.out.println("Set Your Password");
         System.out.println("Passwords should be no longer than 30 characters.");
         System.out.println("Please enter your password: ");
         String password = in.readLine();

         // validate password
         while (password.length() >= 30) {
            System.out.println("\nInvalid Password");
            System.out.println("Please enter your password: ");
            password = in.readLine();
         }

         // Phone Number
         System.out.println("\nContact Information");
         System.out.println("Please enter your phone number (123-456-7890): ");
         String phone = in.readLine();

         // validate phone number
         boolean validPN = validatePhoneNumber(phone);

         while(!validPN) {
            System.out.println("\nInvalid Phone Number");
            System.out.println("Please enter your phone number: ");
            phone = in.readLine();

            validPN = validatePhoneNumber(phone);
         }
         String countryCode = "+1-";
         String update = "INSERT INTO Users Values('" + user + "', '" + password + "', 'customer', NULL, '" + countryCode + phone + "', 0)";
         esql.executeUpdate(update);
         System.out.println("Account Created Successfully");
         System.out.println("Returning to Main Menu...\n");

      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end CreateUser



   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(GameRental esql) {
      try{
         System.out.println(
                 "\n\n*******************************************************\n" +
                         "              Log In      	               \n" +
                         "*******************************************************\n");
         System.out.println("Please enter your username: ");
         String user = in.readLine();
         System.out.println("Please enter your password: ");
         String password = in.readLine();

         String query = "SELECT login, password FROM Users WHERE login = '" + user + "' AND password = '" + password + "'";
         int rowCount = esql.executeQuery(query);
         if (rowCount == 1) {
            return user;
         }
         // Username-password combination not found in users database
         System.out.println("Incorrect username or password.\n");
         System.out.println("Returning to main menu.");

         return null;
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(GameRental esql, String user) {
      try{
          String query = "SELECT login, favGames, phoneNum, numOverDueGames FROM USERS WHERE login = '" + user + "'";
          List<List<String>> profile = esql.executeQueryAndReturnResult(query);
          System.out.println(
                 "\n\n*******************************************************\n" +
                         "              User Profile      	               \n" +
                         "*******************************************************\n");
          System.out.println("Username: " + profile.get(0).get(0));
          System.out.println("Favorite Games: " + profile.get(0).get(1));
          System.out.println("Phone Number: " + profile.get(0).get(2));
          System.out.println("# of Overdue Games: " + profile.get(0).get(3) + "\n") ;

      }catch(Exception e) {
          System.err.println(e.getMessage());
      }
   }
   public static void updateProfile(GameRental esql, String user) {
      try{
         boolean updateMenu = true;
         while (updateMenu) {
            System.out.println(
                    "\n\n*******************************************************\n" +
                            "              Update Profile      	               \n" +
                            "*******************************************************\n");

            System.out.println("PROFILE SETTINGS");
            System.out.println("----------------");

            System.out.println("1. Change Password");
            System.out.println("2. Change Phone Number");
            System.out.println("3. Change Favorite Games");
            System.out.println("9. Return to Main Menu");

            switch (readChoice()) {
               case 1: changePassword(esql, user); break;
               case 2: changePhoneNumber(esql, user); break;
               case 3: changeFavoriteGames(esql, user); break;


               case 9: updateMenu = false; break;

               default: System.out.println("Unrecognized choice!");
            }
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void viewCatalog(GameRental esql) {
      try{
         boolean showCatalog = true;
         String genre = "";
         Double price = 0.0;
         String sort = "DESC";
         while(showCatalog){
            System.out.println(
                    "\n\n*******************************************************\n" +
                            "              Game Catalog      	               \n" +
                            "*******************************************************\n");

            System.out.println("CATALOG SETTINGS");
            System.out.println("----------------");

            System.out.println("1. View Catalog");
            System.out.println("2. Set Genre");
            System.out.println("3. Set Price Range");
            System.out.println("4. Reset Genre");
            System.out.println("5. Reset Price");
            System.out.println("6. Change Sort");

            System.out.println("9. Return to Main Menu");

            switch(readChoice()){
               case 1: filterCatalog(esql, genre, price, sort); break;
               case 2: genre = filterGenre(); break;
               case 3: price = filterPrice(); break;
               case 4: genre = ""; System.out.println("Genre set to default."); break;
               case 5: price = 0.0; System.out.println("Price set to default."); break;
               case 6: sort = changeSort(sort); break;

               case 9: showCatalog = false; break;
               default: System.out.println("Unrecognized choice!");
            }
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void placeOrder(GameRental esql, String user) {
      try {
         System.out.println(
                 "\n\n*******************************************************\n" +
                         "              Place Order      	               \n" +
                         "*******************************************************\n");

         System.out.println("How many different games would you like to order?");
         String num = in.readLine();
         boolean validNum = validateInteger(num);
         while(!validNum) {
            System.out.println("Invalid input");
            System.out.println("How many different games would you like to order?");
            num = in.readLine();
            validNum = validateInteger(num);
         }
         int numGames = Integer.parseInt(num);

         // retrieve gameIDs and number of copies for rental order
         List<String> gameIDs = new ArrayList<>();
         List<Integer> numCopies = new ArrayList<>();
         for (int i = 0; i < numGames; i++) {
            System.out.println("Please enter gameID: ");
            String gameID = in.readLine();
            boolean validGame = validateGameID(esql, gameID);
            while(!validGame) {
               System.out.println("Invalid gameID");
               System.out.println("Please enter gameID: ");
               gameID = in.readLine();
               validGame = validateGameID(esql, gameID);
            }
            gameIDs.add(gameID);
            System.out.println("Please enter number of copies: ");
            String copies = in.readLine();
            boolean validCopies = validateInteger(copies);
            while(!validCopies) {
               System.out.println("Invalid input");
               System.out.println("Please enter number of copies: ");
               copies = in.readLine();
               validCopies = validateInteger(copies);
            }
            numCopies.add(Integer.parseInt(copies));
         }

         // calculate total price
         String query = "SELECT price FROM CATALOG WHERE gameID = '" + gameIDs.get(0) + "'";
         for (int i = 1; i < numGames; i++) {
            // retrieve game price
            query += " OR gameID = '" + gameIDs.get(i) + "'";
         }
         List<List<String>> priceResult = esql.executeQueryAndReturnResult(query);
         double totalPrice = 0.0;
         for (int i = 0; i < numGames; i++) {
            totalPrice += (numCopies.get(i) * Double.parseDouble(priceResult.get(i).get(0)));
         }

         // summarize rental order
         System.out.println("\nItems in Order");
         System.out.println("--------------");
         System.out.println("gameID  \tnumCopies\tPrice");
         Integer totalCopies = 0;
         for(int i = 0; i < numGames; i++) {
            System.out.println(gameIDs.get(i) + "\t    " + numCopies.get(i) + "\t\t" + priceResult.get(i).get(0));
            totalCopies += numCopies.get(i);
         }
         System.out.println("Total: numGames = " + numGames + ", totalCopies = " + totalCopies);
         System.out.println("Total Cost: $" + totalPrice);


         boolean validConfirm = false;
         while(!validConfirm){
            System.out.println("Please confirm order (y/n): ");
            String confirm = in.readLine();
            switch (confirm) {
               case "y": validConfirm = true; break;
               case "n": System.out.println("Order canceled\nReturning to Main Menu..."); return;

               default: System.out.println("Invalid input");
            }

         }

         // create unique rental order
         String rentalID = createOrderID(esql);
         DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         String orderTS = LocalDateTime.now().format(f);
         LocalDate dueDate = LocalDate.now().plusDays(30);
         String rentalOrder = "INSERT INTO RentalOrder VALUES('" +
                 rentalID + "', '" +
                 user + "', " +
                 totalCopies + ", " +
                 totalPrice + ", '" +
                 orderTS + "', '" +
                 dueDate + "')";
         esql.executeUpdate(rentalOrder);

         // create unique tracking info
         String trackingID = createTrackingID(esql);
         String trackingInfo = "INSERT INTO TrackingInfo VALUES('" +
                 trackingID + "', '" +
                 rentalID + "', " +
                 "'Order Received', " +
                 "'Los Angeles,CA', " +
                 "'USPS', '" +
                 orderTS + "')";
         esql.executeUpdate(trackingInfo);

         String gamesInOrder = "INSERT INTO GamesInOrder VALUES";
         for (int i = 0; i < numGames; i++) {
            gamesInOrder += "('" + rentalID + "', '" + gameIDs.get(i) + "', " + numCopies.get(i) + ")";
            if (i < numGames - 1) {
               gamesInOrder += ", ";
            }
         }
         esql.executeUpdate(gamesInOrder);

         System.out.println("Order placed successfully\n");
         System.gc();
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void viewAllOrders(GameRental esql) {}
   public static void viewRecentOrders(GameRental esql) {}
   public static void viewOrderInfo(GameRental esql) {}
   public static void viewTrackingInfo(GameRental esql) {}
   public static void updateTrackingInfo(GameRental esql) {}
   public static void updateCatalog(GameRental esql, String user) {
      try {
         System.out.println("\nYou have selected: Update Catalog");

         if (!checkUserRole(esql, user, "manager")) {
            System.out.println("You are unauthorized to update the catalog");
            System.out.println("Returning to Main Menu...");
            return;
         }

         boolean updateCatalog = true;
         while(updateCatalog) {
            System.out.println(
                    "\n\n*******************************************************\n" +
                            "              Update Game Catalog      	               \n" +
                            "*******************************************************\n");

            System.out.println("CATALOG OPTIONS");
            System.out.println("---------------");
            System.out.println("1. Change Game Name");
            System.out.println("2. Change Genre");
            System.out.println("3. Change Price");
            System.out.println("4. Change Description");
            System.out.println("5. Change Image");
            System.out.println("6. Add New Game");
            System.out.println("9. Return to Main Menu");


            switch(readChoice()){
               case 1: changeGameName(esql); break;
//               case 2:;
//               case 3:;
//               case 4:;
//               case 5:;
//               case 6:;
//
               case 9: updateCatalog = false; break;
               default: System.out.println("Unrecognized choice!");
            }
         }

      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void updateUser(GameRental esql) {
      try {

      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   // input validation
   public static boolean validatePhoneNumber(String phone) {
      if (phone.length() == 12) { // correct phone number length
         for(int i = 0; i < 12; i++) { // correct phone number formatting
            if (i == 3 || i == 7) { // check hyphens are in correct locations
               if (phone.charAt(i) != '-') {
                  return false;
               }
            }
            else {
               if (!Character.isDigit(phone.charAt(i))) {
                  return false;
               }
            }
         }
         return true;
      }
      return false;
   }

   public static boolean validateGameID(GameRental esql, String gameID){
      try{
         if (gameID.length() == 8) {
            if (gameID.startsWith("game")) {
               // check if gameID exists in catalog
               String availableUser = "SELECT EXISTS (Select 1 FROM Catalog WHERE gameID = '" + gameID + "' LIMIT 1)";
               List<List<String>> gameResult = esql.executeQueryAndReturnResult(availableUser);
               return gameResult.get(0).contains("t"); // true if gameID found in database
            }
            return false;
         }
         return false;
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return false;
   }

   public static boolean validateInteger(String string) {
      if (!string.isEmpty()) {
         for (int j = 0; j < string.length(); j++) {
            if (!Character.isDigit(string.charAt(j))) {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   // functions for updating profile
   public static void changePassword(GameRental esql, String user) {
      try{
         System.out.println("You have selected: Change Password\n");

         boolean pwMatch = false;
         while (!pwMatch) {
            System.out.println("Passwords should be no longer than 30 characters.");
            System.out.println("Please enter new password: ");
            String newPW1 = in.readLine();
            System.out.println("Please confirm password: ");
            String newPW2 = in.readLine();

            // new password is confirmed, update in database
            if (newPW1.equals(newPW2)) {
               pwMatch = true;
               System.out.println("Updating password...");
               String update = "UPDATE Users SET password = '" + newPW1 + "' WHERE login = '" + user + "'";
               esql.executeUpdate(update);

               System.out.println("Password changed successfully");
            }
            // allow user to retry changing password
            else {
               System.out.println("Error: Passwords do not match!");

               // let user cancel
               boolean validRetry = false;
               while (!validRetry) {
                  System.out.println("Would you like to try again? (y/n): ");
                  String retry = in.readLine();
                  switch (retry) {
                     case "y": validRetry = true; break;
                     case "n": System.out.println("Returning to Profile Settings..."); return;

                     default: System.out.println("Invalid input");
                  }
               }
            }
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void changePhoneNumber(GameRental esql, String user) {
      try{
         System.out.println("You have selected: Change Phone Number\n");

         boolean pnMatch = false;
         while (!pnMatch) {
            System.out.println("Please enter new phone number (123-456-7890): ");
            String phone1 = in.readLine();
            boolean validPN = validatePhoneNumber(phone1);
            while (!validPN) {
               System.out.println("Invalid phone number");
               System.out.println("Please enter your phone number (123-456-7890): ");
               phone1 = in.readLine();
               validPN = validatePhoneNumber(phone1);
            }
            System.out.println("Please confirm your phone number: ");
            String phone2 = in.readLine();
            validPN = validatePhoneNumber(phone2);
            while (!validPN) {
               System.out.println("Invalid phone number");
               System.out.println("Please confirm your phone number: ");
               phone2 = in.readLine();
               validPN = validatePhoneNumber(phone2);
            }

            // new phone number confirmed, update in database
            if (phone1.equals(phone2)) {
               pnMatch = true;
               String countryCode = "+1-";
               System.out.println("Updating phone number...");
               String update = "UPDATE Users SET phoneNum = '" + countryCode + phone1 + "' WHERE login = '" + user +"'";
               esql.executeUpdate(update);

               System.out.println("Phone number changed successfully");
               System.out.println("New phone number: " + countryCode + phone1);

            }
            // allow user to retry changing password
            else {
               System.out.println("Phone numbers do not match!");

               // let user cancel
               boolean validRetry = false;
               while (!validRetry) {
                  System.out.println("Would you like to try again? (y/n): ");
                  String retry = in.readLine();
                  switch (retry) {
                     case "y":
                        validRetry = true; break;
                     case "n":
                        System.out.println("Returning to Profile Settings..."); return;

                     default: System.out.println("Invalid input");
                  }
               }
            }
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void changeFavoriteGames(GameRental esql, String user) {
      try{
         System.out.println("You have selected: Change Favorite Games\n");

         System.out.println("Please enter total number of favorite games: ");
         int numGames = Integer.parseInt(in.readLine());

         String games = "";
         for (int i = 0; i < numGames; i++) {
            System.out.println("\nPlease enter game: ");
            games += in.readLine();
            if (i < numGames - 1) {
               games += ", ";
            }
         }

         System.out.println("Updating favorite games...");
         String update = "UPDATE Users SET favGames = '" + games + "' WHERE login = '" + user + "'";
         esql.executeUpdate(update);

         System.out.println("Favorite games changed successfully");
         System.out.println("Favorite Games: " + games);
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   // functions for changing catalog filters
   public static void filterCatalog(GameRental esql, String genre, Double price, String sort) {
      try{
         String filters = "Displaying results for: ";
         String query = "SELECT gameID, gameName, genre, price, description FROM Catalog";
         if (!genre.isEmpty() && price > 0) {
            query += " WHERE genre = '" + genre + "' AND price < " + price;
            filters += "Genre = \" " + genre + "\", Price < " + price;
         }
         else if (!genre.isEmpty() && price == 0) {
            query += " WHERE genre = '" + genre + "'";
            filters += "Genre = \"" + genre + "\"";
         }
         else if (genre.isEmpty() && price > 0) {
            query += " WHERE price < " + price;
            filters += "Price < " + price;
         }
         else {
            filters = "Displaying full catalog";
         }
         query += " ORDER BY price " + sort;

         // reformat output of catalog
         // implement pages if time
         esql.executeQueryAndPrintResult(query);
         int rowCount = esql.executeQuery(query);
         System.out.println("total row(s): " + rowCount);
         System.out.println(filters);
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static String filterGenre(){
      try{
         System.out.println("Only 1 genre can be viewed at a time.");
         System.out.println("Please enter genre: ");
         String genre = in.readLine();
         System.out.println("Returning to Catalog Options...");

         return genre;
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }

   public static Double filterPrice(){
      try{
         System.out.println("Please enter maximum price: ");
         Double price = Double.parseDouble(in.readLine());
         System.out.println("Returning to Catalog Options...");

         return price;
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }

   public static String changeSort(String sort){
      try{
         if (sort.equals("DESC")) {
            System.out.println("Sorting by: Price Ascending");
            return "ASC";
         }
         else {
            System.out.println("Sorting by: Price Descending");
            return "DESC";
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }

   // functions for creating rental order and tracking info
   public static String createOrderID (GameRental esql) {
      try {
         String query = "SELECT rentalOrderID FROM RentalOrder ORDER BY rentalOrderID DESC LIMIT 1";
         List<List<String>> IDResult = esql.executeQueryAndReturnResult(query);
         String maxID = IDResult.get(0).get(0).substring(15); // retrieve id of last placed order
         int nextID = Integer.parseInt(maxID) + 1;
          return "gamerentalorder" + nextID;
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }

   public static String createTrackingID (GameRental esql) {
      try {
         String query = "SELECT trackingID FROM TrackingInfo ORDER BY trackingID DESC LIMIT 1";
         List<List<String>> IDResult = esql.executeQueryAndReturnResult(query);
         String maxID = IDResult.get(0).get(0).substring(10); // retrieve id of last placed order
         int nextID = Integer.parseInt(maxID) + 1;
          return "trackingid" + nextID;
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return null;
   }

   public static boolean checkUserRole(GameRental esql, String user, String role) {
      try {
         String query = "SELECT EXISTS(SELECT 1 FROM Users WHERE login = '" + user + "' AND role = '" + role + "')";
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         return result.get(0).get(0).equals("t");
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
      return false;
   }

   // functions to update game catalog
   public static void changeGameName(GameRental esql) {
      try {
         System.out.println("You have selected: Change Game Name");
         System.out.println("Please enter gameID (game0000): ");
         String gameID = in.readLine();

         boolean validGame = validateGameID(esql, gameID);
         while(!validGame) {
            System.out.println("Invalid gameID");
            System.out.println("Please enter gameID (game0000): ");
            gameID = in.readLine();
            validGame = validateGameID(esql, gameID);
         }

         boolean namesMatch = false;
         while(!namesMatch) {

            System.out.println("Please enter new game name: ");
            String name1 = in.readLine();
            System.out.println("Please confirm new game name: ");
            String name2 = in.readLine();

            if(name1.equals(name2)) {
               namesMatch = true;

               System.out.println("Updating game name...");
               String update = "UPDATE Catalog SET gameName = '" + name1 + "' WHERE gameID = '" + gameID +"'";
               esql.executeUpdate(update);


               System.out.println("Game name changed successfully");
               System.out.println("Name of " + gameID + " changed to " + name1);
            }
            else {
               System.out.println("Game names do not match!");

               // let user cancel
               boolean validRetry = false;
               while (!validRetry) {
                  System.out.println("Would you like to try again? (y/n): ");
                  String retry = in.readLine();
                  switch (retry) {
                     case "y":
                        validRetry = true; break;
                     case "n":
                        System.out.println("Returning to Catalog Settings..."); return;

                     default: System.out.println("Invalid input");
                  }
               }
            }
         }
      }catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }
//
//   public static void changeGenre(GameRental esql) {
//      try {
//
//      }catch(Exception e) {
//         System.err.println(e.getMessage());
//      }
//   }
//
//   public static void changePrice(GameRental esql) {
//      try {
//
//      }catch(Exception e) {
//         System.err.println(e.getMessage());
//      }
//   }
//   public static void changeDescription(GameRental esql) {
//      try {
//
//      }catch(Exception e) {
//         System.err.println(e.getMessage());
//      }
//   }
}//end GameRental

