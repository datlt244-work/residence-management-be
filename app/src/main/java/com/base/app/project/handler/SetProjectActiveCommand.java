package com.base.app.project.handler;

import jakarta.validation.constraints.NotNull;

public record SetProjectActiveCommand(@NotNull Boolean active) {}
