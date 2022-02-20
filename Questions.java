import java.util.ArrayList;

public class Questions {
    //variables for questions and choices
    public String Qu, ch1, ch2, ch3;
    public int correctChoice, id; 
    private static int noOfQu = 0;
    static ArrayList<Questions> questions = new ArrayList<Questions>();


    // Constructor for Server 
    public Questions (
        String Qu, 
        String ch1, 
        String ch2, 
        String ch3, 
        int correctChoice) {
            this.Qu = Qu; 
            this.ch1 = ch1; 
            this.ch2 = ch2; 
            this.ch3 = ch3; 
            this.correctChoice = correctChoice; 
            id = ++noOfQu;
            questions.add(this);
    }

        // Constructor for Client 
        public Questions (
            String Qu, 
            String ch1, 
            String ch2, 
            String ch3) {
                this.Qu = Qu; 
                this.ch1 = ch1; 
                this.ch2 = ch2; 
                this.ch3 = ch3; 
        }

    public static Questions QuestionID(int id) {
        for (Questions Qu : questions) {
            if (Qu.id == id) {
                return Qu;
            } 
        }
        return null;
    }
}