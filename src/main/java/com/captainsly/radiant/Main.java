package com.captainsly.radiant;

import com.captainsly.radiant.core.Engine;
import com.captainsly.radiant.test.Game;

public class Main {

	public static void main(String[] args) {
		Engine e = new Engine(new Game());
		e.run();
	}

}
