package com.base.app.employee.command;

import jakarta.validation.constraints.NotNull;

public record SetEmployeeActiveCommand(@NotNull Boolean active) {}
