import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server {

    static int Score = 0; 

    public static void main(String[] args) throws IOException {
        //ServerSocket
        ServerSocket Server = new ServerSocket(8080); 
        Socket Socket = Server.accept(); 
        System.out.println("Client Connected!"); //print onto screen when connected
        //reader and writer
        BufferedReader reader = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
        PrintWriter writer = new PrintWriter(Socket.getOutputStream());
        //arraylist of arraylist
        ArrayList<Questions> questions = new ArrayList<Questions>();
        Questions qu1 = new Questions ("(A+B) * (A+B)", "A*A + B*B", "A*A + A*B + B*B", "A*A + 2*A*B + B*B", 3);
        Questions qu2 = new Questions ("(A + B) * (A - B)", "A*A + 2*B*B", "A*A - B*B", "A*A - 2*A*B + B*B", 2);
        Questions qu3 = new Questions ("sin(x)*sin(x) + cos(x)*cos(x)", "1", "2", "3", 1);
        questions.add(qu1); //add each question list into arraylist
        questions.add(qu2);
        questions.add(qu3);


        while (true) {
            //Read question answers from client then print score
            String ClientMessage = reader.readLine(); 
            if (ClientMessage == null) {
                break;
            }
            if (ClientMessage.equals("QuAva")) {
                writer.println(questions.size());

            } else if (ClientMessage.equals("Requesting to connect")) {
                writer.println("Welcome to the Maths Quiz");

            } else if (ClientMessage.equals("Request qu1")) {
                writer.println(qu1.Qu); 
            } else if (ClientMessage.equals("Request qu1Choice 1")) {
                writer.println(qu1.ch1); 
            } else if (ClientMessage.equals("Request qu1Choice 2")) {
                writer.println(qu1.ch2); 
            } else if (ClientMessage.equals("Request qu1Choice 3")) {
                writer.println(qu1.ch3); 

            } else if (ClientMessage.equals("Request qu2")) {
                writer.println(qu2.Qu); 
            } else if (ClientMessage.equals("Request qu2Choice 1")) {
                writer.println(qu2.ch1); 
            } else if (ClientMessage.equals("Request qu2Choice 2")) {
                writer.println(qu2.ch2); 
            } else if (ClientMessage.equals("Request qu2Choice 3")) {
                writer.println(qu2.ch3); 

            } else if (ClientMessage.equals("Request qu3")) {
                writer.println(qu3.Qu); 
            } else if (ClientMessage.equals("Request qu3Choice 1")) {
                writer.println(qu3.ch1); 
            } else if (ClientMessage.equals("Request qu3Choice 2")) {
                writer.println(qu3.ch2); 
            } else if (ClientMessage.equals("Request qu3Choice 3")) {
                writer.println(qu3.ch3); 

            } else if (ClientMessage.substring(0,8).equals("Qu")) {
                saveChoice(ClientMessage); 
                writer.println("Answer Saved"); 
            } else if (ClientMessage.equals("Request Score")) {
                System.out.println("Server will send score: " + Score); 
                writer.println(Score); 
            }
            writer.flush();
        }
        Socket.close();
        System.out.println("Connection Closed."); 
    }
//save Dropdown choice and update score
    public static void saveChoice(String ClientMessage) {
        int QuNo = 1; 
        int Ans = 0;
        try {
            QuNo = Integer.parseInt(ClientMessage.substring(8,9));
            Ans = Integer.parseInt(ClientMessage.substring(ClientMessage.length() - 1));
        } catch (Exception e) {
            QuNo = 0;
            Ans = 0;
        }
        //coint clients score
        Questions Qu = Questions.QuestionID(QuNo);
        if (Qu.correctChoice == Ans) {
            Score++;
        }
        //print out to user
        System.out.println("Question " + QuNo + " answer has been saved");
        System.out.println("Your score is: " + Score);
    }

    
}
