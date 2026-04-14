package com.base.interfaces.employee.request;

import com.base.app.employee.command.SetEmployeeActiveCommand;
import jakarta.validation.constraints.NotNull;

public record SetEmployeeActiveRequest(@NotNull Boolean active) {

    public SetEmployeeActiveCommand toCommand() {
        return new SetEmployeeActiveCommand(active);
    }
}
