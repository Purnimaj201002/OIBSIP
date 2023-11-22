package Internship;

import java.util.*;

public class Number_Guessing_Game {

    public static void main(String[] args) {
        Random r = new Random();
        int round = 3;
        int maxAttempts = 3;
        int winscore=0;
        int lostscore=0;

        Scanner sc = new Scanner(System.in);

        for (int i = 1; i <= round; i++) {
            int RNum = r.nextInt(101);
            int guessingCount = 0;

            System.out.println();
            System.out.println("Round " + i);

            while (guessingCount < maxAttempts) {
                System.out.println("Enter your Guess between 1-100 : ");

                int guess = sc.nextInt();
                guessingCount++;

                if (guess == RNum)
                {
                    System.out.println("Hurray!!");
                    System.out.println("You Win!!");
                    winscore=winscore+10;
                    System.out.println("your score is : " + winscore);
                    break;
                } 
                else if (RNum > guess) 
                {
                    System.out.println("The number you guessed is lower..Try again!!");
                } 
                else
                {
                    System.out.println("The number you guessed is higher..Try again!!");
                }

                if (guessingCount == maxAttempts) {
                	System.out.println("you lost this round.");
                	System.out.println("your score is :" + lostscore);
                    System.out.println("You've reached the maximum number of attempts for this round.");
                    break;
                }
            }
        }

        sc.close();
        
        System.out.println();
        System.out.println("Game Over.!!");
        System.out.println("Thank you for playing.");
    }
}
