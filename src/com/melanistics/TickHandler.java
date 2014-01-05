package com.melanistics;

import java.util.ArrayList;
import java.util.List;

import com.melanistics.Init.IInputHandler;

public class TickHandler {
	private List<Runnable> run = new ArrayList<>();
	private List<IInputHandler> input = new ArrayList<>();

	public void Tick() {
		for (int i = 0; i < run.size(); i++) {
			run.get(i).run();
		}
	}

	public void Input(int par1, char par2, boolean par3) {
		for (int i = 0; i < input.size(); i++) {
			input.get(i).onKeyInput(par1, par2, par3);
		}
	}

	public void addRunnable(Runnable par1) {
		run.add(par1);
	}

	public void addInputHandler(IInputHandler par1) {
		input.add(par1);
	}

}
