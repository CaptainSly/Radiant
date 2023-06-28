package com.captainsly.radiant.core.impl;

public interface GameLogic extends Disposable {

	void onRender();
	void onUpdate(double delta);
	void onInput(double delta);
	void onInit();
	
}
