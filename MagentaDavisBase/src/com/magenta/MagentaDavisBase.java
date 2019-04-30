package com.magenta;


import com.magenta.prompt.MagentaDavisBasePrompt;

public class MagentaDavisBase {
	static String currentDatabase = "user_data";


	public static void main(String[] args) {
		MagentaDavisBasePrompt davisBasePrompt = new MagentaDavisBasePrompt();
		davisBasePrompt.prompt();
		return;
	}

}
