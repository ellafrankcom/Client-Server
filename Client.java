import java.net.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.awt.*;

public class Client {

    static int Panel_Index = 0;
    static String ServerAnswer = "1";  //answer gets sent tp server
    static String welcome = "1"; //welcome message
    static int QuAva = 0;  //number of questions available
    static String Score = "0";
    static ArrayList<Questions> questions = new ArrayList<Questions>(); //array list of questions, calls from Questions class

    public static void main(String[] args) throws IOException { //main program
        // connect to server 
        Socket Socket = new Socket("localhost", 8080);
        BufferedReader reader = new BufferedReader(new InputStreamReader(Socket.getInputStream()));

        retrieveQus(Socket, reader);
        createFrame(Socket, reader);
    }

    /*
    * Sends requests to the server to retrieve all the questions 
    * and answers 
    */
    public static void retrieveQus(Socket Socket, BufferedReader reader) throws IOException {
        //welcome message
        welcome = ServerRequest(Socket, reader, "Requesting to connect");

        try { 
            QuAva = Integer.parseInt(ServerRequest(Socket, reader, "QuAva"));
            System.out.println("Number of Questions Available: " + QuAva);
        } catch (Exception e) {
            QuAva = 0;
        }
        //prints out the users choices and sends them to server
        for (int i = 1; i <= QuAva ; i++) { 
            String QuTxt = ServerRequest(Socket, reader, "Request qu" + i);
            String ch1 = ServerRequest(Socket, reader, "Request qu" + i + "Choice 1");
            String ch2 = ServerRequest(Socket, reader, "Request qu" + i + "Choice 2");
            String ch3 = ServerRequest(Socket, reader, "Request qu" + i + "Choice 3");
            System.out.println("Question Text: " + QuTxt);
            System.out.println("Choice 1: " + ch1);
            System.out.println("Choice 2: " + ch2);
            System.out.println("Choice 3: " + ch3);
            Questions Qu = new Questions(QuTxt, ch1, ch2, ch3);
            questions.add(Qu); 
        }
    }

    /*
    * Sends request to server and recieves a 
    * response from the server in a form of a String 
    */ 
    public static String ServerRequest(Socket Socket, BufferedReader bufferedReader, String Request) {
        String response;
        try {
            PrintWriter writer = new PrintWriter(Socket.getOutputStream());
            writer.println(Request);
            writer.flush();
            response = bufferedReader.readLine();
        } catch (IOException e) {
            response = "Connection Error...";
        }
        return response; 
    }

    // Sends the answers to the server.
    public static void AnsToServer(Socket Socket, BufferedReader bufferedReader, int QuNo, String Ans) {
        //could be error - Qu, answer
        ServerRequest(Socket, bufferedReader, "Qu" + QuNo + "Ans" + Ans);
    }

     //Create main frame of GUI and add the panels. 
    public static void createFrame(Socket Socket, BufferedReader reader) {
        JFrame Frame = new JFrame("Maths Quiz!");
        Frame.setSize(400, 300);
        Frame.setResizable(false);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel Main = new JPanel(new BorderLayout());    
        
        JPanel welcomePanel = getWelcomePanel(welcome); 
        ArrayList<JPanel> panels = new ArrayList<JPanel>();
        panels.add(welcomePanel);

        for (Questions Qu : questions) {
            JPanel questionPanel = getQuestion(Socket, reader, Qu.Qu, Qu.ch1, Qu.ch2, Qu.ch3);
            panels.add(questionPanel);
        }
        //create buttons for GUI
        JPanel ButtonPanel = new JPanel(new BorderLayout());    
        JButton start = new JButton("Start");
        JButton next = new JButton("Next");
        JButton exit = new JButton("Exit");
        
        ButtonPanel.add(start, BorderLayout.EAST);
        ButtonPanel.add(exit, BorderLayout.WEST);
        //adds them to main 
        Main.add(welcomePanel, BorderLayout.CENTER);
        Main.add(ButtonPanel, BorderLayout.SOUTH);

        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	// remove the welcome panel and add the Qu panel from the list of panels 
                Main.remove(panels.get(Panel_Index));
                Panel_Index++; 
                Main.add(panels.get(Panel_Index), BorderLayout.CENTER);

                // remove the start button and add the Next button
                start.setVisible(false);
                ButtonPanel.add(next, BorderLayout.EAST);
            }
        });

        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // send the answer to the server 
                System.out.println("Answer to send to server: " + ServerAnswer); 
                System.out.println("Question Number: " + Panel_Index); 
                AnsToServer(Socket, reader, Panel_Index, ServerAnswer);
                // reset the value to the default 
                ServerAnswer = "1";
                // replace the current panel with the next 
                Main.remove(panels.get(Panel_Index));
                Panel_Index++; 
                
                if (Panel_Index < panels.size()) {
                    Main.add(panels.get(Panel_Index), BorderLayout.CENTER);
                    
                } else {
                    //show result panel and exit button at the end of the quiz 
                    Score = ServerRequest(Socket, reader, "Request Score");
                    System.out.println("Score received from server " + Score);
                    JPanel resultsPanel = getResults(Score);
                    Main.add(resultsPanel, BorderLayout.CENTER);
                    next.setVisible(false);
                    ButtonPanel.add(exit, BorderLayout.EAST);
                }
                Main.repaint();
                Main.revalidate();
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	 try {
                    Socket.close();
				} 
                 catch (IOException e1) {
                    System.out.println("Error, unable to close the connection");
				}
                System.exit(0);
            }
        });

        Frame.add(Main);
        Frame.setVisible(true);
    }

    /*
    * Take the welcome message as a parameter and returns a JPanel 
    * containing the message. 
    */
    public static JPanel getWelcomePanel(String welcome) {
        JPanel welcomePanel = new JPanel();    
        welcomePanel.setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel(welcome, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Ariel", Font.BOLD,22));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        return welcomePanel;
    }

    /*
    * Create panel with a question and answers choices put in a drop 
    * drop down to select and send to the server. 
    * It takes a Socket and bufferedreader so it can send the choice to the server
    */
    public static JPanel getQuestion(Socket Socket, BufferedReader reader, String Qu, String ch1, String ch2, String ch3) {
        JPanel panel = new JPanel();    
        panel.setLayout(null);
        JLabel questionLabel = new JLabel(Qu);
        questionLabel.setFont(new Font("Ariel", Font.BOLD,18));
        questionLabel.setBounds(10,20,800,25);
        panel.add(questionLabel);

        JLabel ch1Label = new JLabel("<html><p style='padding-left:10px'>1) " + ch1 + "</p></html>");
        ch1Label.setBounds(10,70,200,25);
        panel.add(ch1Label);

        JLabel ch2Label = new JLabel("<html><p style='padding-left:10px'>2) " + ch2 + "</p></html>");
        ch2Label.setBounds(10,110,200,25);
        panel.add(ch2Label);

        JLabel ch3Label = new JLabel("<html><p style='padding-left:10px'>3) " + ch3 + "</p></html>");
        ch3Label.setBounds(10,150,200,25);
        panel.add(ch3Label);

        JLabel label = new JLabel("<html><p style='padding:0 3px 0 10px'>Choose Answer</p></html>");
        label.setBounds(10,190,150,35);
        label.setFont(new Font("Ariel", Font.BOLD,20));
        JComboBox<String> Dropdown =new JComboBox<String> (new String[] {"1","2","3"});    
        Dropdown.setBounds(160,190,80,35);
        panel.add(label);
        panel.add(Dropdown);
        //get answer and sends to server
        Dropdown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();  
                ServerAnswer = box.getSelectedItem().toString();
            }
        });
        return panel;
    }
    
    //Takes a S=score message and returns it on screen
    public static JPanel getResults(String Score) {
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        JLabel CompletedLabel = new JLabel("Quiz Complete!", SwingConstants.CENTER);
        JLabel resultsLabel = new JLabel("<html><p style='padding-bottom:40px'>Results: " + Score + "</p></html>", SwingConstants.CENTER);
        CompletedLabel.setFont(new Font("Ariel", Font.BOLD,18));
        resultsLabel.setFont(new Font("Ariel", Font.BOLD,18));
        resultsPanel.add(CompletedLabel, BorderLayout.CENTER);
        resultsPanel.add(resultsLabel, BorderLayout.SOUTH);
        return resultsPanel;
    }
}
