/* Ibrahim Khan
 * CS 4920
 * Homework 1 - Problem 6
 * 02/02/2022
 * This program was supposed to demonstrate the Hill cipher, but I am unable to complete this
 * part of the homework on time. This program only reads the key from the file and transfers it
 * to a matrix. If the key is 4 characters long, a 2x2 matrix is created for the key. If the key
 * is 9 characters long, a 3x3 matrix is created for the key. This program also adds the 
 * appropriate numerical values to the matrix based on the key from the file.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class KhanHW1P6 {
	
	public static void main(String[] args) throws IOException {
		
		final char[] LETTERS = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		
		//Setting up the plaintext file to read from and ciphertext file to write to
		File bInputFile = new File("class_input_b.txt"); //INSERT INPUT PLAINTEXT FILE NAME HERE
		FileReader bFileReader = new FileReader(bInputFile);
		BufferedReader bBufferedReader = new BufferedReader(bFileReader);
		PrintWriter writeBFile = new PrintWriter(new File("Khan_output_6b.txt")); //INSERT OUTPUT FILE NAME HERE
		
		//Obtain key from file
		String key = bBufferedReader.readLine();
		
		//Temporary array to hold key from file
		int[] tempKeyArray = new int[key.length()];
		
		//Loop runs until entire key has been added to temporary array
		for (int i = 0; i < key.length(); i++) {
			char charKey = Character.toUpperCase(key.charAt(i));
			for (int j = 0; j < LETTERS.length; j++) {
				if (charKey == LETTERS[j]) {
					tempKeyArray[i] = j;
					break;
				}
			}
		}
		
		//Determining matrix size
		int matrixSize = 0;
		if (key.length() > 4) {
			matrixSize = 3;
		}
		else {
			matrixSize = 2;
		}
		
		//Creating matrix (2D array) to hold key
		int[][] matrixKey = new int[matrixSize][matrixSize];
		
		//Transferring key from temporary array to matrix
		int tempCounter = 0;
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				matrixKey[i][j] = tempKeyArray[tempCounter];
				tempCounter++;
			}
		}
		
		//Closing files used for encryption
		bFileReader.close();
		bBufferedReader.close();
		writeBFile.close();
		
		//Setting up the ciphertext file to read from and plaintext file to write to
		File cInputFile = new File("class_input_c.txt"); //INSERT INPUT PLAINTEXT FILE NAME HERE
		FileReader cFileReader = new FileReader(cInputFile);
		BufferedReader cBufferedReader = new BufferedReader(cFileReader);
		PrintWriter writeCFile = new PrintWriter(new File("Khan_output_6c.txt")); //INSERT OUTPUT FILE NAME HERE
		
		//Obtain key from file
		String key2 = cBufferedReader.readLine();
		
		//Temporary array to hold key from file
		int[] tempKeyArray2 = new int[key2.length()];
		
		//Loop runs until entire key has been added to temporary array
		for (int i = 0; i < key2.length(); i++) {
			char charKey = Character.toUpperCase(key.charAt(i));
			for (int j = 0; j < LETTERS.length; j++) {
				if (charKey == LETTERS[j]) {
					tempKeyArray2[i] = j;
					break;
				}
			}
		}
		
		//Determining matrix size
		int matrixSize2 = 0;
		if (key2.length() > 4) {
			matrixSize2 = 3;
		}
		else {
			matrixSize2 = 2;
		}
		
		//Creating matrix (2D array) to hold key
		int[][] matrixKey2 = new int[matrixSize2][matrixSize2];
		
		//Transferring key from temporary array to matrix
		int tempCounter2 = 0;
		for (int i = 0; i < matrixSize2; i++) {
			for (int j = 0; j < matrixSize2; j++) {
				matrixKey2[i][j] = tempKeyArray2[tempCounter2];
				tempCounter2++;
			}
		}
		
		//Closing files used for decryption
		cFileReader.close();
		cBufferedReader.close();
		writeCFile.close();
	}
}