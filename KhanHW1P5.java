/* Ibrahim Khan
 * CS 4920
 * Homework 1 - Problem 5
 * 02/02/2022
 * This program demonstrates the encryption and decryption of a Caesar cipher. First, an array of letters is
 * created to be used for comparison with text files. Then, the program opens a plaintext file, obtains the 
 * key, encrypts the message, and writes the ciphertext to a new file. Afterwards, the program opens another 
 * file, obtains the key, decrypts the ciphertext, and writes the plaintext to a new file.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class KhanHW1P5 {

	public static void main(String[] args) throws IOException {
		
		final char[] LETTERS = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
		
		//Setting up the plaintext file to read from and ciphertext file to write to
		File bInputFile = new File("class_input_b.txt"); //INSERT INPUT PLAINTEXT FILE NAME HERE
		FileReader bFileReader = new FileReader(bInputFile);
		BufferedReader bBufferedReader = new BufferedReader(bFileReader);
		PrintWriter writeBFile = new PrintWriter(new File("Khan_output_5b.txt")); //INSERT OUTPUT FILE NAME HERE
		
		//Obtain key from file
		String key = bBufferedReader.readLine();
		char charKey = Character.toUpperCase(key.charAt(0));
		int numKey = 0;
		for (int i = 0; i < LETTERS.length; i++) {
			if (charKey == LETTERS[i]) {
				numKey = i;
				break;
			}
		}
		
		//Loop that iterates through plaintext file
		int temp = 0;
		while ((temp = bBufferedReader.read()) != -1) {
			char character = (char) Character.toUpperCase(temp);
			for (int i = 0; i < LETTERS.length; i++) {
				//If a match is found, corresponding ciphertext letter is written to file
				if (character == LETTERS[i]) {
					writeBFile.print(LETTERS[(i+numKey) % 26]);
					break;
				}
			}
		}
		
		//Closing files used for encryption
		bFileReader.close();
		bBufferedReader.close();
		writeBFile.close();
		
		//Setting up the ciphertext file to read from and plaintext file to write to
		File cInputFile = new File("class_input_c.txt"); //INSERT INPUT CIPHERTEXT FILE NAME HERE
		FileReader cFileReader = new FileReader(cInputFile);
		BufferedReader cBufferedReader = new BufferedReader(cFileReader);
		PrintWriter writeCFile = new PrintWriter(new File("Khan_output_5c.txt")); //INSERT OUTPUT FILE NAME HERE
		
		//Obtain key from file
		String key2 = cBufferedReader.readLine();
		char charKey2 = Character.toUpperCase(key2.charAt(0));
		int numKey2 = 0;
		for (int i = 0; i < LETTERS.length; i++) {
			if (charKey2 == LETTERS[i]) {
				numKey2 = i;
				break;
			}
		}
		
		//Loop that iterates through ciphertext file
		int temp2 = 0;
		while ((temp2 = cBufferedReader.read()) != -1) {
			char character2 = (char) Character.toUpperCase(temp2);
			for (int i = 0; i < LETTERS.length; i++) {
				//If a match is found, corresponding plaintext letter is written to file
				if (character2 == LETTERS[i]) {
					writeCFile.print(LETTERS[(i+26-numKey2) % 26]);
					break;
				}
			}
		}
		
		//Closing files used for decryption
		cFileReader.close();
		cBufferedReader.close();
		writeCFile.close();
		
	}
}