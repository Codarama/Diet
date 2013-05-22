package net.ayld.facade.ui.console.command.impl;

import net.ayld.facade.ui.console.command.Command;
import net.ayld.facade.ui.console.model.Argument;

public class GangnamCommand extends AbstractCommand implements Command{

	public GangnamCommand() {
		supportNames("oppa", "gangnam");
		supportArguments("sexey", "leydey");
	}

	@Override
	protected void internalExecute(Argument... args) {
		System.out.println("op op op");
	}

}