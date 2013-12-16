package org.teiath.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

	char numberChars[] = "0123456789".toCharArray();
	char lowerChars[] = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	char upperChars[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	char otherSpecialChars[] = "!@#$%-_;:".toCharArray();

	List<Integer> listForLengthOfPassword = new ArrayList<Integer>();
	Random random = new Random();
	List<Character> finalpasswordList;

	public PasswordGenerator() {
		listForLengthOfPassword.add(2); // At least 2 numbers
		listForLengthOfPassword.add(2); // At least 2 lower case chars
		listForLengthOfPassword.add(2); // At least 2 upper case chars
		listForLengthOfPassword.add(2); // At least 2 special chars
	}

	/**
	 * Generates Randomly an 8 length password
	 *
	 * @return String the password
	 */
	public String getPassword() {
		Collections.shuffle(listForLengthOfPassword);
		finalpasswordList = new ArrayList<Character>();

		for (int t = 0; t < listForLengthOfPassword.size(); t++) {
			int numberOfCharPerArray = listForLengthOfPassword.get(t);
			for (int z = 0; z < numberOfCharPerArray; z++) {
				if (t == 0) {
					finalpasswordList.add(numberChars[random.nextInt(10)]);
				} else if (t == 1) {
					finalpasswordList.add(lowerChars[random.nextInt(26)]);
				} else if (t == 2) {
					finalpasswordList.add(upperChars[random.nextInt(26)]);
				} else if (t == 3) {
					finalpasswordList.add(otherSpecialChars[random.nextInt(9)]);   //random.nextInt(10)
				}
			}
		}

		String password = "";
		Collections.shuffle(finalpasswordList);
		for (Character passChar : finalpasswordList) {
			password += passChar;
		}
		return password; //"@#12345a"; //
	}
}