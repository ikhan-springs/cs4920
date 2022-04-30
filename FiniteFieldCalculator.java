/* Ibrahim Khan
 * CS 4920
 * Homework 3 - Problem 5
 * 03/18/2022
 * This program demonstrates how a finite field calculator works, using GF(2^8) with the irreducible
 * polynomial m(x) = x^8 + x^4 + x^3 + x + 1. The four basic math operations (addition, subtraction, 
 * multiplication, division) are implemented and the user has a choice to do one operation on each 
 * program run. The user will also input the two numbers for the operation.
 */

import java.util.Scanner;

public class FiniteFieldCalculator {

	public static void main(String[] args) {
		
		int choice, num1, num2, answer;
		
		System.out.println("This program is a simple four-function calculator.");
		System.out.print("Enter 1 for addition, 2 for subtraction, 3 for multiplication, or 4 for division: ");
		Scanner input1 = new Scanner(System.in);
		choice = input1.nextInt();
		
		while (choice < 1 || choice > 4) {
			System.out.println("You have entered an invalid choice. Please try again.");
			System.out.print("Enter 1 for addition, 2 for subtraction, 3 for multiplication, or 4 for division: ");
			input1 = new Scanner(System.in);
			choice = input1.nextInt();
		}
		
		System.out.print("Enter the first number for your selected operation: ");
		Scanner input2 = new Scanner(System.in);
		num1 = input2.nextInt();
		
		System.out.print("Enter the second number for your selected operation: ");
		Scanner input3 = new Scanner(System.in);
		num2 = input3.nextInt();
		
		switch(choice) {
		case 1:
			answer = addition(num1, num2);
			break;
		case 2:
			answer = subtraction(num1, num2);
			break;
		case 3:
			answer = multiplication(num1, num2);
			break;
		case 4:
			answer = division(num1, num2);
			break;
		default:
			answer = 0;
		}
		
		System.out.println("The answer is: "+answer);
		
		input1.close();
		input2.close();
		input3.close();
		
	} //main
	
	public static int addition(int num1, int num2) {
		
		return (num1 + num2) % 128;
		
	} //addition
	
	public static int subtraction(int num1, int num2) {
		
		return (num1 - num2) % 128;
		
	} //subtraction

	public static int multiplication(int num1, int num2) {
		
		return (num1 * num2) % 128;
	
	} //multiplication

	public static int division(int num1, int num2) {
		
		return (num1 / num2) % 128;
	
	} //division

}
