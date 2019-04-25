package com.magenta;

import com.magenta.prompt.MagentaDavisBasePrompt;

public class MagentaDavisBase {

	public static void main(String[] args) {
		MagentaDavisBasePrompt davisBasePrompt = new MagentaDavisBasePrompt();
		static String currentDatabase = "user_data";
		davisBasePrompt.prompt();
		return;
	}

}
