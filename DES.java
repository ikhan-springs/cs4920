/* Ibrahim Khan
 * CS 4920
 * Homework 3 - Problem 1
 * 03/18/2022
 * This program demonstrates how DES encryption works. The program reads the key, the plaintext, and the
 * number of rounds to be done from an input file and outputs the ciphertext to a new file. In designing 
 * this program, a step-by-step approach was taken and each step in the DES process was translated into its
 * own function (i.e. unique method for E table, P table, IP, Inverse IP, S-boxes, etc.). Instead of making
 * a separate function for each round of DES, this part was implemented within the main function itself. 
 * Also, several arrays were created so that the different sizes of keys/text could fill an entire array. 
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class DES {

	public static void main(String[] args) throws IOException {
		
		//Declaring variables
		int numRounds; //Number of rounds to be done
		String key, plaintext; //Strings to hold key and plaintext values from file
		char[] hexKey = new char[16]; //Original key in hex format
		char[] hexPlaintext = new char[16]; //Original plaintext in hex format
		int[] binaryKey = new int[64]; //Key translated to binary
		int[] binaryText = new int[64]; //Plaintext translated to binary, used for permutations too
		int[] leftBinaryText = new int[32]; //Left side of binary plaintext
		int[] rightBinaryText = new int[32]; //Right side of binary plaintext
		int[] tempRightBinary = new int[48]; //Temporary binary array used in rounds
		int[] permutedKey1 = new int[56]; //Key for PC1 and left shifts
		int[] permutedKey2 = new int[48]; //Key for PC2
		
		//Obtain input file name from user
		System.out.print("Enter name of input file: ");
		Scanner input = new Scanner(System.in);
		String inputFileName = input.nextLine();
		
		//Read from input file
		File inputFile = new File(inputFileName);
		Scanner readInputFile = new Scanner(inputFile);
		numRounds = readInputFile.nextInt();
		readInputFile.nextLine();
		key = readInputFile.nextLine();
		plaintext = readInputFile.nextLine();
		
		//Transfer file data to hex arrays
		for (int i = 0; i < 16; i++) {
			hexKey[i] = key.charAt(i);
			hexPlaintext[i] = plaintext.charAt(i);
		}				
		
		binaryKey = convertHexToBinary(hexKey);
		binaryText = convertHexToBinary(hexPlaintext);
		
		binaryText = initialPermutation(binaryText);
		
		permutedKey1 = permutedChoice1(binaryKey);
		
		//Each loop iteration is a single round of DES
		for (int i = 0; i < numRounds; i++) {
			
			//Split binary text into two (left and right)
			for (int j = 0; j < 32; j++) {
				leftBinaryText[j] = binaryText[j];
				rightBinaryText[j] = binaryText[j+32];
			}
			
			permutedKey1 = leftCircularShift(permutedKey1);
			
			//Switch statement checks if another left circular shift is needed
			switch (i) {
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				permutedKey1 = leftCircularShift(permutedKey1);
				break;
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				permutedKey1 = leftCircularShift(permutedKey1);
				break;
			default: break;
			}
			
			permutedKey2 = permutedChoice2(permutedKey1);
			
			//Right side of binary text now becomes the left side
			for (int j = 0; j < 32; j++) {
				binaryText[j] = rightBinaryText[j];
			}
			
			tempRightBinary = expansion(rightBinaryText);
			tempRightBinary = XOR(tempRightBinary, permutedKey2);
			rightBinaryText = substitution(tempRightBinary);
			rightBinaryText = permutation(rightBinaryText);
			rightBinaryText = XOR(rightBinaryText, leftBinaryText);
			
			//New right side added to binary text
			for (int j = 0; j < 32; j++) {
				binaryText[j+32] = rightBinaryText[j];
			}
			
		}
		
		binaryText = swapSides(binaryText);
		binaryText = inverseInitialPermutation(binaryText);
		
		//Write to output file
		PrintWriter writeFile = new PrintWriter(new File("Khan_output_" + inputFileName.charAt(12) + ".txt"));
		for (int i = 0; i < 64; i++) {
			writeFile.print(binaryText[i]);
		}
		
		input.close();
		readInputFile.close();
		writeFile.close();
		
	} //main
	
	//This function converts a hex array of 16 values into a binary array of 64 values
	public static int[] convertHexToBinary(char[] hexArray) {
		
		int hexTracker = 0;
		int[] binaryArray = new int[64];
		
		for (int i = 0; i < 64; i += 4) {
			switch(hexArray[hexTracker]) {
			case '0':
				binaryArray[i] = 0;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 0;
				break;
			case '1':
				binaryArray[i] = 0;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 1;
				break;
			case '2':
				binaryArray[i] = 0;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 0;
				break;
			case '3':
				binaryArray[i] = 0;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 1;
				break;
			case '4':
				binaryArray[i] = 0;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 0;
				break;
			case '5':
				binaryArray[i] = 0;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 1;
				break;
			case '6':
				binaryArray[i] = 0;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 0;
				break;
			case '7':
				binaryArray[i] = 0;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 1;
				break;
			case '8':
				binaryArray[i] = 1;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 0;
				break;
			case '9':
				binaryArray[i] = 1;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 1;
				break;
			case 'A':
				binaryArray[i] = 1;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 0;
				break;
			case 'B':
				binaryArray[i] = 1;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 1;
				break;
			case 'C':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 0;
				break;
			case 'D':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 1;
				break;
			case 'E':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 0;
				break;
			case 'F':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 1;
				break;
			case 'a':
				binaryArray[i] = 1;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 0;
				break;
			case 'b':
				binaryArray[i] = 1;
				binaryArray[i+1] = 0;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 1;
				break;
			case 'c':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 0;
				break;
			case 'd':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 0;
				binaryArray[i+3] = 1;
				break;
			case 'e':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 0;
				break;
			case 'f':
				binaryArray[i] = 1;
				binaryArray[i+1] = 1;
				binaryArray[i+2] = 1;
				binaryArray[i+3] = 1;
				break;
			}
			
			hexTracker++;
		}
		
		return binaryArray;
		
	} //convertHexToBinary
	
	//This function implements the E table in DES
	public static int[] expansion(int[] data) {
		
		int[] expandedData = new int[48];
		
		expandedData[0] = data[31];
		expandedData[1] = data[0];
		expandedData[2] = data[1];
		expandedData[3] = data[2];
		expandedData[4] = data[3];
		expandedData[5] = data[4];
		expandedData[6] = data[3];
		expandedData[7] = data[4];
		expandedData[8] = data[5];
		expandedData[9] = data[6];
		expandedData[10] = data[7];
		expandedData[11] = data[8];
		expandedData[12] = data[7];
		expandedData[13] = data[8];
		expandedData[14] = data[9];
		expandedData[15] = data[10];
		expandedData[16] = data[11];
		expandedData[17] = data[12];
		expandedData[18] = data[11];
		expandedData[19] = data[12];
		expandedData[20] = data[13];
		expandedData[21] = data[14];
		expandedData[22] = data[15];
		expandedData[23] = data[16];
		expandedData[24] = data[15];
		expandedData[25] = data[16];
		expandedData[26] = data[17];
		expandedData[27] = data[18];
		expandedData[28] = data[19];
		expandedData[29] = data[20];
		expandedData[30] = data[19];
		expandedData[31] = data[20];
		expandedData[32] = data[21];
		expandedData[33] = data[22];
		expandedData[34] = data[23];
		expandedData[35] = data[24];
		expandedData[36] = data[23];
		expandedData[37] = data[24];
		expandedData[38] = data[25];
		expandedData[39] = data[26];
		expandedData[40] = data[27];
		expandedData[41] = data[28];
		expandedData[42] = data[27];
		expandedData[43] = data[28];
		expandedData[44] = data[29];
		expandedData[45] = data[30];
		expandedData[46] = data[31];
		expandedData[47] = data[0];
		
		return expandedData;
		
	} //expansion
	
	//This function implements the IP table in DES
	public static int[] initialPermutation(int[] plaintext) {
		
		int[] permutedResult = new int[64];
		
		permutedResult[0] = plaintext[57];
		permutedResult[1] = plaintext[49];
		permutedResult[2] = plaintext[41];
		permutedResult[3] = plaintext[33];
		permutedResult[4] = plaintext[25];
		permutedResult[5] = plaintext[17];
		permutedResult[6] = plaintext[9];
		permutedResult[7] = plaintext[1];
		permutedResult[8] = plaintext[59];
		permutedResult[9] = plaintext[51];
		permutedResult[10] = plaintext[43];
		permutedResult[11] = plaintext[35];
		permutedResult[12] = plaintext[27];
		permutedResult[13] = plaintext[19];
		permutedResult[14] = plaintext[11];
		permutedResult[15] = plaintext[3];
		permutedResult[16] = plaintext[61];
		permutedResult[17] = plaintext[53];
		permutedResult[18] = plaintext[45];
		permutedResult[19] = plaintext[37];
		permutedResult[20] = plaintext[29];
		permutedResult[21] = plaintext[21];
		permutedResult[22] = plaintext[13];
		permutedResult[23] = plaintext[5];
		permutedResult[24] = plaintext[63];
		permutedResult[25] = plaintext[55];
		permutedResult[26] = plaintext[47];
		permutedResult[27] = plaintext[39];
		permutedResult[28] = plaintext[31];
		permutedResult[29] = plaintext[23];
		permutedResult[30] = plaintext[15];
		permutedResult[31] = plaintext[7];
		permutedResult[32] = plaintext[56];
		permutedResult[33] = plaintext[48];
		permutedResult[34] = plaintext[40];
		permutedResult[35] = plaintext[32];
		permutedResult[36] = plaintext[24];
		permutedResult[37] = plaintext[16];
		permutedResult[38] = plaintext[8];
		permutedResult[39] = plaintext[0];
		permutedResult[40] = plaintext[58];
		permutedResult[41] = plaintext[50];
		permutedResult[42] = plaintext[42];
		permutedResult[43] = plaintext[34];
		permutedResult[44] = plaintext[26];
		permutedResult[45] = plaintext[18];
		permutedResult[46] = plaintext[10];
		permutedResult[47] = plaintext[2];
		permutedResult[48] = plaintext[60];
		permutedResult[49] = plaintext[52];
		permutedResult[50] = plaintext[44];
		permutedResult[51] = plaintext[36];
		permutedResult[52] = plaintext[28];
		permutedResult[53] = plaintext[20];
		permutedResult[54] = plaintext[12];
		permutedResult[55] = plaintext[4];
		permutedResult[56] = plaintext[62];
		permutedResult[57] = plaintext[54];
		permutedResult[58] = plaintext[46];
		permutedResult[59] = plaintext[38];
		permutedResult[60] = plaintext[30];
		permutedResult[61] = plaintext[22];
		permutedResult[62] = plaintext[14];
		permutedResult[63] = plaintext[6];
		
		return permutedResult;
		
	} //initialPermutation
	
	//This function implements the IP^-1 table in DES
	public static int[] inverseInitialPermutation(int[] text) {
		
		int[] permutedResult = new int[64];
		
		permutedResult[0] = text[39];
		permutedResult[1] = text[7];
		permutedResult[2] = text[47];
		permutedResult[3] = text[15];
		permutedResult[4] = text[55];
		permutedResult[5] = text[23];
		permutedResult[6] = text[63];
		permutedResult[7] = text[31];
		permutedResult[8] = text[38];
		permutedResult[9] = text[6];
		permutedResult[10] = text[46];
		permutedResult[11] = text[14];
		permutedResult[12] = text[54];
		permutedResult[13] = text[22];
		permutedResult[14] = text[62];
		permutedResult[15] = text[30];
		permutedResult[16] = text[37];
		permutedResult[17] = text[5];
		permutedResult[18] = text[45];
		permutedResult[19] = text[13];
		permutedResult[20] = text[53];
		permutedResult[21] = text[21];
		permutedResult[22] = text[61];
		permutedResult[23] = text[29];
		permutedResult[24] = text[36];
		permutedResult[25] = text[4];
		permutedResult[26] = text[44];
		permutedResult[27] = text[12];
		permutedResult[28] = text[52];
		permutedResult[29] = text[20];
		permutedResult[30] = text[60];
		permutedResult[31] = text[28];
		permutedResult[32] = text[35];
		permutedResult[33] = text[3];
		permutedResult[34] = text[43];
		permutedResult[35] = text[11];
		permutedResult[36] = text[51];
		permutedResult[37] = text[19];
		permutedResult[38] = text[59];
		permutedResult[39] = text[27];
		permutedResult[40] = text[34];
		permutedResult[41] = text[2];
		permutedResult[42] = text[42];
		permutedResult[43] = text[10];
		permutedResult[44] = text[50];
		permutedResult[45] = text[18];
		permutedResult[46] = text[58];
		permutedResult[47] = text[26];
		permutedResult[48] = text[33];
		permutedResult[49] = text[1];
		permutedResult[50] = text[41];
		permutedResult[51] = text[9];
		permutedResult[52] = text[49];
		permutedResult[53] = text[17];
		permutedResult[54] = text[57];
		permutedResult[55] = text[25];
		permutedResult[56] = text[32];
		permutedResult[57] = text[0];
		permutedResult[58] = text[40];
		permutedResult[59] = text[8];
		permutedResult[60] = text[48];
		permutedResult[61] = text[16];
		permutedResult[62] = text[56];
		permutedResult[63] = text[24];
		
		return permutedResult;
		
	} //inverseInitialPermutation
	
	//This function conducts a left circular shift on an array of 56 values
	public static int[] leftCircularShift(int[] key) {
		
		int[] leftShiftedResult = new int[28];
		int[] rightShiftedResult = new int[28];
		int[] shiftedResult = new int[56];
		
		for (int i = 0; i < 27; i++) {
			leftShiftedResult[i] = key[i+1];
		}
		leftShiftedResult[27] = key[0];
		
		for (int i = 0; i < 27; i++) {
			rightShiftedResult[i] = key[i+29];
		}
		rightShiftedResult[27] = key[28];
		
		for (int i = 0; i < 28; i++) {
			shiftedResult[i] = leftShiftedResult[i];
			shiftedResult[i+28] = rightShiftedResult[i];
		}
		
		return shiftedResult;
		
	} //leftCircularShift
	
	//This function implements the P table in DES
	public static int[] permutation(int[] data) {
		
		int[] permutedData = new int[32];
		
		permutedData[0] = data[15];
		permutedData[1] = data[6];
		permutedData[2] = data[19];
		permutedData[3] = data[20];
		permutedData[4] = data[28];
		permutedData[5] = data[11];
		permutedData[6] = data[27];
		permutedData[7] = data[16];
		permutedData[8] = data[0];
		permutedData[9] = data[14];
		permutedData[10] = data[22];
		permutedData[11] = data[25];
		permutedData[12] = data[4];
		permutedData[13] = data[17];
		permutedData[14] = data[30];
		permutedData[15] = data[9];
		permutedData[16] = data[1];
		permutedData[17] = data[7];
		permutedData[18] = data[23];
		permutedData[19] = data[13];
		permutedData[20] = data[31];
		permutedData[21] = data[26];
		permutedData[22] = data[2];
		permutedData[23] = data[8];
		permutedData[24] = data[18];
		permutedData[25] = data[12];
		permutedData[26] = data[29];
		permutedData[27] = data[5];
		permutedData[28] = data[21];
		permutedData[29] = data[10];
		permutedData[30] = data[3];
		permutedData[31] = data[24];
		
		return permutedData;
		
	} //permutation
	
	//This function implements PC1 in DES
	public static int[] permutedChoice1(int[] key) {
		
		int[] permutedResult = new int[56];
		
		permutedResult[0] = key[56];
		permutedResult[1] = key[48];
		permutedResult[2] = key[40];
		permutedResult[3] = key[32];
		permutedResult[4] = key[24];
		permutedResult[5] = key[16];
		permutedResult[6] = key[8];
		permutedResult[7] = key[0];
		permutedResult[8] = key[57];
		permutedResult[9] = key[49];
		permutedResult[10] = key[41];
		permutedResult[11] = key[33];
		permutedResult[12] = key[25];
		permutedResult[13] = key[17];
		permutedResult[14] = key[9];
		permutedResult[15] = key[1];
		permutedResult[16] = key[58];
		permutedResult[17] = key[50];
		permutedResult[18] = key[42];
		permutedResult[19] = key[34];
		permutedResult[20] = key[26];
		permutedResult[21] = key[18];
		permutedResult[22] = key[10];
		permutedResult[23] = key[2];
		permutedResult[24] = key[59];
		permutedResult[25] = key[51];
		permutedResult[26] = key[43];
		permutedResult[27] = key[35];
		permutedResult[28] = key[62];
		permutedResult[29] = key[54];
		permutedResult[30] = key[46];
		permutedResult[31] = key[38];
		permutedResult[32] = key[30];
		permutedResult[33] = key[22];
		permutedResult[34] = key[14];
		permutedResult[35] = key[6];
		permutedResult[36] = key[61];
		permutedResult[37] = key[53];
		permutedResult[38] = key[45];
		permutedResult[39] = key[37];
		permutedResult[40] = key[29];
		permutedResult[41] = key[21];
		permutedResult[42] = key[13];
		permutedResult[43] = key[5];
		permutedResult[44] = key[60];
		permutedResult[45] = key[52];
		permutedResult[46] = key[44];
		permutedResult[47] = key[36];
		permutedResult[48] = key[28];
		permutedResult[49] = key[20];
		permutedResult[50] = key[12];
		permutedResult[51] = key[4];
		permutedResult[52] = key[27];
		permutedResult[53] = key[19];
		permutedResult[54] = key[11];
		permutedResult[55] = key[5];
		
		return permutedResult;
		
	} //permutedChoice1
	
	//This function implements PC2 in DES
	public static int[] permutedChoice2(int[] key) {
		
		int[] permutedResult = new int[48];
		
		permutedResult[0] = key[13];
		permutedResult[1] = key[16];
		permutedResult[2] = key[10];
		permutedResult[3] = key[23];
		permutedResult[4] = key[0];
		permutedResult[5] = key[4];
		permutedResult[6] = key[2];
		permutedResult[7] = key[27];
		permutedResult[8] = key[14];
		permutedResult[9] = key[5];
		permutedResult[10] = key[20];
		permutedResult[11] = key[9];
		permutedResult[12] = key[22];
		permutedResult[13] = key[18];
		permutedResult[14] = key[11];
		permutedResult[15] = key[3];
		permutedResult[16] = key[25];
		permutedResult[17] = key[7];
		permutedResult[18] = key[15];
		permutedResult[19] = key[6];
		permutedResult[20] = key[26];
		permutedResult[21] = key[19];
		permutedResult[22] = key[12];
		permutedResult[23] = key[1];
		permutedResult[24] = key[40];
		permutedResult[25] = key[51];
		permutedResult[26] = key[30];
		permutedResult[27] = key[36];
		permutedResult[28] = key[46];
		permutedResult[29] = key[54];
		permutedResult[30] = key[29];
		permutedResult[31] = key[39];
		permutedResult[32] = key[50];
		permutedResult[33] = key[44];
		permutedResult[34] = key[32];
		permutedResult[35] = key[47];
		permutedResult[36] = key[43];
		permutedResult[37] = key[48];
		permutedResult[38] = key[38];
		permutedResult[39] = key[55];
		permutedResult[40] = key[33];
		permutedResult[41] = key[52];
		permutedResult[42] = key[45];
		permutedResult[43] = key[41];
		permutedResult[44] = key[49];
		permutedResult[45] = key[35];
		permutedResult[46] = key[28];
		permutedResult[47] = key[31];
		
		return permutedResult;
		
	} //permutedChoice2
	
	//This function implements the 8 S-boxes in DES
	public static int[] substitution(int[] data) {
		
		int[] tempData = new int[6];
		int[] tempResult = new int[4];
		int[] substitutedData = new int[32];
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i];
		}
		tempResult = sBox1(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+6];
		}
		tempResult = sBox2(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+4] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+12];
		}
		tempResult = sBox3(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+8] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+18];
		}
		tempResult = sBox4(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+12] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+24];
		}
		tempResult = sBox5(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+16] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+30];
		}
		tempResult = sBox6(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+20] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+36];
		}
		tempResult = sBox7(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+24] = tempResult[i];
		}
		
		for (int i = 0; i < 6; i++) {
			tempData[i] = data[i+42];
		}
		tempResult = sBox8(tempData);
		for (int i = 0; i < 4; i++) {
			substitutedData[i+28] = tempResult[i];
		}
		
		return substitutedData;
		
	} //substitution
	
	public static int[] sBox1(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox1
	
	public static int[] sBox2(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox2
	
	public static int[] sBox3(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox3
	
	public static int[] sBox4(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox4
	
	public static int[] sBox5(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox5
	
	public static int[] sBox6(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox6
	
	public static int[] sBox7(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox7
	
	public static int[] sBox8(int[] input) {
		
		int[] output = new int[4];
		
		switch (input[0]) {
		case 0:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		case 1:
			switch (input[5]) {
			case 0:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			case 1:
				switch (input[1]) {
				case 0:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				case 1:
					switch (input[2]) {
					case 0:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 1;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 1;
								output[1] = 1;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 1;
								output[1] = 0;
								output[2] = 0;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 0;
								output[2] = 0;
								output[3] = 0;
								break;
							}
							break;
						}
						break;
					case 1:
						switch (input[3]) {
						case 0:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							case 1:
								output[0] = 0;
								output[1] = 1;
								output[2] = 0;
								output[3] = 1;
								break;
							}
							break;
						case 1:
							switch (input[4]) {
							case 0:
								output[0] = 0;
								output[1] = 1;
								output[2] = 1;
								output[3] = 0;
								break;
							case 1:
								output[0] = 1;
								output[1] = 0;
								output[2] = 1;
								output[3] = 1;
								break;
							}
							break;
						}
						break;
					}
					break;
				}
				break;
			}
			break;
		}
		
		return output;
		
	} //sBox8
	
	//This function swaps the left and right sides of an array with 64 values
	public static int[] swapSides(int[] array) {
		
		int[] side1 = new int[32];
		int[] side2 = new int[32];
		
		for (int i = 0; i < 32; i++) {
			side1[i] = array[i];
			side2[i] = array[i+32];
		}
		
		for (int i = 0; i < 32; i++) {
			array[i] = side2[i];
			array[i+32] = side1[i];
		}
		
		return array;
		
	} //swapSides
	
	//This function returns an array with XORed values from two arrays of the same length
	public static int[] XOR(int[] array1, int[] array2) {
		
		if (array1.length != array2.length) {
			System.out.println("UNABLE TO XOR!");
			return array1;
		}
		
		int[] arrayXOR = new int[array1.length];
		
		for (int i = 0; i < array1.length; i++) {
			arrayXOR[i] = array1[i] ^ array2[i];
		}
		
		return arrayXOR;
		
	} //XOR
	
}